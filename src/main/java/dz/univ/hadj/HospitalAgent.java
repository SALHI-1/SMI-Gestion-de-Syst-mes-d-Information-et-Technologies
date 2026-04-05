package dz.univ.hadj;

import java.util.HashMap;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HospitalAgent extends Agent {
    // Simulation du STOCKAGE (Base de données locale)
    private HashMap<String, Integer> resources = new HashMap<>();
    private HospitalGui myGui;
    protected void setup() {
        // Initialisation des données
        resources.put("LIT_URGENCE", 5);
        resources.put("SCANNER", 2);
// 2. Création et affichage de l'interface graphique
        myGui = new HospitalGui();
        System.out.println("--- [Hôpital] Agent de gestion des ressources prêt ---");

        addBehaviour(new CyclicBehaviour() {
// Dans le CyclicBehaviour de HospitalAgent
public void action() {
    ACLMessage msg = receive();
    if (msg != null) {
        String content = msg.getContent();
        
        // SECURITÉ : On vérifie si le message est bien formaté "Nom:Ressource"
        if (content != null && content.contains(":")) {
            String[] parts = content.split(":");
            
// Dans le CyclicBehaviour de HospitalAgent
if (parts.length >= 3) {
    String patientName = parts[0];
    String resourceNeeded = parts[1];
    String priority = parts[2];

    ACLMessage reply = msg.createReply();

// 1. VÉRIFICATION : Est-ce que l'hôpital gère cette ressource ?
    if (!resources.containsKey(resourceNeeded)) {
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContent("ERREUR : La ressource '" + resourceNeeded + "' n'existe pas dans notre catalogue.");
    } 
    else {
        // 2. Si elle existe, on vérifie le stock et la priorité
        int dispo = resources.get(resourceNeeded);
        boolean accorde = false;

        if (priority.equals("URGENTE") && dispo > 0) {
            accorde = true;
        } else if (priority.equals("NORMALE") && dispo > 1) {
            accorde = true;
        }

        if (accorde) {
            int nouveauStock = dispo - 1;
            resources.put(resourceNeeded, nouveauStock);
            myGui.updateStock(resourceNeeded, nouveauStock);
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent("ADMIS : " + patientName + " (" + priority + ")");
        } else {
            // REFUS INTELLIGENT
            reply.setPerformative(ACLMessage.FAILURE);
            String raison = (dispo <= 1 && priority.equals("NORMALE")) ? 
                            "Réservé aux urgences" : "Stock épuisé";
            reply.setContent("REFUS pour " + patientName + " : " + raison);
        }
    }
    send(reply);
} else {
                System.out.println("[Hôpital] Erreur : Message mal formé (données manquantes).");
            }
        } else {
            System.out.println("[Hôpital] Erreur : Message reçu sans séparateur ':' ");
        }
    } else {
        block();
    }
}
        });
    }
}