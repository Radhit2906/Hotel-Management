package view;

import model.Room;
import dao.RoomDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class RoomManagementPanel extends JPanel {
    private RoomDAO roomDAO = new RoomDAO();
    private JTable roomTable;
    private DefaultTableModel roomTableModel;

    public RoomManagementPanel() {
        setLayout(new BorderLayout());

        roomTableModel = new DefaultTableModel(new String[]{"ID", "Room Number", "Room Type", "Price", "Status"}, 0);
        roomTable = new JTable(roomTableModel);
        add(new JScrollPane(roomTable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Room");
        addButton.addActionListener(e -> addRoom());
        JButton editButton = new JButton("Edit Room");
        editButton.addActionListener(e -> editRoom());
        JButton deleteButton = new JButton("Delete Room");
        deleteButton.addActionListener(e -> deleteRoom());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);

        add(panel, BorderLayout.SOUTH);

        loadRoomData();
    }

    private void loadRoomData() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            roomTableModel.setRowCount(0);
            for (Room room : rooms) {
                roomTableModel.addRow(new Object[]{
                    room.getId(), 
                    room.getRoomNumber(), 
                    room.getRoomType(), 
                    room.getPrice(), 
                    room.getStatus()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading room data: " + e.getMessage());
        }
    }

    private void addRoom() {
        String roomNumber = JOptionPane.showInputDialog("Enter Room Number:");
        String roomType = JOptionPane.showInputDialog("Enter Room Type:");
        String priceString = JOptionPane.showInputDialog("Enter Price:");

        // Check if any input is null or empty
        if (roomNumber == null || roomNumber.trim().isEmpty() ||
            roomType == null || roomType.trim().isEmpty() ||
            priceString == null || priceString.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            double price = Double.parseDouble(priceString.trim());

            Room room = new Room();
            room.setRoomNumber(roomNumber.trim());
            room.setRoomType(roomType.trim());
            room.setPrice(price);
            room.setStatus("available"); // Default status

            roomDAO.addRoom(room);
            loadRoomData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding room: " + e.getMessage());
        }
    }

    private void editRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to edit.");
            return;
        }

        int roomId = (int) roomTableModel.getValueAt(selectedRow, 0);
        Room room = new Room();
        room.setId(roomId);
        room.setRoomNumber((String) roomTableModel.getValueAt(selectedRow, 1));
        room.setRoomType((String) roomTableModel.getValueAt(selectedRow, 2));
        room.setPrice((Double) roomTableModel.getValueAt(selectedRow, 3));

        room.setRoomNumber(JOptionPane.showInputDialog("Enter Room Number:", room.getRoomNumber()));
        room.setRoomType(JOptionPane.showInputDialog("Enter Room Type:", room.getRoomType()));
        String priceString = JOptionPane.showInputDialog("Enter Price:", room.getPrice());

        // Check if any input is null or empty
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty() ||
            room.getRoomType() == null || room.getRoomType().trim().isEmpty() ||
            priceString == null || priceString.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            room.setPrice(Double.parseDouble(priceString.trim()));
            room.setStatus((String) roomTableModel.getValueAt(selectedRow, 4));
            roomDAO.updateRoom(room);
            loadRoomData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating room: " + e.getMessage());
        }
    }

    private void deleteRoom() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room to delete.");
            return;
        }

        int roomId = (int) roomTableModel.getValueAt(selectedRow, 0);
        try {
            roomDAO.deleteRoom(roomId);
            loadRoomData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting room: " + e.getMessage());
        }
    }
}
