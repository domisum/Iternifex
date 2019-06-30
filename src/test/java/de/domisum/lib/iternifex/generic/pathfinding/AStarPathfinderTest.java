package de.domisum.lib.iternifex.generic.pathfinding;

import de.domisum.lib.iternifex.EuclideanPlaneNode;
import de.domisum.lib.iternifex.generic.pathfinding.AStarPathfinder;
import de.domisum.lib.iternifex.generic.pathfinding.Pathfinder;
import de.domisum.lib.iternifex.generic.pathfinding.PathfindingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AStarPathfinderTest
{

	@Test
	void testSimpleGraph() throws PathfindingException
	{
		EuclideanPlaneNode nodeA = new EuclideanPlaneNode("a", 0, 0);
		EuclideanPlaneNode nodeB = new EuclideanPlaneNode("b", 2, 0);
		EuclideanPlaneNode nodeC = new EuclideanPlaneNode("c", 0, 1);
		EuclideanPlaneNode nodeD = new EuclideanPlaneNode("d", 1, 1);

		nodeA.addEdgeTo(nodeB);
		nodeA.addEdgeTo(nodeC);
		nodeB.addEdgeTo(nodeD);
		nodeC.addEdgeTo(nodeD);


		List<String> path = findPath(nodeA, nodeD);
		Assertions.assertIterableEquals(path, Arrays.asList("a", "c", "d"));
	}

	@Test
	void testSimpleGraph2() throws PathfindingException
	{
		EuclideanPlaneNode nodeA = new EuclideanPlaneNode("a", 0, 0);
		EuclideanPlaneNode nodeB = new EuclideanPlaneNode("b", 0.5, 0.5);
		EuclideanPlaneNode nodeC = new EuclideanPlaneNode("c", 0, 1);

		nodeA.addEdgeTo(nodeB);
		nodeB.addEdgeTo(nodeA);
		nodeB.addEdgeTo(nodeC);
		nodeC.addEdgeTo(nodeB);
		nodeA.addEdgeTo(nodeC);
		nodeC.addEdgeTo(nodeA);

		List<String> path = findPath(nodeA, nodeC);
		Assertions.assertIterableEquals(path, Arrays.asList("a", "c"));
	}

	private List<String> findPath(EuclideanPlaneNode nodeA, EuclideanPlaneNode nodeB) throws PathfindingException
	{
		Pathfinder pathfinder = new AStarPathfinder();
		List<EuclideanPlaneNode> path = pathfinder.findPath(nodeA, nodeB);

		List<String> pathNodeNames = new ArrayList<>();
		for(EuclideanPlaneNode node : path)
			pathNodeNames.add(node.getName());

		return pathNodeNames;
	}

}
