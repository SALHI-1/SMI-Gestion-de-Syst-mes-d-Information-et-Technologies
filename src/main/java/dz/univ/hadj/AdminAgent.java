package dz.univ.hadj;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
public class AdminAgent extends Agent {
    protected void setup() {
        System.out.println("--- [Admin] Agent de supervision actif ---");

        // Diffusion périodique des statistiques (toutes les 10 secondes)
        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                System.out.println("\n--- RAPPORT SYSTEME D'INFORMATION ---");
                System.out.println("Etat du réseau : OK");
                System.out.println("Flux patients : Stable");
                System.out.println("-------------------------------------\n");

                ACLMessage report = new ACLMessage(ACLMessage.INFORM);
report.addReceiver(new AID("HospitalManager", AID.ISLOCALNAME));
report.setContent("STATUT_RESEAU_OK");
send(report); // Cela créera une flèche toutes les 10 secondes
            }
        });
    }
}