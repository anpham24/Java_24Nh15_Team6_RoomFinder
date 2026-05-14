package DTOs;

public class LoginResultDTO {
    private boolean success;
    private String message;
    private UserDTO user;

    public LoginResultDTO() {
    }

    public LoginResultDTO(boolean success, String message, UserDTO user) {
        this.success = success;
        this.message = message;
        this.user = user;
    }

    public static LoginResultDTO success(UserDTO user) {
        return new LoginResultDTO(true, "Login successful", user);
    }

    public static LoginResultDTO failure(String message) {
        return new LoginResultDTO(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
