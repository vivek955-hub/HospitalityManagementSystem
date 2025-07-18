package gui;

import dao.GuestDAO;
import entities.Guest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GuestPanel extends JPanel {
    private final GuestDAO guestDAO = new GuestDAO();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0);
    private final JTable table = new JTable(model);

    public GuestPanel() {
        setLayout(new BorderLayout());
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add"), editBtn = new JButton("Edit"),
                deleteBtn = new JButton("Delete"), refreshBtn = new JButton("Refresh");

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        addBtn.addActionListener(e -> openDialog(null));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int id = (int) model.getValueAt(row, 0);
                openDialog(guestDAO.getGuestById(id));
            }
        });
        deleteBtn.addActionListener(e -> deleteSelectedGuest());
        refreshBtn.addActionListener(e -> refreshTable());

        refreshTable(); // Load data on init
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Guest g : guestDAO.getAllGuests()) {
            model.addRow(new Object[]{g.getGuestId(), g.getName(), g.getEmail(), g.getPhone()});
        }
    }

    private void deleteSelectedGuest() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete guest?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                if (guestDAO.deleteGuest(id)) {
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Delete failed", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void openDialog(Guest guest) {
        JTextField nameField = new JTextField(guest != null ? guest.getName() : "", 20);
        JTextField emailField = new JTextField(guest != null ? guest.getEmail() : "", 20);
        JTextField phoneField = new JTextField(guest != null ? guest.getPhone() : "", 20);

        JPanel form = new JPanel(new GridLayout(0, 1));
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Email:")); form.add(emailField);
        form.add(new JLabel("Phone:")); form.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, form,
                guest == null ? "Add Guest" : "Edit Guest",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and phone are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (guest == null) guest = new Guest();
            guest.setName(name);
            guest.setEmail(emailField.getText().trim());
            guest.setPhone(phone);

            boolean saved = (guest.getGuestId() == 0) ? guestDAO.addGuest(guest) : guestDAO.updateGuest(guest);
            if (saved) refreshTable();
            else JOptionPane.showMessageDialog(this, "Failed to save guest.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
