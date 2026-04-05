package tests.model_examples;

import io.restassured.http.ContentType;
import models.registration.model_examples.lombok.RegistrationResponseLombokModel;
import models.registration.model_examples.pojo.RegistrationBodyPojoModel;
import models.registration.model_examples.pojo.RegistrationResponsePojoModel;
import models.registration.model_examples.lombok.RegistrationBodyLombokModel;
import models.registration.model_examples.records.ExistingUser400ResponseRecordsModel;
import models.registration.model_examples.records.RegistrationBodyRecordsModel;
import models.registration.model_examples.records.RegistrationResponseRecordsModel;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
public class RegistrationWithModelTests {

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

        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username,password);

        RegistrationResponseRecordsModel registrationResponseRecordsModel = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseRecordsModel.class);

        assertEquals(username, registrationResponseRecordsModel.username());

    }

    @Test
    public void successfulWithLombokRegistrationTest() {

        RegistrationBodyLombokModel data = new RegistrationBodyLombokModel();
        data.setUsername(username);
        data.setPassword(password);

        RegistrationResponseLombokModel registrationResponseLombokModel = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseLombokModel.class);

        assertEquals(username, registrationResponseLombokModel.getUsername());

    }


    @Test
    public void successfulWithPojoRegistrationTest() {

        RegistrationBodyPojoModel data = new RegistrationBodyPojoModel();
        data.setUserName(username);
        data.setPassword(password);

        RegistrationResponsePojoModel registrationResponsePojoModel = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponsePojoModel.class);

        assertEquals(username, registrationResponsePojoModel.getUsername());

    }

    @Test
    public void invalidUsername400Test() {


        String data = "{\"username\": \"" + username + "\",\"password\": \"" + password + "\"}";

        given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .body("username", is(username))
                .body("id", notNullValue());

    }


    @Test
    public void existingUser400Test() {

        RegistrationBodyRecordsModel data = new RegistrationBodyRecordsModel(username,password);

        RegistrationResponseRecordsModel registrationResponseRecordsModel = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(201)
                .extract()
                .as(RegistrationResponseRecordsModel.class);

        assertEquals(username, registrationResponseRecordsModel.username());


        ExistingUser400ResponseRecordsModel existingUser400ResponseRecordsModel = given()
                .log().all()
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register/")
                .then()
                .log().all()
                .statusCode(400)
                .extract()
                .as(ExistingUser400ResponseRecordsModel.class);

        String expectedError = "A user with that username already exists.";
        assertEquals(expectedError, existingUser400ResponseRecordsModel.username().getFirst());

    }


    @Test
    public void negativeRegistration500Test() {


        String data = "{\"username\": \"" + username + "\",\"password\": \"" + password + "\"}";

        given()
                .body(data)
                .contentType(ContentType.JSON)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register")
                .then()
                .statusCode(500)
                .body("username", is(username))
                .body("id", notNullValue());

    }

    @Test
    public void unsupportedMediaType415Test() {


        String data = "{\"username\": \"" + username + "\",\"password\": \"" + password + "\"}";

        given()
                .body(data)
                .when()
                .post("http://bookclub.qa.guru:8000/api/v1/users/register")
                .then()
                .statusCode(415)
                .body("username", is(username))
                .body("id", notNullValue());

    }
}
