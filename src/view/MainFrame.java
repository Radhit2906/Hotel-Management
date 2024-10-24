package view;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Hotel Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu roomMenu = new JMenu("Room Management");
        JMenuItem roomManageItem = new JMenuItem("Manage Rooms");
        roomManageItem.addActionListener(e -> showRoomManagement());
        roomMenu.add(roomManageItem);

        JMenu guestMenu = new JMenu("Guest Management");
        JMenuItem guestManageItem = new JMenuItem("Manage Guests");
        guestManageItem.addActionListener(e -> showGuestManagement());
        guestMenu.add(guestManageItem);

        JMenu bookingMenu = new JMenu("Booking Management");
        JMenuItem bookingManageItem = new JMenuItem("Manage Bookings");
        bookingManageItem.addActionListener(e -> showBookingManagement());
        bookingMenu.add(bookingManageItem);

        JMenu checkInOutMenu = new JMenu("Check-In/Check-Out");
        JMenuItem checkInOutItem = new JMenuItem("Check-In/Check-Out");
        checkInOutItem.addActionListener(e -> showCheckInOutManagement());
        checkInOutMenu.add(checkInOutItem);

        menuBar.add(roomMenu);
        menuBar.add(guestMenu);
        menuBar.add(bookingMenu);
        menuBar.add(checkInOutMenu);

        setJMenuBar(menuBar);
    }

    private void showRoomManagement() {
        getContentPane().removeAll();
        add(new RoomManagementPanel());
        revalidate();
        repaint();
    }

    private void showGuestManagement() {
        getContentPane().removeAll();
        add(new GuestManagementPanel());
        revalidate();
        repaint();
    }

    private void showBookingManagement() {
        getContentPane().removeAll();
        add(new BookingPanel());
        revalidate();
        repaint();
    }

    private void showCheckInOutManagement() {
        getContentPane().removeAll();
        add(new CheckInOutPanel());
        revalidate();
        repaint();
    }
}
