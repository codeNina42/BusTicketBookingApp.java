import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

// =================== MAIN FRAME ===================
public class BusTicketBookingApp extends JFrame {

    private ArrayList<Bus> buses = new ArrayList<>();
    private ArrayList<Booking> bookings = new ArrayList<>();

    // Bus tab
    private JTextField txtBusNumber, txtFrom, txtTo, txtDate, txtTime, txtTotalSeats, txtFare;
    private JTable busTable;
    private DefaultTableModel busTableModel;

    // Booking tab
    private JComboBox<Bus> comboBus;
    private JTextField txtPassengerName, txtPassengerPhone, txtSeatsToBook;
    private JLabel lblAvailableSeats, lblFarePerSeat, lblTotalAmount;
    private JTable bookingTable;
    private DefaultTableModel bookingTableModel;

    public BusTicketBookingApp() {
        setTitle("Bus Ticket Booking System");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Buses", createBusPanel());
        tabs.addTab("Book Ticket", createBookingPanel());
        tabs.addTab("All Bookings", createBookingsListPanel());

        add(tabs);
    }

    // =================== BUS PANEL ===================
    private JPanel createBusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Add New Bus"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBusNumber = new JTextField(15);
        txtFrom = new JTextField(15);
        txtTo = new JTextField(15);
        txtDate = new JTextField(10); // e.g. 2025-12-02
        txtTime = new JTextField(10); // e.g. 10:30 AM
        txtTotalSeats = new JTextField(5);
        txtFare = new JTextField(7);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Bus Number:"), gbc);
        gbc.gridx = 1;
        form.add(txtBusNumber, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("From:"), gbc);
        gbc.gridx = 1;
        form.add(txtFrom, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("To:"), gbc);
        gbc.gridx = 1;
        form.add(txtTo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Date (e.g. 2025-12-02):"), gbc);
        gbc.gridx = 1;
        form.add(txtDate, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Time (e.g. 10:30 AM):"), gbc);
        gbc.gridx = 1;
        form.add(txtTime, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Total Seats:"), gbc);
        gbc.gridx = 1;
        form.add(txtTotalSeats, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Fare per Seat:"), gbc);
        gbc.gridx = 1;
        form.add(txtFare, gbc);

        row++;
        JButton btnAdd = new JButton("Add Bus");
        JButton btnClear = new JButton("Clear");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnAdd);
        btnPanel.add(btnClear);

        gbc.gridx = 1; gbc.gridy = row;
        form.add(btnPanel, gbc);

        btnAdd.addActionListener(e -> addBus());
        btnClear.addActionListener(e -> clearBusForm());

        busTableModel = new DefaultTableModel(
                new String[]{"ID", "Bus No", "From", "To", "Date", "Time", "Seats", "Fare"},
                0
        );
        busTable = new JTable(busTableModel);
        JScrollPane scroll = new JScrollPane(busTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Bus List"));

        panel.add(form, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void addBus() {
        String busNo = txtBusNumber.getText().trim();
        String from = txtFrom.getText().trim();
        String to = txtTo.getText().trim();
        String date = txtDate.getText().trim();
        String time = txtTime.getText().trim();
        String seatsStr = txtTotalSeats.getText().trim();
        String fareStr = txtFare.getText().trim();

        if (busNo.isEmpty() || from.isEmpty() || to.isEmpty() ||
                date.isEmpty() || time.isEmpty() || seatsStr.isEmpty() || fareStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int seats;
        double fare;
        try {
            seats = Integer.parseInt(seatsStr);
            fare = Double.parseDouble(fareStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats must be integer, Fare must be number.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Bus bus = new Bus(busNo, from, to, date, time, seats, fare);
        buses.add(bus);

        busTableModel.addRow(new Object[]{
                bus.getId(), bus.getBusNumber(), bus.getFrom(), bus.getTo(),
                bus.getDate(), bus.getTime(), bus.getTotalSeats(), bus.getFare()
        });

        // also update booking combo
        comboBus.addItem(bus);

        clearBusForm();
    }

    private void clearBusForm() {
        txtBusNumber.setText("");
        txtFrom.setText("");
        txtTo.setText("");
        txtDate.setText("");
        txtTime.setText("");
        txtTotalSeats.setText("");
        txtFare.setText("");
    }

    // =================== BOOKING PANEL ===================
    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Book Ticket"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboBus = new JComboBox<>();
        txtPassengerName = new JTextField(20);
        txtPassengerPhone = new JTextField(15);
        txtSeatsToBook = new JTextField(5);

        lblAvailableSeats = new JLabel("Available: -");
        lblFarePerSeat = new JLabel("Fare: -");
        lblTotalAmount = new JLabel("Total: -");

        comboBus.addActionListener(e -> updateBusInfoLabels());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Select Bus:"), gbc);
        gbc.gridx = 1;
        form.add(comboBus, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Passenger Name:"), gbc);
        gbc.gridx = 1;
        form.add(txtPassengerName, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Passenger Phone:"), gbc);
        gbc.gridx = 1;
        form.add(txtPassengerPhone, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Seats to Book:"), gbc);
        gbc.gridx = 1;
        form.add(txtSeatsToBook, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Bus Info:"), gbc);
        gbc.gridx = 1;
        form.add(lblAvailableSeats, gbc);

        row++;
        gbc.gridx = 1; gbc.gridy = row;
        form.add(lblFarePerSeat, gbc);

        row++;
        gbc.gridx = 1; gbc.gridy = row;
        form.add(lblTotalAmount, gbc);

        row++;
        JButton btnCalc = new JButton("Calculate Total");
        JButton btnBook = new JButton("Confirm Booking");
        JButton btnClear = new JButton("Clear");
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(btnCalc);
        btnPanel.add(btnBook);
        btnPanel.add(btnClear);

        gbc.gridx = 1; gbc.gridy = row;
        form.add(btnPanel, gbc);

        btnCalc.addActionListener(e -> calculateTotal());
        btnBook.addActionListener(e -> bookTicket());
        btnClear.addActionListener(e -> clearBookingForm());

        bookingTableModel = new DefaultTableModel(
                new String[]{"ID", "Passenger", "Phone", "Bus", "Route", "Date", "Time", "Seats", "Total"},
                0
        );
        bookingTable = new JTable(bookingTableModel);
        JScrollPane scroll = new JScrollPane(bookingTable);
        scroll.setBorder(BorderFactory.createTitledBorder("Recent Bookings"));

        panel.add(form, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void updateBusInfoLabels() {
        Bus bus = (Bus) comboBus.getSelectedItem();
        if (bus == null) {
            lblAvailableSeats.setText("Available: -");
            lblFarePerSeat.setText("Fare: -");
            return;
        }
        lblAvailableSeats.setText("Available: " + bus.getAvailableSeats());
        lblFarePerSeat.setText("Fare: " + bus.getFare());
        lblTotalAmount.setText("Total: -");
    }

    private void calculateTotal() {
        Bus bus = (Bus) comboBus.getSelectedItem();
        if (bus == null) {
            JOptionPane.showMessageDialog(this, "Please add/select a bus first.",
                    "No Bus", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String seatsStr = txtSeatsToBook.getText().trim();
        if (seatsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter how many seats to book.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nSeats;
        try {
            nSeats = Integer.parseInt(seatsStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats must be integer.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nSeats <= 0) {
            JOptionPane.showMessageDialog(this, "Seats must be positive.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nSeats > bus.getAvailableSeats()) {
            JOptionPane.showMessageDialog(this, "Not enough seats available.",
                    "Seats Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = nSeats * bus.getFare();
        lblTotalAmount.setText("Total: " + total);
    }

    private void bookTicket() {
        Bus bus = (Bus) comboBus.getSelectedItem();
        if (bus == null) {
            JOptionPane.showMessageDialog(this, "Please add/select a bus first.",
                    "No Bus", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtPassengerName.getText().trim();
        String phone = txtPassengerPhone.getText().trim();
        String seatsStr = txtSeatsToBook.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || seatsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int nSeats;
        try {
            nSeats = Integer.parseInt(seatsStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Seats must be integer.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nSeats <= 0) {
            JOptionPane.showMessageDialog(this, "Seats must be positive.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nSeats > bus.getAvailableSeats()) {
            JOptionPane.showMessageDialog(this, "Not enough seats available.",
                    "Seats Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = nSeats * bus.getFare();

        // Update bus booked seats
        bus.bookSeats(nSeats);
        updateBusInfoLabels();

        Booking booking = new Booking(bus, name, phone, nSeats, total);
        bookings.add(booking);

        bookingTableModel.addRow(new Object[]{
                booking.getId(),
                booking.getPassengerName(),
                booking.getPassengerPhone(),
                bus.getBusNumber(),
                bus.getFrom() + " -> " + bus.getTo(),
                bus.getDate(),
                bus.getTime(),
                booking.getSeatsBooked(),
                booking.getTotalAmount()
        });

        // Also push to "All Bookings" table
        allBookingsTableModel.addRow(new Object[]{
                booking.getId(),
                booking.getPassengerName(),
                booking.getPassengerPhone(),
                bus.getBusNumber(),
                bus.getFrom() + " -> " + bus.getTo(),
                bus.getDate(),
                bus.getTime(),
                booking.getSeatsBooked(),
                booking.getTotalAmount()
        });

        JOptionPane.showMessageDialog(this, "Booking confirmed!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        clearBookingForm();
    }

    private void clearBookingForm() {
        txtPassengerName.setText("");
        txtPassengerPhone.setText("");
        txtSeatsToBook.setText("");
        lblTotalAmount.setText("Total: -");
        updateBusInfoLabels();
    }

    // =================== ALL BOOKINGS PANEL ===================
    private JTable allBookingsTable;
    private DefaultTableModel allBookingsTableModel;

    private JPanel createBookingsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        allBookingsTableModel = new DefaultTableModel(
                new String[]{"ID", "Passenger", "Phone", "Bus", "Route", "Date", "Time", "Seats", "Total"},
                0
        );
        allBookingsTable = new JTable(allBookingsTableModel);
        JScrollPane scroll = new JScrollPane(allBookingsTable);
        scroll.setBorder(BorderFactory.createTitledBorder("All Bookings"));

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // =================== MAIN METHOD ===================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BusTicketBookingApp app = new BusTicketBookingApp();
            app.setVisible(true);
        });
    }
}

// =================== MODEL CLASSES ===================
class Bus {
    private static int counter = 1;
    private int id;
    private String busNumber;
    private String from;
    private String to;
    private String date;
    private String time;
    private int totalSeats;
    private int bookedSeats;
    private double fare;

    public Bus(String busNumber, String from, String to,
               String date, String time, int totalSeats, double fare) {
        this.id = counter++;
        this.busNumber = busNumber;
        this.from = from;
        this.to = to;
        this.date = date;
        this.time = time;
        this.totalSeats = totalSeats;
        this.bookedSeats = 0;
        this.fare = fare;
    }

    public int getId() { return id; }
    public String getBusNumber() { return busNumber; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public int getTotalSeats() { return totalSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public double getFare() { return fare; }

    public int getAvailableSeats() {
        return totalSeats - bookedSeats;
    }

    public void bookSeats(int n) {
        this.bookedSeats += n;
    }

    @Override
    public String toString() {
        return id + " - " + busNumber + " (" + from + "->" + to + " " + date + " " + time + ")";
    }
}

class Booking {
    private static int counter = 1;
    private int id;
    private Bus bus;
    private String passengerName;
    private String passengerPhone;
    private int seatsBooked;
    private double totalAmount;

    public Booking(Bus bus, String passengerName, String passengerPhone,
                   int seatsBooked, double totalAmount) {
        this.id = counter++;
        this.bus = bus;
        this.passengerName = passengerName;
        this.passengerPhone = passengerPhone;
        this.seatsBooked = seatsBooked;
        this.totalAmount = totalAmount;
    }

    public int getId() { return id; }
    public Bus getBus() { return bus; }
    public String getPassengerName() { return passengerName; }
    public String getPassengerPhone() { return passengerPhone; }
    public int getSeatsBooked() { return seatsBooked; }
    public double getTotalAmount() { return totalAmount; }
}
