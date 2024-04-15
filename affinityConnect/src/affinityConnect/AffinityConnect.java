package affinityConnect;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JLabel;
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

class ConnectionSingleton {
	private static Connection con;
	public static Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://127.0.0.1:5433/affinityconnect";
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

public class AffinityConnect {

	private JFrame frmAffinityConnect;
	private Connection con;
	private JTextField textFieldName;
	private JTextField textFieldLastName;
	private JTextField textFieldPicText;
	private JDatePickerImpl datePicker;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AffinityConnect window = new AffinityConnect();
					window.frmAffinityConnect.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AffinityConnect() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAffinityConnect = new JFrame();
		frmAffinityConnect.setTitle("Affinity Connect");
		frmAffinityConnect.setBounds(100, 100, 684, 776);
		frmAffinityConnect.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmAffinityConnect.getContentPane().setLayout(null);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(30, 33, 54, 14);
		frmAffinityConnect.getContentPane().add(lblName);
		
		textFieldName = new JTextField();
		textFieldName.setBounds(75, 30, 86, 20);
		frmAffinityConnect.getContentPane().add(textFieldName);
		textFieldName.setColumns(10);
		
		JLabel lblLastName = new JLabel("Last Name:");
		lblLastName.setBounds(182, 33, 76, 14);
		frmAffinityConnect.getContentPane().add(lblLastName);
		
		textFieldLastName = new JTextField();
		textFieldLastName.setBounds(250, 30, 149, 20);
		frmAffinityConnect.getContentPane().add(textFieldLastName);
		textFieldLastName.setColumns(10);
		
		JLabel lblDateOfBirth = new JLabel("Date of birth:");
		lblDateOfBirth.setBounds(409, 33, 76, 14);
		frmAffinityConnect.getContentPane().add(lblDateOfBirth);
		
		JLabel lblImage = new JLabel("Image:");
		lblImage.setBounds(30, 69, 54, 14);
		frmAffinityConnect.getContentPane().add(lblImage);
		
		textFieldPicText = new JTextField();
		textFieldPicText.setBounds(85, 66, 273, 20);
		frmAffinityConnect.getContentPane().add(textFieldPicText);
		textFieldPicText.setColumns(10);
		
		JButton btnAddPerson = new JButton("Add Person");
		btnAddPerson.setBounds(83, 476, 110, 23);
		frmAffinityConnect.getContentPane().add(btnAddPerson);
		
		JButton btnUpdatePerson = new JButton("Update Person");
		btnUpdatePerson.setBounds(276, 476, 110, 23);
		frmAffinityConnect.getContentPane().add(btnUpdatePerson);
		
		JButton btnDeletePerson = new JButton("Delete Person");
		btnDeletePerson.setBounds(469, 476, 116, 23);
		frmAffinityConnect.getContentPane().add(btnDeletePerson);
		
		JLabel lblHobbies = new JLabel("Hobbies");
		lblHobbies.setHorizontalAlignment(SwingConstants.CENTER);
		lblHobbies.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblHobbies.setBounds(301, 97, 65, 14);
		frmAffinityConnect.getContentPane().add(lblHobbies);
		
		JButton btnNewButton = new JButton("Select Image");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
		        chooser.showOpenDialog(null);
		        File f= chooser.getSelectedFile();
		        String fileName= f.getAbsolutePath();
		        textFieldPicText.setText(fileName);
			}
		});
		btnNewButton.setBounds(368, 65, 107, 23);
		frmAffinityConnect.getContentPane().add(btnNewButton);
		
		UtilDateModel model = new UtilDateModel();
		Properties properties = new Properties();
		properties.put("text.today", "Today");
        properties.put("text.month", "Month");
        properties.put("text.year", "Year");	
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        datePicker.setBounds(495, 30, 150, 25);
        frmAffinityConnect.getContentPane().add(datePicker);
        
		try {
			con = ConnectionSingleton.getConnection();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}
	}
}