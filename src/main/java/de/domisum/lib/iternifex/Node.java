package de.domisum.lib.iternifex;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a node on a graph.
 * Has to implement #equals() and #hashCode().
 *
 * @param <N> the type of node, the implementing class
 */
public interface Node<N extends Node<N, E>, E extends Edge<N>>
{

	Set<E> getEdges();

	default E getEdgeTo(Node<N, E> other)
	{
		for(E edge : getEdges())
			if(Objects.equals(edge.getOther(this), other))
				return edge;

		return null;
	}

	double getHeuristicWeightTo(N otherNode);

}
