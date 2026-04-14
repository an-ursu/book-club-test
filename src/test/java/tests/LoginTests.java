package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.UnsupportedMediaTypeResponseModel;
import models.login.WrongCredentialLoginResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    String username = LOGIN_USERNAME;
    String password = LOGIN_PASSWORD;
    String wrongPassword = LOGIN_WRONG_PASSWORD;


    @Test
    @DisplayName("Тест на проверку авторизации зарегистрированным пользователем")
    public void successfulLoginTest() {

        SuccessfulLoginResponseModel loginResponse =
                step("Авторизация зарегистрированного пользователя и проверка кода ответа", () -> {
                    LoginBodyModel loginData = new LoginBodyModel(username, password);

                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(successfulLoginResponseSpec)
                            .extract().as(SuccessfulLoginResponseModel.class);

                });

        step("Проверка корректности полученных токенов", () -> {

            String expectedTokenPath = LOGIN_TOKEN_PREFIX;
            String actualAccess = loginResponse.access();
            String actualRefresh = loginResponse.refresh();

            assertThat(actualAccess).startsWith(expectedTokenPath);
            assertThat(actualRefresh).startsWith(expectedTokenPath);
            assertThat(actualAccess).isNotEqualTo(actualRefresh);

        });

    }

    @Test
    @DisplayName("Тест на проверку авторизации c невалидным логином и паролем")
    public void wrongCredentialsLoginTest() {

        WrongCredentialLoginResponseModel wrongCredentialLoginResponse =
                step("Авторизация пользователя с невалидным логином и паролем и проверка кода ответа", () -> {

                    LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);

                    return given(loginRequestSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(wrongCredentialsLoginResponseSpec)
                            .extract().as(WrongCredentialLoginResponseModel.class);
                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;
            String actualDetails = wrongCredentialLoginResponse.detail();

            assertThat(actualDetails).isEqualTo(expectedDetailError);

        });

    }

    @Test
    @DisplayName("Тест на проверку авторизации без передачи Content-type")
    public void unsupportedMediaTypeLoginTest() {

        UnsupportedMediaTypeResponseModel unsupportedMediaTypeResponse =
                step("Авторизация пользователя без передачи Content-type", () -> {

                    LoginBodyModel loginData = new LoginBodyModel(username, password);

                    return given(loginRequestWithoutContentTypeSpec)
                            .body(loginData)
                            .when()
                            .post("/auth/token/")
                            .then()
                            .spec(unsupportedMediaTypeLoginResponseSpec)
                            .extract().as(UnsupportedMediaTypeResponseModel.class);

                });

        step("Проверка текста ошибки в теле ответа", () -> {

            String expectedUnsupportedMediaTypeError = UNSUPPORTED_MEDIA_TYPE_ERROR;
            String actualDetails = unsupportedMediaTypeResponse.detail();

            assertThat(actualDetails).isEqualTo(expectedUnsupportedMediaTypeError);

        });


    }


}





