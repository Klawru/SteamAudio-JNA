package ru.klaw.steamaudio.acousticprobes;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"placement", "spacing", "heightAboveFloor", "maxOctreeTriangles", "maxOctreeDepth"})
public class ProbePlacementParams extends PhononStructure {
    /**
     * @see ProbePlacement
     * The placement algorithm to use for creating probes.
     * C type : IPLProbePlacement
     */
    public ProbePlacement placement;

    /**
     * Spacing between probes along the horizontal plane.
     * Only used if placement is UNIFORMFLOOR.
     * C type : IPLfloat32
     */
    public float spacing;
    /**
     * Height of the probes above the closest floor or terrain
     * surfaces.
     * Only used if placement is UNIFORMFLOOR.
     * C type : IPLfloat32
     */
    public float heightAboveFloor;
    /**
     * The maximum number of triangles to store in an octree leaf
     * node.
     * Only used if placement is OCTREE.
     * C type : IPLint32
     */
    public int maxOctreeTriangles;
    /**
     * The maximum depth of the octree. Increasing this value increases
     * density of the generated probes.
     * Only used if placement is OCTREE.
     * C type : IPLint32
     */
    public int maxOctreeDepth;

    private ProbePlacementParams(ProbePlacement probePlacement) {
        this.placement = probePlacement;
    }

    /**
     * @see ProbePlacement
     */
    @Deprecated
    public ProbePlacementParams(ProbePlacement probePlacement, int maxOctreeTriangles, int maxOctreeDepth) {
        this.placement = probePlacement;
        this.maxOctreeTriangles = maxOctreeTriangles;
        this.maxOctreeDepth = maxOctreeDepth;
    }

    public ProbePlacementParams(ProbePlacement probePlacement, float spacing, float heightAboveFloor) {
        this.placement = probePlacement;
        this.spacing = spacing;
        this.heightAboveFloor = heightAboveFloor;
    }


    @NoArgsConstructor
    public static class ByValue extends ProbePlacementParams implements Structure.ByValue {
        public ByValue(ProbePlacement placement) {
            super(placement);
        }

        @Deprecated
        public ByValue(ProbePlacement probePlacement, int maxOctreeTriangles, int maxOctreeDepth) {
            super(probePlacement, maxOctreeTriangles, maxOctreeDepth);
        }

        public ByValue(ProbePlacement probePlacement, float spacing, float heightAboveFloor) {
            super(probePlacement, spacing, heightAboveFloor);
        }
    }

}
