package tests;


import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.*;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;
import static specs.registration.RegistrationSpec.registrationRequestSpec;
import static specs.registration.RegistrationSpec.successfulRegistrationResponseSpec;
import static tests.TestData.*;

public class LogoutTests extends TestBase {

    String username;
    String password;

    @BeforeEach
    public void prepareTestData() {
        Faker faker = new Faker();
        username = faker.name().firstName();
        password = faker.name().firstName();
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы авторизованного пользователя")
    public void successfulLogoutTest() {

        SuccessfulRegistrationResponseModel successfulRegistrationResponse =
                step("Регистрация нового пользователя", () -> {
                    RegistrationBodyModel registrationBody = new RegistrationBodyModel(username, password);
                    return given(registrationRequestSpec)
                            .body(registrationBody)
                            .when()
                            .post("/users/register/")
                            .then()
                            .spec(successfulRegistrationResponseSpec)
                            .extract().as(SuccessfulRegistrationResponseModel.class);

                });


        String refreshToken =
                step("Авторизация и получение токена", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);
                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class)
                            .refresh();

                });

        SuccessfulLogoutResponseModel successfulLogoutResponse =

                step("Выполнение запроса logout и проверка ответа", () -> {
                    LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);
                    return given(logoutRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(successfulLogoutResponseSpec)
                            .extract().as(SuccessfulLogoutResponseModel.class);
                });
    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с пустым refresh token")
    public void logoutWithEmptyRefreshTokenTest() {

        EmptyRefreshTokenResponseModel emptyRefreshTokenResponse =
                step("Выполнение запроса logout с пустым refresh token и проверка код ответа", () -> {
                    LogoutBodyModel logoutData = new LogoutBodyModel(EMPTY_STRING);

                    return given(logoutRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(logoutWithEmptyRefreshTokenResponseSpec)
                            .extract().as(EmptyRefreshTokenResponseModel.class);
                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String actualRefresh = emptyRefreshTokenResponse.refresh().get(0);

            assertThat(actualRefresh).isEqualTo(EMPTY_REFRESH_TOKEN_ERROR);

        });


    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с невалидным refresh token")
    public void logoutWithInvalidRefreshTokenTest() {

        InvalidRefreshTypeTokenResponseModel invalidRefreshTypeTokenResponse =
                step("Выполнение запроса logout с невалидным refresh token и проверка код ответа", () -> {

                    LogoutBodyModel logoutData = new LogoutBodyModel(INVALID_REFRESH_TOKEN);

                    return given(loginRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(logoutWithInvalidRefreshTokenResponseSpec)
                            .extract().as(InvalidRefreshTypeTokenResponseModel.class);

                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String actualDetail = invalidRefreshTypeTokenResponse.detail();
            String actualCode = invalidRefreshTypeTokenResponse.code();

            assertThat(actualDetail).isEqualTo(INVALID_REFRESH_TOKEN_ERROR);
            assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);

        });

    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с использованным refresh token")
    public void logoutWithExpiredRefreshTokenTest() {

        ExpiredRefreshTokenResponseModel expiredRefreshTokenResponse =
                step("Выполнение запроса logout с использованным refresh token и проверка код ответа", () -> {

                    LogoutBodyModel logoutData = new LogoutBodyModel(EXPIRED_REFRESH_TOKEN);

                    return given(loginRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(logoutWithExpiredRefreshTokenResponseSpec)
                            .extract().as(ExpiredRefreshTokenResponseModel.class);
                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String actualDetail = expiredRefreshTokenResponse.detail();
            String actualCode = expiredRefreshTokenResponse.code();

            assertThat(actualDetail).isEqualTo(EXPIRED_REFRESH_TOKEN_ERROR);
            assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);

        });

    }

    @Test
    @DisplayName("Тест на проверку выхода из системы с access token")
    public void logoutWithWrongTypeTokenTest() {

        WrongTypeTokenResponseModel wrongTypeTokenResponse =
                step("Выполнение запроса logout с access token и проверка код ответа", () -> {

                    LogoutBodyModel logoutData = new LogoutBodyModel(WRONG_TYPE_TOKEN);

                    return given(loginRequestSpec)
                            .body(logoutData)
                            .when()
                            .post("/auth/logout/")
                            .then()
                            .spec(logoutWithWrongTypeTokenResponseSpec)
                            .extract().as(WrongTypeTokenResponseModel.class);

                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String actualDetail = wrongTypeTokenResponse.detail();
            String actualCode = wrongTypeTokenResponse.code();

            assertThat(actualDetail).isEqualTo(WRONG_TOKEN_TYPE_ERROR);
            assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);

        });


    }

}
