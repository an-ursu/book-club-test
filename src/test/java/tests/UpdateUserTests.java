package tests;

import models.login.LoginBodyModel;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.update.InvalidCredentialsResponseModel;
import models.update.PartialUpdateBodyModel;
import models.update.SuccessfulUpdateResponseModel;
import models.update.UpdateBodyModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

import static io.restassured.RestAssured.given;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static specs.update.UpdateUserSpec.*;
import static tests.TestData.*;

public class UpdateUserTests extends TestBase {

    String username;
    String password;
    String firstName;
    String lastName;
    String email;


    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        email = faker.internet().emailAddress();

    }

    @Test
    @DisplayName("Тест на обновление данных пользователя методом PUT")
    public void successfulUpdateUserTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {

                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);


                });

        String accessToken =
                step("Авторизация пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);

                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract()
                            .path("access");
                });

        SuccessfulUpdateResponseModel successfulUpdateResponse =
                step("Обновление данных пользователя методом PUT", () -> {
                    UpdateBodyModel updateBody = new UpdateBodyModel(username, firstName, lastName, email);

                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .body(updateBody)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(successfulUpdateUserResponseSpec)
                            .extract().as(SuccessfulUpdateResponseModel.class);

                });

        step("Проверка корректности обновленных данных", () -> {

            assertThat(successfulUpdateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(successfulUpdateResponse.username()).isEqualTo(username);
            assertThat(successfulUpdateResponse.firstName()).isEqualTo(firstName);
            assertThat(successfulUpdateResponse.lastName()).isEqualTo(lastName);
            assertThat(successfulUpdateResponse.email()).isEqualTo(email);
            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);

        });

        SuccessfulUpdateResponseModel updateUserData =
                step("Отправка запроса методом GET и проверка кода ответа", () -> {

                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + accessToken)
                            .when()
                            .get("/users/me/")
                            .then()
                            .spec(successfulUpdateUserResponseSpec)
                            .extract().as(SuccessfulUpdateResponseModel.class);
                });

        step("Проверка изменений через GET‑запрос", () -> {

            assertThat(updateUserData.id()).isEqualTo(registrationResponse.id());
            assertThat(updateUserData.username()).isEqualTo(username);
            assertThat(updateUserData.firstName()).isEqualTo(firstName);
            assertThat(updateUserData.lastName()).isEqualTo(lastName);
            assertThat(updateUserData.email()).isEqualTo(email);

        });

    }


    @Test
    @DisplayName("Тест на проверку обновления данных пользователя методом PUT c невалидным токеном")
    public void UpdateUserWithInvalidCredentialsTest() {


        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);

                });

        String actualRefreshToken =
                step("Авторизация пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);

                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract()
                            .path("refresh");

                });

        InvalidCredentialsResponseModel invalidCredentialsResponse =
                step("Обновление данных пользователя c невалидным токеном", () -> {
                    UpdateBodyModel updateBody = new UpdateBodyModel(username, firstName, lastName, email);

                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + actualRefreshToken)
                            .body(updateBody)
                            .when()
                            .put("/users/me/")
                            .then()
                            .spec(invalidCredentialsResponseSpec)
                            .extract().as(InvalidCredentialsResponseModel.class);

                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String actualDetail = invalidCredentialsResponse.detail();
            String actualCode = invalidCredentialsResponse.code();

            assertThat(actualDetail).isEqualTo(INVALID_CREDENTIALS_ERROR);
            assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);

        });

    }


    @Test
    @DisplayName("Тест на обновление данных пользователя методом PATCH")
    public void partialUpdateUserTest() {

        SuccessfulRegistrationResponseModel registrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationData = new RegistrationBodyModel(username, password);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract()
                            .as(SuccessfulRegistrationResponseModel.class);


                });

        String actualAccessToken =
                step("Авторизация пользователя", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract()
                            .path("access");

                });

        SuccessfulUpdateResponseModel updateResponse =
                step("Частичное обновление данных пользователя методом PATCH", () -> {
                    PartialUpdateBodyModel partialUpdateBody = new PartialUpdateBodyModel(lastName, email);

                    return given(updateUserRequestSpec)
                            .header("Authorization", "Bearer " + actualAccessToken)
                            .body(partialUpdateBody)
                            .when()
                            .patch("/users/me/")
                            .then()
                            .spec(successfulPartialUpdateUserSpec)
                            .extract().as(SuccessfulUpdateResponseModel.class);

                });

        step("Проверка корректности обновленных данных методом PATCH", () -> {

            assertThat(updateResponse.id()).isEqualTo(registrationResponse.id());
            assertThat(updateResponse.username()).isEqualTo(registrationResponse.username());
            assertThat(updateResponse.firstName()).isEqualTo(registrationResponse.firstName());
            assertThat(updateResponse.lastName()).isEqualTo(lastName);
            assertThat(updateResponse.email()).isEqualTo(email);
            assertThat(registrationResponse.remoteAddr()).matches(REGISTRATION_IP_REGEXP);

        });
    }


}
