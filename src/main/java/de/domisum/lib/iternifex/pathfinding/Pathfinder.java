package de.domisum.lib.iternifex.pathfinding;

import de.domisum.lib.iternifex.Edge;
import de.domisum.lib.iternifex.Node;

import java.util.List;

public interface Pathfinder
{

	<N extends Node<N, E>, E extends Edge<N, E>> List<N> findPath(N startNode, N endNode) throws PathfindingException;

}
