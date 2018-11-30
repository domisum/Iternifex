package de.domisum.lib.iternifex;

/**
 * Represents a node on a graph.
 * Has to implement #equals() and #hashCode().
 *
 * @param <T> the type of node, the implementing class
 */
public interface PathfindingNode<T extends PathfindingNode<T>>
{

	PathfindingEdge<T> getEdges();

	double getHeuristicWeightTo(PathfindingNode<T> otherNode);

}
