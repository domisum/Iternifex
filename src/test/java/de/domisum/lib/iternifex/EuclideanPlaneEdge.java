package de.domisum.lib.iternifex;

import de.domisum.lib.iternifex.generic.Edge;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class EuclideanPlaneEdge implements Edge<EuclideanPlaneNode, EuclideanPlaneEdge>
{

	private final EuclideanPlaneNode nodeA;
	private final EuclideanPlaneNode nodeB;

	@Getter
	private final double weight;


	@Override
	public Set<EuclideanPlaneNode> getNodes()
	{
		return new HashSet<>(Arrays.asList(nodeA, nodeB));
	}

}
