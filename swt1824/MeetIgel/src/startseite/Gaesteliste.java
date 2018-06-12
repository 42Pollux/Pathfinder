package startseite;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import veranstaltungErstellen.Veranstaltung;

public class Gaesteliste extends JDialog {

	// Variablendeklaration
	private Veranstaltung veran;
	private javax.swing.JLabel jGaesteliste;
	private javax.swing.JButton okButton;
	private JLabel[] txtZusagen;
	private int anzahl;
	private JScrollPane scrollpane;
	private JPanel scrollpanePanel;

	// Variablendeklaration für Connection
	private final String host = "meta.informatik.uni-rostock.de";
	private final String port = "5432";
	private final String database = "postgres";
	private final String user = "postgres";
	private final String password = "swat1824";

	private final String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;

	// Connection erstellen
	public Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}

	public Gaesteliste(JFrame frame, Veranstaltung veran, String username) {
		super(frame);
		this.veran = veran;

		initComponents();

	}

	// Initialisierung
	private void initComponents() {

		jGaesteliste = new javax.swing.JLabel();
		okButton = new javax.swing.JButton();
		txtZusagen = new JLabel[anzahl];
		scrollpanePanel = teilnehmerAuslesen();
		scrollpanePanel.setLayout(new GridLayout(0,1));
		scrollpanePanel.setBackground(new java.awt.Color(246, 250, 252));
		scrollpane = new JScrollPane(scrollpanePanel);
		scrollpane.setBackground(new java.awt.Color(246, 250, 252));
		
		// Schließen, Größe und Hintergrundfarbe des JDialogs
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(500, 400);
		getContentPane().setBackground(new java.awt.Color(246, 250, 252));

		// Text und Schrift der JLabels
		jGaesteliste.setFont(new java.awt.Font("Tahoma", 1, 17));
		jGaesteliste.setText("Gästeliste - " + veran.getTitel());

		// Text der Buttons
		okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14));
		okButton.setText("OK");

		// AKtion beim Klicken des OK-Buttons
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		// Platzierung der Komponenten im JDialog
		 javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	            	.addGap(74, 74, 74)
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                	.addGroup(layout.createSequentialGroup()   
	                		.addGap(16, 16, 16)
	    	                .addComponent(jGaesteliste))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(16, 16, 16)
	                        .addComponent(scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(layout.createSequentialGroup()
	                        .addGap(143, 143, 143)
	                        .addComponent(okButton)))  
	            .addContainerGap(90, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(jGaesteliste)
	                .addGap(18, 18, 18)
	                .addComponent(scrollpane, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(32, 32, 32)
	                .addComponent(okButton)
	                .addContainerGap(42, Short.MAX_VALUE))
	        );

	        pack();
	}

	public JPanel teilnehmerAuslesen() {
		// Teilnehmer der angeklickten Veranstaltung auslesen

		JPanel panel = new JPanel();
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = GridBagConstraints.RELATIVE;
		c.weightx = 1.0f;
		c.weighty = 0.0f;

		try (Connection conn = connect()) {
			String sql1 = "SELECT count(*) from besucht where vid = ? and status = 2";
			PreparedStatement st1 = conn.prepareStatement(sql1, Statement.RETURN_GENERATED_KEYS);
			st1.setInt(1, veran.getId());
			ResultSet rs1 = st1.executeQuery();
			rs1.next();
			anzahl = rs1.getInt(1);
			JLabel[] teilnehmer = new JLabel[anzahl];

			if (anzahl > 0) {
				String sql2 = "SELECT username from besucht where vid = ? and status = 2";
				PreparedStatement st2 = conn.prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
				st2.setInt(1, veran.getId());
				ResultSet rs2 = st2.executeQuery();
				rs2.next();
				teilnehmer[0]=new JLabel(rs2.getString(1));
				teilnehmer[0].setPreferredSize(new Dimension(10, 30));
				teilnehmer[0].setFont(new java.awt.Font("Tahoma", 1, 15));
				panel.add(teilnehmer[0], c);
				int index = 1;
				while (rs2.next()) {
					teilnehmer[index]= new JLabel(rs2.getString(1));
					teilnehmer[index].setPreferredSize(new Dimension(10, 30));
					teilnehmer[index].setFont(new java.awt.Font("Tahoma", 1, 15));
					panel.add(teilnehmer[index], c);
					index++;
				}
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return panel;
	}
}
