package de.domisum.lib.iternifex.generic.pathfinding;

import de.domisum.lib.iternifex.generic.Edge;
import de.domisum.lib.iternifex.generic.Node;

import java.util.List;

public interface Pathfinder
{

	<N extends Node<N, E>, E extends Edge<N, E>> List<N> findPath(N startNode, N endNode) throws PathfindingException;

}
