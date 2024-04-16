package matchMaker;

import java.awt.EventQueue;
import java.sql.Blob;
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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;

import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JCheckBox;
import javax.swing.JTextArea;

class ConnectionSingleton {
	private static Connection con;

	public static Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://127.0.0.1:3307/matchmaker";
		String user = "alumno";
		String password = "alumno";
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
	private JLabel imageLabel;

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

	public static void insertHobbies(int personId, int hobbyId) throws SQLException {
		PreparedStatement insHobby_pstmt = con
				.prepareStatement("INSERT INTO Persons_Hobbies (cod_person, cod_hobby) VALUES (?, ?)");
		insHobby_pstmt.setInt(1, personId);
		insHobby_pstmt.setInt(2, hobbyId);
		insHobby_pstmt.executeUpdate();
		insHobby_pstmt.close();
	}

	public static void updateHobbies(int personId, int hobbyId) throws SQLException {
		PreparedStatement delete_stmt = con.prepareStatement("DELETE FROM Persons_Hobbies WHERE cod_person = ?");
		delete_stmt.setInt(1, personId);
		delete_stmt.executeUpdate();
		delete_stmt.close();
		insertHobbies(personId, hobbyId);
	}

	private void displayImage(int selectedPerson) {
		try {
			con = ConnectionSingleton.getConnection();
			PreparedStatement pstmt = con.prepareStatement("SELECT photo FROM Persons WHERE cod_person = ?");
			pstmt.setInt(1, selectedPerson);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				Blob blob = rs.getBlob("photo");
				if (blob != null) {
					int blobLength = (int) blob.length();
					byte[] bytes = blob.getBytes(1, blobLength);
					ImageIcon imageIcon = new ImageIcon(bytes);
					Image image = imageIcon.getImage();
					Image scaledImage = image.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(),
							Image.SCALE_SMOOTH);
					imageIcon = new ImageIcon(scaledImage);
					imageLabel.setIcon(imageIcon);
				}
			}
			pstmt.close();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
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
		frmMatchMaker.setBounds(100, 100, 825, 446);
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
		lblDateOfBirth.setBounds(409, 33, 95, 14);
		frmMatchMaker.getContentPane().add(lblDateOfBirth);

		modelDatePicker = new UtilDateModel();
		Properties properties = new Properties();
		properties.put("text.today", "Today");
		properties.put("text.month", "Month");
		properties.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(modelDatePicker, properties);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		datePicker.setBounds(522, 25, 150, 25);
		frmMatchMaker.getContentPane().add(datePicker);

		JLabel lblPic = new JLabel("Image:");
		lblPic.setBounds(30, 69, 54, 14);
		frmMatchMaker.getContentPane().add(lblPic);

		textFieldPicText = new JTextField();
		textFieldPicText.setEditable(false);
		textFieldPicText.setBounds(85, 66, 273, 20);
		frmMatchMaker.getContentPane().add(textFieldPicText);
		textFieldPicText.setColumns(10);

		JLabel lblHobbies = new JLabel("Hobbies");
		lblHobbies.setHorizontalAlignment(SwingConstants.CENTER);
		lblHobbies.setFont(new Font("Dialog", Font.BOLD, 12));
		lblHobbies.setBounds(30, 106, 65, 14);
		frmMatchMaker.getContentPane().add(lblHobbies);

		JCheckBox chckbxSports = new JCheckBox("Sports");
		chckbxSports.setBounds(30, 128, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxSports);

		JCheckBox chckbxMusic = new JCheckBox("Music");
		chckbxMusic.setBounds(30, 155, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxMusic);

		JCheckBox chckbxReading = new JCheckBox("Reading");
		chckbxReading.setBounds(30, 182, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxReading);

		JCheckBox chckbxTraveling = new JCheckBox("Traveling");
		chckbxTraveling.setBounds(30, 209, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxTraveling);

		JCheckBox chckbxCooking = new JCheckBox("Cooking");
		chckbxCooking.setBounds(30, 236, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxCooking);

		JCheckBox chckbxMovies = new JCheckBox("Movies");
		chckbxMovies.setBounds(30, 263, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxMovies);

		JCheckBox chckbxArt = new JCheckBox("Art");
		chckbxArt.setBounds(30, 290, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxArt);

		JCheckBox chckbxGames = new JCheckBox("Games");
		chckbxGames.setBounds(30, 317, 129, 23);
		frmMatchMaker.getContentPane().add(chckbxGames);

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
		btnNewButton.setBounds(368, 65, 136, 23);
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

					File imageFile = null;
					FileInputStream fis = null;
					if (!photoPath.isEmpty()) {
						imageFile = new File(photoPath);
						fis = new FileInputStream(imageFile);
					}

					PreparedStatement ins_pstmt = con.prepareStatement(
							"INSERT INTO Persons (first_name, last_name, birth_date, age, photo) VALUES (?, ?, ?, ?, ?)",
							Statement.RETURN_GENERATED_KEYS);
					ins_pstmt.setString(1, textFieldFirstName.getText());
					ins_pstmt.setString(2, textFieldLastName.getText());
					ins_pstmt.setDate(3, birthDate);
					ins_pstmt.setInt(4, age);
					if (imageFile != null) {
						ins_pstmt.setBlob(5, fis, (int) imageFile.length());
					} else {
						ins_pstmt.setNull(5, java.sql.Types.BLOB);
					}
					ins_pstmt.executeUpdate();

					ResultSet generatedKeys = ins_pstmt.getGeneratedKeys();
					int insertedPersonId = -1;
					if (generatedKeys.next()) {
						insertedPersonId = generatedKeys.getInt(1);
					}

					if (insertedPersonId != -1) {
						JCheckBox[] hobbyCheckBoxes = { chckbxSports, chckbxMusic, chckbxReading, chckbxTraveling,
								chckbxCooking, chckbxMovies, chckbxArt, chckbxGames };
						for (int i = 0; i < hobbyCheckBoxes.length; i++) {
							JCheckBox checkBox = hobbyCheckBoxes[i];
							if (checkBox.isSelected()) {
								insertHobbies(insertedPersonId, (i + 1));
							}
						}
					}
					ins_pstmt.close();
					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		btnAddPerson.setBounds(56, 357, 149, 23);
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

					PreparedStatement upd_pstmt = con.prepareStatement(
							"UPDATE Persons SET first_name = ?, last_name = ?, birth_date = ?, age = ?, photo = ? WHERE cod_person = ?");
					upd_pstmt.setString(1, textFieldFirstName.getText());
					upd_pstmt.setString(2, textFieldLastName.getText());
					upd_pstmt.setDate(3, birthDate);
					upd_pstmt.setInt(4, age);
					if (!photoPath.isEmpty()) {
					    File imageFile = new File(photoPath);
					    FileInputStream fis = new FileInputStream(imageFile);
					    upd_pstmt.setBlob(5, fis, (int) imageFile.length());
					} else {
					    upd_pstmt.setNull(5, java.sql.Types.BLOB);
					}
					upd_pstmt.setInt(6, selectedPerson);
					upd_pstmt.executeUpdate();
					upd_pstmt.close();
					upd_pstmt.setInt(6, selectedPerson);
					upd_pstmt.executeUpdate();
					upd_pstmt.close();

					JCheckBox[] hobbyCheckBoxes = { chckbxSports, chckbxMusic, chckbxReading, chckbxTraveling,
							chckbxCooking, chckbxMovies, chckbxArt, chckbxGames };
					for (int i = 0; i < hobbyCheckBoxes.length; i++) {
						JCheckBox checkBox = hobbyCheckBoxes[i];
						if (checkBox.isSelected()) {
							insertHobbies(selectedPerson, (i + 1));
						}
					}

					refresh();
				} catch (SQLException e) {
					System.err.println(e.getMessage());
					e.getErrorCode();
					e.printStackTrace();
				} catch (FileNotFoundException fileNotFound) {
					
				}
			}
		});
		btnUpdatePerson.setBounds(261, 357, 149, 23);
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
		btnDeletePerson.setBounds(466, 357, 149, 23);
		frmMatchMaker.getContentPane().add(btnDeletePerson);

		tableModelPersons = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		tableModelPersons.addColumn("cod_person");
		tableModelPersons.addColumn("first_name");
		tableModelPersons.addColumn("last_name");
		tableModelPersons.addColumn("birth_date");
		tableModelPersons.addColumn("age");

		tablePersons = new JTable(tableModelPersons);
		tablePersons.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int i = tablePersons.getSelectedRow();
				selectedPerson = (int) tableModelPersons.getValueAt(i, 0);
				textFieldFirstName.setText(tableModelPersons.getValueAt(i, 1).toString());
				textFieldLastName.setText(tableModelPersons.getValueAt(i, 2).toString());
				modelDatePicker.setValue((java.sql.Date) tableModelPersons.getValueAt(i, 3));
				displayImage(selectedPerson);
			}
		});
		tablePersons.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		scrollPanePersons = new JScrollPane(tablePersons);
		scrollPanePersons.setBounds(182, 128, 463, 204);
		frmMatchMaker.getContentPane().add(scrollPanePersons);

		JLabel lblPersons = new JLabel("Persons");
		lblPersons.setHorizontalAlignment(SwingConstants.CENTER);
		lblPersons.setBounds(378, 106, 70, 15);
		frmMatchMaker.getContentPane().add(lblPersons);

		JLabel lblMatches = new JLabel("Matches");
		lblMatches.setHorizontalAlignment(SwingConstants.CENTER);
		lblMatches.setBounds(700, 240, 70, 15);
		frmMatchMaker.getContentPane().add(lblMatches);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(665, 267, 138, 114);
		frmMatchMaker.getContentPane().add(textArea);

		imageLabel = new JLabel("");
		imageLabel.setBounds(663, 106, 140, 125);
		frmMatchMaker.getContentPane().add(imageLabel);

		try {
			con = ConnectionSingleton.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT cod_person, first_name, last_name, birth_date, age FROM Persons ORDER BY cod_person");
			while (rs.next()) {
				Object[] row = new Object[5];
				row[0] = rs.getInt("cod_person");
				row[1] = rs.getString("first_name");
				row[2] = rs.getString("last_name");
				row[3] = rs.getDate("birth_date");
				row[4] = rs.getInt("age");
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