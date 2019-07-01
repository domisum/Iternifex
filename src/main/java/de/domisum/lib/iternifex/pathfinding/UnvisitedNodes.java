package de.domisum.lib.iternifex.pathfinding;

import de.domisum.lib.iternifex.pathfinding.AStarPathfinder.PathFinding.PathFindingNode;
import de.domisum.lib.iternifex.pathfinding.FibonacciHeap.Entry;

import java.util.HashMap;
import java.util.Map;

public class UnvisitedNodes
{

	// CONTENT
	private final FibonacciHeap<PathFindingNode> fibonacciHeap = new FibonacciHeap<>();
	private final Map<PathFindingNode, Entry<PathFindingNode>> entries = new HashMap<>();


	// GETTERS
	public boolean isEmpty()
	{
		return fibonacciHeap.isEmpty();
	}

	public int size()
	{
		return fibonacciHeap.size();
	}

	public boolean contains(PathFindingNode node)
	{
		return entries.containsKey(node);
	}


	// ACTIONS
	public void insert(PathFindingNode node)
	{
		Entry<PathFindingNode> entry = fibonacciHeap.enqueue(node, node.getCombinedWeight());
		entries.put(node, entry);
	}

	public void decreaseWeight(PathFindingNode node)
	{
		Entry<PathFindingNode> entry = entries.get(node);
		fibonacciHeap.decreaseKey(entry, node.getCombinedWeight());
	}

	public PathFindingNode popBest()
	{
		return fibonacciHeap.dequeueMin().getValue();
	}

}
