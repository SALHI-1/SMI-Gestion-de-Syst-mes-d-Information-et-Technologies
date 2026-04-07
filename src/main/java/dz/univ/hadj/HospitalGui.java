package dz.univ.hadj;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HospitalGui extends JFrame {
    // Déclaration des labels pour chaque service
    private JLabel scannerLabel, diagnosticLabel, kineLabel, consultationLabel;

    public HospitalGui() {
        super("Moniteur des Ressources Hospitalières");
        
        // Configuration de la fenêtre : 4 lignes pour nos 4 services
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. SCANNER
        p.add(new JLabel("Scanners dispos :"));
        scannerLabel = createStyledLabel("5"); 
        p.add(scannerLabel);

        // 2. DIAGNOSTIC
        p.add(new JLabel("Salles Diagnostic :"));
        diagnosticLabel = createStyledLabel("10");
        p.add(diagnosticLabel);

        // 3. KINESITHERAPIE
        p.add(new JLabel("Postes Kiné :"));
        kineLabel = createStyledLabel("3");
        p.add(kineLabel);

        // 4. CONSULTATION
        p.add(new JLabel("Bureaux Consultation :"));
        consultationLabel = createStyledLabel("20");
        p.add(consultationLabel);

        getContentPane().add(p, BorderLayout.CENTER);
        
        setSize(450, 300); // Fenêtre un peu plus grande pour les 4 lignes
        setResizable(false);
        setLocationRelativeTo(null); // Centre l'interface au milieu de l'écran
        setVisible(true); 
    }

    // Petite méthode utilitaire pour éviter de répéter le code de style
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(0, 102, 204)); // Un bleu professionnel
        return label;
    }

    public void updateStock(String ressource, int quantite) {
        // Mise à jour dynamique selon le nom de la ressource reçue
        switch (ressource) {
            case "SCANNER":
                scannerLabel.setText("" + quantite);
                updateColor(scannerLabel, quantite);
                break;
            case "DIAGNOSTIC":
                diagnosticLabel.setText("" + quantite);
                updateColor(diagnosticLabel, quantite);
                break;
            case "KINESITHERAPIE":
                kineLabel.setText("" + quantite);
                updateColor(kineLabel, quantite);
                break;
            case "CONSULTATION":
                consultationLabel.setText("" + quantite);
                updateColor(consultationLabel, quantite);
                break;
        }
    }

    // Alerte visuelle : devient rouge s'il ne reste qu'une place ou moins
    private void updateColor(JLabel label, int qty) {
        if (qty <= 1) label.setForeground(Color.RED);
        else label.setForeground(new Color(0, 102, 204));
    }
}