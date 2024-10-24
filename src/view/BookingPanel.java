package view;

import com.toedter.calendar.JDateChooser;
import model.Booking;
import dao.BookingDAO;
import dao.GuestDAO;
import model.Room;
import model.Guest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class BookingPanel extends JPanel {
    private BookingDAO bookingDAO = new BookingDAO();
    private GuestDAO guestDAO = new GuestDAO();
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;

    public BookingPanel() {
        setLayout(new BorderLayout());

        bookingTableModel = new DefaultTableModel(new String[]{"ID", "Room", "Guest", "Check-In Date", "Check-Out Date", "Total Price"}, 0);
        bookingTable = new JTable(bookingTableModel);
        add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        JPanel panel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Booking");
        addButton.addActionListener(e -> addBooking());
        JButton editButton = new JButton("Edit Booking");
        editButton.addActionListener(e -> editBooking());
        JButton deleteButton = new JButton("Delete Booking");
        deleteButton.addActionListener(e -> deleteBooking());
        JButton printButton = new JButton("Print Confirmation");
        printButton.addActionListener(e -> printConfirmation());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(printButton);

        add(panel, BorderLayout.SOUTH);

        loadBookingData();
    }

    private void loadBookingData() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            bookingTableModel.setRowCount(0);
            DecimalFormat df = new DecimalFormat("#,###.00"); // Format desimal
            for (Booking booking : bookings) {
                bookingTableModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getRoom().getRoomNumber(),
                    booking.getGuest().getName(),
                    booking.getCheckInDate(),
                    booking.getCheckOutDate(),
                    df.format(booking.calculateTotalPrice()) // Format total price
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading booking data: " + e.getMessage());
        }
    }

    private void addBooking() {
        try {
            Room selectedRoom = selectRoom();
            Guest selectedGuest = selectGuest();
            if (selectedRoom == null || selectedGuest == null) {
                JOptionPane.showMessageDialog(this, "Please select a room and guest.");
                return;
            }

            JDateChooser checkInChooser = new JDateChooser();
            JDateChooser checkOutChooser = new JDateChooser();
            JPanel datePanel = new JPanel(new GridLayout(2, 2));
            datePanel.add(new JLabel("Check-In Date:"));
            datePanel.add(checkInChooser);
            datePanel.add(new JLabel("Check-Out Date:"));
            datePanel.add(checkOutChooser);

            int result = JOptionPane.showConfirmDialog(this, datePanel, "Select Dates", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            LocalDate checkInDate = checkInChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate checkOutDate = checkOutChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Map<String, Integer> additionalCharges = selectAdditionalCharges();
            double additionalChargesTotal = calculateAdditionalCharges(additionalCharges);

            Booking booking = new Booking();
            booking.setRoom(selectedRoom);
            booking.setGuest(selectedGuest);
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);
            booking.setAdditionalCharges(additionalChargesTotal);
            booking.setAdditionalChargesList(new ArrayList<>(additionalCharges.keySet()));

            double totalPrice = booking.calculateTotalPrice();
            booking.setTotalPrice(totalPrice);

            bookingDAO.addBooking(booking);
            printBookingConfirmation(booking);
            loadBookingData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding booking: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for additional charges.");
        }
    }

    private Map<String, Integer> selectAdditionalCharges() {
        Map<String, Integer> charges = new HashMap<>();
        JSpinner extraBedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner breakfastSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        JSpinner parkingSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        JPanel chargesPanel = new JPanel(new GridLayout(4, 2));
        chargesPanel.add(new JLabel("Extra Bed (Rp 50.000 per bed):"));
        chargesPanel.add(extraBedSpinner);
        chargesPanel.add(new JLabel("Breakfast (Rp 30.000 per person):"));
        chargesPanel.add(breakfastSpinner);
        chargesPanel.add(new JLabel("Parking (Rp 10.000 per day):"));
        chargesPanel.add(parkingSpinner);

        int result = JOptionPane.showConfirmDialog(this, chargesPanel, "Select Additional Charges", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            charges.put("Extra Bed: " + extraBedSpinner.getValue() + " beds", (Integer) extraBedSpinner.getValue() * 50000);
            charges.put("Breakfast: " + breakfastSpinner.getValue() + " persons", (Integer) breakfastSpinner.getValue() * 30000);
            charges.put("Parking: " + parkingSpinner.getValue() + " days", (Integer) parkingSpinner.getValue() * 10000);
        }
        return charges;
    }

    private double calculateAdditionalCharges(Map<String, Integer> charges) {
        return charges.values().stream().mapToDouble(Integer::doubleValue).sum();
    }

    private void editBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to edit.");
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                JOptionPane.showMessageDialog(this, "Booking not found.");
                return;
            }

            Room selectedRoom = selectRoom();
            Guest selectedGuest = selectGuest();
            if (selectedRoom == null || selectedGuest == null) {
                JOptionPane.showMessageDialog(this, "Please select a room and guest.");
                return;
            }

            JDateChooser checkInChooser = new JDateChooser(Date.from(booking.getCheckInDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JDateChooser checkOutChooser = new JDateChooser(Date.from(booking.getCheckOutDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JPanel datePanel = new JPanel(new GridLayout(2, 2));
            datePanel.add(new JLabel("Check-In Date:"));
            datePanel.add(checkInChooser);
            datePanel.add(new JLabel("Check-Out Date:"));
            datePanel.add(checkOutChooser);

            int result = JOptionPane.showConfirmDialog(this, datePanel, "Select Dates", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            LocalDate checkInDate = checkInChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate checkOutDate = checkOutChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            Map<String, Integer> additionalCharges = selectAdditionalCharges();
            double additionalChargesTotal = calculateAdditionalCharges(additionalCharges);

            booking.setRoom(selectedRoom);
            booking.setGuest(selectedGuest);
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);
            booking.setAdditionalCharges(additionalChargesTotal);
            booking.setAdditionalChargesList(new ArrayList<>(additionalCharges.keySet()));

            double totalPrice = booking.calculateTotalPrice();
            booking.setTotalPrice(totalPrice);

            bookingDAO.updateBooking(booking);
            loadBookingData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error editing booking: " + e.getMessage());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input for additional charges.");
        }
    }

    private void deleteBooking() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to delete.");
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        try {
            bookingDAO.deleteBooking(bookingId);
            loadBookingData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting booking: " + e.getMessage());
        }
    }

    private Room selectRoom() {
        List<Room> rooms;
        try {
            rooms = bookingDAO.getAllRooms();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching rooms: " + e.getMessage());
            return null;
        }

        Object selectedRoom = JOptionPane.showInputDialog(
                this,
                "Select Room",
                "Room Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                rooms.toArray(),
                rooms.get(0)
        );

        return (Room) selectedRoom;
    }

    private Guest selectGuest() {
        List<Guest> guests;
        try {
            guests = guestDAO.getAllGuests();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching guests: " + e.getMessage());
            return null;
        }

        Guest selectedGuest = (Guest) JOptionPane.showInputDialog(
                this,
                "Select Guest",
                "Guest Selection",
                JOptionPane.PLAIN_MESSAGE,
                null,
                guests.toArray(),
                guests.get(0)
        );

        return selectedGuest;
    }

    private void printBookingConfirmation(Booking booking) {
        JOptionPane.showMessageDialog(this, 
            "Booking Confirmation\n" +
            "Guest Name: " + booking.getGuest().getName() + "\n" +
            "Room Number: " + booking.getRoom().getRoomNumber() + "\n" +
            "Check-In Date: " + booking.getCheckInDate() + "\n" +
            "Check-Out Date: " + booking.getCheckOutDate() + "\n" +
            "Total Price: " + booking.calculateTotalPrice(),
            "Booking Confirmation",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void printConfirmation() {
        int selectedRow = bookingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to print.");
            return;
        }

        int bookingId = (int) bookingTableModel.getValueAt(selectedRow, 0);
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            printBookingConfirmation(booking);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching booking: " + e.getMessage());
        }
    }
}
