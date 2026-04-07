package dz.univ.hadj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class HospitalGui extends JFrame {
    private JLabel scannerLabel, diagnosticLabel, kineLabel, consultationLabel;
    private JLabel scannerStatus, diagnosticStatus, kineStatus, consultationStatus;

    public HospitalGui() {
        super("Moniteur Temps Réel - Système Hospitalier");
        
        // Grille de 5 lignes (1 header + 4 services) et 3 colonnes
        JPanel p = new JPanel(new GridLayout(5, 3, 15, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- EN-TÊTES ---
        p.add(createHeaderLabel("SERVICE"));
        p.add(createHeaderLabel("STOCK"));
        p.add(createHeaderLabel("ÉTAT TECHNIQUE"));

        // 1. SCANNER
        p.add(new JLabel("Scanner :"));
        scannerLabel = createStyledLabel("5");
        scannerStatus = createStatusLabel("OPÉRATIONNEL");
        p.add(scannerLabel);
        p.add(scannerStatus);

        // 2. DIAGNOSTIC
        p.add(new JLabel("Salles Diagnostic :"));
        diagnosticLabel = createStyledLabel("10");
        diagnosticStatus = createStatusLabel("OPÉRATIONNEL");
        p.add(diagnosticLabel);
        p.add(diagnosticStatus);

        // 3. KINESITHERAPIE
        p.add(new JLabel("Postes Kiné :"));
        kineLabel = createStyledLabel("3");
        kineStatus = createStatusLabel("OPÉRATIONNEL");
        p.add(kineLabel);
        p.add(kineStatus);

        // 4. CONSULTATION
        p.add(new JLabel("Consultation :"));
        consultationLabel = createStyledLabel("20");
        consultationStatus = createStatusLabel("OPÉRATIONNEL");
        p.add(consultationLabel);
        p.add(consultationStatus);

        getContentPane().add(p, BorderLayout.CENTER);
        
        setSize(600, 350); // Plus large pour les 3 colonnes
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true); 
    }

    private JLabel createHeaderLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setForeground(Color.GRAY);
        return l;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(0, 102, 204));
        return label;
    }

    private JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.ITALIC, 11));
        label.setForeground(new Color(46, 204, 113)); // Vert par défaut
        return label;
    }

    // --- MISE À JOUR DU STOCK ---
    public void updateStock(String ressource, int quantite) {
        switch (ressource) {
            case "SCANNER": scannerLabel.setText(""+quantite); break;
            case "DIAGNOSTIC": diagnosticLabel.setText(""+quantite); break;
            case "KINESITHERAPIE": kineLabel.setText(""+quantite); break;
            case "CONSULTATION": consultationLabel.setText(""+quantite); break;
        }
    }

    // --- NOUVELLE MÉTHODE : MISE À JOUR DE L'ÉTAT ---
    public void updateServiceStatus(String ressource, String status) {
        JLabel target = null;
        switch (ressource) {
            case "SCANNER": target = scannerStatus; break;
            case "DIAGNOSTIC": target = diagnosticStatus; break;
            case "KINESITHERAPIE": target = kineStatus; break;
            case "CONSULTATION": target = consultationStatus; break;
        }

        if (target != null) {
            target.setText(status.toUpperCase());
            if (status.equalsIgnoreCase("OUI") || status.equalsIgnoreCase("OPÉRATIONNEL")) {
                target.setText("OPÉRATIONNEL");
                target.setForeground(new Color(46, 204, 113)); // Vert
            } else {
                target.setText("EN PANNE / INDISPO");
                target.setForeground(Color.RED); // Rouge
            }
        }
    }
}