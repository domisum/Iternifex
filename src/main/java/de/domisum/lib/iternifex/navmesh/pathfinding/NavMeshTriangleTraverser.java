package de.domisum.lib.iternifex.navmesh.pathfinding;

import de.domisum.lib.auxilium.data.container.math.LineSegment3D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.data.container.tuple.Duo;
import de.domisum.lib.auxilium.util.PHR;
import de.domisum.lib.iternifex.debug.DebugLogger;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NavMeshTriangleTraverser
{

	// CONSTANTS
	private static final double DEFAULT_PORTAL_EDGE_DISTANCE = 0.45;

	// SETTINGS
	@Getter
	@Setter
	private double portalEdgeDistance = DEFAULT_PORTAL_EDGE_DISTANCE;

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

		private int currentTargetTriangleIndex = 1;
		private int funnelPointLeftTriangleIndex = -1;
		private int funnelPointRightTriangleIndex = -1;

		// OUTPUT
		private final List<PathSegment> pathSegments = new ArrayList<>();


		// TRAVERSE
		public List<PathSegment> traverse()
		{
			DebugLogger.log("starting traversal");
			reachPoint(getImprovedStartLocation());
			traverseEdges(triangleSequence.size());

			DebugLogger.log("upcoming: arrive at end location");
			arriveAtLocation(endLocation);

			DebugLogger.log("path segments: "+pathSegments);
			return new ArrayList<>(pathSegments);
		}

		/*
		 * moves start location off triangle edges.
		 *
		 * this is done in order to avoid issues with vectors to portal points which are directly opposed, which in turn
		 * would make it impossible to differentiate left and right point
		 */
		private Vector3D getImprovedStartLocation()
		{
			if(triangleSequence.isEmpty())
				return startLocation;

			NavMeshTriangle startTriangle = triangleSequence.get(0);
			Vector3D startLocationToStartTriangleCenter = startTriangle.getCenter().subtract(startLocation);

			Vector3D startLocationOffset = startLocationToStartTriangleCenter.normalize().multiply(0.01);
			return startLocation.add(startLocationOffset);
		}


		// EDGE TRAVERSAL
		private void traverseEdges(int indexBound)
		{
			for(; currentTargetTriangleIndex < indexBound; currentTargetTriangleIndex++)
			{
				NavMeshTriangle triangleBefore = triangleSequence.get(currentTargetTriangleIndex-1);
				NavMeshTriangle triangle = triangleSequence.get(currentTargetTriangleIndex);
				traverseEdge(triangleBefore, triangle);
			}
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


		// portal traversal
		private void traversePortal(NavMeshTriangle from, NavMeshTriangle to)
		{
			DebugLogger.log(PHR.rcs("Traversing portal from triangle '{}' to triangle '{}'", from.getId(), to.getId()));

			Duo<NavMeshPoint> sharedPoints = getSharedPoints(from, to);
			LineSegment3D portal = new LineSegment3D(sharedPoints.getA(), sharedPoints.getB()).getShortenedBothEnds(
					portalEdgeDistance);

			Vector3D currentToA = portal.getA().subtract(currentLocation);
			Vector3D currentToB = portal.getB().subtract(currentLocation);
			Vector3D portalPointLeft = isLeftOf(currentToA, currentToB, true) ? portal.getA() : portal.getB();
			Vector3D portalPointRight = isLeftOf(currentToA, currentToB, true) ? portal.getB() : portal.getA();

			boolean initialized = tryInitializeFunnelPoints(portalPointLeft, portalPointRight);
			if(initialized)
				return;

			handleFunnel(portalPointLeft, portalPointRight);
		}

		private boolean tryInitializeFunnelPoints(Vector3D portalPointLeft, Vector3D portalPointRight)
		{
			if(funnelPointLeft != null)
				return false;

			DebugLogger.log("funnel points not set, setting them to current portal points");

			funnelPointLeft = portalPointLeft;
			funnelPointRight = portalPointRight;
			funnelPointLeftTriangleIndex = currentTargetTriangleIndex;
			funnelPointRightTriangleIndex = currentTargetTriangleIndex;

			return true;
		}

		private void handleFunnel(Vector3D portalPointLeft, Vector3D portalPointRight)
		{
			Vector3D toFunnelPointLeft = funnelPointLeft.subtract(currentLocation);
			Vector3D toPortalPointLeft = portalPointLeft.subtract(currentLocation);

			Vector3D toFunnelPointRight = funnelPointRight.subtract(currentLocation);
			Vector3D toPortalPointRight = portalPointRight.subtract(currentLocation);


			boolean leftCornerNewWaypoint = tryAcceptLeftCornerAsNewWaypoint(toFunnelPointLeft,
					toPortalPointLeft,
					toPortalPointRight
			);
			if(leftCornerNewWaypoint)
				return;

			boolean rightCornerNewWaypoint = tryAcceptRightCornerAsNewWaypoint(toPortalPointLeft,
					toFunnelPointRight,
					toPortalPointRight
			);
			if(rightCornerNewWaypoint)
				return;

			tryRestrictFunnelOnLeftSide(portalPointLeft, toFunnelPointLeft, toPortalPointLeft);
			tryRestrictFunnelOnRightSide(portalPointRight, toFunnelPointRight, toPortalPointRight);
		}

		private boolean tryAcceptLeftCornerAsNewWaypoint(
				Vector3D toFunnelPointLeft, Vector3D toPortalPointLeft, Vector3D toPortalPointRight)
		{
			if(isLeftOf(toPortalPointLeft, toFunnelPointLeft, false) && isLeftOf(toPortalPointRight, toFunnelPointLeft, false))
			{
				pathToFunnelPointLeft(false);
				return true;
			}

			return false;
		}

		private boolean tryAcceptRightCornerAsNewWaypoint(
				Vector3D toPortalPointLeft, Vector3D toFunnelPointRight, Vector3D toPortalPointRight)
		{
			if(isRightOf(toPortalPointLeft, toFunnelPointRight, false) && isRightOf(toPortalPointRight,
					toFunnelPointRight,
					false
			))
			{
				pathToFunnelPointRight(false);
				return true;
			}

			return false;
		}

		private void tryRestrictFunnelOnLeftSide(Vector3D portalPointLeft, Vector3D toFunnelPointLeft, Vector3D toPortalPointLeft)
		{
			if(isRightOf(toPortalPointLeft, toFunnelPointLeft, true))
			{
				DebugLogger.log("restricting funnel on left side");

				funnelPointLeft = portalPointLeft;
				funnelPointLeftTriangleIndex = currentTargetTriangleIndex;
			}
		}

		private void tryRestrictFunnelOnRightSide(
				Vector3D portalPointRight, Vector3D toFunnelPointRight, Vector3D toPortalPointRight)
		{
			if(isLeftOf(toPortalPointRight, toFunnelPointRight, true))
			{
				DebugLogger.log("restricting funnel on right side");

				funnelPointRight = portalPointRight;
				funnelPointRightTriangleIndex = currentTargetTriangleIndex;
			}
		}


		// ladder traversal
		private void traverseLadder(NavMeshTriangle from, NavMeshEdgeLadder ladder)
		{
			DebugLogger.log(PHR.rcs("traversing ladder from triangle '{}' to '{}'", from, ladder.getOther(from)));

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
			reachPoint(ladderEndLocation);
		}


		// used throughout
		private int pathToFunnelPointLeft(boolean triangleIndexPlusOne)
		{
			DebugLogger.log("creating path to funnel point left "+funnelPointLeft);

			PathSegmentWalk pathSegmentWalk = new PathSegmentWalk(currentLocation, funnelPointLeft);
			pathSegments.add(pathSegmentWalk);

			int retraverseUpToIndex = funnelPointLeftTriangleIndex;
			currentTargetTriangleIndex = funnelPointLeftTriangleIndex+(triangleIndexPlusOne ? 1 : 0);
			reachPoint(funnelPointLeft);

			return retraverseUpToIndex;
		}

		private int pathToFunnelPointRight(boolean triangleIndexPlusOne)
		{
			DebugLogger.log("creating path to funnel point right "+funnelPointRight);

			PathSegmentWalk pathSegmentWalk = new PathSegmentWalk(currentLocation, funnelPointRight);
			pathSegments.add(pathSegmentWalk);

			int retraverseUpToIndex = funnelPointRightTriangleIndex;
			currentTargetTriangleIndex = funnelPointRightTriangleIndex+(triangleIndexPlusOne ? 1 : 0);
			reachPoint(funnelPointRight);

			return retraverseUpToIndex;
		}

		private void reachPoint(Vector3D point)
		{
			DebugLogger.log("reaching point: "+point);

			currentLocation = point;
			funnelPointLeft = null;
			funnelPointRight = null;
		}


		// ARRIVE AT
		private void arriveAtLocation(Vector3D location)
		{
			Integer retraverseUpToIndex = handleLastCornerIfNeeded(location);

			DebugLogger.log(PHR.rcs("arriving at location: {} (traverse again: {})", location, retraverseUpToIndex));

			if(retraverseUpToIndex != null)
			{
				traverseEdges(retraverseUpToIndex);
				DebugLogger.log("returning from inner traverseEdges");
			}

			PathSegmentWalk pathSegmentWalk = new PathSegmentWalk(currentLocation, location);
			DebugLogger.log("arriving; added path segment: "+pathSegmentWalk);

			pathSegments.add(pathSegmentWalk);
			reachPoint(location);
		}

		private Integer handleLastCornerIfNeeded(Vector3D location)
		{
			if(funnelPointLeft == null)
				return null;

			Vector3D toLocation = location.subtract(currentLocation);
			Vector3D toFunnelPointLeft = funnelPointLeft.subtract(currentLocation);
			Vector3D toFunnelPointRight = funnelPointRight.subtract(currentLocation);

			if(isLeftOf(toLocation, toFunnelPointLeft, false))
			{
				int retraverseUpToIndex = pathToFunnelPointLeft(true);
				return retraverseUpToIndex;
			}

			if(isRightOf(toLocation, toFunnelPointRight, false))
			{
				int retraverseUpToIndex = pathToFunnelPointRight(true);
				return retraverseUpToIndex;
			}

			return null;
		}

	}


	// UTIL
	private static Duo<NavMeshPoint> getSharedPoints(NavMeshTriangle triangle1, NavMeshTriangle triangle2)
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
