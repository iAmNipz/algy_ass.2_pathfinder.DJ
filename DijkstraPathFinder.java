package pathFinder;

import map.Coordinate;
import map.PathMap;

import java.util.*;

public class DijkstraPathFinder implements PathFinder {
	// TODO: You might need to implement some attributes

	protected PathMap map;

	// in skeleton code the hashCode() and equals() is overridden and change other
	// states doesn't change hash value or
	// equal result so we can use object as key
	protected HashMap<Coordinate, HashMap<Coordinate, Integer>> edgeList = new HashMap<Coordinate, HashMap<Coordinate, Integer>>();
	protected HashMap<Coordinate, Integer> CoorIndex = new HashMap<>();

	public DijkstraPathFinder(PathMap map) {
		// TODO :Implement
		this.map = map;
	} // end of DijkstraPathFinder()

	@Override
	public List<Coordinate> findPath() {

		PathMap map = this.map;
		this.iniMyMap(map);
		List<Coordinate> path = new ArrayList<Coordinate>();

		if (map.originCells.size() == 1 && map.destCells.size() == 1 && map.waypointCells.size() == 0) {

			// part A also took terrain cost into consideration so part B will call this
			// method as well
			path = this.partA(map.originCells.get(0), map.destCells.get(0));
			Collections.reverse(path);
		} else if ((map.originCells.size() != 1 || map.destCells.size() != 1) && map.waypointCells.size() == 0) {
			// method partC() is used to handle mutiple ori/desti with/without terrain cost
			// no waypoint
			path = this.partC(map.originCells, map.destCells);
			Collections.reverse(path);
		} else if (map.waypointCells.size() != 0) {
			path = this.partD(map.originCells, map.destCells, map.waypointCells);
		}

		return path;
	} // end of findPath()

	private List<Coordinate> partD(List<Coordinate> ori, List<Coordinate> dest, List<Coordinate> waypoint) {
		List<Coordinate> result = new ArrayList<>();

		// store all possible path
		ArrayList<Coordinate> temp = new ArrayList<>();
		int index = 0;
		Coordinate o = ori.get(0);
		Coordinate de = dest.get(0);
		Coordinate current = o;

		// store local optimal path
		List<Coordinate> localtemp = new ArrayList<>();		
		while (waypoint.size() != 0) {  	
			localtemp = this.decideNextCoor(current, waypoint);		
			 current =localtemp.get(0);
			 localtemp.remove(0);
			 Collections.reverse(localtemp);
			 temp.addAll(localtemp);							   
			waypoint.remove(current);	
			current.setPreCoor(null);
		}
		current.setPreCoor(null);
		de.setPreCoor(null);
		localtemp = this.partA(current, de);
		Collections.reverse(localtemp);
		temp.addAll(localtemp);

		return temp;
		
	}

	private List<Coordinate> decideNextCoor(Coordinate ori, List<Coordinate> waypoint) {
		ArrayList<List<Coordinate>> temp = new ArrayList<>();	
		System.out.println("decide shortest for "+ori.getColumn()+ori.getRow());
		for (Coordinate wp : waypoint) {
			System.out.println("testing "+wp.getColumn()+wp.getRow());
				List<Coordinate> temp2 = new ArrayList<>();
			temp2 = this.partA(ori, wp);
			temp.add(temp2);			
			System.out.println("finish");
		}
		int length = Integer.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).size() < length) {
				length = temp.get(i).size();
				index = i;
			}
		}
		return temp.get(index);	
		
		
		
	}

	private List<Coordinate> partC(List<Coordinate> ori, List<Coordinate> dest) {
		List<Coordinate> result = new ArrayList<>();
		ArrayList<List<Coordinate>> temp = new ArrayList<>();
		for (Coordinate o : ori)
			for (Coordinate de : dest) {
				List<Coordinate> temp2 = new ArrayList<>();
				temp2 = this.partA(o, de);
				temp.add(temp2);
			}
		int length = Integer.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < temp.size(); i++) {
			if (temp.get(i).size() < length) {
				length = temp.get(i).size();
				index = i;
			}
		}
		result = temp.get(index);
		return result;
	}

	private List<Coordinate> partA(Coordinate source, Coordinate desti) {
		ArrayList<Coordinate> result = new ArrayList<>();

		// s=start u=unfinished
		// coordinate,cost_to_source
		HashMap<Coordinate, Integer> s = new HashMap<>();
		HashMap<Coordinate, Integer> u = new HashMap<>();

		// initialize s and u
		s.put(source, 0);
		for (Coordinate co : this.edgeList.keySet()) {
			if (!co.equals(source)) {
				// set edge cost for reachable coordinate and max value for unreachable ones
				u.put(co, this.edgeList.get(co).keySet().contains(source) ? this.edgeList.get(co).get(source)
						: Integer.MAX_VALUE);
				if (this.edgeList.get(co).keySet().contains(source))
					co.setPreCoor(source);
			}
		}
		// initialize s and u

		while (u.size() != 0) {

			Coordinate co = this.Utility_findSmallest(u);

			if (co.equals(desti)) {
				result.add(co);
				while (co.getPreCoor() != null) {					
					result.add(co.getPreCoor());
					co = co.getPreCoor();
				}

				return result;
			} else {
				// for all the coordinates that this co can reach
				for (Coordinate tar : this.edgeList.get(co).keySet()) {
					// if the cost for source to reach tar through co is smaller than
					// current cost
					if (!tar.equals(source) && u.get(tar) != null) {
						if (this.edgeList.get(co).get(tar) + u.get(co) < u.get(tar)) {
							// then do the relaxation
							u.put(tar, this.edgeList.get(co).get(tar) + u.get(co));
							// reach this tar through co							
							tar.setPreCoor(co);
						}
					}
				}

				s.put(co, u.get(co));
				u.remove(co);
			}

		}
		return null;
	}

	private Coordinate Utility_findSmallest(HashMap<Coordinate, Integer> u) {
		// will overwrite coordinates that have same edge cost
		// but it doesn't matter
		// for unreachable coordinates, it's OK
		// for coordinates that have same cost, after remove this one from u, finally
		// all coordinates will get picked
		HashMap<Integer, Coordinate> u_reverse = new HashMap<>();
		for (Coordinate co : u.keySet())
			u_reverse.put(u.get(co), co);

		int key = Integer.MAX_VALUE;
		for (Integer in : u_reverse.keySet()) {
			if (in < key)
				key = in;
		}

		return u_reverse.get(key);

	}

	private void iniMyMap(PathMap map) {

		int index = 0;
		for (int i = 0; i < map.sizeR; i++)
			for (int j = 0; j < map.sizeC; j++) {
				Coordinate co = map.cells[i][j];
				if (!co.getImpassable()) {
					// set index for each coordinate and store it in hash map
					this.CoorIndex.put(co, index);
					co.setIndex(index);
					HashMap<Coordinate, Integer> edges = new HashMap<>();
					this.edgeList.put(co, edges);

					// visit all neighbors
					// will create two edges for coordinate A,B: A->B and B->A, this is for
					// potential backtrack in part D
					// these two edges will have same cost
					this.iniMyMap_createEdges(co, i + 1, j);
					this.iniMyMap_createEdges(co, i - 1, j);
					this.iniMyMap_createEdges(co, i, j + 1);
					this.iniMyMap_createEdges(co, i, j - 1);
					index++;
				}

			}
	}

	private void iniMyMap_createEdges(Coordinate source, int row, int col) {
		// if this coordinate is in the map
		if (row >= 0 && row <= this.map.sizeR - 1 && col <= this.map.sizeC - 1 && col >= 0
				&& map.isIn(map.cells[row][col])) {
			Coordinate temp = map.cells[row][col];
			if (!temp.getImpassable())
			// so it make sense to create an edge
			{
				int DesTerrianCost = temp.getTerrainCost();
				int SourceCost = source.getTerrainCost();
				// add a new edge to source's edge list, cost is destination's terrain
				// cost+source's terrain cost
				this.edgeList.get(source).put(temp, DesTerrianCost + SourceCost);
			}
		}
	}

	@Override
	public int coordinatesExplored() {
		// TODO: Implement (optional)

		// placeholder
		return 0;
	} // end of cellsExplored()

} // end of class DijsktraPathFinder
