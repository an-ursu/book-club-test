package models.logout;

import com.fasterxml.jackson.annotation.JsonCreator;

public record SuccessfulLogoutResponseModel(String response) {

    @JsonCreator
    public SuccessfulLogoutResponseModel() {
        this(null);
    }

}
