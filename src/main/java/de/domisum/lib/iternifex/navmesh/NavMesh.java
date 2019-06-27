package de.domisum.lib.iternifex.navmesh;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.iternifex.navmesh.components.NavMeshPoint;
import de.domisum.lib.iternifex.navmesh.components.NavMeshTriangle;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode(of = "id")
public class NavMesh
{

	@Getter
	private final String id;

	@Getter
	private final Vector3D center;
	@Getter
	private final double radius;

	private final Map<String, NavMeshPoint> points;
	private final Map<String, NavMeshTriangle> triangles;


	// INIT
	public NavMesh(String id, Vector3D center, double radius, Set<NavMeshPoint> points, Set<NavMeshTriangle> triangles)
	{
		Validate.notNull(id, "id can't be null");
		Validate.notBlank(id, "id can't be blank");

		Validate.notNull(center, "center can't be null");
		Validate.inclusiveBetween(0, Double.MAX_VALUE, radius, "radius has to be positive");

		Validate.notNull(points, "points can't be null");
		Validate.notNull(triangles, "triangles can't be null");

		this.id = id;

		this.center = center;
		this.radius = radius;

		Map<String, NavMeshPoint> pointsMap = new HashMap<>();
		for(NavMeshPoint point : points)
			pointsMap.put(point.getId(), point);
		this.points = pointsMap;

		Map<String, NavMeshTriangle> triangleMap = new HashMap<>();
		for(NavMeshTriangle triangle : triangles)
			triangleMap.put(triangle.getId(), triangle);
		this.triangles = triangleMap;
	}


	// GETTERS
	public Set<NavMeshPoint> getPoints()
	{
		return new HashSet<>(points.values());
	}

	public Set<NavMeshTriangle> getTriangles()
	{
		return new HashSet<>(triangles.values());
	}

	public NavMeshTriangle getTriangleAt(Vector3D location)
	{
		for(NavMeshTriangle triangle : triangles.values())
			if(triangle.containsLocation(location))
				return triangle;

		return null;
	}

	public boolean containsLocation(Vector3D location)
	{
		return center.distanceTo(location) <= radius;
	}

}
