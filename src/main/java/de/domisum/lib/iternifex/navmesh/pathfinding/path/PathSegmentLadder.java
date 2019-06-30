package de.domisum.lib.iternifex.navmesh.pathfinding.path;

import de.domisum.lib.auxilium.data.container.direction.Direction2D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.navmesh.pathfinding.MovementType;
import lombok.Getter;

public class PathSegmentLadder extends PathSegment
{

	// PROPERTIES
	@Getter
	private final Direction2D ladderDirection;


	// INIT
	public PathSegmentLadder(Vector3D startLocation, Vector3D endLocation, Direction2D ladderDirection)
	{
		super(startLocation, endLocation, MovementType.LADDER);
		this.ladderDirection = ladderDirection;
	}

}
