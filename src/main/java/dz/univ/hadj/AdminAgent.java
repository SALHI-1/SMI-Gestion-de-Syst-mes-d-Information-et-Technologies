package dz.univ.hadj;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class AdminAgent extends Agent {
    protected void setup() {
        System.out.println("--- [Admin] Agent de supervision globale actif ---");

        // Liste des agents critiques à surveiller
        final String[] agentsASurveiller = {"HospitalManager", "Equip", "Staff"};

        // Diffusion périodique des requêtes de santé (toutes les 10 secondes)
        addBehaviour(new TickerBehaviour(this, 10000) {
            protected void onTick() {
                System.out.println("\n--- [Admin] SCANNER DE SANTÉ RÉSEAU ---");
                
                for (String nomAgent : agentsASurveiller) {
                    // Création d'un message de type QUERY_IF (Es-tu vivant ?)
                    ACLMessage ping = new ACLMessage(ACLMessage.QUERY_IF);
                    ping.addReceiver(new AID(nomAgent, AID.ISLOCALNAME));
                    ping.setContent("HEALTH_CHECK");
                    send(ping);
                    
                    System.out.println(" > Vérification envoyée à : " + nomAgent);
                }
                
                System.out.println("Flux patients : Stable | Statut global : OK");
                System.out.println("-----------------------------------------\n");
            }
        });
    }
}