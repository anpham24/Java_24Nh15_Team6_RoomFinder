package DTOs;

public enum Role {
    ADMIN,
    LANDLORD,
    TENANT;

    public static Role fromDbValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Role is required");
        }

        String normalized = value.trim().toUpperCase();
        for (Role role : values()) {
            if (role.name().equals(normalized)) {
                return role;
            }
        }

        throw new IllegalArgumentException("Unsupported role: " + value);
    }

    public String toDbValue() {
        return name();
    }

    public boolean canRegister() {
        return this == TENANT || this == LANDLORD;
    }
}
