package de.domisum.lib.iternifex.navmesh.pathfinding.path;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.navmesh.pathfinding.MovementType;

public class PathSegmentWalk extends PathSegment
{

	// INIT
	public PathSegmentWalk(Vector3D startLocation, Vector3D endLocation)
	{
		super(startLocation, endLocation, MovementType.WALK);
	}

}
