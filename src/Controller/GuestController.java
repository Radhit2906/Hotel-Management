package controller;

import dao.GuestDAO;
import model.Guest;

import java.sql.SQLException;
import java.util.List;

public class GuestController {
    private GuestDAO guestDAO;

    public GuestController() {
        guestDAO = new GuestDAO();
    }

    public List<Guest> getAllGuests() throws SQLException {
        return guestDAO.getAllGuests();
    }

    public void addGuest(Guest guest) throws SQLException {
        guestDAO.addGuest(guest);
    }

    public void updateGuest(Guest guest) throws SQLException {
        guestDAO.updateGuest(guest);
    }

    public void deleteGuest(int guestId) throws SQLException {
        guestDAO.deleteGuest(guestId);
    }
}
