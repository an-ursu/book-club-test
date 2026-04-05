package models.registration;

import java.util.List;

public record EmptyRegistrationDataResponseModel(
        List<String> username,
        List<String> password
) {}
