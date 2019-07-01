package de.domisum.lib.iternifex.navmesh.pathfinding.path;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.navmesh.pathfinding.MovementType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public abstract class PathSegment
{

	@Getter
	private final Vector3D startLocation;
	@Getter
	private final Vector3D endLocation;

	@Getter
	private final MovementType movementType;

}
