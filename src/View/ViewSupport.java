package View;

import DTOs.Role;
import java.awt.Component;
import java.awt.Image;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

final class ViewSupport {
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private ViewSupport() {
    }

    static void openFrame(JFrame currentFrame, JFrame nextFrame) {
        nextFrame.setLocationRelativeTo(null);
        nextFrame.setVisible(true);
        if (currentFrame != null) {
            currentFrame.dispose();
        }
    }

    static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    static void showError(Component parent, Exception ex) {
        ex.printStackTrace(System.err);
        JOptionPane.showMessageDialog(parent, errorMessage(ex), "Error", JOptionPane.ERROR_MESSAGE);
    }

    static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    static String safe(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    static String money(double value) {
        return NUMBER_FORMAT.format(value) + " VND";
    }

    static String area(double value) {
        return NUMBER_FORMAT.format(value) + " m2";
    }

    static String rating(double average, int count) {
        if (count <= 0) {
            return "No rating";
        }
        return String.format(Locale.US, "%.1f/5 (%d)", average, count);
    }

    static String availability(boolean available) {
        return available ? "Available" : "Unavailable";
    }

    static String status(boolean approved) {
        return approved ? "Approved" : "Pending";
    }

    static String role(Role role) {
        return role == null ? "-" : role.name();
    }

    static String dateTime(LocalDateTime value) {
        return value == null ? "-" : value.format(DATE_TIME_FORMAT);
    }

    static void setScaledImage(JLabel label, String imagePath, int width, int height) {
        ImageIcon icon = imageIcon(imagePath);
        if (icon == null) {
            label.setIcon(null);
            label.setText("No image");
            label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            return;
        }

        Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaledImage));
        label.setText("");
        label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    }

    static String html(String value) {
        return "<html>" + escapeHtml(safe(value)).replace("\n", "<br>") + "</html>";
    }

    private static ImageIcon imageIcon(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            return null;
        }

        ImageIcon icon = new ImageIcon(imagePath.trim());
        return icon.getIconWidth() <= 0 ? null : icon;
    }

    private static String errorMessage(Exception ex) {
        String message = safe(ex.getMessage());
        if (ex instanceof java.sql.SQLException) {
            return "Database error: " + message;
        }
        if (ex instanceof SecurityException) {
            return "Permission denied: " + message;
        }
        return message;
    }

    private static String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
