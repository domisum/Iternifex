package de.domisum.lib.iternifex;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public interface Edge<N extends Node<N, E>, E extends Edge<N, E>>
{

	/**
	 * Returns the nodes this edge connects. Always returns two nodes.
	 *
	 * @return the nodes this edge connects
	 */
	Set<N> getNodes();

	default N getOther(N node)
	{
		ArrayList<N> nodes = new ArrayList<>(getNodes());

		if(Objects.equals(nodes.get(0), node))
			return nodes.get(1);
		else if(Objects.equals(nodes.get(1), node))
			return nodes.get(0);
		else
			throw new IllegalArgumentException("edge does not connect to node "+node);
	}


	double getWeight();

}
