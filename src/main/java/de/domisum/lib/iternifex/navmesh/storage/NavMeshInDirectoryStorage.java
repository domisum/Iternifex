package de.domisum.lib.iternifex.navmesh.storage;

import de.domisum.lib.auxilium.util.FileUtil;
import de.domisum.lib.auxilium.util.FileUtil.FileType;
import de.domisum.lib.iternifex.navmesh.NavMesh;
import de.domisum.lib.iternifex.navmesh.storage.serialization.NavMeshSerializer;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class NavMeshInDirectoryStorage implements NavMeshStorage
{

	private final File directory;
	private final NavMeshSerializer serializer = new NavMeshSerializer();


	// STORAGE
	@Override
	public Set<NavMesh> readAll()
	{
		Set<NavMesh> navMeshes = new HashSet<>();
		for(File file : FileUtil.listFilesRecursively(directory, FileType.FILE))
			navMeshes.add(read(file));

		return navMeshes;
	}

	@Override
	public NavMesh read(String navMeshId)
	{
		File file = getFile(navMeshId);

		return read(file);
	}

	private NavMesh read(File file)
	{
		String navMeshSerialized = FileUtil.readString(file);
		return serializer.deserialize(navMeshSerialized);
	}

	@Override
	public void store(NavMesh navMesh)
	{
		File navMeshFile = getFile(navMesh.getId());
		String navMeshSerialized = serializer.serialize(navMesh);
		FileUtil.writeString(navMeshFile, navMeshSerialized);
	}

	@Override
	public void delete(String navMeshId)
	{
		File navMeshFile = getFile(navMeshId);
		FileUtil.deleteFile(navMeshFile);
	}


	// UTIL
	private File getFile(String navMeshId)
	{
		return new File(directory, navMeshId+".navMesh");
	}

}
