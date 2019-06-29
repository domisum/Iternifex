package de.domisum.lib.iternifex.navmesh.components;

import de.domisum.lib.auxilium.data.container.math.Vector2D;
import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.data.container.math.shape.Polygon2D;
import de.domisum.lib.iternifex.Node;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EqualsAndHashCode(of = "id")
public class NavMeshTriangle implements Node<NavMeshTriangle, NavMeshEdge>
{

	// MAIN ATTRUBUTES
	@Getter
	private final String id;

	@Getter
	private final NavMeshPoint pointA;
	@Getter
	private final NavMeshPoint pointB;
	@Getter
	private final NavMeshPoint pointC;

	// CONNECTIVITY
	private final Set<NavMeshEdge> edges = new HashSet<>();


	// INIT
	public NavMeshTriangle(NavMeshPoint pointA, NavMeshPoint pointB, NavMeshPoint pointC)
	{
		this(UUID.randomUUID().toString(), pointA, pointB, pointC);
	}

	public NavMeshTriangle(String id, NavMeshPoint pointA, NavMeshPoint pointB, NavMeshPoint pointC)
	{
		Validate.notNull(id, "id can't be null");
		Validate.notBlank(id, "id can't be empty");

		Validate.notNull(pointA, "pointA can't be null");
		Validate.notNull(pointB, "pointB can't be null");
		Validate.notNull(pointC, "pointC can't be null");

		this.id = id;

		this.pointA = pointA;
		this.pointB = pointB;
		this.pointC = pointC;
	}


	// OBJECT
	@Override
	public String toString()
	{
		return "NavMeshTriangle{"+"id='"+id+'\''+", pointA="+pointA+", pointB="+pointB+", pointC="+pointC+'}';
	}


	// GETTERS
	public boolean containsLocation(Vector3D location)
	{
		Vector2D pointA2D = new Vector2D(pointA.getX(), pointA.getZ());
		Vector2D pointB2D = new Vector2D(pointB.getX(), pointB.getZ());
		Vector2D pointC2D = new Vector2D(pointC.getX(), pointC.getZ());

		Polygon2D triangleAsPolygon2D = new Polygon2D(pointA2D, pointB2D, pointC2D);
		Vector2D location2D = new Vector2D(location.getX(), location.getZ());
		if(!triangleAsPolygon2D.contains(location2D))
			return false;

		// TODO height check

		return true;
	}

	public Vector3D getCenter()
	{
		return pointA.add(pointB).add(pointC).divide(3);
	}


	// NODE
	@Override
	public Set<NavMeshEdge> getEdges()
	{
		return Collections.unmodifiableSet(edges);
	}

	@Override
	public double getHeuristicWeightTo(NavMeshTriangle otherNode)
	{
		return getCenter().distanceTo(otherNode.getCenter());
	}


	public void addEdge(NavMeshEdge edge)
	{
		edges.add(edge);
	}

	public void removeEdge(NavMeshEdge edge)
	{
		edges.remove(edge);
	}

}
