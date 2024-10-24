package controller;

import dao.BookingDAO;
import dao.GuestDAO;
import dao.RoomDAO;
import model.Booking;
import model.Guest;
import model.Room;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CheckInOutController {
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    private GuestDAO guestDAO;

    public CheckInOutController() {
        bookingDAO = new BookingDAO();
        roomDAO = new RoomDAO();
        guestDAO = new GuestDAO();
    }

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public void checkIn(int bookingId, LocalDate actualCheckInDate) throws SQLException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking != null) {
            Room room = booking.getRoom();
            booking.setActualCheckInDate(actualCheckInDate);
            bookingDAO.updateBooking(booking);
            roomDAO.updateRoomStatus(room.getId(), "occupied"); // Perbarui status kamar menjadi 'occupied'
            System.out.println("Check-in successful for booking ID: " + bookingId);
        } else {
            throw new SQLException("Booking ID not found: " + bookingId);
        }
    }

    public void checkOut(int bookingId, LocalDate actualCheckOutDate, double totalPrice, double additionalCharges) throws SQLException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking != null) {
            Room room = booking.getRoom();
            booking.setActualCheckOutDate(actualCheckOutDate);
            booking.setTotalPrice(totalPrice);
            booking.setAdditionalCharges(additionalCharges);
            bookingDAO.updateBooking(booking);
            roomDAO.updateRoomStatus(room.getId(), "available"); // Perbarui status kamar menjadi 'available'
            System.out.println("Check-out successful for booking ID: " + bookingId);
        } else {
            throw new SQLException("Booking ID not found: " + bookingId);
        }
    }

    public double calculateFinalBill(int bookingId) throws SQLException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking != null) {
            return booking.calculateTotalPrice(); // Menghitung total biaya
        } else {
            throw new SQLException("Booking ID not found: " + bookingId);
        }
    }

    public void addBooking(Booking booking) throws SQLException {
        bookingDAO.addBooking(booking);
    }

    public Room getRoomByNumber(String roomNumber) throws SQLException {
        return roomDAO.getRoomByNumber(roomNumber);
    }

    public Guest getGuestByKtp(String ktpNumber) throws SQLException {
        return guestDAO.getGuestByKtp(ktpNumber);
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        return bookingDAO.getBookingById(bookingId);
    }
}
