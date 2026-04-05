package tests;

public class TestData {

    public static final String LOGIN_USERNAME = "qaguru";
    public static final String LOGIN_PASSWORD = "qaguru123";
    public static final String LOGIN_WRONG_PASSWORD = "qaguru1234";

    public static final String LOGIN_TOKEN_PREFIX = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9";
    public static final String LOGIN_WRONG_CREDENTIALS_ERROR = "Invalid username or password.";

    public static final String REGISTRATION_EXISTING_USER_ERROR =
            "A user with that username already exists.";

    public static final String UNSUPPORTED_MEDIA_TYPE_ERROR = "Unsupported media type \"text/plain; charset=ISO-8859-1\" in request.";

    public static final String REGISTRATION_IP_REGEXP =
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}"
                    + "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$";

    public static final String EMPTY_STRING = "";

    public static final String REGISTRATION_EMPTY_DATA_ERROR =
            "This field may not be blank.";

    public static final String WRONG_TYPE_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoiYWNjZXNzIiwiZXhwIjoxNzc1OTc1NTYyLCJpYXQiOjE3NzUzNzA3NjIsImp0aSI6ImVmMDQxYzY1ZGI0ZDRlM2Y5YmVjNTVmZmYwNTA5NTMyIiwidXNlcl9pZCI6OTUyfQ.n6yI-L59Sqt22bqCWm3XxMBGJF3Iurca_hmR8PAnqjI";
    public static final String INVALID_REFRESH_TOKEN = "refresh";
    public static final String EXPIRED_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoicmVmcmVzaCIsImV4cCI6MTc3NzQ0NDM2MiwiaWF0IjoxNzc1MzcwNzYyLCJqdGkiOiI3NzQxYTc0NzU2MTQ0YTk2YTgxZGNhMGQ4YTEwYTcyYSIsInVzZXJfaWQiOjk1Mn0.rDjTTR_oImZAwmI-vYKt1eS2X3fOhQGiK_xNyIo_m4I";

    public static final String ERROR_CODE_NAME = "token_not_valid";
    public static final String INVALID_REFRESH_TOKEN_ERROR = "Token is invalid";
    public static final String EXPIRED_REFRESH_TOKEN_ERROR = "Token is blacklisted";
    public static final String WRONG_TOKEN_TYPE_ERROR = "Token has wrong type";
    public static final String EMPTY_REFRESH_TOKEN_ERROR = "This field may not be blank.";

    public static final String INVALID_CREDENTIALS_ERROR = "Given token not valid for any token type";

}
