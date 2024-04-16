package matchMaker;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.jdatepicker.impl.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class ConnectionSingleton {
	private static Connection con;

	public static Connection getConnection() throws SQLException {
		String url = "jdbc:postgresql://127.0.0.1:5433/matchmaker";
		String user = "postgres";
		String password = "bitnamio";
		if (con == null || con.isClosed()) {
			con = DriverManager.getConnection(url, user, password);
		}
		return con;
	}
}

class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
	private String datePattern = "yyyy-MM-dd";
	private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	@Override
	public Object stringToValue(String text) throws ParseException {
		return dateFormatter.parseObject(text);
	}

	@Override
	public String valueToString(Object value) throws ParseException {
		if (value != null) {
			Calendar cal = (Calendar) value;
			return dateFormatter.format(cal.getTime());
		}
		return "";
	}
}

public class MatchMaker {

	private JFrame frmMatchMaker;
	private static Connection con;
	private JTextField textFieldFirstName;
	private JTextField textFieldLastName;
	private JTextField textFieldPicText;
	private static UtilDateModel modelDatePicker;
	private JDatePickerImpl datePicker;
	private static DefaultTableModel tableModelPersons;
	private JTable tablePersons;
	private JScrollPane scrollPanePersons;
	private static int selectedPerson;

	public static void refresh() {
		Statement stmt;
		ResultSet rs;
		tableModelPersons.setRowCount(0);
		try {
			con = ConnectionSingleton.getConnection();
			stmt = con.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Persons ORDER BY cod_person");
			while (rs.next()) {
				Object[] row = new Object[6];
				row[0] = rs.getInt("cod_person");
				row[1] = rs.getString("first_name");
				row[2] = rs.getString("last_name");
				row[3] = rs.getDate("birth_date");
				row[4] = rs.getInt("age");
				row[5] = rs.getString("photo");
				tableModelPersons.addRow(row);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MatchMaker window = new MatchMaker();
					window.frmMatchMaker.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MatchMaker() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmMatchMaker = new JFrame();
		frmMatchMaker.setBackground(new Color(240, 240, 240));
		frmMatchMaker.setTitle("MatchMaker");
		frmMatchMaker.setBounds(100, 100, 684, 776);
		frmMatchMaker.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMatchMaker.getContentPane().setLayout(null);

		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(30, 33, 54, 14);
		frmMatchMaker.getContentPane().add(lblName);

		textFieldFirstName = new JTextField();
		textFieldFirstName.setBounds(75, 30, 86, 20);
		frmMatchMaker.getContentPane().add(textFieldFirstName);
		textFieldFirstName.setColumns(10);

		JLabel lblLastName = new JLabel("Last Name:");
		lblLastName.setBounds(182, 33, 76, 14);
		frmMatchMaker.getContentPane().add(lblLastName);

		textFieldLastName = new JTextField();
		textFieldLastName.setBounds(250, 30, 149, 20);
		frmMatchMaker.getContentPane().add(textFieldLastName);
		textFieldLastName.setColumns(10);

		JLabel lblDateOfBirth = new JLabel("Date of birth:");
		lblDateOfBirth.setBounds(409, 33, 76, 14);
		frmMatchMaker.getContentPane().add(lblDateOfBirth);

		modelDatePicker = new UtilDateModel();
		Properties properties = new Properties();
		properties.put("text.today", "Today");
		properties.put("text.month", "Month");
		properties.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(modelDatePicker, properties);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		datePicker.setBounds(495, 30, 150, 25);
		frmMatchMaker.getContentPane().add(datePicker);

		JLabel lblImage = new JLabel("Image:");
		lblImage.setBounds(30, 69, 54, 14);
		frmMatchMaker.getContentPane().add(lblImage);

		textFieldPicText = new JTextField();
		textFieldPicText.setBounds(85, 66, 273, 20);
		frmMatchMaker.getContentPane().add(textFieldPicText);
		textFieldPicText.setColumns(10);

		JLabel lblHobbies = new JLabel("Hobbies");
		lblHobbies.setHorizontalAlignment(SwingConstants.CENTER);
		lblHobbies.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblHobbies.setBounds(301, 97, 65, 14);
		frmMatchMaker.getContentPane().add(lblHobbies);

		JButton btnNewButton = new JButton("Select Image");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.showOpenDialog(null);
				File f = chooser.getSelectedFile();
				String fileName = f.getAbsolutePath();
				textFieldPicText.setText(fileName);
			}
		});
		btnNewButton.setBounds(368, 65, 107, 23);
		frmMatchMaker.getContentPane().add(btnNewButton);

		JButton btnAddPerson = new JButton("Add Person");
		btnAddPerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					con = ConnectionSingleton.getConnection();
					java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
					java.sql.Date birthDate = new java.sql.Date(selectedDate.getTime());
					LocalDate dob = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate today = LocalDate.now();
					int age = Period.between(dob, today).getYears();
					String photoPath = textFieldPicText.getText();
					PreparedStatement ins_pstmt = con.prepareStatement(
							"INSERT INTO persons (first_name, last_name, birth_date, age, photo) VALUES (?, ?, ?, ?, ?)");
					ins_pstmt.setString(1, textFieldFirstName.getText());
					ins_pstmt.setString(2, textFieldLastName.getText());
					ins_pstmt.setDate(3, birthDate);
					ins_pstmt.setInt(4, age);
					ins_pstmt.setString(5, photoPath);
					ins_pstmt.executeUpdate();
					ins_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnAddPerson.setBounds(68, 391, 132, 23);
		frmMatchMaker.getContentPane().add(btnAddPerson);

		JButton btnUpdatePerson = new JButton("Update Person");
		btnUpdatePerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					con = ConnectionSingleton.getConnection();
					java.util.Date selectedDate = (java.util.Date) datePicker.getModel().getValue();
					java.sql.Date birthDate = new java.sql.Date(selectedDate.getTime());
					LocalDate dob = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate today = LocalDate.now();
					int age = Period.between(dob, today).getYears();
					String photoPath = textFieldPicText.getText();
					
					PreparedStatement upd_pstmt = con.prepareStatement("UPDATE Persons SET first_name = ?, last_name = ?, birth_date = ?, age = ?, photo = ? WHERE cod_person = ?");
					upd_pstmt.setString(1, textFieldFirstName.getText());
					upd_pstmt.setString(2, textFieldLastName.getText());
					upd_pstmt.setDate(3, birthDate);
					upd_pstmt.setInt(4, age);
					upd_pstmt.setString(5, photoPath);
					upd_pstmt.setInt(6, selectedPerson);
					upd_pstmt.executeUpdate();
					upd_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnUpdatePerson.setBounds(268, 391, 132, 23);
		frmMatchMaker.getContentPane().add(btnUpdatePerson);

		JButton btnDeletePerson = new JButton("Delete Person");
		btnDeletePerson.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent a) {
				try {
					con = ConnectionSingleton.getConnection();
					PreparedStatement del_pstmt = con.prepareStatement("DELETE FROM Persons WHERE cod_person = ?");
					del_pstmt.setInt(1, selectedPerson);
					del_pstmt.executeUpdate();
					del_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				}
			}
		});
		btnDeletePerson.setBounds(468, 391, 132, 23);
		frmMatchMaker.getContentPane().add(btnDeletePerson);

		tableModelPersons = new DefaultTableModel();
		tableModelPersons.addColumn("cod_person");
		tableModelPersons.addColumn("first_name");
		tableModelPersons.addColumn("last_name");
		tableModelPersons.addColumn("birth_date");
		tableModelPersons.addColumn("age");
		tableModelPersons.addColumn("photo");

		tablePersons = new JTable(tableModelPersons);
		tablePersons.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = tablePersons.getSelectedRow();
				selectedPerson = (int) tableModelPersons.getValueAt(i, 0);
				textFieldFirstName.setText(tableModelPersons.getValueAt(i, 1).toString());
				textFieldLastName.setText(tableModelPersons.getValueAt(i, 2).toString());
				if (tableModelPersons.getValueAt(i, 5) != null) {
					textFieldPicText.setText(tableModelPersons.getValueAt(i, 5).toString());
				}
				modelDatePicker.setValue((java.sql.Date) tableModelPersons.getValueAt(i, 3));
			}
		});
		tablePersons.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		scrollPanePersons = new JScrollPane(tablePersons);
		scrollPanePersons.setBounds(30, 448, 615, 200);
		frmMatchMaker.getContentPane().add(scrollPanePersons);

		try {
			con = ConnectionSingleton.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Persons ORDER BY cod_person");
			while (rs.next()) {
				Object[] row = new Object[6];
				row[0] = rs.getInt("cod_person");
				row[1] = rs.getString("first_name");
				row[2] = rs.getString("last_name");
				row[3] = rs.getDate("birth_date");
				row[4] = rs.getInt("age");
				row[5] = rs.getString("photo");
				tableModelPersons.addRow(row);
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}

		try {
			con = ConnectionSingleton.getConnection();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}
	}
}