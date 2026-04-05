package specs.update;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.filter.log.LogDetail.ALL;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static specs.BaseSpec.baseRequestSpec;

public class UpdateUserSpec {

    public static RequestSpecification updateUserRequestSpec = baseRequestSpec;

    public static ResponseSpecification successfulUpdateUserResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/update/successful_update_response_schema.json"))
            .build();


    public static ResponseSpecification invalidCredentialsResponseSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(401)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/update/invalid_credentials_schema.json"))
            .build();

    public static ResponseSpecification successfulPartialUpdateUserSpec = new ResponseSpecBuilder()
            .log(ALL)
            .expectStatusCode(200)
            .expectBody(matchesJsonSchemaInClasspath(
                    "schemas/update/successful_update_response_schema.json"))
            .build();

}
