package de.domisum.lib.iternifex.navmesh.pathfinding;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.PHR;
import de.domisum.lib.auxilium.util.time.ProfilerStopWatch;
import de.domisum.lib.iternifex.navmesh.NavMesh;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import de.domisum.lib.iternifex.navmesh.pathfinding.path.PathSegment;
import de.domisum.lib.iternifex.pathfinding.AStarPathfinder;
import de.domisum.lib.iternifex.pathfinding.PathfindingException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class NavMeshPathfinder
{

	private final Logger logger = Logger.getLogger("navMeshPathfinder");


	// DEPENDENCIES
	private final AStarPathfinder nodePathfinder = new AStarPathfinder();
	private final NavMeshTriangleTraverser navMeshTriangleTraverser = new NavMeshTriangleTraverser();


	// PATHFINDING
	public List<PathSegment> findPath(NavMesh navMesh, Vector3D start, Vector3D end) throws PathfindingException
	{
		NavMeshTriangle startTriangle = getStartTriangle(navMesh, start);
		NavMeshTriangle endTriangle = getEndTriangle(navMesh, end);

		List<NavMeshTriangle> triangleSequence = findTriangleSequence(start, end, startTriangle, endTriangle);
		List<PathSegment> pathSegments = findPathSegments(start, end, triangleSequence);

		return pathSegments;
	}


	private NavMeshTriangle getStartTriangle(NavMesh navMesh, Vector3D start) throws PathfindingException
	{
		NavMeshTriangle startTriangle = navMesh.getTriangleAt(start);
		if(startTriangle == null)
			throw new PathfindingException("start point not on triangle");

		return startTriangle;
	}

	private NavMeshTriangle getEndTriangle(NavMesh navMesh, Vector3D end) throws PathfindingException
	{
		NavMeshTriangle endTriangle = navMesh.getTriangleAt(end);
		if(endTriangle == null)
			throw new PathfindingException("end point not on triangle");

		return endTriangle;
	}


	private List<NavMeshTriangle> findTriangleSequence(
			Vector3D start, Vector3D end, NavMeshTriangle startTriangle, NavMeshTriangle endTriangle) throws PathfindingException
	{
		ProfilerStopWatch stopWatch = new ProfilerStopWatch("triangleSequence");
		stopWatch.start();

		List<NavMeshTriangle> triangleSequence = nodePathfinder.findPath(startTriangle, endTriangle);

		stopWatch.stop();
		logger.info(PHR.r("Pathfinding from {} to {}: {}", start, end, stopWatch));
		return triangleSequence;
	}

	private List<PathSegment> findPathSegments(Vector3D start, Vector3D end, List<NavMeshTriangle> triangleSequence)
	{
		ProfilerStopWatch stopWatch = new ProfilerStopWatch("triangleTraverser");
		stopWatch.start();

		List<PathSegment> pathSegments = navMeshTriangleTraverser.traverse(triangleSequence, start, end);

		stopWatch.stop();
		logger.info(PHR.r("Pathfinding from {} to {}: {}", start, end, stopWatch));
		return pathSegments;
	}

}
