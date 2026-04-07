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

    protected void setup() {
        // 1. Initialisation des équipements et services
        resources.put("SCANNER", 5);
        resources.put("DIAGNOSTIC", 10);
        resources.put("KINESITHERAPIE", 3);
        resources.put("CONSULTATION", 20);

        myGui = new HospitalGui();
        System.out.println("[Hôpital] Orchestrateur prêt. En attente de patients...");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getPerformative() == ACLMessage.REQUEST) {
                        traiterDemandePatient(msg);
                    }
                } else {
                    block();
                }
            }
        });
    }

private void traiterDemandePatient(ACLMessage msgFromPatient) {
    String[] parts = msgFromPatient.getContent().split(":");
    String patient = parts[0];
    String besoin = parts[1];
    String priorite = parts[2];

    // --- ENVOI RÉEL AUX AGENTS EXPERTS (Pour le Sniffer) ---

    // 1. Message pour l'agent Équipement
    ACLMessage msgEquip = new ACLMessage(ACLMessage.QUERY_IF);
    msgEquip.addReceiver(new AID("Equip", AID.ISLOCALNAME));
    msgEquip.setContent("Etat:" + besoin);
    send(msgEquip); // <--- L'ARROU APPARAÎTRA ICI DANS LE SNIFFER

    // 2. Message pour l'agent Staff
    ACLMessage msgStaff = new ACLMessage(ACLMessage.QUERY_IF);
    msgStaff.addReceiver(new AID("Staff", AID.ISLOCALNAME));
    msgStaff.setContent("Dispo:Medecin");
    send(msgStaff); // <--- DEUXIÈME ARROW ICI

    // --- LOGIQUE DE DÉCISION (Le reste du code ne change pas) ---
    int stock = resources.getOrDefault(besoin, 0);
    boolean accorde = (stock > 1 || (priorite.equals("URGENTE") && stock > 0));

    ACLMessage reply = msgFromPatient.createReply();
    if (accorde) {
        int nouveauStock = stock - 1;
        resources.put(besoin, nouveauStock);
        myGui.updateStock(besoin, nouveauStock);
        sauvegarderDansCSV(patient, besoin, priorite);
        reply.setPerformative(ACLMessage.INFORM);
        reply.setContent("ADMIS : " + besoin + " réservé pour " + patient);
    } else {
        reply.setPerformative(ACLMessage.FAILURE);
        reply.setContent("REFUS : Ressource indisponible");
    }
    send(reply);
}

    // Fonction de stockage (Persistance des données)
    private void sauvegarderDansCSV(String patient, String service, String priorite) {
        try (FileWriter writer = new FileWriter("historique_hopital.csv", true)) {
            String ligne = new Date().toString() + "," + patient + "," + service + "," + priorite + "\n";
            writer.write(ligne);
            System.out.println("[Stockage] Entrée ajoutée au fichier CSV.");
        } catch (IOException e) {
            System.err.println("Erreur stockage : " + e.getMessage());
        }
    }
}