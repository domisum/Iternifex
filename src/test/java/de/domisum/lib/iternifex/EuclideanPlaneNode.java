package de.domisum.lib.iternifex;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class EuclideanPlaneNode implements Node<EuclideanPlaneNode, EuclideanPlaneEdge>
{

	@Getter
	private final String name;

	@Getter
	private final double x;
	@Getter
	private final double y;

	private final Set<EuclideanPlaneEdge> edges = new HashSet<>();


	// INIT
	public void addEdgeTo(EuclideanPlaneNode node)
	{
		double edgeWeight = getHeuristicWeightTo(node);
		edges.add(new EuclideanPlaneEdge(this, node, edgeWeight));
	}


	// OBJECT
	@Override
	public String toString()
	{
		return "EuclideanPlaneNode{"+"name='"+name+'\''+", x="+x+", y="+y+'}';
	}


	// NODE
	@Override
	public Set<EuclideanPlaneEdge> getEdges()
	{
		return Collections.unmodifiableSet(edges);
	}

	@Override
	public double getHeuristicWeightTo(EuclideanPlaneNode otherNode)
	{
		double dX = otherNode.getX()-getX();
		double dY = otherNode.getY()-getY();

		return Math.sqrt((dX*dX)+(dY*dY));
	}

}
