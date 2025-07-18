package gui;

import dao.*;
import entities.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ReservationPanel extends JPanel {

    /* --- DAOs --- */
    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final GuestDAO guestDAO   = new GuestDAO();
    private final RoomDAO  roomDAO    = new RoomDAO();

    /* --- UI --- */
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID","Guest","Room","Check‑In","Check‑Out","₹ Price","Status"},0) {
        public boolean isCellEditable(int r,int c){ return false; }
        public Class<?> getColumnClass(int c){ return c==0?Integer.class : c==5?Double.class : String.class; }
    };
    private final JTable table = new JTable(model);
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

    public ReservationPanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(makeButtons(), BorderLayout.SOUTH);
        loadTable();
    }

    /* ---------------- buttons ---------------- */
    private JPanel makeButtons() {
        JButton add = new JButton("Add");
        JButton cancel = new JButton("Cancel");
        JButton refresh = new JButton("Refresh");

        add.addActionListener(e -> openAddDialog());
        cancel.addActionListener(e -> cancelSelected());
        refresh.addActionListener(e -> loadTable());

        JPanel p = new JPanel();
        p.add(add); p.add(cancel); p.add(refresh);
        return p;
    }

    /* ---------------- data load -------------- */
    private void loadTable() {
        model.setRowCount(0);
        for (Reservation r : reservationDAO.getAllReservations()) {
            Guest g = guestDAO.getGuestById(r.getGuestId());
            Room  rm= roomDAO.getRoomById(r.getRoomId());
            model.addRow(new Object[]{
                    r.getReservationId(),
                    g==null? "?" : g.getName(),
                    rm==null? "?" : rm.getRoomNumber(),
                    fmt.format(r.getCheckInDate()),
                    fmt.format(r.getCheckOutDate()),
                    r.getTotalPrice(),
                    r.getStatus()
            });
        }
    }

    /* ---------------- add dialog ------------- */
    private void openAddDialog() {
        JComboBox<Guest> guestBox = new JComboBox<>(guestDAO.getAllGuests().toArray(new Guest[0]));
        JComboBox<Room>  roomBox  = new JComboBox<>( roomDAO.getAllRooms().toArray(new Room[0]));
        JTextField inField  = new JTextField(10);  // yyyy‑MM‑dd
        JTextField outField = new JTextField(10);
        JTextField priceF   = new JTextField(10);

        JPanel form = new JPanel(new GridLayout(0,2));
        form.add(new JLabel("Guest:"));   form.add(guestBox);
        form.add(new JLabel("Room:"));    form.add(roomBox);
        form.add(new JLabel("Check‑in:"));form.add(inField);
        form.add(new JLabel("Check‑out:"));form.add(outField);
        form.add(new JLabel("Price ₹:")); form.add(priceF);

        if (JOptionPane.showConfirmDialog(this, form, "Add Reservation",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                Guest g = (Guest) guestBox.getSelectedItem();
                Room  rm= (Room)  roomBox.getSelectedItem();
                Date  ci= fmt.parse(inField.getText().trim());
                Date  co= fmt.parse(outField.getText().trim());
                double price = Double.parseDouble(priceF.getText().trim());

                Reservation r = new Reservation();
                r.setGuestId(g.getGuestId());
                r.setRoomId(rm.getRoomId());
                r.setCheckInDate(ci);
                r.setCheckOutDate(co);
                r.setTotalPrice(price);
                r.setStatus(Reservation.CONFIRMED);

                if (reservationDAO.addReservation(r)) loadTable();
                else JOptionPane.showMessageDialog(this,"Save failed","Error",JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /* -------------- cancel logic ------------- */
    private void cancelSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a reservation first.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this,"Cancel this reservation?") == JOptionPane.YES_OPTION) {
            if (reservationDAO.cancelReservation(id)) loadTable();
            else JOptionPane.showMessageDialog(this, "Cancel failed", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
