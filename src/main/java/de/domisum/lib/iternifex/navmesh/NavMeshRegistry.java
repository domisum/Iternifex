package de.domisum.lib.iternifex.navmesh;

import de.domisum.lib.auxilium.data.container.math.Vector3D;

import java.util.HashMap;
import java.util.Map;

public class NavMeshRegistry
{

	private final Map<String, NavMesh> navMeshes = new HashMap<>();


	// GETTERS
	public NavMesh getNavMesh(String id)
	{
		return navMeshes.get(id);
	}

	public NavMesh getNavMeshAt(Vector3D location)
	{
		for(NavMesh navMesh : navMeshes.values())
			if(navMesh.containsLocation(location))
				return navMesh;

		return null;
	}


	// REGISTRATION
	public void register(NavMesh navMesh)
	{
		navMeshes.put(navMesh.getId(), navMesh);
	}

	public void unregister(NavMesh navMesh)
	{
		unregister(navMesh.getId());
	}

	public void unregister(String navMeshId)
	{
		navMeshes.get(navMeshId);
	}


	// IO
	public void save()
	{
		// TODO
	}

}
