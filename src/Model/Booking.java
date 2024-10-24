package model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private int id;
    private Room room;
    private Guest guest;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDate actualCheckInDate;
    private LocalDate actualCheckOutDate;
    private double totalPrice;
    private double additionalCharges; // Biaya tambahan
    private List<String> additionalChargesList = new ArrayList<>(); // Rincian biaya tambahan

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public LocalDate getActualCheckInDate() {
        return actualCheckInDate;
    }

    public void setActualCheckInDate(LocalDate actualCheckInDate) {
        this.actualCheckInDate = actualCheckInDate;
    }

    public LocalDate getActualCheckOutDate() {
        return actualCheckOutDate;
    }

    public void setActualCheckOutDate(LocalDate actualCheckOutDate) {
        this.actualCheckOutDate = actualCheckOutDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getAdditionalCharges() {
        return additionalCharges;
    }

    public void setAdditionalCharges(double additionalCharges) {
        this.additionalCharges = additionalCharges;
    }

    public List<String> getAdditionalChargesList() {
        return additionalChargesList;
    }

    public void setAdditionalChargesList(List<String> additionalChargesList) {
        this.additionalChargesList = additionalChargesList;
    }

    public double calculateTotalPrice() {
        long daysStayed = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return (daysStayed * room.getPrice()) + additionalCharges;
    }

    public double calculateActualTotalPrice() {
        if (actualCheckInDate != null && actualCheckOutDate != null) {
            long actualDaysStayed = ChronoUnit.DAYS.between(actualCheckInDate, actualCheckOutDate);
            return (actualDaysStayed * room.getPrice()) + additionalCharges;
        }
        return 0;
    }
}
