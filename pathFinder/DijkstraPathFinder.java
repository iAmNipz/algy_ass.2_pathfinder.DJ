package pathFinder;

import map.Coordinate;
import map.PathMap;

import java.util.*;

import static java.lang.Integer.MAX_VALUE;

public class DijkstraPathFinder implements PathFinder {

    private PathMap map;

    private List<Coordinate> originCells;               // start cells
    private List<Coordinate> destination;               // end cells

    // Coordinates determined
    private Set<Coordinate> settledNodes;
    private Set<Coordinate> unSettledNodes;

    // collection of previous Coordinates in shortest path
    private Map<Coordinate, Coordinate> predecessors;
    private Map<Coordinate, Integer> distance;          // hashmap to update cost

    public DijkstraPathFinder(PathMap map) {
        // TODO :Implement
        this.map = map;
        // 2d array to ArrayList
        this.originCells = map.originCells;
        this.destination = map.destCells;
    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        return getMinDistance(originCells.get(1));
    } // end of findPath()

    private List<Coordinate> getMinDistance(Coordinate s) {
        this.settledNodes = new HashSet<>();
        this.unSettledNodes = new HashSet<>();
        this.distance = new HashMap<>();
        this.predecessors = new HashMap<>();

        unSettledNodes.add(s);

        for (int i = 0; i < map.cells.length; i++) {
            //if c matches the source, set cost to 0
            for (int j = 0; 0 < map.cells[0].length; j++) {
                if (map.cells[i][j].equals(s)) {
                    distance.put(map.cells[i][j], 0);
                }
                //set all other terrain costs to infinite values
                distance.put(map.cells[i][j], MAX_VALUE);
            }

        }
		//////////////////////////////////////////////////////////////////////////////////
		///this is where the queue would be better. And I think this is causing problems//
		//////////////////////////////////////////////////////////////////////////////////
        while (unSettledNodes.size() > 0 && !isSettled(destination.get(0))) {
            Coordinate node = getMinimum(unSettledNodes);
            //mark as settled
            settledNodes.add(node);
            unSettledNodes.remove(node);
            //relax edges on min node
            relax(node);
        }												///////////////////////
        return getPath(destination.get(0));				// I believe this throws an IOE, but can't trace
    }													///////////////////////


	// I am happy that this should be working
    private void relax(Coordinate node) {
        Coordinate w;
        // TODO: Have accounted for N/S/E/W --- we may need NE/NW/SE/SW also
        //directions for row and column
        int[] d_Row = {-1, 1, 0, 0};
        int[] d_Col = {0, 0, 1, -1};
        //loop through adjacent coordinates
        for (int i = 0; i < 4; i++) {
            int adj_Row = node.getRow() + d_Row[i];
            int adj_col = node.getColumn() + d_Col[i];
            if (map.isIn(adj_Row, adj_col) && map.isPassable(adj_Row, adj_col)) {
                //access coordinates
                w = map.cells[adj_Row][adj_col];
                if (distance.get(w) > distance.get(node) + w.getTerrainCost()) {
                    //update distance to
                    distance.replace(w, distance.get(node) + w.getTerrainCost());
                    predecessors.put(w, node);
                    unSettledNodes.add(w);
                } else {
                    distance.put(w, w.getTerrainCost());
                }
            }
        }
    }

    private Coordinate getMinimum(Set<Coordinate> c) {
        Coordinate minimum = null;
        for (Coordinate vertex : c) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    private boolean isSettled(Coordinate c) {
        return settledNodes.contains(c);
    }


    private int getShortestDistance(Coordinate destination) {
        Integer d = distance.get(destination);
        return Objects.requireNonNullElse(d, MAX_VALUE);
    }

	// method should be fine...
    // This method returns the path from the originCells to the selected target 
    private LinkedList<Coordinate> getPath(Coordinate target) {
        LinkedList<Coordinate> path = new LinkedList<>();
        Coordinate step = target;
        // check if a path exists
        if (predecessors.get(step) == null) {
            return null;
        }
        path.add(step);
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }


    //TODO: ignore.
    @Override
    public int coordinatesExplored() {
        // TODO: Implement (optional)

        // placeholder
        return 0;
    } // end of cellsExplored()


} // end of class DijsktraPathFinder
