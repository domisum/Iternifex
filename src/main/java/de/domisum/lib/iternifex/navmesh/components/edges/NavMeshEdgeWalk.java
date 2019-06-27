package de.domisum.lib.iternifex.navmesh.components.edges;

import de.domisum.lib.iternifex.navmesh.components.NavMeshEdge;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;

public class NavMeshEdgeWalk extends NavMeshEdge
{

	// INIT
	public NavMeshEdgeWalk(NavMeshTriangle triangleA, NavMeshTriangle triangleB)
	{
		super(triangleA, triangleB);
	}


	// EDGE
	@Override
	public double getWeight()
	{
		return getTriangleA().getCenter().distanceTo(getTriangleB().getCenter());
	}

}
