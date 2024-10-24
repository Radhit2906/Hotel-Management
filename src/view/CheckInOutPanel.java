package view;

import controller.CheckInOutController;
import model.Booking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

public class CheckInOutPanel extends JPanel {
    private CheckInOutController controller = new CheckInOutController();
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;

    public CheckInOutPanel() {
        setLayout(new BorderLayout());

        bookingTableModel = new DefaultTableModel(new String[]{"ID", "Room", "Guest", "Check-In Date", "Check-Out Date", "Total Price", "Actual Check-In Date", "Actual Check-Out Date"}, 0);
        bookingTable = new JTable(bookingTableModel);
        add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout());
        JButton checkInButton = new JButton("Check-In");
        checkInButton.addActionListener(e -> checkIn());
        JButton checkOutButton = new JButton("Check-Out");
        checkOutButton.addActionListener(e -> checkOut());
        JButton printInvoiceButton = new JButton("Print Invoice");
        printInvoiceButton.addActionListener(e -> printInvoice());

        panel.add(checkInButton);
        panel.add(checkOutButton);
        panel.add(printInvoiceButton);

        add(panel, BorderLayout.SOUTH);

        loadBookingData();
    }

    private void loadBookingData() {
        try {
            List<Booking> bookings = controller.getAllBookings();
            bookingTableModel.setRowCount(0);
            DecimalFormat df = new DecimalFormat("#,###.00"); // Format desimal
            for (Booking booking : bookings) {
                bookingTableModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getRoom().getRoomNumber(),
                    booking.getGuest().getName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    df.format(booking.getTotalPrice()), // Format total price
                    booking.getActualCheckInDate() != null ? booking.getActualCheckInDate().toString() : "",
                    booking.getActualCheckOutDate() != null ? booking.getActualCheckOutDate().toString() : ""
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading booking data: " + e.getMessage());
        }
    }

    private void checkIn() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to check in.");
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        LocalDate actualCheckInDate = LocalDate.now(); // Menggunakan tanggal saat ini sebagai tanggal check-in aktual

        try {
            controller.checkIn(bookingId, actualCheckInDate);
            loadBookingData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking in: " + e.getMessage());
        }
    }

    private void checkOut() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to check out.");
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        LocalDate actualCheckOutDate = LocalDate.now(); // Menggunakan tanggal saat ini sebagai tanggal check-out aktual

        try {
            Booking booking = controller.getBookingById(bookingId);
            double additionalCharges = booking.getAdditionalCharges();
            double totalPrice = booking.calculateActualTotalPrice() + additionalCharges;
            controller.checkOut(bookingId, actualCheckOutDate, totalPrice, additionalCharges);
            loadBookingData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking out: " + e.getMessage());
        }
    }

    private void printInvoice() {
        int selectedRow = bookingTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to print the invoice.");
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        try {
            Booking booking = controller.getBookingById(bookingId);
            String invoice = generateInvoice(booking);
            JTextArea textArea = new JTextArea(invoice);
            textArea.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea), "Invoice", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error printing invoice: " + e.getMessage());
        }
    }

    private String generateInvoice(Booking booking) {
        DecimalFormat df = new DecimalFormat("#,###.00"); // Format desimal
        StringBuilder sb = new StringBuilder();
        sb.append("Invoice\n");
        sb.append("=========================================\n");
        sb.append("Booking ID: ").append(booking.getId()).append("\n");
        sb.append("Guest Name: ").append(booking.getGuest().getName()).append("\n");
        sb.append("Guest KTP: ").append(booking.getGuest().getKtpNumber()).append("\n");
        sb.append("Room Number: ").append(booking.getRoom().getRoomNumber()).append("\n");
        sb.append("Room Type: ").append(booking.getRoom().getRoomType()).append("\n");
        sb.append("Check-In Date: ").append(booking.getCheckInDate()).append("\n");
        sb.append("Check-Out Date: ").append(booking.getCheckOutDate()).append("\n");
        sb.append("Actual Check-In Date: ").append(booking.getActualCheckInDate()).append("\n");
        sb.append("Actual Check-Out Date: ").append(booking.getActualCheckOutDate()).append("\n");
        sb.append("Total Price: ").append(df.format(booking.getTotalPrice())).append("\n");
        sb.append("Additional Charges: ").append(df.format(booking.getAdditionalCharges())).append("\n");

        sb.append("Detailed Additional Charges:\n");
        for (String charge : booking.getAdditionalChargesList()) {
            sb.append(charge).append("\n");
        }

        sb.append("=========================================\n");
        return sb.toString();
    }
}
