package DTO;

public class UserDTO {

    public enum Role {
        ADMIN, LANDLORD, TENANT
    }

    private String userId;
    private String username;
    private String name;
    private String phoneNumber;
    private Role   role;

    public UserDTO() {}

    public UserDTO(String userId, String username, String name, String phoneNumber, Role role) {
        this.userId      = userId;
        this.username    = username;
        this.name        = name;
        this.phoneNumber = phoneNumber;
        this.role        = role;
    }

    public String getUserId()         { return userId; }
    public void setUserId(String id)  { this.userId = id; }

    public String getUsername()               { return username; }
    public void setUsername(String username)  { this.username = username; }

    public String getName()           { return name; }
    public void setName(String name)  { this.name = name; }

    public String getPhoneNumber()                   { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber)   { this.phoneNumber = phoneNumber; }

    public Role getRole()            { return role; }
    public void setRole(Role role)   { this.role = role; }

    @Override
    public String toString() {
        return "UserDTO{userId='" + userId + "', username='" + username
                + "', name='" + name + "', role=" + role + "}";
    }
}
