package models.logout;

public record ExpiredRefreshTokenResponseModel(String detail,
                                               String code) {
}
