package BLL;

import DTOs.Role;
import DTOs.UserDTO;

public final class SessionContext {
    private static UserDTO currentUser;

    private SessionContext() {
    }

    public static UserDTO getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserDTO user) {
        currentUser = user;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static Role getCurrentRole() {
        return currentUser == null ? null : currentUser.getRole();
    }

    public static UserDTO requireLogin() {
        if (currentUser == null) {
            throw new SecurityException("Đăng nhập là bắt buộc");
        }
        return currentUser;
    }

    public static UserDTO requireRole(Role role) {
        UserDTO user = requireLogin();
        if (user.getRole() != role) {
            throw new SecurityException("Quyền truy cập bị từ chối");
        }
        return user;
    }

    public static UserDTO requireAnyRole(Role... roles) {
        UserDTO user = requireLogin();
        for (Role role : roles) {
            if (user.getRole() == role) {
                return user;
            }
        }
        throw new SecurityException("Quyền truy cập bị từ chối");
    }

    public static boolean hasRole(Role role) {
        return currentUser != null && currentUser.getRole() == role;
    }
}
