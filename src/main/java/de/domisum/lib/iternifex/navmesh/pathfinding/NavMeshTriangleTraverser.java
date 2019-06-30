package de.domisum.lib.iternifex.navmesh.pathfinding;

import de.domisum.lib.auxilium.data.container.math.LineSegment3D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.data.container.tuple.Duo;
import de.domisum.lib.iternifex.DebugSettings;
import de.domisum.lib.iternifex.navmesh.components.NavMeshEdge;
import de.domisum.lib.iternifex.navmesh.components.NavMeshPoint;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import de.domisum.lib.iternifex.navmesh.components.edges.NavMeshEdgeLadder;
import de.domisum.lib.iternifex.navmesh.components.edges.NavMeshEdgeWalk;
import de.domisum.lib.iternifex.navmesh.pathfinding.path.PathSegment;
import de.domisum.lib.iternifex.navmesh.pathfinding.path.PathSegmentLadder;
import de.domisum.lib.iternifex.navmesh.pathfinding.path.PathSegmentWalk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NavMeshTriangleTraverser
{

	private final Logger logger = LoggerFactory.getLogger(getClass());


	// SETTINGS
	@Getter
	@Setter
	private double portalEdgeDistance = 0.4;

	// TRAVERSE
	public List<PathSegment> traverse(List<NavMeshTriangle> triangleSequence, Vector3D startLocation, Vector3D endLocation)
	{
		return new TraverseMethodObject(triangleSequence, startLocation, endLocation).traverse();
	}

	@RequiredArgsConstructor
	private class TraverseMethodObject
	{

		// INPUT
		private final List<NavMeshTriangle> triangleSequence;
		private final Vector3D startLocation;
		private final Vector3D endLocation;

		// STATUS
		private Vector3D currentLocation;
		private Vector3D funnelPointLeft = null;
		private Vector3D funnelPointRight = null;

		// OUTPUT
		private final List<PathSegment> pathSegments = new ArrayList<>();


		// TRAVERSE
		public List<PathSegment> traverse()
		{
			currentLocation = startLocation;

			NavMeshTriangle lastTriangle = null;
			for(NavMeshTriangle triangle : triangleSequence)
			{
				if(lastTriangle != null)
					traverseEdge(lastTriangle, triangle);

				lastTriangle = triangle;
			}

			arriveAtLocation(endLocation);

			return new ArrayList<>(pathSegments);
		}

		private void traverseEdge(NavMeshTriangle from, NavMeshTriangle to)
		{
			NavMeshEdge edge = from.getEdgeTo(to);
			if(edge instanceof NavMeshEdgeWalk)
				traversePortal(from, to);
			else if(edge instanceof NavMeshEdgeLadder)
				traverseLadder(from, (NavMeshEdgeLadder) edge);
			else
				throw new UnsupportedOperationException("triangle traversal doesn't yet support edge type: "+edge.getClass());
		}

		private void traversePortal(NavMeshTriangle from, NavMeshTriangle to)
		{
			if(DebugSettings.DEBUG_ACTIVE)
				logger.info("Traversing portal from triangle '{}' to triangle '{}'", from.getId(), to.getId());

			Duo<NavMeshPoint> sharedPoints = getSharedPoints(from, to);
			LineSegment3D portal = new LineSegment3D(sharedPoints.getA(), sharedPoints.getB()).getShortenedBothEnds(
					portalEdgeDistance);

			Vector3D currentToA = portal.getA().subtract(currentLocation);
			Vector3D currentToB = portal.getB().subtract(currentLocation);
			NavMeshPoint portalPointLeft = isLeftOf(currentToA, currentToB, true) ? sharedPoints.getA() : sharedPoints.getB();
			NavMeshPoint portalPointRight = isLeftOf(currentToA, currentToB, true) ? sharedPoints.getB() : sharedPoints.getA();

			if(funnelPointLeft == null)
			{
				if(DebugSettings.DEBUG_ACTIVE)
					logger.info("funnel points not set, setting them to current portal points");

				funnelPointLeft = portalPointLeft;
				funnelPointRight = portalPointRight;
				return;
			}

			Vector3D toFunnelPointLeft = funnelPointLeft.subtract(currentLocation);
			Vector3D toPortalPointLeft = portalPointLeft.subtract(currentLocation);

			Vector3D toFunnelPointRight = funnelPointRight.subtract(currentLocation);
			Vector3D toPortalPointRight = portalPointRight.subtract(currentLocation);

			// accept left corner as new waypoint
			if(isLeftOf(toPortalPointLeft, toFunnelPointLeft, false) && isLeftOf(toPortalPointRight, toFunnelPointLeft, false))
			{
				pathToFunnelPointLeft();
				return;
			}

			// accept right corner as new waypoint
			if(isRightOf(toPortalPointLeft, toFunnelPointRight, false) && isRightOf(toPortalPointRight,
					toFunnelPointRight,
					false
			))
			{
				pathToFunnelPointRight();

				return;
			}

			// further restrict funnel on left side
			if(isRightOf(toPortalPointLeft, toFunnelPointLeft, true))
			{
				if(DebugSettings.DEBUG_ACTIVE)
					logger.info("restricting funnel on left side");

				funnelPointLeft = portalPointLeft;
			}

			// further restrict funnel on right side
			if(isLeftOf(toPortalPointRight, toFunnelPointRight, true))
			{
				if(DebugSettings.DEBUG_ACTIVE)
					logger.info("restricting funnel on right side");

				funnelPointRight = portalPointRight;
			}
		}

		private void pathToFunnelPointLeft()
		{
			if(DebugSettings.DEBUG_ACTIVE)
				logger.info("creating path to funnel point left");

			PathSegmentWalk pathSegmentWalk = new PathSegmentWalk(currentLocation, funnelPointLeft);
			pathSegments.add(pathSegmentWalk);

			currentLocation = funnelPointLeft;
			funnelPointLeft = null;
			funnelPointRight = null;
		}

		private void pathToFunnelPointRight()
		{
			if(DebugSettings.DEBUG_ACTIVE)
				logger.info("creating path to funnel point right");

			PathSegmentWalk pathSegmentWalk = new PathSegmentWalk(currentLocation, funnelPointRight);
			pathSegments.add(pathSegmentWalk);

			currentLocation = funnelPointRight;
			funnelPointLeft = null;
			funnelPointRight = null;
		}

		private Duo<NavMeshPoint> getSharedPoints(NavMeshTriangle triangle1, NavMeshTriangle triangle2)
		{
			Set<NavMeshPoint> alreadyProcessedPoints = new HashSet<>(triangle1.getPoints());
			List<NavMeshPoint> sharedPoints = new ArrayList<>();

			for(NavMeshPoint point : triangle2.getPoints())
			{
				if(alreadyProcessedPoints.contains(point))
					sharedPoints.add(point);

				alreadyProcessedPoints.add(point);
			}

			if(sharedPoints.size() != 2)
				throw new IllegalArgumentException("supplied points don't have 2 shared points");

			return new Duo<>(sharedPoints.get(0), sharedPoints.get(1));
		}

		private void traverseLadder(NavMeshTriangle from, NavMeshEdgeLadder ladder)
		{
			Vector3D ladderStartLocation = ladder.getTriangleA().equals(from) ?
					ladder.getBottomLadderLocation() :
					ladder.getTopLadderLocation();

			Vector3D ladderEndLocation = ladder.getTriangleA().equals(from) ?
					ladder.getTopLadderLocation() :
					ladder.getBottomLadderLocation();

			arriveAtLocation(ladderStartLocation);

			PathSegmentLadder segmentLadder = new PathSegmentLadder(ladderStartLocation,
					ladderEndLocation,
					ladder.getDirection()
			);
			pathSegments.add(segmentLadder);
			currentLocation = ladderEndLocation;
		}

		private void arriveAtLocation(Vector3D location)
		{
			handleLastCornerIfNeeded(location);

			PathSegmentWalk pathSegmentWalk = new PathSegmentWalk(currentLocation, location);
			pathSegments.add(pathSegmentWalk);
			currentLocation = location;
		}

		private void handleLastCornerIfNeeded(Vector3D location)
		{
			if(funnelPointLeft == null)
				return;

			Vector3D toLocation = location.subtract(currentLocation);
			Vector3D toFunnelPointLeft = funnelPointLeft.subtract(currentLocation);
			Vector3D toFunnelPointRight = funnelPointRight.subtract(currentLocation);

			if(isLeftOf(toLocation, toFunnelPointLeft, false))
				pathToFunnelPointLeft();

			if(isRightOf(toLocation, toFunnelPointRight, false))
				pathToFunnelPointRight();
		}

	}


	// UTIL
	private static boolean isRightOf(Vector3D v1, Vector3D v2, boolean onColinear)
	{
		double crossY = v1.crossProduct(v2).getY();

		if(crossY == 0)
			return onColinear;

		return crossY > 0;
	}

	private static boolean isLeftOf(Vector3D v1, Vector3D v2, boolean onColinear)
	{
		double crossY = v1.crossProduct(v2).getY();

		if(crossY == 0)
			return onColinear;

		return crossY < 0;
	}

}
