package view;

import model.Guest;
import dao.GuestDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GuestManagementPanel extends JPanel {
    private GuestDAO guestDAO = new GuestDAO();
    private JTable guestTable;
    private DefaultTableModel guestTableModel;

    public GuestManagementPanel() {
        setLayout(new BorderLayout());

        guestTableModel = new DefaultTableModel(new String[]{"ID", "Name", "KTP Number", "Phone"}, 0);
        guestTable = new JTable(guestTableModel);
        add(new JScrollPane(guestTable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Guest");
        addButton.addActionListener(e -> addGuest());
        JButton editButton = new JButton("Edit Guest");
        editButton.addActionListener(e -> editGuest());
        JButton deleteButton = new JButton("Delete Guest");
        deleteButton.addActionListener(e -> deleteGuest());
        JButton searchButton = new JButton("Search Guest");
        searchButton.addActionListener(e -> searchGuest());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(searchButton);

        add(panel, BorderLayout.SOUTH);

        loadGuestData();
    }

    private void loadGuestData() {
        try {
            List<Guest> guests = guestDAO.getAllGuests();
            guestTableModel.setRowCount(0);
            for (Guest guest : guests) {
                guestTableModel.addRow(new Object[]{guest.getId(), guest.getName(), guest.getKtpNumber(), guest.getPhone()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading guest data: " + e.getMessage());
        }
    }

    private void addGuest() {
        Guest guest = new Guest();
        guest.setName(JOptionPane.showInputDialog("Enter Name:"));
        guest.setKtpNumber(JOptionPane.showInputDialog("Enter KTP Number:"));
        guest.setPhone(JOptionPane.showInputDialog("Enter Phone:"));

        // Check if any input is null or empty
        if (guest.getName() == null || guest.getName().trim().isEmpty() ||
            guest.getKtpNumber() == null || guest.getKtpNumber().trim().isEmpty() ||
            guest.getPhone() == null || guest.getPhone().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            guestDAO.addGuest(guest);
            loadGuestData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding guest: " + e.getMessage());
        }
    }

    private void editGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guest to edit.");
            return;
        }

        int guestId = (int) guestTableModel.getValueAt(selectedRow, 0);
        Guest guest = new Guest();
        guest.setId(guestId);
        guest.setName((String) guestTableModel.getValueAt(selectedRow, 1));
        guest.setKtpNumber((String) guestTableModel.getValueAt(selectedRow, 2));
        guest.setPhone((String) guestTableModel.getValueAt(selectedRow, 3));

        guest.setName(JOptionPane.showInputDialog("Enter Name:", guest.getName()));
        guest.setKtpNumber(JOptionPane.showInputDialog("Enter KTP Number:", guest.getKtpNumber()));
        guest.setPhone(JOptionPane.showInputDialog("Enter Phone:", guest.getPhone()));

        // Check if any input is null or empty
        if (guest.getName() == null || guest.getName().trim().isEmpty() ||
            guest.getKtpNumber() == null || guest.getKtpNumber().trim().isEmpty() ||
            guest.getPhone() == null || guest.getPhone().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        try {
            guestDAO.updateGuest(guest);
            loadGuestData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating guest: " + e.getMessage());
        }
    }

    private void deleteGuest() {
        int selectedRow = guestTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guest to delete.");
            return;
        }

        int guestId = (int) guestTableModel.getValueAt(selectedRow, 0);
        try {
            guestDAO.deleteGuest(guestId);
            loadGuestData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting guest: " + e.getMessage());
        }
    }

    private void searchGuest() {
        String keyword = JOptionPane.showInputDialog("Enter guest name or KTP number:");
        if (keyword == null || keyword.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Search keyword is required.");
            return;
        }

        try {
            List<Guest> guests = guestDAO.searchGuests(keyword);
            guestTableModel.setRowCount(0);
            for (Guest guest : guests) {
                guestTableModel.addRow(new Object[]{guest.getId(), guest.getName(), guest.getKtpNumber(), guest.getPhone()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error searching guests: " + e.getMessage());
        }
    }
}
