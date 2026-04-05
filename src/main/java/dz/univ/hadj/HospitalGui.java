package dz.univ.hadj;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HospitalGui extends JFrame {
    private JLabel litLabel, scannerLabel;

    public HospitalGui() {
        super("Tableau de Bord - Hôpital");
        
        // Configuration de la fenêtre
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        p.add(new JLabel("Lits Urgence disponibles :"));
        litLabel = new JLabel("5"); // Valeur initiale
        litLabel.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(litLabel);

        p.add(new JLabel("Scanners disponibles :"));
        scannerLabel = new JLabel("2"); // Valeur initiale
        scannerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        p.add(scannerLabel);

        getContentPane().add(p, BorderLayout.CENTER);
        
        setSize(400, 200);
        setResizable(false);
        // Important pour que la fenêtre apparaisse !
        setVisible(true); 
    }

    public void updateStock(String ressource, int quantite) {
        if (ressource.equals("LIT_URGENCE")) {
            litLabel.setText("" + quantite);
        } else if (ressource.equals("SCANNER")) {
            scannerLabel.setText("" + quantite);
        }
    }
}