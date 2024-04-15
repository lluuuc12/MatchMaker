package affinityConnect;

import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Font;

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

public class AffinityConnect {

	private JFrame frmAffinityConnect;
	private Connection con;
	private JTextField textFieldName;
	private JTextField textFieldLastName;
	private JTextField textField;

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
		lblName.setBounds(50, 50, 54, 14);
		frmAffinityConnect.getContentPane().add(lblName);
		
		textFieldName = new JTextField();
		textFieldName.setBounds(95, 47, 86, 20);
		frmAffinityConnect.getContentPane().add(textFieldName);
		textFieldName.setColumns(10);
		
		JLabel lblLastName = new JLabel("Last Name:");
		lblLastName.setBounds(202, 50, 76, 14);
		frmAffinityConnect.getContentPane().add(lblLastName);
		
		textFieldLastName = new JTextField();
		textFieldLastName.setBounds(270, 47, 149, 20);
		frmAffinityConnect.getContentPane().add(textFieldLastName);
		textFieldLastName.setColumns(10);
		
		JLabel lblDateOfBirth = new JLabel("Date of birth:");
		lblDateOfBirth.setBounds(429, 50, 76, 14);
		frmAffinityConnect.getContentPane().add(lblDateOfBirth);
		
		JLabel lblImage = new JLabel("Select Image:");
		lblImage.setBounds(50, 86, 86, 14);
		frmAffinityConnect.getContentPane().add(lblImage);
		
		textField = new JTextField();
		textField.setBounds(146, 83, 273, 20);
		frmAffinityConnect.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton btnAddPerson = new JButton("Add Person");
		btnAddPerson.setBounds(83, 130, 110, 23);
		frmAffinityConnect.getContentPane().add(btnAddPerson);
		
		JButton btnUpdatePerson = new JButton("Update Person");
		btnUpdatePerson.setBounds(276, 130, 110, 23);
		frmAffinityConnect.getContentPane().add(btnUpdatePerson);
		
		JButton btnDeletePerson = new JButton("Delete Person");
		btnDeletePerson.setBounds(469, 130, 116, 23);
		frmAffinityConnect.getContentPane().add(btnDeletePerson);
		
		JLabel lblHobbies = new JLabel("Hobbies");
		lblHobbies.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblHobbies.setBounds(311, 203, 46, 14);
		frmAffinityConnect.getContentPane().add(lblHobbies);
		
		try {
			con = ConnectionSingleton.getConnection();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
			e.getErrorCode();
			e.printStackTrace();
		}
	}

}
