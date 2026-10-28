import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class psc extends JFrame {

    private static final String URL =
            "jdbc:mysql://localhost:3306/password_checker";
    private static final String USER = "admin";
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    private final Color BG = new Color(18, 18, 28);
    private final Color PANEL = new Color(29, 29, 45);
    private final Color PURPLE = new Color(170, 100, 255);
    private final Color WHITE = new Color(240, 240, 250);

    private JPasswordField passwordField;
    private JCheckBox showPassword;
    private JLabel strengthLabel;
    private JTextArea feedbackArea;

    public psc() {
        setTitle("Password Strength Checker");
        setSize(520, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(false);
        setResizable(true);
        setExtendedState(JFrame.NORMAL);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(
                new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(
                new EmptyBorder(25, 30, 25, 30));
        mainPanel.setBackground(BG);

        JLabel title = new JLabel("Password Strength Checker");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(PURPLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Check your password security");
        subtitle.setForeground(Color.LIGHT_GRAY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Enter Password:");
        passwordLabel.setForeground(WHITE);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 38));
        passwordField.setBackground(PANEL);
        passwordField.setForeground(WHITE);
        passwordField.setCaretColor(PURPLE);
        passwordField.setBorder(
                BorderFactory.createLineBorder(PURPLE));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

        showPassword = new JCheckBox("Show password");
        showPassword.setBackground(BG);
        showPassword.setForeground(WHITE);
        showPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        strengthLabel = new JLabel("Strength: Not checked");
        strengthLabel.setForeground(PURPLE);
        strengthLabel.setFont(new Font("Arial", Font.BOLD, 17));
        strengthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        feedbackArea = new JTextArea(5, 30);
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setFont(new Font("Arial", Font.PLAIN, 13));
        feedbackArea.setBackground(PANEL);
        feedbackArea.setForeground(WHITE);
        feedbackArea.setCaretColor(WHITE);
        feedbackArea.setBorder(new EmptyBorder(8, 8, 8, 8));

        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 115));

        JButton checkButton = new JButton("Check Strength");
        JButton clearButton = new JButton("Clear");

        checkButton.setBackground(PURPLE);
        checkButton.setForeground(Color.WHITE);
        checkButton.setFocusPainted(false);

        clearButton.setBackground(PANEL);
        clearButton.setForeground(WHITE);
        clearButton.setFocusPainted(false);

        JPanel buttonPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BG);
        buttonPanel.add(checkButton);
        buttonPanel.add(clearButton);

        mainPanel.add(title);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitle);
        mainPanel.add(Box.createVerticalStrut(25));
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(7));
        mainPanel.add(passwordField);
        mainPanel.add(showPassword);
        mainPanel.add(Box.createVerticalStrut(18));
        mainPanel.add(strengthLabel);
        mainPanel.add(Box.createVerticalStrut(8));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(buttonPanel);

        add(mainPanel);

        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('\u2022');
            }
        });

        checkButton.addActionListener(e -> checkPassword());

        clearButton.addActionListener(e -> {
            passwordField.setText("");
            strengthLabel.setText("Strength: Not checked");
            feedbackArea.setText("");
            passwordField.requestFocusInWindow();
        });
    }

    private void checkPassword() {
        char[] chars = passwordField.getPassword();

        if (chars.length == 0) {
            JOptionPane.showMessageDialog(
                    this, "Please enter a password.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String password = new String(chars);

        boolean upper = false, lower = false;
        boolean digit = false, special = false;

        for (char ch : chars) {
            if (Character.isUpperCase(ch))
                upper = true;
            else if (Character.isLowerCase(ch))
                lower = true;
            else if (Character.isDigit(ch))
                digit = true;
            else
                special = true;
        }

        int criteria = 0;
        if (upper) criteria++;
        if (lower) criteria++;
        if (digit) criteria++;
        if (special) criteria++;

        String strength;
        StringBuilder feedback = new StringBuilder();

        if (chars.length >= 12 && criteria == 4) {
            strength = "Strong";
            feedback.append("Your password meets all the checks.\n");
        } else if (chars.length >= 8 && criteria >= 3) {
            strength = "Medium";
            feedback.append("Your password has reasonable variety.\n");
        } else {
            strength = "Weak";
            feedback.append("Your password needs improvement.\n");
        }

        if (chars.length < 12)
            feedback.append("- Use at least 12 characters.\n");
        if (!upper)
            feedback.append("- Add an uppercase letter (A-Z).\n");
        if (!lower)
            feedback.append("- Add a lowercase letter (a-z).\n");
        if (!digit)
            feedback.append("- Add a number (0-9).\n");
        if (!special)
            feedback.append("- Add a special character, such as ! or @.\n");

        strengthLabel.setText("Strength: " + strength);
        feedbackArea.setText(feedback.toString());

        try {
            saveResultToDatabase(strength);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    private void saveResultToDatabase(String strength) {
        String sql = "INSERT INTO password_history "
                   + "(password_strength) VALUES (?)";

        try (Connection con =

        
        
                     DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, strength);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Result saved to database successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (SQLException e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    "Database save failed: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            psc app = new psc();
            app.setVisible(true);
        });
    }
}