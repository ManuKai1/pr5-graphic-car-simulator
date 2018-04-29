package es.ucm.fdi.view;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import es.ucm.fdi.extra.graphlayout.Dot;
import es.ucm.fdi.extra.graphlayout.Edge;
import es.ucm.fdi.extra.graphlayout.Graph;
import es.ucm.fdi.extra.graphlayout.GraphComponent;
import es.ucm.fdi.extra.graphlayout.Node;
import es.ucm.fdi.model.SimObj.Junction;
import es.ucm.fdi.model.SimObj.Road;
import es.ucm.fdi.model.SimObj.Vehicle;
import es.ucm.fdi.model.simulation.RoadMap;

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

    private void generateGraph() {

        Graph graph = new Graph();
        Map<Junction, Node> junctToNode = new HashMap<>();

        for ( Junction j : roadMap.getJunctions().values() ) {
            Node n = new Node( j.getID() );
            junctToNode.put(j, n);

            graph.addNode(n);
        }

        for ( Road r : roadMap.getRoads().values() ) {
            // Nueva arista
            Edge e = new Edge( 
                r.getID(),
                junctToNode.get( r.getFromJunction() ),
                junctToNode.get( r.getToJunction() ),
                r.getLength()
            );

            // Puntos en la arista
            for ( Vehicle v : r.getRoadVehicles() ) {
                Dot d = new Dot( v.getID(), v.getLocation() );
                
                e.addDot(d);
            }

            graph.addEdge(e);
        }

        _graphComp.setGraph(graph);
    }
}