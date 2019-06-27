package de.domisum.lib.iternifex.navmesh;

import de.domisum.lib.auxilium.contracts.serialization.JsonSerializer;
import de.domisum.lib.auxilium.data.container.tuple.Duo;
import de.domisum.lib.iternifex.Edge;
import de.domisum.lib.iternifex.navmesh.components.NavMeshPoint;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import de.domisum.lib.iternifex.navmesh.components.edges.NavMeshEdgeLadder;
import de.domisum.lib.iternifex.navmesh.components.edges.NavMeshEdgeWalk;

import java.util.HashSet;
import java.util.Set;

public class NavMeshSerializer implements JsonSerializer<NavMesh>
{

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
					.append("point ")
					.append(point.getId())
					.append(" ")
					.append(point.getX())
					.append(" ")
					.append(point.getY())
					.append(" ")
					.append(point.getZ())
					.append("\n");
	}

	private void writeTriangles(NavMesh navMesh, StringBuilder navMeshString)
	{
		for(NavMeshTriangle triangle : navMesh.getTriangles())
			navMeshString
					.append("triangle ")
					.append(triangle.getId())
					.append(" ")
					.append(triangle.getPointA().getId())
					.append(" ")
					.append(triangle.getPointB().getId())
					.append(" ")
					.append(triangle.getPointC())
					.append("\n");
	}

	private void writeEdges(NavMesh navMesh, StringBuilder navMeshString)
	{
		Set<Duo<NavMeshTriangle>> edgesSerialized = new HashSet<>();
		for(NavMeshTriangle triangle : navMesh.getTriangles())
			for(Edge<NavMeshTriangle> edge : triangle.getEdges())
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
			StringBuilder navMeshString, NavMeshTriangle triangle, Edge<NavMeshTriangle> edge, NavMeshTriangle otherTriangle)
	{
		navMeshString.append("edge ").append(triangle.getId()).append(" ").append(otherTriangle.getId());
		if(edge instanceof NavMeshEdgeWalk)
			;
		else if(edge instanceof NavMeshEdgeLadder)
		{
			NavMeshEdgeLadder ladder = (NavMeshEdgeLadder) edge;
			navMeshString
					.append(" ladder ")
					.append(ladder.getBottomLadderLocation().getX())
					.append(" ")
					.append(ladder.getBottomLadderLocation().getY())
					.append(" ")
					.append(ladder.getBottomLadderLocation().getZ())
					.append(" ")
					.append(ladder.getTopLadderLocation().getX())
					.append(" ")
					.append(ladder.getTopLadderLocation().getY())
					.append(" ")
					.append(ladder.getTopLadderLocation().getZ());
		}
	}

	private void writeNavMeshProperties(NavMesh navMesh, StringBuilder navMeshString)
	{
		navMeshString
				.append("navMesh ")
				.append(navMesh.getCenter().getX())
				.append(" ")
				.append(navMesh.getCenter().getY())
				.append(" ")
				.append(navMesh.getCenter().getZ())
				.append(" ")
				.append(navMesh.getRadius());
	}

	@Override
	public NavMesh deserialize(String navMeshString)
	{
		return null;
	}

}
