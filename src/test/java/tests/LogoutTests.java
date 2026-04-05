package tests;


import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.*;
import models.registration.RegistrationBodyModel;
import models.registration.SuccessfulRegistrationResponseModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void successfulLogoutTest() {

        RegistrationBodyModel registrationBody = new RegistrationBodyModel(username, password);

        SuccessfulRegistrationResponseModel successfulRegistrationResponse = given(registrationRequestSpec)
                .body(registrationBody)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String refreshToken = loginResponse.refresh();

        LogoutBodyModel logoutData = new LogoutBodyModel(refreshToken);

        SuccessfulLogoutResponseModel successfulLogoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract().as(SuccessfulLogoutResponseModel.class);

    }

    @Test
    public void logoutWithEmptyRefreshTokenTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(EMPTY_STRING);

        EmptyRefreshTokenResponseModel emptyRefreshTokenResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(logoutWithEmptyRefreshTokenResponseSpec)
                .extract().as(EmptyRefreshTokenResponseModel.class);

        String actualRefresh = emptyRefreshTokenResponse.refresh().getFirst();

        assertThat(actualRefresh).isEqualTo(EMPTY_REFRESH_TOKEN_ERROR);
    }

    @Test
    public void logoutWithInvalidRefreshTokenTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(INVALID_REFRESH_TOKEN);

        InvalidRefreshTypeTokenResponseModel invalidRefreshTypeTokenResponse = given(loginRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(logoutWithInvalidRefreshTokenResponseSpec)
                .extract().as(InvalidRefreshTypeTokenResponseModel.class);

        String actualDetail = invalidRefreshTypeTokenResponse.detail();
        String actualCode = invalidRefreshTypeTokenResponse.code();

        assertThat(actualDetail).isEqualTo(INVALID_REFRESH_TOKEN_ERROR);
        assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);

    }

    @Test
    public void logoutWithExpiredRefreshTokenTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(EXPIRED_REFRESH_TOKEN);

        ExpiredRefreshTokenResponseModel expiredRefreshTokenResponse = given(loginRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(logoutWithExpiredRefreshTokenResponseSpec)
                .extract().as(ExpiredRefreshTokenResponseModel.class);

        String actualDetail = expiredRefreshTokenResponse.detail();
        String actualCode = expiredRefreshTokenResponse.code();

        assertThat(actualDetail).isEqualTo(EXPIRED_REFRESH_TOKEN_ERROR);
        assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);
    }

    @Test
    public void logoutWithWrongTypeTokenTest() {

        LogoutBodyModel logoutData = new LogoutBodyModel(WRONG_TYPE_TOKEN);

        WrongTypeTokenResponseModel wrongTypeTokenResponse = given(loginRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(logoutWithWrongTypeTokenResponseSpec)
                .extract().as(WrongTypeTokenResponseModel.class);

        String actualDetail = wrongTypeTokenResponse.detail();
        String actualCode = wrongTypeTokenResponse.code();

        assertThat(actualDetail).isEqualTo(WRONG_TOKEN_TYPE_ERROR);
        assertThat(actualCode).isEqualTo(ERROR_CODE_NAME);

    }

}
