package tests;

import models.registration.EmptyRegistrationDataResponseModel;
import models.registration.ExistingUserResponseModel;
import models.registration.SuccessfulRegistrationResponseModel;
import models.registration.model_examples.records.RegistrationBodyRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    public void successfulWithRecordRegistrationTest() {

        RegistrationBodyRecordsModel registrationData = new RegistrationBodyRecordsModel(username, password);

        SuccessfulRegistrationResponseModel successfulRegistrationResponseModel = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);

        String actualUserName = successfulRegistrationResponseModel.username();

        assertThat(actualUserName).isEqualTo(username);
        assertThat(successfulRegistrationResponseModel.id()).isGreaterThan(0);
        assertThat(successfulRegistrationResponseModel.firstName()).isEqualTo("");
        assertThat(successfulRegistrationResponseModel.lastName()).isEqualTo("");
        assertThat(successfulRegistrationResponseModel.email()).isEqualTo("");
        assertThat(successfulRegistrationResponseModel.remoteAddr().matches(REGISTRATION_IP_REGEXP));

    }

    @Test
    public void existingUserWrongRegistrationTest() {

        RegistrationBodyRecordsModel registrationData = new RegistrationBodyRecordsModel(username, password);

        SuccessfulRegistrationResponseModel firstRegistrationResponseModel = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(successfulRegistrationResponseSpec)
                .extract().as(SuccessfulRegistrationResponseModel.class);

        String actualUserName = firstRegistrationResponseModel.username();
        assertThat(actualUserName).isEqualTo(username);

        ExistingUserResponseModel secondRegistrationResponseModel = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(existingUserRegistrationResponseSpec)
                .extract().as(ExistingUserResponseModel.class);

        String actualError = secondRegistrationResponseModel.username().get(0);
        assertThat(actualError).isEqualTo(REGISTRATION_EXISTING_USER_ERROR);

    }

    @Test
    public void emptyRegistrationDataTests() {


        RegistrationBodyRecordsModel registrationData = new RegistrationBodyRecordsModel(EMPTY_STRING, EMPTY_STRING);

        EmptyRegistrationDataResponseModel emptyRegistrationDataResponseModel = given(registrationRequestSpec)
                .body(registrationData)
                .when()
                .post("/users/register/")
                .then()
                .spec(emptyRegistrationDataResponseSpec)
                .extract().as(EmptyRegistrationDataResponseModel.class);

        String usernameError = emptyRegistrationDataResponseModel.username().get(0);
        String passwordError = emptyRegistrationDataResponseModel.password().get(0);

        assertThat(usernameError).isEqualTo(REGISTRATION_EMPTY_DATA_ERROR);
        assertThat(passwordError).isEqualTo(REGISTRATION_EMPTY_DATA_ERROR);




    }

    //todo negative tests

}
