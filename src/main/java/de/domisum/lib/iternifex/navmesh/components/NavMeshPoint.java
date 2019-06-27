package de.domisum.lib.iternifex.navmesh.components;

import de.domisum.lib.auxilium.data.container.math.Vector3D;
import de.domisum.lib.auxilium.util.math.MathUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.util.UUID;

@EqualsAndHashCode(of = "id", callSuper = false)
public class NavMeshPoint extends Vector3D
{

	// MAIN ATTRUBUTES
	@Getter
	private final String id;


	// INIT
	public NavMeshPoint(double x, double y, double z)
	{
		this(UUID.randomUUID().toString(), x, y, z);
	}

	public NavMeshPoint(String id, double x, double y, double z)
	{
		super(x, y, z);

		Validate.notNull(id, "id can't be null");
		Validate.notBlank(id, "id can't be empty");

		this.id = id;
	}


	// OBJECT
	@Override
	public String toString()
	{
		return "NavMeshPoint{"+"id='"+id+'\''+", x="+MathUtil.round(getX(), 3)+", y="+MathUtil.round(getY(), 3)+", z="
				+MathUtil.round(getZ(), 3)+'}';
	}

}
