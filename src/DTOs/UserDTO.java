package DTOs;

public class UserDTO {
    private int userId;
    private String username;
    private String name;
    private String phoneNumber;
    private Role role;

    public UserDTO() {
    }

    public UserDTO(int userId, String username, String name, String phoneNumber, Role role) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
