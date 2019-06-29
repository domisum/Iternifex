package de.domisum.lib.iternifex.navmesh.storage.serialization;

import de.domisum.lib.auxilium.contracts.serialization.JsonSerializer;
import de.domisum.lib.auxilium.data.container.direction.Direction2D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.data.container.tuple.Duo;
import de.domisum.lib.auxilium.util.PHR;
import de.domisum.lib.iternifex.navmesh.NavMesh;
import de.domisum.lib.iternifex.navmesh.components.NavMeshEdge;
import de.domisum.lib.iternifex.navmesh.components.NavMeshPoint;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import de.domisum.lib.iternifex.navmesh.components.edges.NavMeshEdgeLadder;
import de.domisum.lib.iternifex.navmesh.components.edges.NavMeshEdgeWalk;
import org.apache.commons.lang3.SerializationException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NavMeshSerializer implements JsonSerializer<NavMesh>
{

	// CONSTANTS
	private static final String LINE_SEPARATOR = "\n";
	@SuppressWarnings("RegExpRepeatedSpace")
	private static final String LINE_ELEMENT_SEPARATOR = "     ";

	private static final String POINT_LINE_PREFIX = "point";
	private static final String TRIANGLE_LINE_PREFIX = "triangle";
	private static final String EDGE_LINE_PREFIX = "edge";
	private static final String NAV_MESH_LINE_PREFIX = "navMesh";


	// SERIALIZATION
	@Override
	public String serialize(NavMesh navMesh)
	{
		StringBuilder navMeshString = new StringBuilder();

		writePoints(navMesh, navMeshString);
		writeTriangles(navMesh, navMeshString);
		writeEdges(navMesh, navMeshString);
		writeNavMeshProperties(navMesh, navMeshString);

		return navMeshString.toString();
	}

	private void writePoints(NavMesh navMesh, StringBuilder navMeshString)
	{
		for(NavMeshPoint point : navMesh.getPoints())
			navMeshString
					.append(POINT_LINE_PREFIX)
					.append(LINE_ELEMENT_SEPARATOR)
					.append(point.getId())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(point.getX())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(point.getY())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(point.getZ())
					.append(LINE_SEPARATOR);
	}

	private void writeTriangles(NavMesh navMesh, StringBuilder navMeshString)
	{
		for(NavMeshTriangle triangle : navMesh.getTriangles())
			navMeshString
					.append(TRIANGLE_LINE_PREFIX)
					.append(LINE_ELEMENT_SEPARATOR)
					.append(triangle.getId())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(triangle.getPointA().getId())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(triangle.getPointB().getId())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(triangle.getPointC().getId())
					.append(LINE_SEPARATOR);
	}

	private void writeEdges(NavMesh navMesh, StringBuilder navMeshString)
	{
		Set<Duo<NavMeshTriangle>> edgesSerialized = new HashSet<>();
		for(NavMeshTriangle triangle : navMesh.getTriangles())
			for(NavMeshEdge edge : triangle.getEdges())
			{
				NavMeshTriangle otherTriangle = edge.getOther(triangle);

				Duo<NavMeshTriangle> duo = new Duo<>(triangle, otherTriangle);
				if(edgesSerialized.contains(duo) || edgesSerialized.contains(duo.getInverted()))
					continue;

				writeEdge(navMeshString, triangle, edge, otherTriangle);
				edgesSerialized.add(duo);
			}
	}

	private void writeEdge(
			StringBuilder navMeshString, NavMeshTriangle triangle, NavMeshEdge edge, NavMeshTriangle otherTriangle)
	{
		navMeshString
				.append(EDGE_LINE_PREFIX)
				.append(LINE_ELEMENT_SEPARATOR)
				.append(triangle.getId())
				.append(LINE_ELEMENT_SEPARATOR)
				.append(otherTriangle.getId());
		if(edge instanceof NavMeshEdgeWalk)
			;
		else if(edge instanceof NavMeshEdgeLadder)
		{
			NavMeshEdgeLadder ladder = (NavMeshEdgeLadder) edge;
			navMeshString
					.append(LINE_ELEMENT_SEPARATOR)
					.append("ladder")
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getBottomLadderLocation().getX())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getBottomLadderLocation().getY())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getBottomLadderLocation().getZ())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getTopLadderLocation().getX())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getTopLadderLocation().getY())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getTopLadderLocation().getZ())
					.append(LINE_ELEMENT_SEPARATOR)
					.append(ladder.getDirection());
		}
		else
			throw new NavMeshSerializationException(
					"serialization of edge type '"+edge.getClass().getName()+"' is not yet supported");

		navMeshString.append(LINE_SEPARATOR);
	}

	private void writeNavMeshProperties(NavMesh navMesh, StringBuilder navMeshString)
	{
		navMeshString
				.append(NAV_MESH_LINE_PREFIX)
				.append(LINE_ELEMENT_SEPARATOR)
				.append(navMesh.getId())
				.append(LINE_ELEMENT_SEPARATOR)
				.append(navMesh.getCenter().getX())
				.append(LINE_ELEMENT_SEPARATOR)
				.append(navMesh.getCenter().getY())
				.append(LINE_ELEMENT_SEPARATOR)
				.append(navMesh.getCenter().getZ())
				.append(LINE_ELEMENT_SEPARATOR)
				.append(navMesh.getRadius());
	}


	@Override
	public NavMesh deserialize(String navMeshString)
	{
		Map<String, NavMeshPoint> pointMap = new HashMap<>();
		Map<String, NavMeshTriangle> triangleMap = new HashMap<>();

		String[] split = navMeshString.split(LINE_SEPARATOR);
		for(int i = 0; i < split.length; i++)
		{
			int lineNumber = i+1;

			String line = split[i];
			if(line.startsWith(POINT_LINE_PREFIX))
			{
				NavMeshPoint point = parsePoint(line);
				pointMap.put(point.getId(), point);
			}
			else if(line.startsWith(TRIANGLE_LINE_PREFIX))
			{
				NavMeshTriangle triangle = parseTriangle(line, pointMap);
				triangleMap.put(triangle.getId(), triangle);
			}
			else if(line.startsWith(EDGE_LINE_PREFIX))
				parseAndAddEdge(line, triangleMap, lineNumber);
			else if(line.startsWith(NAV_MESH_LINE_PREFIX))
				return parseNavMesh(pointMap, triangleMap, line);
		}

		throw new NavMeshSerializationException("navMesh string didn't contain final navMesh line");
	}

	private NavMeshPoint parsePoint(String line)
	{
		String[] lineSplit = line.split(LINE_ELEMENT_SEPARATOR);

		String id = lineSplit[1];
		double x = Double.parseDouble(lineSplit[2]);
		double y = Double.parseDouble(lineSplit[3]);
		double z = Double.parseDouble(lineSplit[4]);

		return new NavMeshPoint(id, x, y, z);
	}

	private NavMeshTriangle parseTriangle(String line, Map<String, NavMeshPoint> pointMap)
	{
		String[] lineSplit = line.split(LINE_ELEMENT_SEPARATOR);

		String id = lineSplit[1];
		String pointAId = lineSplit[2];
		String pointBId = lineSplit[3];
		String pointCId = lineSplit[4];

		NavMeshPoint pointA = pointMap.get(pointAId);
		NavMeshPoint pointB = pointMap.get(pointBId);
		NavMeshPoint pointC = pointMap.get(pointCId);

		return new NavMeshTriangle(id, pointA, pointB, pointC);
	}

	private void parseAndAddEdge(String line, Map<String, NavMeshTriangle> triangleMap, int lineNumber)
	{
		String[] lineSplit = line.split(LINE_ELEMENT_SEPARATOR);

		String triangleAId = lineSplit[1];
		String triangleBId = lineSplit[2];

		NavMeshTriangle triangleA = triangleMap.get(triangleAId);
		NavMeshTriangle triangleB = triangleMap.get(triangleBId);

		if(triangleA == null)
			throw new SerializationException(PHR.r("unknown triangle {} in line {}", triangleAId, lineNumber));
		if(triangleB == null)
			throw new SerializationException(PHR.r("unknown triangle {} in line {}", triangleBId, lineNumber));

		NavMeshEdge edge;
		if(lineSplit.length == 3)
			edge = new NavMeshEdgeWalk(triangleA, triangleB);
		else if("ladder".equals(lineSplit[3]))
		{
			double bottomLadderLocationX = Double.parseDouble(lineSplit[4]);
			double bottomLadderLocationY = Double.parseDouble(lineSplit[5]);
			double bottomLadderLocationZ = Double.parseDouble(lineSplit[6]);
			Vector3D bottomLadderLocation = new Vector3D(bottomLadderLocationX, bottomLadderLocationY, bottomLadderLocationZ);

			double topLadderLocationX = Double.parseDouble(lineSplit[6]);
			double topLadderLocationY = Double.parseDouble(lineSplit[7]);
			double topLadderLocationZ = Double.parseDouble(lineSplit[8]);
			Vector3D topLadderLocation = new Vector3D(topLadderLocationX, topLadderLocationY, topLadderLocationZ);

			Direction2D direction = Direction2D.valueOf(lineSplit[9]);

			edge = new NavMeshEdgeLadder(triangleA, triangleB, bottomLadderLocation, topLadderLocation, direction);
		}
		else
			throw new NavMeshSerializationException("deserialization of edge type '"+lineSplit[3]+"' not yet supported");

		triangleA.addEdge(edge);
		triangleB.addEdge(edge);
	}

	private NavMesh parseNavMesh(Map<String, NavMeshPoint> pointMap, Map<String, NavMeshTriangle> triangleMap, String line)
	{
		String[] lineSplit = line.split(LINE_ELEMENT_SEPARATOR);

		String id = lineSplit[1];

		double navMeshCenterX = Double.parseDouble(lineSplit[2]);
		double navMeshCenterY = Double.parseDouble(lineSplit[3]);
		double navMeshCenterZ = Double.parseDouble(lineSplit[4]);
		Vector3D navMeshCenter = new Vector3D(navMeshCenterX, navMeshCenterY, navMeshCenterZ);

		double navMeshRadius = Double.parseDouble(lineSplit[5]);

		return new NavMesh(id,
				navMeshCenter,
				navMeshRadius,
				new HashSet<>(pointMap.values()),
				new HashSet<>(triangleMap.values())
		);
	}

}
