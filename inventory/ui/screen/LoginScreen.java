package inventory.ui.screen;

import javax.swing.*;

import inventory.ui.components.form.Form;

import java.awt.*;

public class LoginScreen<T> extends JPanel {
    private Boolean isLogin = true;
    public Boolean getIsLogin() { return isLogin; }
    public void setIsLogin(Boolean value) { this.isLogin = value; }

    protected Form<T> form;
    protected JPanel actionPanel;

    public LoginScreen() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);

        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        setPreferredSize(new Dimension(300, 200));

        buildUiOptionSwitchOption();
    }

    public void buildFormLogin() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(16, 30, 20, 30));
        content.setOpaque(false);

        content.add(form);
        content.add(Box.createVerticalStrut(20));

        actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionPanel.setOpaque(false);
        content.add(actionPanel);

        Button btn = new Button("Se connecter");
        btn.addActionListener(e -> {
            
        });
    }

    public void buildUiOptionSwitchOption() {
        
        String pathButton = "";
        String titleUi = "";
        if (this.getIsLogin()) {
            pathButton = "Créer un compte";
            titleUi = "Connecter mon compte";
        }
        else {
            pathButton = "Se connecter avec mon compte";
            titleUi = "Créer mon compte";
        }

        JLabel title = new JLabel(titleUi);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(30, 40, 65));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description
        JLabel description = new JLabel(
            "<html>Inscrivez-vous pour accéder à toutes les<br>" +
            "fonctionnalités de gestion de stock.</html>"
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        description.setForeground(Color.GRAY);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnCreate = new JButton(pathButton);
        btnCreate.setBackground(new Color(52, 120, 246));
        btnCreate.setForeground(Color.BLUE);
        btnCreate.setFocusPainted(false);
        btnCreate.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCreate.addActionListener(e -> {
            this.setIsLogin(false);
        });
        
        add(title);
        add(Box.createVerticalStrut(12));
        add(description);
        add(Box.createVerticalStrut(20));
        add(btnCreate);
    }
}
