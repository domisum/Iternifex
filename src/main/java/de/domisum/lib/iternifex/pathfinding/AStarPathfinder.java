package de.domisum.lib.iternifex.pathfinding;

import com.google.common.collect.Lists;
import de.domisum.lib.auxilium.util.PHR;
import de.domisum.lib.iternifex.Edge;
import de.domisum.lib.iternifex.Node;
import de.domisum.lib.iternifex.debug.DebugLogger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class AStarPathfinder implements Pathfinder
{

	// FIND PATH
	@Override
	public <N extends Node<N, E>, E extends Edge<N, E>> List<N> findPath(N startNode, N endNode) throws PathfindingException
	{
		return new PathFinding<>(startNode, endNode).findPath();
	}

	@RequiredArgsConstructor
	protected static class PathFinding<N extends Node<N, E>, E extends Edge<N, E>>
	{

		// INPUT
		private final N startNode;
		private final N endNode;

		// TEMP
		private final UnvisitedNodes unvisitedNodes = new UnvisitedNodes();
		private final Map<N, PathFindingNode> pathFindingNodeMap = new HashMap<>();


		// FIND PATH
		public List<N> findPath() throws PathfindingException
		{
			unvisitedNodes.insert(getPathfindingNodeFor(startNode));
			DebugLogger.log("Starting pathfinding...");

			while(!unvisitedNodes.isEmpty())
			{
				DebugLogger.log("Unvisited nodes: "+unvisitedNodes.size());

				@SuppressWarnings("unchecked")
				PathFindingNode nodeToVisit = (PathFindingNode) unvisitedNodes.popBest();
				visitNode(nodeToVisit);

				if(Objects.equals(nodeToVisit.getNode(), endNode))
					return buildPath(nodeToVisit);
			}

			throw new PathfindingException("No connection between start and end node, therefore couldn't find path");
		}

		private List<N> buildPath(PathFindingNode endPathfindingNode)
		{
			DebugLogger.log("Starting to build path...");

			List<N> nodesReversed = new ArrayList<>();
			PathFindingNode node = endPathfindingNode;
			while(node != null)
			{
				nodesReversed.add(node.getNode());
				DebugLogger.log(PHR.rcs("Added node '{}' to reverse path", node.getNode()));

				node = node.getReachedFrom();
			}

			DebugLogger.log(PHR.rcs("Building reversed path done, reversing to get it into right order"));

			return new ArrayList<>(Lists.reverse(nodesReversed));
		}

		private void visitNode(PathFindingNode node)
		{
			DebugLogger.log(PHR.rcs("Visiting node '{}'", node.getNode()));

			for(Edge<N, E> edge : node.getNode().getEdges())
			{
				N reachedNode = edge.getOther(node.getNode());
				PathFindingNode reachedPathfindingNode = getPathfindingNodeFor(reachedNode);

				reachNode(node, reachedPathfindingNode);
			}
		}

		private void reachNode(PathFindingNode nodeFrom, PathFindingNode node)
		{
			DebugLogger.log(PHR.rcs("Reaching node '{}' from node '{}'", node.getNode(), nodeFrom.getNode()));

			boolean nodeUnreachedBefore = node.getReachedFrom() == null;

			double newWeightToNode = nodeFrom.getStartToNodeWeight()+nodeFrom.node.getEdgeTo(node.getNode()).getWeight();
			if(!node.getNode().equals(startNode)) // don't set reached from on start node
				if(nodeUnreachedBefore)
				{
					node.setReachedFrom(nodeFrom);
					nodeFrom.addChild(node);
				}
				else if(newWeightToNode < node.getStartToNodeWeight())
				{
					DebugLogger.log("Found shorter way to previously reached node");

					node.reachedFrom.removeChild(node);
					node.setReachedFrom(nodeFrom);
					nodeFrom.addChild(node);

					decreaseNodeWeightForSubTree(node);
				}

			if(nodeUnreachedBefore)
				unvisitedNodes.insert(node);
		}

		private void decreaseNodeWeightForSubTree(PathFindingNode node)
		{
			if(unvisitedNodes.contains(node))
				unvisitedNodes.decreaseWeight(node);

			for(PathFindingNode child : node.getChildren())
				decreaseNodeWeightForSubTree(child);
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
		@EqualsAndHashCode(of = "node")
		protected class PathFindingNode
		{

			@Getter
			private final N node;

			@Setter
			@Getter
			private PathFindingNode reachedFrom;
			@Getter
			private final Set<PathFindingNode> children = new HashSet<>();


			// CHILDREN
			public void addChild(PathFindingNode child)
			{
				children.add(child);
			}

			public void removeChild(PathFindingNode child)
			{
				children.remove(child);
			}


			// WEIGHT
			public double getCombinedWeight()
			{
				return getStartToNodeWeight()+getNodeToEndWeight();
			}

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

		}

	}

}
