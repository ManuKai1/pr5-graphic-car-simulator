package es.ucm.fdi.view;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import es.ucm.fdi.extra.graphlayout.*;

import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.simulation.RoadMap;

@SuppressWarnings("serial")
public class SimGraph extends JPanel {

    private GraphComponent _graphComp;
    private RoadMap roadMap;

    public SimGraph(RoadMap map) {
        roadMap = map;
        initGUI();
    }


    private void initGUI() {
        _graphComp = new GraphComponent();

        generateGraph();

        this.add(_graphComp);
        this.setVisible(true);
    }

    /**
     * Genera un {@code Graph} a partir del {@code _roadMap} 
     * guardado como atributo y se pasa al atributo
     * {@code _graphComp}.
     */
    public void generateGraph() {
        // Nuevo grafo y mapa Junction-Node.
        Graph graph = new Graph();
        Map<Junction, Node> junctToNode = new HashMap<>();

        // Se añaden las Junction (nodos) al grafo.
        for ( Junction j : roadMap.getJunctions().values() ) {
            Node n = new Node( j.getID() );
            junctToNode.put(j, n);

            graph.addNode(n);
        }

        // Se añaden las Roads (aristas) al grafo
        // junto con los Vehicles (puntos) en ellas
        for ( Road r : roadMap.getRoads().values() ) {
            // Nueva arista
            Edge e = new Edge( 
                r.getID(),
                junctToNode.get( r.getFromJunction() ),
                junctToNode.get( r.getToJunction() ),
                r.getLength(),
                r.isGreen()
            );

            // Puntos en la arista
            for ( Vehicle v : r.getRoadVehicles() ) {
                Dot d = new Dot( v.getID(), v.getLocation(), v.isFaulty() );
                
                e.addDot(d);
            }

            graph.addEdge(e);
        }

        // Se pasa el grafo al GraphComponent
        _graphComp.setGraph(graph);
    }
    
}