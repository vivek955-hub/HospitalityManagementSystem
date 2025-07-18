package gui;

import dao.HotelDAO;
import entities.Hotel;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class HotelPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger(HotelPanel.class.getName());

    private final HotelDAO hotelDAO = new HotelDAO();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Location", "Amenities", "Created At"}, 0
    ) {
        public boolean isCellEditable(int r, int c) { return false; }
        public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
    };
    private final JTable table = new JTable(model);

    public HotelPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(260);
        table.setDefaultRenderer(Object.class, tooltipRenderer());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel(), BorderLayout.SOUTH);

        refresh();
    }

    /* ---------- Data refresh ---------- */
    private void refresh() {
        try {
            model.setRowCount(0);
            for (Hotel h : hotelDAO.getAllHotels()) {
                model.addRow(new Object[]{
                        h.getHotelId(),
                        h.getName(),
                        h.getLocation(),
                        formatAmenities(h.getAmenities()),
                        h.getCreatedAt() == null ? "" : h.getCreatedAt().toString()
                });
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading hotels", e);
            error("Error loading hotels:\n" + e.getMessage());
        }
    }

    /* ---------- Buttons ---------- */
    private JPanel buttonPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        JButton ref = new JButton("Refresh");

        add.addActionListener(e -> openDialog(null));

        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a hotel to edit"); return; }
            int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
            Hotel h = hotelDAO.getHotelById(id);
            if (h == null) { error("Hotel not found"); return; }
            openDialog(h);
        });

        del.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { warn("Select a hotel to delete"); return; }
            int id = (int) model.getValueAt(table.convertRowIndexToModel(row), 0);
            int ok = JOptionPane.showConfirmDialog(this,
                    "Delete this hotel and all its rooms?", "Confirm",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION && hotelDAO.deleteHotel(id)) refresh();
        });

        ref.addActionListener(e -> refresh());

        p.add(add); p.add(edit); p.add(del); p.add(ref);
        return p;
    }

    /* ---------- Dialog ---------- */
    private void openDialog(Hotel original) {
        JDialog d = new JDialog(SwingUtilities.getWindowAncestor(this),
                original == null ? "Add Hotel" : "Edit Hotel", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4,4,4,4);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField nameF = new JTextField(25);
        JTextField locF  = new JTextField(25);
        JTextArea  amenA = new JTextArea(4,25);
        amenA.setLineWrap(true);
        amenA.setWrapStyleWord(true);

        if (original != null) {
            nameF.setText(original.getName());
            locF.setText(original.getLocation());
            amenA.setText(original.getAmenities());
        }

        addField(form, gbc, 0, "Name*", nameF);
        addField(form, gbc, 1, "Location*", locF);
        addField(form, gbc, 2, "Amenities", new JScrollPane(amenA));

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(cancel); btns.add(save);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        form.add(btns, gbc);

        save.addActionListener(e -> {
            String n = nameF.getText().trim(), l = locF.getText().trim();
            if (n.isEmpty() || l.isEmpty()) { warn("Name and Location are required", d); return; }

            Hotel h = original == null ? new Hotel() : original;
            h.setName(n); h.setLocation(l); h.setAmenities(amenA.getText().trim());

            boolean ok = original == null ? hotelDAO.addHotel(h) : hotelDAO.updateHotel(h);
            if (ok) { refresh(); d.dispose(); }
            else error("Failed to save hotel", d);
        });

        cancel.addActionListener(e -> d.dispose());

        d.setContentPane(form);
        d.pack();
        d.setLocationRelativeTo(this);
        nameF.requestFocusInWindow();
        d.setVisible(true);
    }

    /* ---------- Small helpers ---------- */
    private static void addField(JPanel p, GridBagConstraints gbc, int y, String lbl, JComponent c) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0; p.add(new JLabel(lbl), gbc);
        gbc.gridx = 1; gbc.weightx = 1; p.add(c, gbc);
    }

    private static DefaultTableCellRenderer tooltipRenderer() {
        return new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                setToolTipText(v == null ? null : v.toString());
                return comp;
            }
        };
    }

    private static String formatAmenities(String s) {
        return (s == null || s.isBlank()) ? "None"
               : s.length() > 60 ? s.substring(0,57) + "..." : s;
    }

    private static void warn (String m)                   { warn(m,null); }
    private static void warn (String m, Component p)      { JOptionPane.showMessageDialog(p,m,"Warning",JOptionPane.WARNING_MESSAGE); }
    private static void error(String m)                   { error(m,null); }
    private static void error(String m, Component p)      { JOptionPane.showMessageDialog(p,m,"Error",JOptionPane.ERROR_MESSAGE); }
}
