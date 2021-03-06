package de.domisum.lib.iternifex.navmesh.components.edges;

import de.domisum.lib.auxilium.data.container.direction.Direction2D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.navmesh.components.NavMeshEdge;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

public class NavMeshEdgeLadder extends NavMeshEdge
{

	// CONSTANTS
	private static final double LADDER_DISTANCE_WEIGHT_MULTIPLIER = 2.0;

	// ATTRIBUTES
	@Getter
	private final Vector3D bottomLadderLocation;
	@Getter
	private final Vector3D topLadderLocation;

	@Getter
	private final Direction2D direction;


	// INIT
	public NavMeshEdgeLadder(
			NavMeshTriangle triangleA,
			NavMeshTriangle triangleB,
			Vector3D bottomLadderLocation,
			Vector3D topLadderLocation,
			Direction2D direction)
	{
		super(triangleA, triangleB);

		Validate.notNull(bottomLadderLocation, "bottomLadderLocation can't be null");
		Validate.notNull(topLadderLocation, "topLadderLocation can't be null");

		Validate.notNull(direction, "direction can't be null");

		this.bottomLadderLocation = bottomLadderLocation;
		this.topLadderLocation = topLadderLocation;

		this.direction = direction;
	}


	// EDGE
	@Override
	public double getWeight()
	{
		return bottomLadderLocation.distanceTo(topLadderLocation)*LADDER_DISTANCE_WEIGHT_MULTIPLIER;
	}

}
