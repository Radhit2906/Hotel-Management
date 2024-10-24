package controller;

import dao.RoomDAO;
import model.Room;

import java.sql.SQLException;
import java.util.List;

public class RoomController {
    private RoomDAO roomDAO;

    public RoomController() {
        roomDAO = new RoomDAO();
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    public void addRoom(Room room) throws SQLException {
        roomDAO.addRoom(room);
    }

    public void updateRoom(Room room) throws SQLException {
        roomDAO.updateRoom(room);
    }

    public void deleteRoom(int roomId) throws SQLException {
        roomDAO.deleteRoom(roomId);
    }
}
