
package RRS;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.sql.*;
import java.time.LocalDate;

public class RRS extends JFrame {
    private JPanel panel;
    private JButton retrieveTrainListButton ;
    private JButton displayTrainInformationButton;
    private JButton getConfirmedTicketListButton;
    private JButton getTrainRunningInfoButton;
    private JButton cancelTrainTicketButton;
    private JButton retrievePassengerInfoButton;
    private JButton bookTicketButton;
    private JList list1;
    private JTable table;
    private JButton searchButton;
    private JTextField trainNameField;
    private JComboBox<String> statusComboBox;
    private JTable ticketTable;
    private JTextArea textArea;
    private JLabel statusLabel;
    private JLabel nameLabel = new JLabel("Name:");
  JTextField nameField= new JTextField(20);
    JLabel ageLabel = new JLabel("Age:");
   JTextField ageField = new JTextField(3);
    JLabel genderLabel = new JLabel("Gender:");
   JTextField genderField = new JTextField(10);
    JLabel trainNoLabel = new JLabel("Train No.:");
    JTextField trainNoField = new JTextField(10);
    JLabel dateLabel = new JLabel("Date:");
    JTextField dateField = new JTextField(10);
    JLabel classLabel = new JLabel("Class:");
    JTextField classField = new JTextField(10);


    public RRS() {
        //Action Listener to  button components added
        retrieveTrainListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retrieveTrainList();
            }
        });
        displayTrainInformationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayTrainInfo();
            }
        });


        getConfirmedTicketListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getConfirmedTicketListBasedOnName();
            }
        });
        retrievePassengerInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayPassengerAndTrainInfoBasedOnAge();
            }
        });
        getTrainRunningInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getTrainRunningInfo();
            }
        });
        cancelTrainTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelTicket();
            }
        });
        bookTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookTicket();
            }
        });
    }

    private void retrieveTrainList() {
        JPanel inputPanel = new JPanel();
        JTextField firstNameField = new JTextField(10);
        JTextField lastNameField = new JTextField(10);
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(firstNameField);
        inputPanel.add(Box.createHorizontalStrut(15));
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lastNameField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Please enter your name", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            if (!firstName.matches("^[a-zA-Z ]+$") || !lastName.matches("^[a-zA-Z ]+$")) {
                JOptionPane.showMessageDialog(null, "Please enter valid first and last names (letters only).", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String url = "jdbc:mysql://localhost:3306/RRS";
            String username = "root";
            String password = "";

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String query = "SELECT t.train_number, t.train_name, t.source, t.destination\n" +
                        "FROM train t, reservation r, passenger p\n" +
                        "WHERE t.train_number = r.train_number\n" +
                        "AND r.passenger_id = p.passenger_id\n" +
                        "AND p.first_name = ?\n" +
                        "AND p.last_name = ?;";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                ResultSet rs = stmt.executeQuery();



                 DefaultTableModel model = new DefaultTableModel();
                 JTable resultTable = new JTable(model);


                 ResultSetMetaData rsmd = rs.getMetaData();
                 int columnCount = rsmd.getColumnCount();
                 String[] columnNames = new String[columnCount];
                 for (int i = 1; i <= columnCount; i++) {
                     columnNames[i - 1] = rsmd.getColumnName(i);
                 }
                 model.setColumnIdentifiers(columnNames);


                 while (rs.next()) {
                     String[] row = new String[columnCount];
                     for (int i = 1; i <= columnCount; i++) {
                         row[i - 1] = rs.getString(i);
                     }
                     model.addRow(row);
                 }

                 JScrollPane scrollPane = new JScrollPane(resultTable);
                 JOptionPane.showMessageDialog(null, scrollPane, "Train List", JOptionPane.PLAIN_MESSAGE);
             }




             catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void displayTrainInfo() {
        JPanel inputPanel = new JPanel();
        JTextField dayField = new JTextField(10);
        inputPanel.add(new JLabel("Day:"));
        inputPanel.add(dayField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Please enter the day", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String day = dayField.getText().trim();

            //UI validation
                if (day.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a day.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (!day.matches("^(Monday|Tuesday|Wednesday|Thursday|Friday|Saturday|Sunday)$")) {
                    JOptionPane.showMessageDialog(null, "Invalid day. Please enter a valid day (e.g. Monday, Tuesday, etc.).", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            String url = "jdbc:mysql://localhost:3306/RRS";
            String username = "root";
            String password = "";

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String query ="SELECT train_number, train_name, source, destination, available_days FROM train WHERE available_days LIKE '%weekday%' OR available_days LIKE ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, "%" + day + "%");
                ResultSet rs = stmt.executeQuery();

                // Create a table model with the result set
                DefaultTableModel model = new DefaultTableModel();
                JTable resultTable = new JTable(model);

                // Retrieve metadata and set column names
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                String[] columnNames = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    columnNames[i-1] = rsmd.getColumnName(i);
                }
                model.setColumnIdentifiers(columnNames);

                // Add rows to the table model
                while (rs.next()) {
                    String[] row = new String[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i-1] = rs.getString(i);
                    }
                    model.addRow(row);
                }

                // Display the table in a dialog box
                JScrollPane scrollPane = new JScrollPane(resultTable);
                JOptionPane.showMessageDialog(null, scrollPane, "Train List", JOptionPane.PLAIN_MESSAGE);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void getConfirmedTicketListBasedOnName() {
        JTextField trainNameField = new JTextField(10);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Confirmed", "Cancelled", "Waitlisted"});
        JTable ticketTable = new JTable(new DefaultTableModel(new Object[]{"PNR", "Train Name", "From", "To", "Passenger Name", "Status"}, 0));
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Train Name:"));
        inputPanel.add(trainNameField);
        inputPanel.add(new JLabel("Status:"));
        inputPanel.add(statusComboBox);
        int result = JOptionPane.showConfirmDialog(null, inputPanel, "Please enter your input", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String trainName = trainNameField.getText();
            String status = (String) statusComboBox.getSelectedItem();
            if (!trainName.matches("^[a-zA-Z ]+$")) {
                JOptionPane.showMessageDialog(null, "Invalid Train Name! Train Name should contain only letters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }



            String url = "jdbc:mysql://localhost:3306/RRS";
            String username = "root";
            String password = "";

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                // Prepare the SQL query
                String query = "SELECT P.passenger_id, P.first_name, P.last_name " +
                        "FROM PASSENGER P, RESERVATION R, TRAIN T " +
                        "WHERE P.passenger_id = R.passenger_id " +
                        "AND R.status = ? " +
                        "AND R.train_number = T.train_number " +
                        "AND T.train_name = ?;";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, status);
                stmt.setString(2, trainName);
                ResultSet rs = stmt.executeQuery();

                DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();
                model.setRowCount(0);

                while (rs.next()) {
                    String passengerId = rs.getString("passenger_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    model.addRow(new Object[]{passengerId, trainName, "", "", firstName + " " + lastName, status});
                }
                JScrollPane scrollPane = new JScrollPane(ticketTable);
                JOptionPane.showMessageDialog(null, scrollPane, "Confirmed Ticket List", JOptionPane.PLAIN_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }




    private void getTrainRunningInfo() {
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        String url = "jdbc:mysql://localhost:3306/RRS";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT t.train_name, COUNT(r.passenger_id) AS passenger_count\n" +
                    "FROM TRAIN t, RESERVATION r\n" +
                    "WHERE t.train_number = r.train_number\n" +
                    "GROUP BY t.train_name";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String trainName = rs.getString("train_name");
                int passengerCount = rs.getInt("passenger_count");
                textArea.append(trainName + ": " + passengerCount + " passengers\n");
            }

            JScrollPane scrollPane = new JScrollPane(textArea);
            JOptionPane.showMessageDialog(null, scrollPane, "Train Running Information", JOptionPane.PLAIN_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }



    private void displayPassengerAndTrainInfoBasedOnAge() {
        JPanel inputPanel = new JPanel();
        JTextField minAgeField = new JTextField(5);
        JTextField maxAgeField = new JTextField(5);
        inputPanel.add(new JLabel("Minimum Age:"));
        inputPanel.add(minAgeField);
        inputPanel.add(new JLabel("Maximum Age:"));
        inputPanel.add(maxAgeField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Please enter age range", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int minAge = Integer.parseInt(minAgeField.getText());
            int maxAge = Integer.parseInt(maxAgeField.getText());
            String minAgeText = minAgeField.getText();
            String maxAgeText = maxAgeField.getText();

            if (minAgeText.isEmpty() || maxAgeText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter both minimum and maximum age.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            try {
                minAge = Integer.parseInt(minAgeText);
                maxAge = Integer.parseInt(maxAgeText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid integer values for age.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (minAge > maxAge) {
                JOptionPane.showMessageDialog(null, "Minimum age cannot be greater than maximum age.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String url = "jdbc:mysql://localhost:3306/RRS";
            String username = "root";
            String password = "";

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                String query = "SELECT t.train_number, t.train_name, t.source, t.destination,\n" +
                        "p.first_name, p.last_name, a.address_line, a.city, a.state, a.zipcode,\n" +
                        "r.ticket_type, r.status\n" +
                        "FROM train t, reservation r, passenger p, address a\n" +
                        "WHERE t.train_number = r.train_number\n" +
                        "AND r.passenger_id = p.passenger_id\n" +
                        "AND p.address_id = a.id\n" +
                        "AND TIMESTAMPDIFF(YEAR, p.birth_date, CURDATE()) BETWEEN ? AND ?;";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, minAge);
                stmt.setInt(2, maxAge);
                ResultSet rs = stmt.executeQuery();

                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Train Number");
                model.addColumn("Train Name");
                model.addColumn("Source");
                model.addColumn("Destination");
                model.addColumn("Passenger Name");
                model.addColumn("Address");
                model.addColumn("Category");
                model.addColumn("Ticket Status");
                while (rs.next()) {
                    Object[] row = new Object[8];
                    row[0] = rs.getString("train_number");
                    row[1] = rs.getString("train_name");
                    row[2] = rs.getString("source");
                    row[3] = rs.getString("destination");
                    row[4] = rs.getString("first_name") + " " + rs.getString("last_name");
                    row[5] = rs.getString("address_line") + ", " + rs.getString("city") + ", " + rs.getString("state") + " " + rs.getString("zipcode");
                    row[6] = rs.getString("ticket_type");
                    row[7] = rs.getString("status");
                    model.addRow(row);
                }

                JTable table = new JTable(model);

                JScrollPane scrollPane = new JScrollPane(table);


                JOptionPane.showMessageDialog(null, scrollPane, "Passenger and Train Info", JOptionPane.PLAIN_MESSAGE);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }



    public void cancelTicket() {
        String trainNumber = JOptionPane.showInputDialog(null, "Enter Train Number:");
        String passengerId = JOptionPane.showInputDialog(null, "Enter Passenger ID:");

        String url = "jdbc:mysql://localhost:3306/RRS";
        String username = "root";
        String password = "";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            String deleteQuery = "DELETE FROM reservation WHERE train_number = ? AND passenger_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setString(1, trainNumber);
            deleteStmt.setString(2, passengerId);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted == 0) {
                JOptionPane.showMessageDialog(null, "Ticket not found.");
                return;
            }

            String waitingListQuery = "SELECT reservation.*, train_status.booking_date\n" +
                    "FROM reservation, train_status\n" +
                    "WHERE reservation.train_number = train_status.train_number\n" +
                    "AND reservation.train_number = ?\n" +
                    "AND reservation.status = 'Waitlist'\n" +
                    "ORDER BY train_status.booking_date ASC";
            PreparedStatement waitingListStmt = conn.prepareStatement(waitingListQuery);
            waitingListStmt.setString(1, trainNumber);
            ResultSet waitingListRs = waitingListStmt.executeQuery();

            if (!waitingListRs.next()) {
                JOptionPane.showMessageDialog(null, "No passengers waiting for this train.");
                return;
            }

            String waitingPassengerId = waitingListRs.getString("passenger_id");
            int reservationId = waitingListRs.getInt("reservation_id");
            String updateWaitingPassengerQuery = "UPDATE reservation SET status = 'Confirmed', reservation_id = ? " +
                    "WHERE train_number = ? AND passenger_id = ?";
            PreparedStatement updateWaitingPassengerStmt = conn.prepareStatement(updateWaitingPassengerQuery);
            updateWaitingPassengerStmt.setInt(1, reservationId);
            updateWaitingPassengerStmt.setString(2, trainNumber);
            updateWaitingPassengerStmt.setString(3, waitingPassengerId);
            updateWaitingPassengerStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Ticket canceled. Passenger " + waitingPassengerId + " from the waiting list has been confirmed with ticket number " + reservationId + ".");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void bookTicket() {
        JPanel panel = new JPanel(new GridLayout(8, 2));

        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField();
        JLabel birthDateLabel = new JLabel("Birth Date (YYYY-MM-DD):");
        JTextField birthDateField = new JTextField();
        JLabel ssnLabel = new JLabel("SSN:");
        JTextField ssnField = new JTextField();
        JLabel streetLabel = new JLabel("Street:");
        JTextField streetField = new JTextField();
        JLabel cityLabel = new JLabel("City:");
        JTextField cityField = new JTextField();
        JLabel stateLabel = new JLabel("State:");
        JTextField stateField = new JTextField();
        JLabel zipLabel = new JLabel("Zip:");
        JTextField zipField = new JTextField();
        JLabel train_numberLabel = new JLabel("Train Number:");
        JTextField train_numberField = new JTextField();

        panel.add(firstNameLabel);
        panel.add(firstNameField);
        panel.add(lastNameLabel);
        panel.add(lastNameField);
        panel.add(birthDateLabel);
        panel.add(birthDateField);
        panel.add(ssnLabel);
        panel.add(ssnField);
        panel.add(streetLabel);
        panel.add(streetField);
        panel.add(cityLabel);
        panel.add(cityField);
        panel.add(stateLabel);
        panel.add(stateField);
        panel.add(zipLabel);
        panel.add(zipField);
        panel.add(train_numberLabel);
        panel.add(train_numberField);

        JButton bookTicketButton = new JButton("Book Ticket");
        panel.add(bookTicketButton);

        setTitle("Book Ticket");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(panel);
        pack();
        setVisible(true);

        bookTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String birthDate = birthDateField.getText();
                String ssn = ssnField.getText();
                String street = streetField.getText();
                String city = cityField.getText();
                String state = stateField.getText();
                String zip = zipField.getText();
                String train_number = train_numberField.getText();

                try {
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/RRS", "root", "");

                    String addressQuery = "INSERT INTO ADDRESS (address_line, city, state, zipcode) VALUES (?, ?, ?, ?)";
                    PreparedStatement addressStatement = conn.prepareStatement(addressQuery, Statement.RETURN_GENERATED_KEYS);
                    addressStatement.setString(1, street);
                    addressStatement.setString(2, city);
                    addressStatement.setString(3, state);
                    addressStatement.setString(4, zip);
                    addressStatement.executeUpdate();
                    ResultSet addressResult = addressStatement.getGeneratedKeys();
                    addressResult.next();
                    int addressId = addressResult.getInt(1);

                    String passengerQuery = "INSERT INTO PASSENGER (first_name, last_name, birth_date, SSN) VALUES (?, ?, ?, ?)";
                    PreparedStatement passengerStatement = conn.prepareStatement(passengerQuery, Statement.RETURN_GENERATED_KEYS);
                    passengerStatement.setString(1, firstName);
                    passengerStatement.setString(2, lastName);
                    passengerStatement.setDate(3, Date.valueOf(birthDate));
                    passengerStatement.setString(4, ssn);
                    passengerStatement.executeUpdate();
                    ResultSet passengerResult = passengerStatement.getGeneratedKeys();
                    passengerResult.next();
                    int passengerId = passengerResult.getInt(1);

                    String reservationQuery = "INSERT INTO RESERVATION (passenger_id, train_number, journey_date,status) VALUES (?, ?, ?,?)";
                    PreparedStatement reservationStatement = conn.prepareStatement(reservationQuery);
                    reservationStatement.setInt(1, passengerId);
                    reservationStatement.setString(2, train_number);
                    reservationStatement.setDate(3, Date.valueOf(LocalDate.now()));
                    reservationStatement.setString(4,"Confirmed");
                    reservationStatement.executeUpdate();

                    JOptionPane.showMessageDialog(panel, "Reservation successful.");
                    dispose();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }



        public static void main(String[] args) {
        RRS obj = new RRS();
        obj.setContentPane(obj.panel);
        obj.setVisible(true);
        obj.setSize(600,500);
    }



}
