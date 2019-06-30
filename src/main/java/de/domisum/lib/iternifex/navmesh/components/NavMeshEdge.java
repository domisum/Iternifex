package de.domisum.lib.iternifex.navmesh.components;

import de.domisum.lib.iternifex.Edge;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class NavMeshEdge implements Edge<NavMeshTriangle, NavMeshEdge>
{
	@Getter
	private final NavMeshTriangle triangleA;
	@Getter
	private final NavMeshTriangle triangleB;


	// INIT
	protected NavMeshEdge(NavMeshTriangle triangleA, NavMeshTriangle triangleB)
	{
		Validate.notNull(triangleA, "triangleA can't be null");
		Validate.notNull(triangleB, "triangleB can't be null");

		this.triangleA = triangleA;
		this.triangleB = triangleB;
	}


	// OBJECT
	@Override
	public final boolean equals(Object o)
	{
		if(this == o)
			return true;
		if((o == null) || (getClass() != o.getClass()))
			return false;

		NavMeshEdge that = (NavMeshEdge) o;
		boolean equalsRightWayAround = triangleA.equals(that.triangleA) && triangleB.equals(that.triangleB);
		boolean equalsInverted = triangleA.equals(that.triangleB) && triangleB.equals(that.triangleA);
		return equalsRightWayAround || equalsInverted;
	}

	@Override
	public final int hashCode()
	{
		return Objects.hash(triangleA)+Objects.hash(triangleB);
	}


	// EDGE
	@Override
	public Set<NavMeshTriangle> getNodes()
	{
		return new HashSet<>(Arrays.asList(triangleA, triangleB));
	}

}
