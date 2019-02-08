package de.domisum.lib.iternifex.pathfinding;

import de.domisum.lib.iternifex.Node;

import java.util.List;

public interface Pathfinder
{

	<T extends Node<T>> List<T> findPath(T startNode, T endNode) throws PathfindingException;

}
