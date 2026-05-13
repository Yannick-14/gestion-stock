package inventory.ui;

import javax.swing.*;
import java.awt.*;

public class Window {

    private static final String DEFAULT_TITLE = "Inventory Management System";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int DEFAULT_MIN_WIDTH = 800;
    private static final int DEFAULT_MIN_HEIGHT = 600;
    
    private final JFrame frame;
    
    public Window() {
        this(DEFAULT_TITLE, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public Window(String title) {
        this(title, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public Window(String title, int width, int height) {
        frame = new JFrame(title);
        configureWindow(width, height);
    }

    private void configureWindow(int width, int height) {
        // Configuration de base
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setMinimumSize(new Dimension(DEFAULT_MIN_WIDTH, DEFAULT_MIN_HEIGHT));
        
        // Centrer la fenêtre sur l'écran
        frame.setLocationRelativeTo(null);
        
        // Layout par défaut
        frame.setLayout(new BorderLayout());
        
        // Look and feel système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public JFrame getFrame() {
        return frame;
    }
    
    public void setContentPane(JPanel panel) {
        frame.setContentPane(panel);
        frame.revalidate();
    }
    
    public void setTitle(String title) {
        frame.setTitle(title);
    }
    
    public void setSize(int width, int height) {
        frame.setSize(width, height);
    }
    
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }
    
    public void showWindow() {
        setVisible(true);
    }
    
    public void hideWindow() {
        setVisible(false);
    }
    
    public void maximize() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
    
    public void centerOnScreen() {
        frame.setLocationRelativeTo(null);
    }
}
