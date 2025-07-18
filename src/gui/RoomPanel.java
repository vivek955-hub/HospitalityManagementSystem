package gui;

import dao.HotelDAO;
import dao.RoomDAO;
import entities.Hotel;
import entities.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RoomPanel extends JPanel {
    private final RoomDAO roomDAO = new RoomDAO();
    private final HotelDAO hotelDAO = new HotelDAO();
    private final DefaultTableModel model = new DefaultTableModel(
        new String[]{"ID", "Hotel", "Room #", "Type", "Price", "Status"}, 0);
    private final JTable table = new JTable(model);

    public RoomPanel() {
        setLayout(new BorderLayout());

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        JButton add = new JButton("Add");
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        JButton ref = new JButton("Refresh");
        btnPanel.add(add); btnPanel.add(edit); btnPanel.add(del); btnPanel.add(ref);
        add(btnPanel, BorderLayout.SOUTH);

        add.addActionListener(e -> openDialog(null));
        edit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            openDialog(roomDAO.getRoomById(id));
        });
        del.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) model.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "Delete room?") == JOptionPane.YES_OPTION) {
                if (roomDAO.deleteRoom(id)) refresh();
            }
        });
        ref.addActionListener(e -> refresh());

        refresh();
    }

    private void refresh() {
        model.setRowCount(0);
        for (Room r : roomDAO.getAllRooms()) {
            Hotel h = hotelDAO.getHotelById(r.getHotelId());
            model.addRow(new Object[]{
                r.getRoomId(),
                h != null ? h.getName() : "Unknown",
                r.getRoomNumber(),
                r.getType(),
                r.getPrice(),
                r.getStatus()
            });
        }
    }

    private void openDialog(Room room) {
        JTextField numF = new JTextField(10);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"STANDARD", "DELUXE", "SUITE"});
        JTextField priceF = new JTextField(10);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"AVAILABLE", "OCCUPIED", "MAINTENANCE"});
        JComboBox<Hotel> hotelBox = new JComboBox<>(hotelDAO.getAllHotels().toArray(new Hotel[0]));

        if (room != null) {
            numF.setText(room.getRoomNumber());
            typeBox.setSelectedItem(room.getType());
            priceF.setText(String.valueOf(room.getPrice()));
            statusBox.setSelectedItem(room.getStatus());
            for (int i = 0; i < hotelBox.getItemCount(); i++) {
                if (hotelBox.getItemAt(i).getHotelId() == room.getHotelId()) {
                    hotelBox.setSelectedIndex(i); break;
                }
            }
        }

        JPanel p = new JPanel(new GridLayout(0, 2));
        p.add(new JLabel("Hotel:")); p.add(hotelBox);
        p.add(new JLabel("Room #:")); p.add(numF);
        p.add(new JLabel("Type:")); p.add(typeBox);
        p.add(new JLabel("Price:")); p.add(priceF);
        p.add(new JLabel("Status:")); p.add(statusBox);

        int result = JOptionPane.showConfirmDialog(this, p,
                room == null ? "Add Room" : "Edit Room", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Room r = room == null ? new Room() : room;
                r.setHotelId(((Hotel) hotelBox.getSelectedItem()).getHotelId());
                r.setRoomNumber(numF.getText());
                r.setType((String) typeBox.getSelectedItem());
                r.setPrice(Double.parseDouble(priceF.getText()));
                r.setStatus((String) statusBox.getSelectedItem());

                boolean ok = room == null ? roomDAO.addRoom(r) : roomDAO.updateRoom(r);
                if (ok) refresh(); else JOptionPane.showMessageDialog(this, "Save failed");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        }
    }
}
