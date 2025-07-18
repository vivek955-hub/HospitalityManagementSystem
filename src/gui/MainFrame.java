package gui;

import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final JTabbedPane tabbedPane = new JTabbedPane();

    public MainFrame() {
        setTitle("Hospitality Management System");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initTabs();
        initMenu();

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void initTabs() {
        tabbedPane.addTab("Hotels", new HotelPanel());
        tabbedPane.addTab("Rooms", new RoomPanel());
        tabbedPane.addTab("Guests", new GuestPanel());
        tabbedPane.addTab("Reservations", new ReservationPanel());
    }

    private void initMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "Hospitality Management System\nVersion 1.0",
                "About",
                JOptionPane.INFORMATION_MESSAGE
        ));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}
