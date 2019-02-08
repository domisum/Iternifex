package de.domisum.lib.iternifex;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public interface Edge<NodeT extends Node<NodeT>>
{

	/**
	 * Returns the nodes this edge connects. Always returns two nodes.
	 *
	 * @return the nodes this edge connects
	 */
	Set<NodeT> getNodes();

	default NodeT getOther(Node<NodeT> node)
	{
		ArrayList<NodeT> nodes = new ArrayList<>(getNodes());

		if(Objects.equals(nodes.get(0), node))
			return nodes.get(1);
		else if(Objects.equals(nodes.get(1), node))
			return nodes.get(0);
		else
			throw new IllegalArgumentException("edge does not connect to node "+node);
	}


	double getWeight();

}
