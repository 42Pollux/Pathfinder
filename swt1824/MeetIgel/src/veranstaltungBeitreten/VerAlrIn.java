package veranstaltungBeitreten;


import javax.swing.JDialog;
import javax.swing.JFrame;

import veranstaltungErstellen.Einladungen;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VerAlrIn extends JDialog {

	// Variablendeklaration 
	private javax.swing.JLabel txtVeranstaltungBeigetreten;
	private javax.swing.JButton okButton;

	// neuer Dialog 
	public VerAlrIn(JFrame frame) {
		super(frame);
		initComponents();
		
		//Aufrufen der Methode dispose beim Klicken des nochmal Buttons  
		// zurückkehren zum Parent Frame	
		okButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	// Initialisierung des Dialogs
    private void initComponents() {

        txtVeranstaltungBeigetreten = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();

        //Schließen und Hintergrundfarbe des Dialogs
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new java.awt.Color(246, 250, 252));

        //Text und Schrift des JLabels
        txtVeranstaltungBeigetreten.setFont(new java.awt.Font("Tahoma", Font.PLAIN, 16)); 
        txtVeranstaltungBeigetreten.setText("Du bist dieser Veranstaltung bereits beigetreten oder hast abgesagt.");

        //Text und Schrift des Buttons
        okButton.setFont(new java.awt.Font("Tahoma", Font.BOLD, 14)); 
        okButton.setText("O.K.");

        //Platzierung der Komponenten im JDialog 
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane()); 
        // Festlegen des Layouts (GroupLayout) für den Dialog 
        getContentPane().setLayout(layout); 
        layout.setHorizontalGroup( //Horizontale Ausrichtung der Komponenten
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(txtVeranstaltungBeigetreten)) // Hinzufügen des Texts
                    .addGroup(layout.createSequentialGroup()
                        .addGap(195, 195, 195)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))) 
                		// Hinzufügen des Buttons 
                .addContainerGap(55, Short.MAX_VALUE))
        );
        layout.setVerticalGroup( // Vertikale Ausrichtung der Komponenten
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(txtVeranstaltungBeigetreten) // Hinzufügen des Texts
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addComponent(okButton) // Hinzufügen des Buttons 
                .addGap(33, 33, 33))
        );

        pack();
    }
    
}