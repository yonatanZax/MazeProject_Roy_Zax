package algorithms.search;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;


public class DepthFirstSearch extends ASearchingAlgorithm
{

	private HashMap<Integer,Boolean> visited;

	public DepthFirstSearch(){
		super();
		stateQueue = new PriorityQueue<AState>();
	}

	@Override
	public Solution solve(ISearchable searchable) {
		if (searchable == null)
			return null;
		clear();
		AState curLoc = searchable.getStartState();
		ArrayList<AState> neighbors = searchable.getAllPossibleStates(curLoc);
		visited = new HashMap<Integer, Boolean>();
		visited.put(curLoc.hashCode(),true);
		visitedNodes++;
		for (AState neighborState: neighbors) {
			visited.put(neighborState.hashCode(),false);
		}
		AState result = null;
		for (AState neighborState:neighbors) {
			if(!visited.get(neighborState.hashCode())){
				result = DFSVisit(searchable,neighborState);
				if (result != null)
					return formSolution(result);
			}
		}
		return null;
	}


	/**
	 * The DFSVisit method of DFS algorithm.
	 * A recursive method that goes recursively to all the neighbors we haven't visited yet of the neighborState.
	 * @param searchable The searchable problem we are solving.
	 * @param neighborState The state we will check all of its neighbors.
	 * @return A 'AState' representing the goalState and the path the algorithm went to get there, or null if there isn't a solution.
	 */
	private AState DFSVisit(ISearchable searchable, AState neighborState) {
		visitedNodes++;
		visited.put(neighborState.hashCode(), true);
		for (AState neighbor: searchable.getAllPossibleStates(neighborState)) {
			if(!visited.containsKey(neighbor.hashCode()))
				visited.put(neighbor.hashCode(),false);
			if(!visited.get(neighbor.hashCode())){
				if(neighbor.equals(searchable.getGoalState()))
					return neighbor;
				AState ans =  DFSVisit(searchable,neighbor);
				if (ans != null)
					return ans;
			}
		}
		return null;
	}



	public String getName(){
		return "Depth First Search";
	}

}

