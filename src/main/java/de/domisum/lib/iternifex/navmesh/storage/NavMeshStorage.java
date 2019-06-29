package de.domisum.lib.iternifex.navmesh.storage;

import de.domisum.lib.iternifex.navmesh.NavMesh;

import java.util.Set;

public interface NavMeshStorage
{

	Set<NavMesh> readAll();

	NavMesh read(String navMeshId);

	void store(NavMesh navMesh);

	default void delete(NavMesh navMesh)
	{
		delete(navMesh.getId());
	}

	void delete(String navMeshId);

}
