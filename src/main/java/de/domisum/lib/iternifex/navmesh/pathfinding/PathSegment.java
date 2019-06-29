package de.domisum.lib.iternifex.navmesh.pathfinding;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PathSegment
{

	@Getter
	private final Vector3D startLocation;
	@Getter
	private final Vector3D endLocation;

	@Getter
	private final MovementType movementType;

}
