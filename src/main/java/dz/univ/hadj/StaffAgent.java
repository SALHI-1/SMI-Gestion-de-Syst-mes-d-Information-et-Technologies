package dz.univ.hadj;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class StaffAgent extends Agent {
    protected void setup() {
        System.out.println("[StaffAgent] Gestion des RH opérationnelle.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.QUERY_IF) {
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("OUI"); // Simule : Personnel disponible
                    send(reply);
                } else {
                    block();
                }
            }
        });
    }
}