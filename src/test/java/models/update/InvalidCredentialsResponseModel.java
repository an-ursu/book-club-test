package models.update;

import java.util.List;

public record InvalidCredentialsResponseModel(
        String detail,
        String code,
        List<Message> messages
) {
    public record Message(
            String tokenClass,
            String tokenType,
            String message
    ) {

    }
}
