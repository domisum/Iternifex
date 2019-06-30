package de.domisum.lib.iternifex;

import java.util.Objects;
import java.util.Set;

/**
 * Represents a node on a graph.
 * Has to implement #equals() and #hashCode().
 *
 * @param <N> the type of node, the implementing class
 */
public interface Node<N extends Node<N, E>, E extends Edge<N, E>>
{

	Set<E> getEdges();

	default E getEdgeTo(N other)
	{
		for(E edge : getEdges())
		{
			@SuppressWarnings("unchecked")
			Node<N, E> fromEdgeOther = edge.getOther((N) this);
			if(Objects.equals(fromEdgeOther, other))
				return edge;
		}

		return null;
	}

	double getHeuristicWeightTo(N otherNode);

}
