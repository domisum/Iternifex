package de.domisum.lib.iternifex.pathfinding;

import com.google.common.collect.Lists;
import de.domisum.lib.iternifex.Edge;
import de.domisum.lib.iternifex.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

public class AStarPathfinder implements Pathfinder
{

	// FIND PATH
	@Override
	public <N extends Node<N, E>, E extends Edge<N, E>> List<N> findPath(N startNode, N endNode) throws PathfindingException
	{
		return new PathFinding<>(startNode, endNode).findPath();
	}

	@RequiredArgsConstructor
	private static class PathFinding<N extends Node<N, E>, E extends Edge<N, E>>
	{

		// INPUT
		private final N startNode;
		private final N endNode;

		// TEMP
		private final SortedSet<PathFindingNode> unvisitedNodes = new TreeSet<>(Comparator.comparingDouble(PathFindingNode::getCombinedWeight));
		private final Map<N, PathFindingNode> pathFindingNodeMap = new HashMap<>();


		// FIND PATH
		public List<N> findPath() throws PathfindingException
		{
			unvisitedNodes.add(getPathfindingNodeFor(startNode));

			while(!unvisitedNodes.isEmpty())
			{
				PathFindingNode nodeToVisit = getBestUnvisitedNode();
				unvisitedNodes.remove(nodeToVisit);
				visitNode(nodeToVisit);

				if(Objects.equals(nodeToVisit.getNode(), endNode))
					return buildPath(nodeToVisit);
			}

			throw new PathfindingException("No connection between start and end node, therefore couldn't find path");
		}

		private PathFindingNode getBestUnvisitedNode()
		{
			return unvisitedNodes.first();
		}

		private List<N> buildPath(PathFindingNode endPathfindingNode)
		{
			List<N> nodesReversed = new ArrayList<>();
			PathFindingNode node = endPathfindingNode;
			while(node != null)
			{
				nodesReversed.add(node.getNode());
				node = node.getReachedFrom();
			}

			return new ArrayList<>(Lists.reverse(nodesReversed));
		}

		private void visitNode(PathFindingNode node)
		{
			for(Edge<N, E> edge : node.getNode().getEdges())
			{
				N reachedNode = edge.getOther(node.getNode());
				PathFindingNode reachedPathfindingNode = getPathfindingNodeFor(reachedNode);

				reachNode(node, reachedPathfindingNode);
			}
		}

		private void reachNode(PathFindingNode nodeFrom, PathFindingNode node)
		{
			boolean nodeUnreachedBefore = node.getReachedFrom() == null;

			double newWeightToNode = nodeFrom.getStartToNodeWeight()+nodeFrom.node.getEdgeTo(node.getNode()).getWeight();
			if(!node.getNode().equals(startNode)) // don't set reached from on start node
				if(nodeUnreachedBefore || (newWeightToNode < node.getStartToNodeWeight()))
					node.setReachedFrom(nodeFrom);

			if(nodeUnreachedBefore)
				unvisitedNodes.add(node);
		}


		private PathFindingNode getPathfindingNodeFor(N node)
		{
			PathFindingNode pathFindingNode = pathFindingNodeMap.get(node);
			if(pathFindingNode == null)
			{
				pathFindingNode = new PathFindingNode(node);
				pathFindingNodeMap.put(node, pathFindingNode);
			}

			return pathFindingNode;
		}


		@RequiredArgsConstructor
		private class PathFindingNode
		{

			@Getter
			private final N node;
			@Setter
			@Getter
			private PathFindingNode reachedFrom;


			public double getStartToNodeWeight()
			{
				if(Objects.equals(node, startNode))
					return 0;

				double toPreviousWeight = reachedFrom.getStartToNodeWeight();
				double lastEdgeWeight = reachedFrom.node.getEdgeTo(node).getWeight();

				return toPreviousWeight+lastEdgeWeight;
			}

			public double getNodeToEndWeight()
			{
				return node.getHeuristicWeightTo(endNode);
			}

			public double getCombinedWeight()
			{
				return getStartToNodeWeight()+getNodeToEndWeight();
			}
		}

	}

}
