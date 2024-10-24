package controller;

import dao.BookingDAO;
import model.Booking;

import java.sql.SQLException;
import java.util.List;

public class BookingController {
    private BookingDAO bookingDAO;

    public BookingController() {
        bookingDAO = new BookingDAO();
    }

    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }

    public void addBooking(Booking booking) throws SQLException {
        bookingDAO.addBooking(booking);
    }

    public void updateBooking(Booking booking) throws SQLException {
        bookingDAO.updateBooking(booking);
    }

    public void deleteBooking(int bookingId) throws SQLException {
        bookingDAO.deleteBooking(bookingId);
    }
    public void printBookingConfirmation(Booking booking) {
    System.out.println("Booking Confirmation");
    System.out.println("Guest Name: " + booking.getGuest().getName());
    System.out.println("Room Number: " + booking.getRoom().getRoomNumber());
    System.out.println("Check-In Date: " + booking.getCheckInDate());
    System.out.println("Check-Out Date: " + booking.getCheckOutDate());
    System.out.println("Total Price: " + booking.getTotalPrice());
}

}
