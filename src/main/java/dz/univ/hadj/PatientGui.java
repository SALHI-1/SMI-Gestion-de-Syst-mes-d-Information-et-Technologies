package dz.univ.hadj;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox; // Ajouté
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class PatientGui extends JFrame {
    private PatientAgent myAgent;
    private JTextField nameField, resourceField;
    private JComboBox<String> priorityBox; // Ajouté
    private JTextArea logArea;
    public PatientGui(PatientAgent a) {
        super("Portail Patient - Collecte de Données");
        myAgent = a;

        // Configuration de la fenêtre - On passe à 4 lignes
        JPanel p = new JPanel();
        p.setLayout(new GridLayout(4, 2, 5, 5)); 
        
        p.add(new JLabel("Nom du Patient:"));
        nameField = new JTextField(15);
        p.add(nameField);
        
        p.add(new JLabel("Besoin (LIT_URGENCE/SCANNER):"));
        resourceField = new JTextField(15);
        p.add(resourceField);

        // Ajout du menu déroulant pour la priorité
        p.add(new JLabel("Priorité:"));
        String[] priorities = { "NORMALE", "URGENTE" };
        priorityBox = new JComboBox<>(priorities);
        p.add(priorityBox);

        logArea = new JTextArea(10, 30);
        logArea.setEditable(false); // L'utilisateur ne peut pas écrire dedans
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN); // Style "Console"
        JScrollPane scrollPane = new JScrollPane(logArea);

        getContentPane().add(scrollPane, BorderLayout.EAST); // On l'ajoute à droite ou en bas

        JButton addButton = new JButton("Envoyer la demande");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                String name = nameField.getText().trim();
                String resource = resourceField.getText().trim();
                String priority = (String) priorityBox.getSelectedItem(); // Récupération de la priorité

                // SÉCURITÉ : Empêcher l'envoi de données vides
                if (name.isEmpty() || resource.isEmpty()) {
                    JOptionPane.showMessageDialog(PatientGui.this, "Veuillez remplir tous les champs !");
                    return;
                }

                // Appel de la méthode avec 3 arguments
                myAgent.envoyerDemande(name, resource, priority);
                
                nameField.setText("");
                resourceField.setText("");
            }
        });
        
        getContentPane().add(p, BorderLayout.CENTER);
        getContentPane().add(addButton, BorderLayout.SOUTH);
        
        setResizable(false);
    }
public void log(String message) {
    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    logArea.append("[" + time + "] " + message + "\n");
    logArea.setCaretPosition(logArea.getDocument().getLength());
}

    public void showGui() {
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int)screenSize.getWidth() / 2;
        int centerY = (int)screenSize.getHeight() / 2;
        setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
        setVisible(true);
    }
}