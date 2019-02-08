package de.domisum.lib.iternifex;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a node on a graph.
 * Has to implement #equals() and #hashCode().
 *
 * @param <T> the type of node, the implementing class
 */
public interface Node<T extends Node<T>>
{

	Set<Edge<T>> getEdges();

	default Edge<T> getEdgeTo(Node<T> other)
	{
		for(Edge<T> edge : getEdges())
			if(Objects.equals(edge.getOther(this), other))
				return edge;

		throw new IllegalArgumentException("no edge to node "+other);
	}

	double getHeuristicWeightTo(T otherNode);

}
