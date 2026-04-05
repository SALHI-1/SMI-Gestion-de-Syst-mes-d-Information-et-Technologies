package dz.univ.hadj;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class PatientAgent extends Agent {
    private PatientGui myGui;

    protected void setup() {
        System.out.println("[Patient] " + getLocalName() + " prêt.");

        // Lancement de l'interface graphique
        myGui = new PatientGui(this);
        myGui.showGui();

        // Comportement pour écouter les réponses de l'hôpital
addBehaviour(new CyclicBehaviour() {
    public void action() {
        ACLMessage msg = receive();
        if (msg != null) {
            // 1. On récupère le texte envoyé par l'hôpital
            String reponse = msg.getContent();

            // 2. On l'affiche DIRECTEMENT dans l'UI (la zone noire)
            myGui.log("[RECU] " + reponse);

            // Petit bonus : si c'est une erreur, on peut ajouter une ligne d'alerte
            if (msg.getPerformative() == ACLMessage.FAILURE) {
                myGui.log(" -> Attention : Demande rejetée.");
            }
        } else {
            block();
        }
    }
});
    }

    // Méthode appelée par le bouton de la GUI
public void envoyerDemande(final String nom, final String ressource, final String priorite) {
    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.addReceiver(new AID("HospitalManager", AID.ISLOCALNAME));
    // Format : Nom:Ressource:Priorite
    msg.setContent(nom + ":" + ressource + ":" + priorite);
    send(msg);
    // System.out.println("[Patient] Envoi - " + nom + " (" + priorite + ")");
myGui.log("[ACTION] Envoi requête : " + nom + " (" + priorite + ")");
    myGui.log(" -> Besoin de : " + ressource);
}

    protected void takeDown() {
        if (myGui != null) myGui.dispose();
        System.out.println("[Patient] Agent terminé.");
    }
}