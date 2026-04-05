package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.UnsupportedMediaTypeResponseModel;
import models.login.WrongCredentialLoginResponseModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.*;
import static tests.TestData.*;

public class LoginTests extends TestBase {

    String username = LOGIN_USERNAME;
    String password = LOGIN_PASSWORD;
    String wrongPassword = LOGIN_WRONG_PASSWORD;


    @Test
    public void successfulLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        String expectedTokenPath = LOGIN_TOKEN_PREFIX;

        String actualAccess = loginResponse.access();
        String actualRefresh = loginResponse.refresh();

        assertThat(actualAccess).startsWith(expectedTokenPath);
        assertThat(actualRefresh).startsWith(expectedTokenPath);
        assertThat(actualAccess).isNotEqualTo(actualRefresh);

    }

    @Test
    public void wrongCredentialsLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(username, wrongPassword);

        WrongCredentialLoginResponseModel wrongCredentialLoginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialLoginResponseModel.class);

        String expectedDetailError = LOGIN_WRONG_CREDENTIALS_ERROR;

        String actualDetails = wrongCredentialLoginResponse.detail();

        assertThat(actualDetails).isEqualTo(expectedDetailError);


    }

    @Test
    public void unsupportedMediaTypeLoginTest() {

        LoginBodyModel loginData = new LoginBodyModel(username, password);

        UnsupportedMediaTypeResponseModel unsupportedMediaTypeResponse = given(loginRequestWithoutContentTypeSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(unsupportedMediaTypeLoginResponseSpec)
                .extract().as(UnsupportedMediaTypeResponseModel.class);

        String expectedUnsupportedMediaTypeError = UNSUPPORTED_MEDIA_TYPE_ERROR;

        String actualDetails = unsupportedMediaTypeResponse.detail();

        assertThat(actualDetails).isEqualTo(expectedUnsupportedMediaTypeError);

    }


}





