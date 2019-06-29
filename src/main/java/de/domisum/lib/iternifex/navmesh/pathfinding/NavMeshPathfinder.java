package de.domisum.lib.iternifex.navmesh.pathfinding;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.generic.pathfinding.AStarPathfinder;
import de.domisum.lib.iternifex.generic.pathfinding.PathfindingException;
import de.domisum.lib.iternifex.navmesh.NavMesh;
import de.domisum.lib.iternifex.navmesh.NavMeshRegistry;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class NavMeshPathfinder
{

	// DEPENDENCIES
	private final NavMeshRegistry navMeshRegistry;

	private final AStarPathfinder nodePathfinder = new AStarPathfinder();
	private final NavMeshTriangleTraverser navMeshTriangleTraverser = new NavMeshTriangleTraverser();


	// PATHFINDING
	public List<PathSegment> findPath(Vector3D start, Vector3D end) throws PathfindingException
	{
		NavMeshTriangle endTriangle = getEndTriangle(end);
		NavMeshTriangle startTriangle = getStartTriangle(start);

		if(!navMeshRegistry.getNavMeshAt(start).equals(navMeshRegistry.getNavMeshAt(end)))
			throw new PathfindingException("start and end are on different navMeshes");

		List<NavMeshTriangle> triangleSequence = nodePathfinder.findPath(startTriangle, endTriangle);
		List<PathSegment> pathSegments = navMeshTriangleTraverser.traverse(triangleSequence, start, end);

		return pathSegments;
	}

	private NavMeshTriangle getStartTriangle(Vector3D start) throws PathfindingException
	{
		NavMesh navMeshAtStart = navMeshRegistry.getNavMeshAt(start);
		if(navMeshAtStart == null)
			throw new PathfindingException("no navMesh at start position");

		NavMeshTriangle startTriangle = navMeshAtStart.getTriangleAt(start);
		if(startTriangle == null)
			throw new PathfindingException("start point not on triangle");

		return startTriangle;
	}

	private NavMeshTriangle getEndTriangle(Vector3D end) throws PathfindingException
	{
		NavMesh navMeshAtEnd = navMeshRegistry.getNavMeshAt(end);
		if(navMeshAtEnd == null)
			throw new PathfindingException("no navMesh at end position");

		NavMeshTriangle endTriangle = navMeshAtEnd.getTriangleAt(end);
		if(endTriangle == null)
			throw new PathfindingException("end point not on triangle");

		return endTriangle;
	}

}
