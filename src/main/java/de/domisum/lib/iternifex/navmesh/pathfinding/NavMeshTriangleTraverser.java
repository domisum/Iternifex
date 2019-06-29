package de.domisum.lib.iternifex.navmesh.pathfinding;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;

import java.util.Arrays;
import java.util.List;

public class NavMeshTriangleTraverser
{

	public List<PathSegment> traverse(List<NavMeshTriangle> triangleSequence, Vector3D startLocation, Vector3D endLocation)
	{
		if(triangleSequence.size() == 1)
			return Arrays.asList(new PathSegment(startLocation, endLocation, MovementType.WALK));

		// TODO
		return null;
	}

}
