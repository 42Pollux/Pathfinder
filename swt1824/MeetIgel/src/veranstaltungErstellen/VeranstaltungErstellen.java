package veranstaltungErstellen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.server.UID;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.swing.JFrame;

import startseite.BevorstehendeVeranstaltungen;

public class VeranstaltungErstellen extends javax.swing.JPanel {

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

	// Variablendeklaration für Panelkomponenten 
	private javax.swing.JButton jButtonWeiter;
	private javax.swing.JButton jButtonZurueck;
	private javax.swing.JLabel jTitel;
	private javax.swing.JLabel jBeschreibung;
	private javax.swing.JLabel jVeranstaltungErstellen;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTextArea jTextAreaBeschreibung;
	private javax.swing.JTextField jTextField1;

	// neue Eingabemaske
	public VeranstaltungErstellen(JFrame frame, final String username) {
		initComponents(frame);

		// Aktionen beim Klicken des Weiter-Buttons
		jButtonWeiter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				// Falls der Text im Titel oder die Beschreibung zu viele Zeichen beinhalten,
				// erscheint ein Dialog
				String titelZuLang = jTextField1.getText();
				String beschreibungZuLang = jTextAreaBeschreibung.getText();
				if (titelZuLang.length() > 40) {
					TitelZuLang tzL = new TitelZuLang(frame, titelZuLang);
					tzL.setModal(true);
					tzL.setLocationRelativeTo(frame);
					tzL.setTitle("Zu langer Text");
					tzL.setVisible(true);

				} else if (beschreibungZuLang.length() > 200) {
					BeschreibungZuLang bzL = new BeschreibungZuLang(frame, beschreibungZuLang);
					bzL.setModal(true);
					bzL.setLocationRelativeTo(frame);
					bzL.setTitle("Zu langer Text");
					bzL.setVisible(true);
				} else {

					// erstellt neue Veranstaltung
					Veranstaltung veran = new Veranstaltung();
					veran.setTitel(jTextField1.getText());
					if (veran.getTitel().equals(""))
						veran.setTitel("Kein Titel");
					veran.setBeschreibung(jTextAreaBeschreibung.getText());

					// Erstellen eines zufälligen Veranstaltungspassworts
					Random rand = new Random();
					veran.setvPasswort(String.format("%08d", rand.nextInt(100000000)));

					try (Connection conn = connect()) {
						// Anzahl vorhandener Veranstaltungen auslesen
						String sql = "SELECT count(*) from Veranstaltung";
						Statement st = conn.createStatement();
						ResultSet rs = st.executeQuery(sql);
						rs.next();
						// Veranstaltungs-ID generieren
						if (rs.getInt(1) != 0) {
							String sql1 = "SELECT max(id) from Veranstaltung";
							Statement st1 = conn.createStatement();
							ResultSet rs1 = st1.executeQuery(sql1);
							rs1.next();
							int nr = rs1.getInt(1);
							veran.setId(nr + 1);
						} else {
							veran.setId(1);
						}
					} catch (SQLException se) {
						se.printStackTrace();
					}

					// Weiterleitung zu VeranstaltungErstellenTermine
					frame.getContentPane().removeAll();
					frame.getContentPane().add(new VeranstaltungErstellenTermine(frame, veran, username));
					frame.setTitle("Veranstaltung Erstellen: Termine");
					frame.pack();
				}
			}
		});
		add(jButtonWeiter);
		
		
		jButtonZurueck.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.getContentPane().removeAll();
				frame.getContentPane().add(new BevorstehendeVeranstaltungen(frame, username));
				frame.setTitle("Startseite");
				frame.pack();
			}
		});
		add(jButtonZurueck);
	}

	// Initialisieren der Eingabemaske
	private void initComponents(JFrame frame) {

		jButtonWeiter = new javax.swing.JButton();
		jButtonZurueck = new javax.swing.JButton();
		jTitel = new javax.swing.JLabel();
		jTextField1 = new javax.swing.JTextField();
		jBeschreibung = new javax.swing.JLabel();
		jScrollPane1 = new javax.swing.JScrollPane();
		jTextAreaBeschreibung = new javax.swing.JTextArea();
		jVeranstaltungErstellen = new javax.swing.JLabel();

		// Festlegen der Hintergrundfarbe und Größe des Panels
		setBackground(new java.awt.Color(246, 250, 252));
		setPreferredSize(new java.awt.Dimension(691, 559));

		// Buttons Text und Schriftart
		jButtonWeiter.setFont(new java.awt.Font("Tahoma", 1, 16));
		jButtonWeiter.setText("Weiter");
		jButtonZurueck.setFont(new java.awt.Font("Tahoma", 1, 16));
		jButtonZurueck.setText("Zurück");
		
		// Text der Labels und Schriftart
		jVeranstaltungErstellen.setFont(new java.awt.Font("Tahoma", 1, 18));
		jVeranstaltungErstellen.setText("Veranstaltung erstellen");
		jTitel.setFont(new java.awt.Font("Tahoma", 1, 16));
		jTitel.setText("Titel");
		jTitel.setMaximumSize(new java.awt.Dimension(10, 20));
		jTitel.setMinimumSize(new java.awt.Dimension(10, 20));
		jBeschreibung.setFont(new java.awt.Font("Tahoma", 1, 16));
		jBeschreibung.setText("Beschreibung");

		// Textfeld für die Beschreibung
		jTextAreaBeschreibung.setColumns(20);
		jTextAreaBeschreibung.setRows(5);
		jScrollPane1.setViewportView(jTextAreaBeschreibung);
		
		//Weiter durch Entertaste auslösen
        frame.getRootPane().setDefaultButton(jButtonWeiter);

		// Hinzufügen der einzelnen Komponenten zum Panel
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        // Horizontale Ausrichtung der Elemente
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButtonZurueck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonWeiter)
                .addGap(40, 40, 40))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jVeranstaltungErstellen)
                .addGap(236, 236, 236))
            .addGroup(layout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jBeschreibung)
                    .addComponent(jTitel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(135, Short.MAX_VALUE))
        );
        // Vertikale Ausrichtung der Elemente 
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jVeranstaltungErstellen)
                .addGap(45, 45, 45)
                .addComponent(jTitel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jBeschreibung)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonWeiter)
                    .addComponent(jButtonZurueck))
                .addGap(48, 48, 48))
        );
    }
}
