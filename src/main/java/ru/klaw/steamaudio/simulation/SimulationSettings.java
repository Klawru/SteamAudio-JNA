package ru.klaw.steamaudio.simulation;

import com.sun.jna.Structure;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.klaw.utility.PhononStructure;

@NoArgsConstructor
@AllArgsConstructor
@Structure.FieldOrder({"sceneType", "maxNumOcclusionSamples", "numRays", "numDiffuseSamples", "numBounces", "numThreads", "irDuration", "ambisonicsOrder", "maxConvolutionSources", "bakingBatchSize", "irradianceMinDistance"})
public class SimulationSettings extends PhononStructure {
    /**
     * @see SceneType
     * The ray tracer to use for simulation. \see IPLSceneType.
     * C type : IPLSceneType
     */
    public SceneType sceneType;
    /**
     * The maximum number of rays to trace from the listener to a
     * source when simulating volumetric occlusion. Increasing this
     * number allows increased smoothness of occlusion transitions,
     * but also increases memory consumption. Any positive integer
     * may be specified, but typical values are in the range of 32 to
     * 512.
     * C type : IPLint32
     */
    public int maxNumOcclusionSamples;
    /**
     * The number of rays to trace from the listener. Increasing this
     * number increases the accuracy of the simulation, but also
     * increases CPU usage. Any positive integer may be specified,
     * but typical values are in the range of 1024 to 131072.
     * C type : IPLint32
     */
    public int numRays;
    /**
     * The number of directions to consider when a ray bounces off
     * a diffuse (or partly diffuse) surface. Increasing this number
     * increases the accuracy of diffuse reflections, and does not
     * significantly impact CPU usage. Any positive integer may be
     * specified, but typical values are in the range of 32 to 4096.
     * C type : IPLint32
     */
    public int numDiffuseSamples;
    /**
     * The maximum number of times any ray can bounce within the scene.
     * Increasing this number allows the simulation to more accurately
     * model reverberant spaces, at the cost of increased CPU usage.
     * Any positive integer may be specified, but typical values are
     * in the range of 1 to 32.
     * C type : IPLint32
     */
    public int numBounces;
    /**
     * The number of threads to create for the simulation. The performance
     * improves linearly with the number of threads upto the number of
     * physical cores available on the CPU.
     * C type : IPLint32
     */
    public int numThreads;
    /**
     * The time delay between a sound being emitted and the last
     * audible reflection. Echoes and reverberation longer than this
     * amount will not be modeled by the simulation. Any positive
     * number may be specified, but typical values are in the range
     * of 0.5 to 4.0.
     * C type : IPLfloat32
     */
    public float irDuration;
    /**
     * The amount of directional detail in the simulation results.
     * Phonon encodes the simulation results using Ambisonics.
     * Increasing this number increases the amount of directional
     * detail in the simulated acoustics, but at the cost of
     * increased CPU usage and memory consumption. Supported values
     * are between 0 and 3.
     * C type : IPLint32
     */
    public int ambisonicsOrder;
    /**
     * The maximum number of sound sources that can be simulated
     * and rendered using a Convolution Effect object at any point
     * in time. If you attempt to create more than this many
     * Convolution Effect objects, creation will fail. Increasing
     * this number allows more sound sources to be rendered with
     * sound propagation effects, but at the cost of increased
     * memory consumption.
     * C type : IPLint32
     */
    public int maxConvolutionSources;
    /**
     * The number of probes that should be baked simultaneously.
     * Only used if sceneType is set to RADEONRAYS, ignored otherwise. Set this to
     * 1 unless you are creating a Scene for the purposes of
     * baking indirect sound using iplBakeReverb,
     * iplBakePropagation, or iplBakeStaticListener.
     * C type : IPLint32
     */
    public int bakingBatchSize;
    /**
     * The minimum distance between a source and a scene surface,
     * used when calculating the energy received at the surface from
     * the source during indirect sound simulation. Increasing this
     * number reduces the loudness of reflections when standing
     * close to a wall; decreasing this number results in a more
     * physically realistic model.
     * C type : IPLfloat32
     */
    public float irradianceMinDistance;
}
