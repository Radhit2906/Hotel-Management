package dao;

import model.Guest;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {
    public void addGuest(Guest guest) throws SQLException {
        String query = "INSERT INTO guests (name, ktp_number, phone) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getKtpNumber());
            stmt.setString(3, guest.getPhone());
            stmt.executeUpdate();
        }
    }

    public void updateGuest(Guest guest) throws SQLException {
        String query = "UPDATE guests SET name = ?, ktp_number = ?, phone = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, guest.getName());
            stmt.setString(2, guest.getKtpNumber());
            stmt.setString(3, guest.getPhone());
            stmt.setInt(4, guest.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteGuest(int guestId) throws SQLException {
        String query = "DELETE FROM guests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, guestId);
            stmt.executeUpdate();
        }
                resetAutoIncrement(); // Panggil metode reset auto increment

    }
    private void resetAutoIncrement() throws SQLException {
        String query = "ALTER TABLE guests AUTO_INCREMENT = 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        }
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
    
    public Guest getGuestByKtp(String ktpNumber) throws SQLException {
        String query = "SELECT * FROM guests WHERE ktp_number = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ktpNumber);
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

    public List<Guest> searchGuests(String keyword) throws SQLException {
        List<Guest> guests = new ArrayList<>();
        String query = "SELECT * FROM guests WHERE name LIKE ? OR ktp_number LIKE ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Guest guest = new Guest();
                    guest.setId(rs.getInt("id"));
                    guest.setName(rs.getString("name"));
                    guest.setKtpNumber(rs.getString("ktp_number"));
                    guest.setPhone(rs.getString("phone"));
                    guests.add(guest);
                }
            }
        }
        return guests;
    }
}
