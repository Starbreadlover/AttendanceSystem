import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class MainApp {

    private static final String DATABASE_FILE = "userDatabase.txt";
    private static Map<String, String> userDatabase = new HashMap<>();

    public static void main(String[] args) {
        loadUserDatabase();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to create an account? (yes/no)");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            createAccount();
        } else {
            System.out.println("Proceeding to login...");
        }

        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }

    private static void createAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Create an account\n");
        System.out.print("Set a Username: ");
        String userSet = scanner.nextLine();

        while (userDatabase.containsKey(userSet)) {
            System.out.println("Username already exists! Choose another.");
            System.out.print("Set a Username: ");
            userSet = scanner.nextLine();
        }

        System.out.print("Set a Password: ");
        String passSet = scanner.nextLine();

        System.out.print("Confirm your password: ");
        String conf = scanner.nextLine();

        while (!conf.equals(passSet)) {
            System.out.println("The password doesn't match! Type again:");
            conf = scanner.nextLine();
        }

        userDatabase.put(userSet, passSet);
        saveUserDatabase();
        System.out.println("Account created successfully!");
    }

    private static void loadUserDatabase() {
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    userDatabase.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Database file not found. Starting fresh.");
        }
    }

    private static void saveUserDatabase() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATABASE_FILE))) {
            for (Map.Entry<String, String> entry : userDatabase.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }
        } catch (IOException e) {
            System.out.println("Error saving the user database.");
        }
    }

    public static class LoginPage extends JFrame {
        private JTextField usernameField;
        private JPasswordField passwordField;
        private JComboBox<String> roleComboBox;
        private JButton loginButton;

        public LoginPage() {
            setTitle("Login Page");
            setSize(300, 200);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(4, 2));

            add(new JLabel("Username:"));
            usernameField = new JTextField();
            add(usernameField);

            add(new JLabel("Password:"));
            passwordField = new JPasswordField();
            add(passwordField);

            add(new JLabel("Role:"));
            roleComboBox = new JComboBox<>(new String[]{"Teacher", "Beadle"});
            add(roleComboBox);

            loginButton = new JButton("Login");
            add(loginButton);

            loginButton.addActionListener(e -> {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String role = roleComboBox.getSelectedItem().toString();

                if (userDatabase.containsKey(username) && userDatabase.get(username).equals(password)) {
                    JOptionPane.showMessageDialog(null, "Login successful as " + role);
                    new HomePage(LoginPage.this, role); // Proceed to HomePage based on role
                    LoginPage.this.setVisible(false);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid credentials.");
                }
            });
        }
    }
}
