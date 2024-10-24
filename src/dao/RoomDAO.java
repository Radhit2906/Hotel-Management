package dao;

import model.Room;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    public void addRoom(Room room) throws SQLException {
        String query = "INSERT INTO rooms (room_number, room_type, price, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setDouble(3, room.getPrice());
            stmt.setString(4, room.getStatus());
            stmt.executeUpdate();
        }
    }

    public void updateRoom(Room room) throws SQLException {
        String query = "UPDATE rooms SET room_number = ?, room_type = ?, price = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, room.getRoomNumber());
            stmt.setString(2, room.getRoomType());
            stmt.setDouble(3, room.getPrice());
            stmt.setString(4, room.getStatus());
            stmt.setInt(5, room.getId());
            stmt.executeUpdate();
        }
    }

    public void updateRoomStatus(int roomId, String status) throws SQLException {
        String query = "UPDATE rooms SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, roomId);
            stmt.executeUpdate();
        }
    }
    
    public void deleteBookingsByRoomId(int roomId) throws SQLException {
        String query = "DELETE FROM bookings WHERE room_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        }
    }

    public void deleteRoom(int roomId) throws SQLException {
        // Hapus booking terkait sebelum menghapus room
        deleteBookingsByRoomId(roomId);

        String query = "DELETE FROM rooms WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, roomId);
            stmt.executeUpdate();
        }
                resetAutoIncrement();

    }
    private void resetAutoIncrement() throws SQLException {
        String query = "ALTER TABLE rooms AUTO_INCREMENT = 1";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.executeUpdate();
        }
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
                room.setStatus(rs.getString("status"));
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
                    room.setStatus(rs.getString("status"));
                    return room;
                }
            }
        }
        return null;
    }
    
    public Room getRoomByNumber(String roomNumber) throws SQLException {
        String query = "SELECT * FROM rooms WHERE room_number = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, roomNumber);
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
    
}
