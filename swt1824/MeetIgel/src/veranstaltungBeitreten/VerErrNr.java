package veranstaltungBeitreten;

import javax.swing.JDialog;
import javax.swing.JFrame;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerErrNr extends JDialog {

	// Variablendeklaration 
	private javax.swing.JLabel txtKeineVeranstaltungsNr;
	private javax.swing.JButton okButton;

	// neuer Dialog
	public VerErrNr(JFrame frame) {
		super(frame);
		initComponents();
		
		// Aufrufen der Methode dispose beim Klicken des nochmal Buttons 
		// zurückkehren zum Parent Frame 
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	// Initialisieren des Dialogs
    private void initComponents() {

        txtKeineVeranstaltungsNr = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();

        // Schließen und Hintergrundfarbe des Dialogs
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new java.awt.Color(246, 250, 252));

        // Text und Schrift des JLabels
        txtKeineVeranstaltungsNr.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16)); 
        txtKeineVeranstaltungsNr.setText("Veranstaltungsnummer existiert nicht");

        // Text und Schrift des Buttons
        okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14)); 
        okButton.setText("O.K.");

        // Platzierung der Komponenten im JDialog 
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        // Horizontale Ausrichtung der Komponenten im Dialog
        layout.setHorizontalGroup( 
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(txtKeineVeranstaltungsNr))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(155, 155, 155)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(139, Short.MAX_VALUE))
        );
        // Vertikale Ausrichtung der Komponenten im Dialog
        layout.setVerticalGroup( 
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(txtKeineVeranstaltungsNr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(okButton)
                .addGap(33, 33, 33))
        );

        pack();
	}
}

