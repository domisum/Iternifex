package de.domisum.lib.iternifex.pathfinding;

import de.domisum.lib.iternifex.pathfinding.AStarPathfinder.PathFinding.PathFindingNode;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class UnvisitedNodes
{

	// CONTENT
	private final Queue<PathFindingNode> nodes = new PriorityQueue<>(Comparator.comparingDouble(PathFindingNode::getCombinedWeight));


	// GETTERS
	public boolean isEmpty()
	{
		return nodes.isEmpty();
	}

	public int size()
	{
		return nodes.size();
	}


	// ACTIONS
	public void insert(PathFindingNode node)
	{
		nodes.add(node);
	}

	public PathFindingNode getBest()
	{
		return nodes.poll();
	}

}
