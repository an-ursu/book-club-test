package models.registration.model_examples.lombok;

import lombok.Data;

import static java.lang.String.format;

@Data
public class RegistrationResponseLombokModel {

    Integer id;
    String username;
    String firstName;
    String lastName;
    String email;
    String remoteAddr;

    /*public String toString() {
        return format("{\"id\": \"%s\"," +
                        "\"username\": \"%s\", " +
                        "\"firstName\": \"%s\", " +
                        "\"lastName\": \"%s\", " +
                        "\"email\": \"%s\", " +
                        "\"remoteAddr\": \"%s\"}",
                this.id,
                this.username,
                this.firstName,
                this.lastName,
                this.email,
                this.remoteAddr);
    }*/
}
