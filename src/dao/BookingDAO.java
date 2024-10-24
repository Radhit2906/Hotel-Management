package dao;

import model.Booking;
import model.Room;
import model.Guest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookingDAO {
    public void addBooking(Booking booking) throws SQLException {
        String query = "INSERT INTO bookings (room_id, guest_id, check_in_date, check_out_date, total_price, additional_charges, additional_charges_list) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, booking.getRoom().getId());
            stmt.setInt(2, booking.getGuest().getId());
            stmt.setDate(3, java.sql.Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, java.sql.Date.valueOf(booking.getCheckOutDate()));
            stmt.setDouble(5, booking.calculateTotalPrice());
            stmt.setDouble(6, booking.getAdditionalCharges());
            stmt.setString(7, String.join(",", booking.getAdditionalChargesList())); // Simpan daftar sebagai string
            stmt.executeUpdate();
        }
    }

    public void updateBooking(Booking booking) throws SQLException {
        String query = "UPDATE bookings SET room_id = ?, guest_id = ?, check_in_date = ?, check_out_date = ?, total_price = ?, additional_charges = ?, additional_charges_list = ?, actual_check_in_date = ?, actual_check_out_date = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, booking.getRoom().getId());
            stmt.setInt(2, booking.getGuest().getId());
            stmt.setDate(3, java.sql.Date.valueOf(booking.getCheckInDate()));
            stmt.setDate(4, java.sql.Date.valueOf(booking.getCheckOutDate()));
            stmt.setDouble(5, booking.calculateTotalPrice());
            stmt.setDouble(6, booking.getAdditionalCharges());
            stmt.setString(7, String.join(",", booking.getAdditionalChargesList())); // Simpan daftar sebagai string

            if (booking.getActualCheckInDate() != null) {
                stmt.setDate(8, java.sql.Date.valueOf(booking.getActualCheckInDate()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }

            if (booking.getActualCheckOutDate() != null) {
                stmt.setDate(9, java.sql.Date.valueOf(booking.getActualCheckOutDate()));
            } else {
                stmt.setNull(9, java.sql.Types.DATE);
            }

            stmt.setInt(10, booking.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBooking(int bookingId) throws SQLException {
        String query = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            stmt.executeUpdate();
        }
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT * FROM bookings";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                booking.setAdditionalCharges(rs.getDouble("additional_charges"));

                // Mengatur nilai actualCheckInDate
                Date actualCheckInDate = rs.getDate("actual_check_in_date");
                if (actualCheckInDate != null) {
                    booking.setActualCheckInDate(actualCheckInDate.toLocalDate());
                }

                // Mengatur nilai actualCheckOutDate
                Date actualCheckOutDate = rs.getDate("actual_check_out_date");
                if (actualCheckOutDate != null) {
                    booking.setActualCheckOutDate(actualCheckOutDate.toLocalDate());
                }

                booking.setTotalPrice(rs.getDouble("total_price"));
                String additionalChargesList = rs.getString("additional_charges_list");
                if (additionalChargesList != null && !additionalChargesList.isEmpty()) {
                    booking.setAdditionalChargesList(Arrays.asList(additionalChargesList.split(","))); // Muat daftar dari string
                } else {
                    booking.setAdditionalChargesList(new ArrayList<>()); // Set empty list jika null
                }

                // Populate Room and Guest objects (implement getRoomById and getGuestById methods)
                booking.setRoom(getRoomById(rs.getInt("room_id")));
                booking.setGuest(getGuestById(rs.getInt("guest_id")));
                bookings.add(booking);
            }
        }
        return bookings;
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String query = "SELECT * FROM bookings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Booking booking = new Booking();
                    booking.setId(rs.getInt("id"));
                    booking.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
                    booking.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
                    booking.setAdditionalCharges(rs.getDouble("additional_charges"));

                    // Mengatur nilai actualCheckInDate
                    Date actualCheckInDate = rs.getDate("actual_check_in_date");
                    if (actualCheckInDate != null) {
                        booking.setActualCheckInDate(actualCheckInDate.toLocalDate());
                    }

                    // Mengatur nilai actualCheckOutDate
                    Date actualCheckOutDate = rs.getDate("actual_check_out_date");
                    if (actualCheckOutDate != null) {
                        booking.setActualCheckOutDate(actualCheckOutDate.toLocalDate());
                    }

                    booking.setTotalPrice(rs.getDouble("total_price"));
                    String additionalChargesList = rs.getString("additional_charges_list");
                    if (additionalChargesList != null && !additionalChargesList.isEmpty()) {
                        booking.setAdditionalChargesList(Arrays.asList(additionalChargesList.split(","))); // Muat daftar dari string
                    } else {
                        booking.setAdditionalChargesList(new ArrayList<>()); // Set empty list jika null
                    }

                    // Populate Room and Guest objects (implement getRoomById and getGuestById methods)
                    booking.setRoom(getRoomById(rs.getInt("room_id")));
                    booking.setGuest(getGuestById(rs.getInt("guest_id")));
                    return booking;
                }
            }
        }
        return null;
    }

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM rooms";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setPrice(rs.getDouble("price"));
                rooms.add(room);
            }
        }
        return rooms;
    }

    public Room getRoomById(int id) throws SQLException {
        String query = "SELECT * FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Room room = new Room();
                    room.setId(rs.getInt("id"));
                    room.setRoomNumber(rs.getString("room_number"));
                    room.setRoomType(rs.getString("room_type"));
                    room.setPrice(rs.getDouble("price"));
                    return room;
                }
            }
        }
        return null;
    }

    public List<Guest> getAllGuests() throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Guest guest = new Guest();
                guest.setId(rs.getInt("id"));
                guest.setName(rs.getString("name"));
                guest.setKtpNumber(rs.getString("ktp_number"));
                guest.setPhone(rs.getString("phone"));
                guests.add(guest);
            }
        }
        return guests;
    }

    public Guest getGuestById(int id) throws SQLException {
        String query = "SELECT * FROM guests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt("id"));
                    guest.setName(rs.getString("name"));
                    guest.setKtpNumber(rs.getString("ktp_number"));
                    guest.setPhone(rs.getString("phone"));
                    return guest;
                }
            }
        }
        return null;
    }
}
