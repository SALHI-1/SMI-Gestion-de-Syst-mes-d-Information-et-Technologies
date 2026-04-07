package dz.univ.hadj;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HospitalAgent extends Agent {
    private HashMap<String, Integer> resources = new HashMap<>();
    private HospitalGui myGui;

    // Variables de session pour la requête en cours
    private ACLMessage currentPatientMsg = null;
    private boolean staffResponseReceived = false;
    private boolean equipResponseReceived = false;
    private boolean staffStatus = false;
    private boolean equipStatus = false;

protected void setup() {
    resources.put("SCANNER", 5);
    resources.put("DIAGNOSTIC", 10);
    resources.put("KINESITHERAPIE", 3);
    resources.put("CONSULTATION", 20);

    myGui = new HospitalGui();
    System.out.println("[Hôpital] Initialisation du tableau de bord...");

    // --- NOUVEAU : On vérifie l'état AVANT l'arrivée des patients ---
    envoyerRequete("Equip", "INITIAL_CHECK");
    envoyerRequete("Staff", "INITIAL_CHECK");

    addBehaviour(new CyclicBehaviour() {
        public void action() {
            ACLMessage msg = receive();
            if (msg != null) {
                if (msg.getPerformative() == ACLMessage.REQUEST) {
                    lancerVerificationDynamique(msg);
                } 
                else if (msg.getPerformative() == ACLMessage.INFORM) {
                    collecterReponseExpert(msg);
                }
            } else {
                block();
            }
        }
    });
}

private void lancerVerificationDynamique(ACLMessage msg) {
    this.currentPatientMsg = msg;
    String besoin = msg.getContent().split(":")[1];
    
    // On réinitialise les drapeaux
    this.staffResponseReceived = false;
    this.equipResponseReceived = false;

    // --- LOGIQUE SÉLECTIVE ---
    if (besoin.equals("SCANNER") || besoin.equals("DIAGNOSTIC")) {
        // Besoins Mixtes : On interroge les deux
        envoyerRequete("Equip", "CHECK:" + besoin);
        envoyerRequete("Staff", "CHECK:STAFF");
    } 
    else {
        // Besoins RH uniquement : On n'interroge QUE le Staff
        envoyerRequete("Staff", "CHECK:STAFF");
        this.equipResponseReceived = true; // On simule que l'équipement est OK par défaut
        this.equipStatus = true;
    }
}

// Petite méthode utilitaire pour éviter de répéter le code send()
private void envoyerRequete(String agentName, String content) {
    ACLMessage request = new ACLMessage(ACLMessage.QUERY_IF);
    request.addReceiver(new AID(agentName, AID.ISLOCALNAME));
    request.setContent(content);
    send(request);
}

private void collecterReponseExpert(ACLMessage msg) {
    String sender = msg.getSender().getLocalName();
    String content = msg.getContent();

    if (sender.equals("Equip")) {
        equipStatus = content.equalsIgnoreCase("OUI");
        equipResponseReceived = true;
        // Met à jour le scanner
        myGui.updateServiceStatus("SCANNER", content);
    } 
    else if (sender.equals("Staff")) {
        staffStatus = content.equalsIgnoreCase("OUI");
        staffResponseReceived = true;
        // Met à jour les services RH
        myGui.updateServiceStatus("CONSULTATION", content);
        myGui.updateServiceStatus("KINESITHERAPIE", content);
    }

    // --- CORRECTION ICI : Mise à jour logique du Diagnostic ---
    // Le diagnostic n'est "OUI" (vert) que si les DEUX sont OK
    if (equipStatus && staffStatus) {
        myGui.updateServiceStatus("DIAGNOSTIC", "OUI");
    } else {
        myGui.updateServiceStatus("DIAGNOSTIC", "NON");
    }

    // Condition pour finaliser la décision si un patient attend
    if (staffResponseReceived && equipResponseReceived && currentPatientMsg != null) {
        finaliserDecisionDynamique();
    }
}

    private void finaliserDecisionDynamique() {
        String[] parts = currentPatientMsg.getContent().split(":");
        String patient = parts[0];
        String besoin = parts[1];
        String priorite = parts[2];

        int stock = resources.getOrDefault(besoin, 0);
        
        // --- LOGIQUE DE TRI INTELLIGENT ---
        boolean expertiseValide = false;
        if (besoin.equals("SCANNER") || besoin.equals("DIAGNOSTIC")) {
            expertiseValide = (equipStatus && staffStatus);
        } else {
            expertiseValide = staffStatus; // Consultation/Kiné n'ont besoin que du Staff
        }

        boolean stockDisponible = (stock > 1 || (priorite.equals("URGENTE") && stock > 0));

        ACLMessage reply = currentPatientMsg.createReply();
        if (expertiseValide && stockDisponible) {
            int nouveauStock = stock - 1;
            resources.put(besoin, nouveauStock);
            myGui.updateStock(besoin, nouveauStock);
            sauvegarderDansCSV(patient, besoin, priorite);
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("ADMIS : " + besoin + " réservé (Vérification temps réel OK).");
        } else {
            reply.setPerformative(ACLMessage.FAILURE);
            String raison = !expertiseValide ? "Avis technique/RH défavorable" : "Stock épuisé";
            reply.setContent("REFUS : " + raison);
        }
        
        send(reply);
        currentPatientMsg = null; // Reset pour le prochain
    }

    private void sauvegarderDansCSV(String patient, String service, String priorite) {
        try (FileWriter writer = new FileWriter("historique_hopital.csv", true)) {
            writer.write(new Date().toString() + "," + patient + "," + service + "," + priorite + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }
}