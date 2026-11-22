package inventory.ui.screen;

import inventory.feature.repository.repositories.UsersRepository;
import inventory.models.Users;
import inventory.ui.components.fields.FieldInput;
import inventory.ui.components.form.Form;
import inventory.ui.components.button.Button;

import inventory.ui.auth.AuthResult;
import inventory.feature.applicationService.Session;
import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class LoginScreen extends JPanel {
    private boolean isLogin = true;
    private Form<Users> form;
    private FieldInput confirmPasswordField;
    Session session = new Session();

    private final UsersRepository repository = new UsersRepository();
    private Consumer<AuthResult> onAuthResult = result -> {};

    public LoginScreen() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        buildUi();
    }

    public boolean getIsLogin() { return isLogin; }
    public void setIsLogin(boolean value) {
        this.isLogin = value;
        buildUi();
    }

    public void setOnAuthResult(Consumer<AuthResult> onAuthResult) {
        this.onAuthResult = onAuthResult;
    }

    // ── Helpers ─────────────────────────────────────────────────────

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void sendResult(AuthResult result) {
        onAuthResult.accept(result);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(Math.max(d.width, 360), d.height);
    }

    // ── Logique ─────────────────────────────────────────────────────

    private String validateForm(Users formUser) {
        if (isBlank(formUser.getEmail())) return "L'email est obligatoire.";
        try {
            formUser.setEmail(formUser.getEmail());
        } catch(Exception e) {
            return e.getMessage();
        }
        if (isBlank(formUser.getPassword())) return "Le mot de passe est obligatoire.";
        if (formUser.getPassword().length() < 6) return "Le mot de passe doit contenir au moins 6 caractères.";

        if (!isLogin) {
            if (isBlank(formUser.getName())) return "Le nom est obligatoire.";
            if (!formUser.getPassword().equals(confirmPasswordField.getText())) {
                return "Les mots de passe ne correspondent pas.";
            }
        }
        return null;
    }

    private void handleLogin(Users formUser) throws Exception {
        Users user = repository.login(
            new Users(formUser.getEmail(), formUser.getPassword())
        );
        session.login(user);
        sendResult(AuthResult.success("Bienvenue " + user.getName() + " !", user, true));
    }

    private void handleSignup(Users formUser) throws Exception {
        Users user = repository.signup(
            new Users(formUser.getName(), formUser.getEmail(), formUser.getPassword())
        );
        session.login(user);
        sendResult(AuthResult.success("Compte créé ! Vous pouvez vous connecter.", user, true));
        // setIsLogin(true);
    }

    private void handleSubmit() {
        Users formUser;
        try {
            formUser = form.getData(new Users());
        } catch (IllegalAccessException ex) {
            sendResult(AuthResult.error("Erreur lors de la lecture du formulaire."));
            return;
        }

        String error = validateForm(formUser);
        if (error != null) {
            sendResult(AuthResult.error(error));
            return;
        }

        try {
            if (isLogin) {
                handleLogin(formUser);
            } else {
                handleSignup(formUser);
            }
        } catch (Exception ex) {
            sendResult(AuthResult.error(ex.getMessage()));
        }
    }

    // ── Interface ───────────────────────────────────────────────────

    private void buildUi() {
        removeAll();

        JLabel title = new JLabel(isLogin ? "Connecter mon compte" : "Créer mon compte");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 40, 65));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(isLogin
            ? "<html>Connectez-vous pour accéder à la<br>gestion de stock.</html>"
            : "<html>Inscrivez-vous pour accéder à toutes les<br>fonctionnalités de gestion de stock.</html>");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(title);
        add(Box.createVerticalStrut(12));
        add(description);
        add(Box.createVerticalStrut(20));

        form = new Form<>(new Users());
        form.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (isLogin) {
            form.setFieldVisible("name", false);
        }
        add(form);

        if (!isLogin) {
            confirmPasswordField = new FieldInput("Confirm Password", true);
            confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(confirmPasswordField);
        } else {
            confirmPasswordField = null;
        }

        add(Box.createVerticalStrut(20));

        Button btnSubmit = new Button(isLogin ? "Se connecter" : "Créer mon compte");
        btnSubmit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubmit.addActionListener(e -> handleSubmit());
        add(btnSubmit);

        add(Box.createVerticalStrut(10));

        Button btnSwitch = new Button(isLogin ? "Créer un compte" : "Se connecter avec mon compte");
        btnSwitch.setBorderPainted(false);
        btnSwitch.setContentAreaFilled(false);
        btnSwitch.setForeground(new Color(52, 120, 246));
        btnSwitch.setFocusPainted(false);
        btnSwitch.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSwitch.addActionListener(e -> setIsLogin(!isLogin));
        add(btnSwitch);

        revalidate();
        repaint();
    }
}
