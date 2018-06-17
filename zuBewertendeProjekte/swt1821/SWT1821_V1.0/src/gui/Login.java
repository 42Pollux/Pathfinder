package gui;

import controlclasses.*;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.awt.event.ActionEvent;

public class Login extends JFrame {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;

  /**
   * Create the frame.
   */
  public Login() {
	  
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 320, 450);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setBackground(new Color(70, 130, 180));
    contentPane.setLayout(null);
    
    JLabel lblEmail = new JLabel("E-Mail");               //Label für E-Mail
    lblEmail.setHorizontalAlignment(SwingConstants.CENTER);
    lblEmail.setFont(new Font("Century", Font.PLAIN, 14));	
    lblEmail.setForeground(Color.WHITE);
    lblEmail.setBounds(0, 114, 302, 27);
    contentPane.add(lblEmail);
    
    JTextField email = new JTextField();                   //Eingabefeld für E-Mail
    email.setBounds(68, 140, 177, 33);
    contentPane.add(email);
    email.setColumns(10);
    
    JLabel lblLogin = new JLabel("Login");                //Label für Login
    lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
    lblLogin.setFont(new Font("Century", Font.PLAIN, 24));
    lblLogin.setForeground(Color.WHITE);
    lblLogin.setBounds(0, 45, 302, 40);
    contentPane.add(lblLogin);
    
    JLabel lblPasswort = new JLabel("Passwort");            //Label für Passwort
    lblPasswort.setHorizontalAlignment(SwingConstants.CENTER);
    lblPasswort.setFont(new Font("Century", Font.PLAIN, 14));
    lblPasswort.setForeground(Color.WHITE);
    lblPasswort.setBounds(0, 206, 302, 27);
    contentPane.add(lblPasswort);
    
    JPasswordField passwordField = new JPasswordField();               //Passwort-Eingabe-Feld
    passwordField.setBounds(68, 233, 177, 33);
    contentPane.add(passwordField);
    
    JButton btnBesttigen = new JButton("Best\u00E4tigen");       	//Button "Bestätigen"
    btnBesttigen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	loginMessage  loginM = new loginMessage(email.getText(), passwordField.getName());	
    	Client user = new Client();										// Client wird bei Login angelegt

    	
    	try {
			user.clientSocket = new Socket("127.0.0.1",3306);
			ObjectOutputStream out = (ObjectOutputStream) user.clientSocket.getOutputStream();
			InputStream datain = new ObjectInputStream(user.clientSocket.getInputStream());
		
			while(datain.available()>0) {
				out.writeObject((MethodeMessage) loginM);
				out.flush();
				
			}
			
			if(datain.available()==0) {
				
				user.setEmail(email.getText());							
				user.setPassword(passwordField.getPassword());
			}
			datain.close();
			
		} catch(UnknownHostException e1){
			
			System.out.println("unknown user");
			System.exit(1);
			
		}catch (IOException e2) {
			System.out.println("No I/O");
			System.exit(1);
		}
    	
        Auswahlbildschirm auswahlbild = new Auswahlbildschirm(user);  	//Bestätigen-Button öffnet Auswahlbildschirm
        auswahlbild.setVisible(true);
      
      }
    });
    btnBesttigen.setBounds(95, 307, 123, 33);
    contentPane.add(btnBesttigen);
    btnBesttigen.addActionListener(new ActionListener() {
		
		public void actionPerformed(ActionEvent arg0) {				
			
			try{
				Class.forName("com.mysql.jdbc.Driver");	 
		    }catch (ClassNotFoundException e){
				System.err.println( "Keine Treiber-Klasse!" );
				return;
		    }
			
		    finally{	  
		    	Connection con = null;
		    	try {
					con =  DriverManager.getConnection( "jdbc:mysql://139.30.96.135/swt1821DB?user=root&password=root");
				} catch (SQLException e) {
					e.printStackTrace();
				} 
		    	Statement stmt = null;
				try {
					stmt = con.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	
		    	String queryGruppe = "SELECT email, passwort FROM Nutzer WHERE email=\""+email.getText()+"\";";
		    	ResultSet rsGruppe = null;
				try {
					usercontrol uc = new usercontrol();
					rsGruppe = stmt.executeQuery( queryGruppe );
					if(passwordField.getPassword()==rsGruppe.getString(2).toCharArray())
					{		Auswahlbildschirm lobby = new Auswahlbildschirm( uc.findClient(rsGruppe.getString(1)));
							lobby.setVisible(true);										
							
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
		    	

		    }
		  
		    
		}
    });
    
    JButton btnNewButton = new JButton("<");           //Button "Zurück"
    btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 11));
    btnNewButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        VisualClass Startbildschirm = new VisualClass();      //durch Drücken von zurück gelangt man wieder zum Startbildschirm
        Startbildschirm.setVisible(true);
        
      }
    });
    btnNewButton.setBounds(10, 11, 40, 40);
    contentPane.add(btnNewButton);
 }

public JTextField getTextField() {
	return textField;
}

public void setTextField(JTextField textField) {
	this.textField = textField;
}

public JPasswordField getPasswordField() {
	return passwordField;
}

public void setPasswordField(JPasswordField passwordField) {
	this.passwordField = passwordField;
}
}
