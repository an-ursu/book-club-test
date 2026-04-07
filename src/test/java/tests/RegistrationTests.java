package tests;

import models.registration.EmptyRegistrationDataResponseModel;
import models.registration.ExistingUserResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.registration.model_examples.records.RegistrationBodyRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.registration.RegistrationSpec.*;
import static tests.TestData.*;


public class RegistrationTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {

        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().fullName();
    }


    @Test
    @DisplayName("Тест на проверку регистрации нового пользователя")
    public void successfulWithRecordRegistrationTest() {

        SuccessfulRegistrationResponseModel successfulRegistrationResponseModel =
                step("Регистрация нового пользователя и проверка кода ответа", () -> {
                    RegistrationBodyRecordsModel registrationData =
                            new RegistrationBodyRecordsModel(username, password);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);
                });

        step("Проверка корректности зарегистрированных данных", () -> {

            assertThat(successfulRegistrationResponseModel.username()).isEqualTo(username);
            assertThat(successfulRegistrationResponseModel.id()).isGreaterThan(0);
            assertThat(successfulRegistrationResponseModel.firstName()).isEqualTo("");
            assertThat(successfulRegistrationResponseModel.lastName()).isEqualTo("");
            assertThat(successfulRegistrationResponseModel.email()).isEqualTo("");
            assertThat(successfulRegistrationResponseModel.remoteAddr().matches(REGISTRATION_IP_REGEXP));


        });

    }


    @Test
    @DisplayName("Тест на проверку регистрации нового пользователя с существующими данными")
    public void existingUserWrongRegistrationTest() {

        SuccessfulRegistrationResponseModel firstRegistrationResponseModel =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyRecordsModel registrationData =
                            new RegistrationBodyRecordsModel(username, password);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);
                });

        step("Проверка соответствия данных в запросе и ответе метода", () -> {
            assertThat(firstRegistrationResponseModel.username()).isEqualTo(username);
        });

        ExistingUserResponseModel secondRegistrationResponseModel =
                step("Регистрация нового пользователя с ранее зарегистрированными данными", () -> {
                    RegistrationBodyRecordsModel registrationData =
                            new RegistrationBodyRecordsModel(username, password);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(existingUserRegistrationResponseSpec)
                            .extract().as(ExistingUserResponseModel.class);
                });


        step("Проверка текста ошибки в ответе метода", () -> {

            String actualError = secondRegistrationResponseModel.username().get(0);
            assertThat(actualError).isEqualTo(REGISTRATION_EXISTING_USER_ERROR);

        });

    }

    @Test
    @DisplayName("Тест на проверку регистрации нового пользователя с пустыми данными")
    public void emptyRegistrationDataTests() {

        EmptyRegistrationDataResponseModel emptyRegistrationDataResponseModel =
                step("", () -> {
                    RegistrationBodyRecordsModel registrationData =
                            new RegistrationBodyRecordsModel(EMPTY_STRING, EMPTY_STRING);

                    return given(registrationRequestSpec)
                            .body(registrationData)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(emptyRegistrationDataResponseSpec)
                            .extract().as(EmptyRegistrationDataResponseModel.class);

                });

        step("Проверка текста ошибок в ответе метода", () -> {

            String usernameError = emptyRegistrationDataResponseModel.username().get(0);
            String passwordError = emptyRegistrationDataResponseModel.password().get(0);

            assertThat(usernameError).isEqualTo(REGISTRATION_EMPTY_DATA_ERROR);
            assertThat(passwordError).isEqualTo(REGISTRATION_EMPTY_DATA_ERROR);

        });

    }

}
