package ru.klaw.steamaudio;

import com.sun.jna.*;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import ru.klaw.steamaudio.acousticprobes.ProbePlacementParams;
import ru.klaw.steamaudio.audiobuffers.AudioBuffer;
import ru.klaw.steamaudio.audiobuffers.AudioFormat;
import ru.klaw.steamaudio.baking.BakedDataIdentifier;
import ru.klaw.steamaudio.baking.BakingSettings;
import ru.klaw.steamaudio.binauralrenderer.HrtfInterpolation;
import ru.klaw.steamaudio.binauralrenderer.HrtfParams;
import ru.klaw.steamaudio.computedevice.ComputeDeviceFilter;
import ru.klaw.steamaudio.directsound.DirectSoundEffectOptions;
import ru.klaw.steamaudio.directsound.DirectSoundPath;
import ru.klaw.steamaudio.directsound.Source;
import ru.klaw.steamaudio.directsound.enums.DirectOcclusionMethod;
import ru.klaw.steamaudio.directsound.enums.DirectOcclusionMode;
import ru.klaw.steamaudio.geometry.Sphere;
import ru.klaw.steamaudio.geometry.Vector3;
import ru.klaw.steamaudio.renderingsettings.RenderingSettings;
import ru.klaw.steamaudio.scene.Material;
import ru.klaw.steamaudio.scene.Matrix4x4;
import ru.klaw.steamaudio.scene.Triangle;
import ru.klaw.steamaudio.simulation.SceneType;
import ru.klaw.steamaudio.simulation.SimulationSettings;
import ru.klaw.steamaudio.simulation.SimulationType;
import ru.klaw.utility.PhononTypeMapper;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static ru.klaw.utility.Utility.mapOf;

public interface Phonon extends Library {
    String JNA_LIBRARY_NAME = "phonon";
    String version = "2.0-beta.18";

    NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(Phonon.JNA_LIBRARY_NAME);

    Phonon INSTANCE = Native.load(Phonon.JNA_LIBRARY_NAME, Phonon.class, mapOf(Library.OPTION_TYPE_MAPPER, PhononTypeMapper.INSTANCE));


    /**
     * Creates a Context object. A Context object must be created before creating any other API objects.
     *
     * @param logCallback      Callback for logging messages. Can be NULL.
     * @param allocateCallback Callback for allocating memory. Can be NULL.
     * @param freeCallback     Callback for freeing memory. Can be NULL.
     * @param context          [out] Handle to the created Context object.
     * @return @see Error Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateContext(IPLLogFunction, IPLAllocateFunction, IPLFreeFunction, IPLhandle*)</code>
     */
    int iplCreateContext(Phonon.LogFunction logCallback,
                         Phonon.AllocateFunction allocateCallback,
                         Phonon.FreeFunction freeCallback,
                         PointerByReference context);

    /**
     * Destroys a Context object. If any other API objects are still referencing the Context object, it will not be
     * destroyed; destruction occurs when the Context object's reference count reaches zero.
     *
     * @param context [in, out] Address of a handle to the Context object to destroy.
     *                Original signature : <code>void iplDestroyContext(IPLhandle*)</code>
     */
    void iplDestroyContext(PointerByReference context);

    /**
     * Performs last-minute cleanup and finalization. This function must be the last API function to be called before
     * your application exits.
     * Original signature : <code>void iplCleanup()</code>
     */
    void iplCleanup();

    /**
     * Calculates the relative direction from the listener to a sound source. The returned direction
     * vector is expressed in the listener's coordinate system.
     *
     * @param sourcePosition   World-space coordinates of the source.
     * @param listenerPosition World-space coordinates of the listener.
     * @param listenerAhead    World-space unit-length vector pointing ahead relative to the listener.
     * @param listenerUp       World-space unit-length vector pointing up relative to the listener.
     * @return A unit-length vector in the listener's coordinate space, pointing from the listener to the source.
     * Original signature : <code>IPLVector3 iplCalculateRelativeDirection(IPLVector3, IPLVector3, IPLVector3, IPLVector3)</code>
     */
    Vector3 iplCalculateRelativeDirection(Vector3 sourcePosition, Vector3 listenerPosition, Vector3 listenerAhead, Vector3 listenerUp);

    /**
     * Creates a Compute Device object. The same Compute Device must be used by the game engine and audio engine
     * parts of the Phonon integration. Depending on the OpenCL driver and device, this function may take some
     * time to execute, so do not call it from performance-sensitive code.
     *
     * @param context      The Context object used by the game engine.
     * @param device       [out] Handle to the created Compute Device object.
     * @param deviceFilter Constraints on the type of device to create.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateComputeDevice(IPLhandle, IPLComputeDeviceFilter, IPLhandle*)</code>
     */
    int iplCreateComputeDevice(Pointer context, ComputeDeviceFilter deviceFilter, PointerByReference device);

    /**
     * Destroys a Compute Device object. If any other API objects are still referencing the Compute Device object,
     * it will not be destroyed; destruction occurs when the object's reference count reaches zero.
     *
     * @param device [in, out] Address of a handle to the Compute Device object to destroy.
     *               Original signature : <code>void iplDestroyComputeDevice(IPLhandle*)</code>
     */
    void iplDestroyComputeDevice(PointerByReference device);

    /**
     * Creates a Scene object. A Scene object does not store any geometry information on its own; for that you
     * need to create one or more Static Mesh objects and add them to the Scene object. The Scene object
     * does contain an array of materials; all triangles in all Static Mesh objects refer to this array in order
     * to specify their material properties.
     *
     * @param context                   The Context object used by the game engine.
     * @param computeDevice             Handle to a Compute Device object. Only required if using Radeon Rays for
     *                                  ray tracing, may be NULL otherwise.
     * @param sceneType                 The ray tracer to use for scene representation and simulation.
     * @param numMaterials              The number of materials that are used to describe the various surfaces in
     *                                  the scene. Materials may not be added or removed once the Scene object is
     *                                  created.
     * @param materials                 Array containing all the materials in the Scene object. The number of
     *                                  IPLMaterial objects in the array must be equal to the value of numMaterials
     *                                  passed to ::iplCreateScene.
     * @param closestHitCallback        Pointer to a function that returns the closest hit along a ray.
     * @param anyHitCallback            Pointer to a function that returns whether a ray hits anything.
     * @param batchedClosestHitCallback Pointer to a function that returns the closests hits along each ray in a
     *                                  batch of rays. Can be NULL. If not NULL, then this function is used
     *                                  instead of closestHitCallback.
     * @param batchedAnyHitCallback     Pointer to a function that returns, for each ray in a batch of rays,
     *                                  whether the ray hits anything. Can be NULL. If not NULL, then this
     *                                  function is used instead of anyHitCallback.
     * @param userData                  Pointer to a block of memory containing arbitrary data for use
     *                                  by the closest hit and any hit callbacks.
     * @param scene                     [out] Handle to the created Scene object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateScene(IPLhandle, IPLhandle, IPLSceneType, IPLint32, IPLMaterial*, IPLClosestHitCallback, IPLAnyHitCallback, IPLBatchedClosestHitCallback, IPLBatchedAnyHitCallback, void*, IPLhandle*)</code>
     */
    int iplCreateScene(Pointer context,
                       Pointer computeDevice,
                       SceneType sceneType,
                       int numMaterials,
                       Material[] materials,
                       Phonon.ClosestHitCallback closestHitCallback,
                       Phonon.AnyHitCallback anyHitCallback,
                       Phonon.BatchedClosestHitCallback batchedClosestHitCallback,
                       Phonon.BatchedAnyHitCallback batchedAnyHitCallback,
                       Pointer userData,
                       PointerByReference scene);

    /**
     * Destroys a Scene object. If any other API objects are still referencing the Scene object, it will not be
     * destroyed; destruction occurs when the object's reference count reaches zero.
     *
     * @param scene [in, out] Address of a handle to the Scene object to destroy.
     *              Original signature : <code>void iplDestroyScene(IPLhandle*)</code>
     */
    void iplDestroyScene(PointerByReference scene);

    /**
     * Creates a Static Mesh object. A Static Mesh object represents a triangle mesh that does not change after it
     * is created. A Static Mesh object also contains a mapping between each of its triangles and their acoustic
     * material properties. Static Mesh objects should be used for scene geometry that is guaranteed to never change,
     * such as rooms, buildings, or triangulated terrain. A Scene object may contain multiple Static Mesh objects,
     * although typically one is sufficient.
     *
     * @param scene           Handle to the Scene object to which to add the Static Mesh object.
     * @param numVertices     Number of vertices in the triangle mesh.
     * @param numTriangles    Number of triangles in the triangle mesh.
     * @param vertices        Array containing the coordinates of all vertices in the Static Mesh object.
     *                        The number of IPLVector3 objects in the array must be equal to the value of
     *                        numVertices passed to ::iplCreateStaticMesh.
     * @param triangles       Array containing all triangles in the Static Mesh object. The number of
     *                        IPLTriangle objects in the array must be equal to the value of
     *                        numTriangles passed to ::iplCreateStaticMesh.
     * @param materialIndices Array containing material indices for all triangles in the Static Mesh object.
     *                        The number of material indices in the array must be equal to the value of
     *                        numTriangles passed to ::iplCreateStaticMesh.
     * @param staticMesh      [out] Handle to the created Static Mesh object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateStaticMesh(IPLhandle, IPLint32, IPLint32, IPLVector3*, IPLTriangle*, IPLint32*, IPLhandle*)</code>
     * @deprecated use the safer methods {@link #iplCreateStaticMesh(Pointer, int, int, Vector3, Triangle, IntBuffer, PointerByReference)} and {@link #iplCreateStaticMesh(Pointer, int, int, Vector3, Triangle, IntByReference, PointerByReference)} instead
     */
    @Deprecated
    int iplCreateStaticMesh(Pointer scene,
                            int numVertices,
                            int numTriangles,
                            Vector3 vertices,
                            Triangle triangles,
                            IntByReference materialIndices,
                            PointerByReference staticMesh);

    /**
     * Creates a Static Mesh object. A Static Mesh object represents a triangle mesh that does not change after it
     * is created. A Static Mesh object also contains a mapping between each of its triangles and their acoustic
     * material properties. Static Mesh objects should be used for scene geometry that is guaranteed to never change,
     * such as rooms, buildings, or triangulated terrain. A Scene object may contain multiple Static Mesh objects,
     * although typically one is sufficient.
     *
     * @param scene           Handle to the Scene object to which to add the Static Mesh object.
     * @param numVertices     Number of vertices in the triangle mesh.
     * @param numTriangles    Number of triangles in the triangle mesh.
     * @param vertices        Array containing the coordinates of all vertices in the Static Mesh object.
     *                        The number of IPLVector3 objects in the array must be equal to the value of
     *                        numVertices passed to ::iplCreateStaticMesh.
     * @param triangles       Array containing all triangles in the Static Mesh object. The number of
     *                        IPLTriangle objects in the array must be equal to the value of
     *                        numTriangles passed to ::iplCreateStaticMesh.
     * @param materialIndices Array containing material indices for all triangles in the Static Mesh object.
     *                        The number of material indices in the array must be equal to the value of
     *                        numTriangles passed to ::iplCreateStaticMesh.
     * @param staticMesh      [out] Handle to the created Static Mesh object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateStaticMesh(IPLhandle, IPLint32, IPLint32, IPLVector3*, IPLTriangle*, IPLint32*, IPLhandle*)</code>
     */
    int iplCreateStaticMesh(Pointer scene,
                            int numVertices,
                            int numTriangles,
                            Vector3 vertices,
                            Triangle triangles,
                            IntBuffer materialIndices,
                            PointerByReference staticMesh);

    /**
     * Destroys a Static Mesh object. If any other API objects are still referencing the Static Mesh object, it will
     * not be destroyed; destruction occurs when the object's reference count reaches zero. Since the Scene object
     * maintains an internal reference to the Static Mesh object, you may call this function at any point after
     * fully specifying the Static Mesh object using ::iplCreateStaticMesh.
     *
     * @param staticMesh [in, out] Address of a handle to the Static Mesh object to destroy.
     *                   Original signature : <code>void iplDestroyStaticMesh(IPLhandle*)</code>
     */
    void iplDestroyStaticMesh(PointerByReference staticMesh);

    /**
     * Serializes a Scene object to a byte array. This function can only be called on a Scene object that
     * has been created using the Phonon built-in ray tracer.
     *
     * @param scene Handle to the Scene object.
     * @param data  [out] Byte array into which the Scene object will be serialized. It is the
     *              caller's responsibility to manage memory for this array. The array must be large
     *              enough to hold all the data in the Scene object. May be NULL, in which case
     *              no data is returned; this is useful when finding out the size of the data stored
     *              in the Scene object.
     *              Original signature : <code>IPLint32 iplSaveScene(IPLhandle, IPLbyte*)</code>
     * @deprecated use the safer methods {@link #iplSaveScene(Pointer, ByteBuffer)} and {@link #iplSaveScene(Pointer, Pointer)} instead
     */
    @Deprecated
    int iplSaveScene(Pointer scene, Pointer data);

    /**
     * Serializes a Scene object to a byte array. This function can only be called on a Scene object that
     * has been created using the Phonon built-in ray tracer.
     *
     * @param scene Handle to the Scene object.
     * @param data  [out] Byte array into which the Scene object will be serialized. It is the
     *              caller's responsibility to manage memory for this array. The array must be large
     *              enough to hold all the data in the Scene object. May be NULL, in which case
     *              no data is returned; this is useful when finding out the size of the data stored
     *              in the Scene object.
     *              Original signature : <code>IPLint32 iplSaveScene(IPLhandle, IPLbyte*)</code>
     */
    int iplSaveScene(Pointer scene, ByteBuffer data);

    /**
     * Creates a Scene object based on data stored in a byte array.
     *
     * @param context          The Context object used by the game engine.
     * @param sceneType        The ray tracer to use for scene representation and simulation.
     * @param data             Byte array containing the serialized representation of the Scene object. Must
     *                         not be NULL.
     * @param size             Size (in bytes) of the serialized data.
     * @param computeDevice    Handle to a Compute Device object. Only required if using Radeon Rays for
     *                         ray tracing, may be NULL otherwise.
     * @param progressCallback Pointer to a function that reports the percentage of this function's work
     *                         that has been completed. May be NULL.
     * @param scene            [out] Handle to the created Scene object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplLoadScene(IPLhandle, IPLSceneType, IPLbyte*, IPLint32, IPLhandle, IPLLoadSceneProgressCallback, IPLhandle*)</code>
     * @deprecated use the safer methods {@link #iplLoadScene(Pointer, int, ByteBuffer, int, Pointer, Phonon.LoadSceneProgressCallback, PointerByReference)} and {@link #iplLoadScene(Pointer, int, Pointer, int, Pointer, Phonon.LoadSceneProgressCallback, PointerByReference)} instead
     */
    @Deprecated
    int iplLoadScene(Pointer context,
                     int sceneType,
                     Pointer data,
                     int size,
                     Pointer computeDevice,
                     Phonon.LoadSceneProgressCallback progressCallback,
                     PointerByReference scene);

    /**
     * Creates a Scene object based on data stored in a byte array.
     *
     * @param context          The Context object used by the game engine.
     * @param sceneType        The ray tracer to use for scene representation and simulation.
     * @param data             Byte array containing the serialized representation of the Scene object. Must
     *                         not be NULL.
     * @param size             Size (in bytes) of the serialized data.
     * @param computeDevice    Handle to a Compute Device object. Only required if using Radeon Rays for
     *                         ray tracing, may be NULL otherwise.
     * @param progressCallback Pointer to a function that reports the percentage of this function's work
     *                         that has been completed. May be NULL.
     * @param scene            [out] Handle to the created Scene object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplLoadScene(IPLhandle, IPLSceneType, IPLbyte*, IPLint32, IPLhandle, IPLLoadSceneProgressCallback, IPLhandle*)</code>
     */
    int iplLoadScene(Pointer context, int sceneType, ByteBuffer data, int size, Pointer computeDevice, Phonon.LoadSceneProgressCallback progressCallback, PointerByReference scene);

    /**
     * Saves a Scene object to an OBJ file. An OBJ file is a widely-supported 3D model file format, that can be
     * displayed using a variety of software on most PC platforms. The OBJ file generated by this function can be
     * useful for detecting problems that occur when exporting scene data from the game engine to
     * This function can only be called on a Scene object that has been created using the Phonon built-in ray tracer.
     *
     * @param scene        Handle to the Scene object.
     * @param fileBaseName Absolute or relative path to the OBJ file to generate.
     *                     Original signature : <code>void iplSaveSceneAsObj(IPLhandle, IPLstring)</code>
     * @deprecated use the safer methods {@link #iplSaveSceneAsObj(Pointer, ByteBuffer)} and {@link #iplSaveSceneAsObj(Pointer, Pointer)} instead
     */
    @Deprecated
    void iplSaveSceneAsObj(Pointer scene, Pointer fileBaseName);

    /**
     * Saves a Scene object to an OBJ file. An OBJ file is a widely-supported 3D model file format, that can be
     * displayed using a variety of software on most PC platforms. The OBJ file generated by this function can be
     * useful for detecting problems that occur when exporting scene data from the game engine to
     * This function can only be called on a Scene object that has been created using the Phonon built-in ray tracer.
     *
     * @param scene        Handle to the Scene object.
     * @param fileBaseName Absolute or relative path to the OBJ file to generate.
     *                     Original signature : <code>void iplSaveSceneAsObj(IPLhandle, IPLstring)</code>
     */
    void iplSaveSceneAsObj(Pointer scene, ByteBuffer fileBaseName);

    /**
     * Creates an Instanced Mesh object. An Instanced Mesh takes one scene and positions it within another scene.
     * This is useful if you have the same object, like a pillar, that you want to instantiate multiple times within
     * the same scene. A scene can be instantiated multiple times within another scene, without incurring any significant
     * memory overhead. The Instanced Mesh can be moved, rotated, and scaled freely at any time, providing an easy way to
     * implement dynamic objects whose motion can be described purely in terms of rigid-body transformations.
     *
     * @param scene          The scene in which to instantiate another scene.
     * @param instancedScene The scene to instantiate.
     * @param transform      A transform matrix that maps from the coordinate space of instancedScene to the
     *                       coordinate space of scene. This is used to position and orient instancedScene
     *                       within scene. This parameter specifies the initial value of the transform; it can be
     *                       freely changed once the Instanced Mesh is created, using
     *                       iplUpdateInstancedMeshTransform.
     * @param instancedMesh  [out] Handle to the created Instanced Mesh object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateInstancedMesh(IPLhandle, IPLhandle, IPLMatrix4x4, IPLhandle*)</code>
     */
    int iplCreateInstancedMesh(Pointer scene, Pointer instancedScene, Matrix4x4 transform, PointerByReference instancedMesh);

    /**
     * Destroys an Instanced Mesh object. If any other API objects are still referencing the Instanced Mesh object,
     * it will not be destroyed; destruction occurs when the object's reference count reaches zero.
     *
     * @param instancedMesh [in, out] Address of a handle to the Instanced Mesh object to destroy.
     *                      Original signature : <code>void iplDestroyInstancedMesh(IPLhandle*)</code>
     */
    void iplDestroyInstancedMesh(PointerByReference instancedMesh);

    /**
     * Adds an Instanced Mesh object to a Scene object. This function should be called after iplCreateInstancedMesh, or
     * at any point after calling iplRemoveInstancedMesh, for the Instanced Mesh to start affecting sound
     * propagation.
     *
     * @param scene         The Scene to which to add the Instanced Mesh. This must be the Scene which was passed
     *                      as the scene parameter when calling iplCreateInstancedMesh to create the
     *                      Instanced Mesh.
     * @param instancedMesh The Instanced Mesh to add to the Scene.
     *                      Original signature : <code>void iplAddInstancedMesh(IPLhandle, IPLhandle)</code>
     */
    void iplAddInstancedMesh(Pointer scene, Pointer instancedMesh);

    /**
     * Removes an Instanced Mesh object from a Scene object. After this function is called, the Instanced Mesh will stop
     * affecting sound propagation, until a subsequent call to iplAddInstancedMesh.
     *
     * @param scene         The Scene from which to remove the Instanced Mesh.
     * @param instancedMesh The Instanced Mesh to remove from the Scene.
     *                      Original signature : <code>void iplRemoveInstancedMesh(IPLhandle, IPLhandle)</code>
     */
    void iplRemoveInstancedMesh(Pointer scene, Pointer instancedMesh);

    /**
     * Updates the local-to-world transform of an Instanced Mesh within a Scene. This function allows the Instanced
     * Mesh to be moved, rotated, and scaled dynamically. After calling this function, you must call
     * iplCommitScene for the changes to take effect.
     *
     * @param instancedMesh The Instanced Mesh whose transform is to be updated.
     * @param transform     The new 4x4 transform matrix.
     *                      Original signature : <code>void iplUpdateInstancedMeshTransform(IPLhandle, IPLMatrix4x4)</code>
     */
    void iplUpdateInstancedMeshTransform(Pointer instancedMesh, Matrix4x4 transform);

    /**
     * Commits a series of changes to Instanced Meshes in a Scene. This function should be called after any calls to
     * iplUpdateInstancedMeshTransform for the changes to take effect. For best performance, call this function after
     * all transforms have been updated for a given frame.
     *
     * @param scene The Scene to commit changes to.
     *              Original signature : <code>void iplCommitScene(IPLhandle)</code>
     */
    void iplCommitScene(Pointer scene);

    /**
     * Creates an Environment object. It is necessary to call this function even if you are not using the sound
     * propagation features of
     *
     * @param context            The Context object used by the game engine.
     * @param computeDevice      Handle to a Compute Device object. Only required if using Radeon Rays for
     *                           ray tracing, or if using TrueAudio Next for convolution, may be NULL otherwise.
     * @param simulationSettings The settings to use for simulation. This must be the same settings passed to
     *                           ::iplCreateScene or ::iplLoadScene, whichever was used to create
     *                           the Scene object passed in the scene parameter to this function.
     * @param scene              The Scene object. May be NULL, in which case only direct sound will be
     *                           simulated, without occlusion or any other indirect sound propagation.
     * @param probeManager       The Probe Manager object. May be NULL if not using baked data.
     * @param environment        [out] Handle to the created Environment object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateEnvironment(IPLhandle, IPLhandle, IPLSimulationSettings, IPLhandle, IPLhandle, IPLhandle*)</code>
     */
    int iplCreateEnvironment(Pointer context, Pointer computeDevice, SimulationSettings simulationSettings, Pointer scene, Pointer probeManager, PointerByReference environment);

    /**
     * Destroys an Environment object. If any other API objects are still referencing the Environment object, it will
     * not be destroyed; destruction occurs when the object's reference count reaches zero.
     *
     * @param environment [in, out] Address of a handle to the Environment object to destroy.
     *                    Original signature : <code>void iplDestroyEnvironment(IPLhandle*)</code>
     */
    void iplDestroyEnvironment(PointerByReference environment);

    /**
     * Sets the number of bounces to use for real-time simulations that use an Environment object. Calling this
     * function overrides the value of bounces set on the IPLSimulationSettings structure passed when
     * calling ::iplCreateEnvironment to create this Environment object.
     *
     * @param environment Handle to an Environment object.
     * @param numBounces  The number of bounces to use for all subsequent simulations in the Environment.
     *                    Original signature : <code>void iplSetNumBounces(IPLhandle, IPLint32)</code>
     */
    void iplSetNumBounces(Pointer environment, int numBounces);

    /**
     * Mixes a set of audio buffers.  This is primarily useful for mixing the output of multiple Panning Effect
     * objects, before passing them to a single Virtual Surround Effect or a single Ambisonics Binaural Effect. This
     * way, applications can significantly accelerate 3D audio rendering for large numbers of sources.
     *
     * @param numBuffers  The number of input buffers to mix. Must be greater than 0.
     * @param inputAudio  Array of audio buffers to mix. All of these audio buffers must have identical
     *                    formats.
     * @param outputAudio Audio buffer that will contain the mixed audio data. The format of this buffer
     *                    must be identical to all buffers contained in inputAudio.
     *                    Original signature : <code>void iplMixAudioBuffers(IPLint32, IPLAudioBuffer*, IPLAudioBuffer)</code>
     */
    void iplMixAudioBuffers(int numBuffers, AudioBuffer[] inputAudio, AudioBuffer outputAudio);

    /**
     * Interleaves a deinterleaved audio buffer. The formats of inputAudio and outputAudio must be identical
     * except for the channelOrder field.
     *
     * @param inputAudio  The input audio buffer. This audio buffer must be deinterleaved.
     * @param outputAudio The output audio buffer. This audio buffer must be interleaved.
     *                    Original signature : <code>void iplInterleaveAudioBuffer(IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplInterleaveAudioBuffer(AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Deinterleaves an interleaved audio buffer. The formats of inputAudio and outputAudio must be identical
     * except for the channelOrder field.
     *
     * @param inputAudio  The input audio buffer. This audio buffer must be interleaved.
     * @param outputAudio The output audio buffer. This audio buffer must be deinterleaved.
     *                    Original signature : <code>void iplDeinterleaveAudioBuffer(IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplDeinterleaveAudioBuffer(AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Converts the format of an audio buffer into the format of the output audio buffer. This is primarily useful
     * for 360 video and audio authoring workflows. The following format conversions are supported:
     * - mono to multi-channel speaker-based formats (stereo, quadraphonic, 5.1, 7.1)
     * - multi-channel speaker-based (stereo, quadraphonic, 5.1, 7.1) to mono
     * - stereo to 5.1 or 7.1
     * - Ambisonics to multi-channel speaker-based (mono, stereo, quadraphonic, 5.1, 7.1)
     *
     * @param inputAudio  The input audio buffer.
     * @param outputAudio The output audio buffer.
     *                    Original signature : <code>void iplConvertAudioBufferFormat(IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplConvertAudioBufferFormat(AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Creates an Ambisonics Rotator object. An Ambisonics Rotator object is used to apply an arbitrary rotation to
     * audio data encoded in Ambisonics. This is primarily useful in the following situations:
     * - If you have an Ambisonics audio buffer whose coefficients are defined relative to world space coordinates,
     * you can convert them to listener space using an Ambisonics Rotator object. This is necessary when using a
     * Convolution Effect object, since its output is defined in world space, and will not change if the listener
     * looks around.
     * - If your final mix is encoded in Ambisonics, and the user is using headphones with head tracking, you can use
     * the Ambisonics Rotator object to make the sound field stay "in place" as the user looks around in the real
     * world. This is achieved by using the Ambisonics Rotator object to apply the inverse of the user's rotation
     * to the final mix.
     *
     * @param context The Context object used by the audio engine.
     * @param order   The order of the Ambisonics data to rotate.
     * @param rotator [out] Handle to the created Ambisonics Rotator object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateAmbisonicsRotator(IPLhandle, IPLint32, IPLhandle*)</code>
     */
    int iplCreateAmbisonicsRotator(Pointer context, int order, PointerByReference rotator);

    /**
     * Destroys an Ambisonics Rotator object.
     *
     * @param rotator [in, out] Address of a handle to the Ambisonics Rotator object to destroy.
     *                Original signature : <code>void iplDestroyAmbisonicsRotator(IPLhandle*)</code>
     */
    void iplDestroyAmbisonicsRotator(PointerByReference rotator);

    /**
     * Specifies a rotation value. This function must be called before using ::iplRotateAmbisonicsAudioBuffer to
     * rotate an Ambisonics-encoded audio buffer, or the resulting audio will be incorrect.
     *
     * @param rotator       Handle to an Ambisonics Rotator object.
     * @param listenerAhead Unit vector pointing in the direction in which the listener is looking.
     * @param listenerUp    Unit vector pointing upwards from the listener.
     *                      Original signature : <code>void iplSetAmbisonicsRotation(IPLhandle, IPLVector3, IPLVector3)</code>
     */
    void iplSetAmbisonicsRotation(Pointer rotator, Vector3 listenerAhead, Vector3 listenerUp);

    /**
     * Rotates an Ambisonics-encoded audio buffer. The ::iplSetAmbisonicsRotation function must have been called
     * prior to calling this function, or the resulting audio will be incorrect. It is possible to pass the same
     * value for inputAudio and outputAudio. This results in in-place rotation of the Ambisonics data.
     *
     * @param rotator     Handle to an Ambisonics Rotator object.
     * @param inputAudio  Audio buffer containing the Ambisonics-encoded data that is to be rotated. The
     *                    format of this buffer must be Ambisonics.
     * @param outputAudio Audio buffer containing the rotated Ambisonics-encoded data. The format of this
     *                    buffer must be Ambisonics.
     *                    Original signature : <code>void iplRotateAmbisonicsAudioBuffer(IPLhandle, IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplRotateAmbisonicsAudioBuffer(Pointer rotator, AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Creates a Binaural Renderer object. This function must be called before creating any Panning Effect objects,
     * Object-Based Binaural Effect objects, Virtual Surround Effect objects, or Ambisonics Binaural Effect objects.
     * Calling this function for the first time is somewhat expensive; avoid creating Binaural Renderer objects in
     * your audio thread if at all possible. **This function is not thread-safe. It cannot be simultaneously called
     * from multiple threads.**
     *
     * @param context           The Context object used by the audio engine.
     * @param renderingSettings An IPLRenderingSettings object describing the audio pipeline's DSP processing
     *                          parameters. These properties must remain constant throughout the lifetime of your
     *                          application.
     * @param params            Parameters describing the type of HRTF data you wish to use (built-in HRTF data or
     *                          your own custom HRTF data).
     * @param renderer          [out] Handle to the created Binaural Renderer object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateBinauralRenderer(IPLhandle, IPLRenderingSettings, IPLHrtfParams, IPLhandle*)</code>
     */
    int iplCreateBinauralRenderer(Pointer context, RenderingSettings renderingSettings, HrtfParams params, PointerByReference renderer);

    /**
     * Destroys a Binaural Renderer object. If any other API objects are still referencing the Binaural Renderer
     * object, it will not be destroyed; destruction occurs when the object's reference count reaches zero.
     *
     * @param renderer [in, out] Address of a handle to the Binaural Renderer object to destroy.
     *                 Original signature : <code>void iplDestroyBinauralRenderer(IPLhandle*)</code>
     */
    void iplDestroyBinauralRenderer(PointerByReference renderer);

    /**
     * Creates a Panning Effect object. This can be used to render a point source on surround speakers, or using
     * Ambisonics.
     *
     * @param renderer     Handle to a Binaural Renderer object.
     * @param inputFormat  The format of the audio buffers that will be passed as input to this effect. All
     *                     subsequent calls to ::iplApplyPanningEffect for this effect object must use
     *                     IPLAudioBuffer objects with the same format as specified here. The input format
     *                     must not be Ambisonics.
     * @param outputFormat The format of the audio buffers which will be used to retrieve the output from
     *                     this effect. All subsequent calls to ::iplApplyPanningEffect for this effect
     *                     object must use IPLAudioBuffer objects with the same format as specified here.
     *                     Any valid audio format may be specified as the output format.
     * @param effect       [out] Handle to the created Panning Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreatePanningEffect(IPLhandle, IPLAudioFormat, IPLAudioFormat, IPLhandle*)</code>
     */
    int iplCreatePanningEffect(Pointer renderer, AudioFormat inputFormat, AudioFormat outputFormat, PointerByReference effect);

    /**
     * Destroys a Panning Effect object.
     *
     * @param effect [in, out] Address of a handle to the Panning Effect object to destroy.
     *               Original signature : <code>void iplDestroyPanningEffect(IPLhandle*)</code>
     */
    void iplDestroyPanningEffect(PointerByReference effect);

    /**
     * Applies 3D panning to a buffer of audio data, using the configuration of a Panning Effect object. The input
     * audio is treated as emanating from a single point. If the input audio buffer contains more than one channel,
     * it will automatically be downmixed to mono.
     *
     * @param effect           Handle to a Panning Effect object.
     * @param binauralRenderer Handle to a Binaural Renderer object that should be used to apply the panning
     *                         effect.
     * @param inputAudio       Audio buffer containing the data to render using 3D panning. The format of this
     *                         buffer must match the inputFormat parameter passed to ::iplCreatePanningEffect.
     * @param direction        Unit vector from the listener to the point source, relative to the listener's
     *                         coordinate system.
     * @param outputAudio      Audio buffer that should contain the rendered audio data. The format of this buffer
     *                         must match the outputFormat parameter passed to ::iplCreatePanningEffect.
     *                         Original signature : <code>void iplApplyPanningEffect(IPLhandle, IPLhandle, IPLAudioBuffer, IPLVector3, IPLAudioBuffer)</code>
     */
    void iplApplyPanningEffect(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, Vector3 direction, AudioBuffer outputAudio);

    /**
     * Resets any internal state maintained by a Panning Effect object. This is useful if the Panning Effect object
     * is going to be disabled/unused for a few frames; resetting the internal state will prevent an audible glitch
     * when the Panning Effect object is re-enabled at a later time.
     *
     * @param effect Handle to a Panning Effect object.
     *               Original signature : <code>void iplFlushPanningEffect(IPLhandle)</code>
     */
    void iplFlushPanningEffect(Pointer effect);

    /**
     * Creates an Object-Based Binaural Effect object. This can be used to render a point source using HRTF-based
     * binaural rendering.
     *
     * @param renderer     Handle to a Binaural Renderer object.
     * @param inputFormat  The format of the audio buffers that will be passed as input to this effect. All
     *                     subsequent calls to ::iplApplyBinauralEffect for this effect object must use
     *                     IPLAudioBuffer objects with the same format as specified here. The input format
     *                     must not be Ambisonics.
     * @param outputFormat The format of the audio buffers which will be used to retrieve the output from this
     *                     effect. All subsequent calls to ::iplApplyBinauralEffect for this effect object
     *                     must use IPLAudioBuffer objects with the same format as specified here. The
     *                     output format must be stereo (2 channels).
     * @param effect       [out] Handle to the created Object-Based Binaural Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateBinauralEffect(IPLhandle, IPLAudioFormat, IPLAudioFormat, IPLhandle*)</code>
     */
    int iplCreateBinauralEffect(Pointer renderer, AudioFormat inputFormat, AudioFormat outputFormat, PointerByReference effect);

    /**
     * Destroys an Object-Based Binaural Effect object.
     *
     * @param effect [in, out] Address of a handle to the Object-Based Binaural Effect object to
     *               destroy.
     *               Original signature : <code>void iplDestroyBinauralEffect(IPLhandle*)</code>
     */
    void iplDestroyBinauralEffect(PointerByReference effect);

    /**
     * Applies HRTF-based binaural rendering to a buffer of audio data. The input audio is treated as emanating from
     * a single point. If the input audio buffer contains more than one channel, it will automatically be downmixed to
     * mono. Using bilinear interpolation (by setting interpolation to ::IPL_HRTFINTERPOLATION_BILINEAR) can
     * incur a relatively high CPU cost. Use it only on sources where nearest-neighbor filtering
     * (\c ::IPL_HRTFINTERPOLATION_NEAREST) produces suboptimal results. Typically, bilinear filtering is most useful
     * for wide-band noise-like sounds, such as radio static, mechanical noise, fire, etc.
     *
     * @param effect           Handle to an Object-Based Binaural Effect object.
     * @param binauralRenderer Handle to a Binaural Renderer object that should be used to apply the binaural
     *                         effect. Each Binaural Renderer corresponds to an HRTF (which may be loaded from
     *                         SOFA files); the value of this parameter determines which HRTF is used to
     *                         spatialize the input audio.
     * @param inputAudio       Audio buffer containing the data to render using binaural rendering. The format of
     *                         this buffer must match the inputFormat parameter passed to
     *                         ::iplCreateBinauralEffect.
     * @param direction        Unit vector from the listener to the point source, relative to the listener's
     *                         coordinate system.
     * @param interpolation    The interpolation technique to use when rendering a point source at a location
     *                         that is not contained in the measured HRTF data used by  **If using a custom
     *                         HRTF database, this value must be set to IPL_HRTFINTERPOLATION_BILINEAR.**
     * @param spatialBlend     Amount to blend inputAudio with spatialized audio. When set to 0, outputAudio is not
     *                         spatialized at all and is close to inputAudio. If set to 1, outputAudio is fully
     *                         spatialized.
     * @param outputAudio      Audio buffer that should contain the rendered audio data. The format of this
     *                         buffer must match the outputFormat parameter passed to
     *                         ::iplCreateBinauralEffect.
     *                         Original signature : <code>void iplApplyBinauralEffect(IPLhandle, IPLhandle, IPLAudioBuffer, IPLVector3, IPLHrtfInterpolation, IPLfloat32, IPLAudioBuffer)</code>
     */
    void iplApplyBinauralEffect(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, Vector3 direction, HrtfInterpolation interpolation, float spatialBlend, AudioBuffer outputAudio);

    /**
     * Original signature : <code>void iplApplyBinauralEffectWithParameters(IPLhandle, IPLhandle, IPLAudioBuffer, IPLVector3, IPLHrtfInterpolation, IPLbool, IPLfloat32, IPLAudioBuffer, IPLfloat32*, IPLfloat32*)</code>
     *
     * @deprecated use the safer methods {@link #iplApplyBinauralEffectWithParameters(Pointer, Pointer, AudioBuffer, Vector3, int, int, float, AudioBuffer, FloatBuffer, FloatBuffer)} and {@link #iplApplyBinauralEffectWithParameters(Pointer, Pointer, AudioBuffer, Vector3, int, int, float, AudioBuffer, FloatByReference, FloatByReference)} instead
     */
    @Deprecated
    void iplApplyBinauralEffectWithParameters(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, Vector3 direction, int interpolation, int enableSpatialBlend, float spatialBlend, AudioBuffer outputAudio, FloatByReference leftDelay, FloatByReference rightDelay);

    /**
     * Original signature : <code>void iplApplyBinauralEffectWithParameters(IPLhandle, IPLhandle, IPLAudioBuffer, IPLVector3, IPLHrtfInterpolation, IPLbool, IPLfloat32, IPLAudioBuffer, IPLfloat32*, IPLfloat32*)</code>
     */
    void iplApplyBinauralEffectWithParameters(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, Vector3 direction, int interpolation, int enableSpatialBlend, float spatialBlend, AudioBuffer outputAudio, FloatBuffer leftDelay, FloatBuffer rightDelay);

    /**
     * Resets any internal state maintained by an Object-Based Binaural Effect object. This is useful if the
     * Object-Based Binaural Effect object is going to be disabled/unused for a few frames; resetting the internal
     * state will prevent an audible glitch when the Object-Based Binaural Effect object is re-enabled at a later
     * time.
     *
     * @param effect Handle to an Object-Based Binaural Effect object.
     *               Original signature : <code>void iplFlushBinauralEffect(IPLhandle)</code>
     */
    void iplFlushBinauralEffect(Pointer effect);

    /**
     * Creates a Virtual Surround Effect object. This can be used to render a multichannel surround sound data using
     * HRTF-based binaural rendering.
     *
     * @param renderer     Handle to a Binaural Renderer object.
     * @param inputFormat  The format of the audio buffers that will be passed as input to this effect. All
     *                     subsequent calls to ::iplApplyVirtualSurroundEffect for this effect object must
     *                     use IPLAudioBuffer objects with the same format as specified here. The input
     *                     format must not be Ambisonics.
     * @param outputFormat The format of the audio buffers which will be used to retrieve the output from this
     *                     effect. All subsequent calls to ::iplApplyVirtualSurroundEffect for this effect
     *                     object must use IPLAudioBuffer objects with the same format as specified here.
     *                     The output format must be stereo (2 channels).
     * @param effect       [out] Handle to the created Virtual Surround Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateVirtualSurroundEffect(IPLhandle, IPLAudioFormat, IPLAudioFormat, IPLhandle*)</code>
     */
    int iplCreateVirtualSurroundEffect(Pointer renderer, AudioFormat inputFormat, AudioFormat outputFormat, PointerByReference effect);

    /**
     * Destroys a Virtual Surround Effect object.
     *
     * @param effect [in, out] Address of a handle to the Virtual Surround Effect object to destroy.
     *               Original signature : <code>void iplDestroyVirtualSurroundEffect(IPLhandle*)</code>
     */
    void iplDestroyVirtualSurroundEffect(PointerByReference effect);

    /**
     * Applies HRTF-based binaural rendering to a buffer of multichannel audio data.
     *
     * @param effect           Handle to a Virtual Surround Effect.
     * @param binauralRenderer Handle to a Binaural Renderer object that should be used to apply the virtual surround
     *                         effect. Each Binaural Renderer corresponds to an HRTF (which may be loaded from
     *                         SOFA files); the value of this parameter determines which HRTF is used to
     *                         spatialize the input audio.
     * @param inputAudio       Audio buffer containing the data to render using binaural rendering. The format of
     *                         this buffer must match the inputFormat parameter passed to
     *                         ::iplCreateVirtualSurroundEffect.
     * @param outputAudio      Audio buffer that should contain the rendered audio data. The format of this buffer
     *                         must match the outputFormat parameter passed to
     *                         ::iplCreateVirtualSurroundEffect.
     *                         \remark When using a custom HRTF database, calling this function is not supported.
     *                         Original signature : <code>void iplApplyVirtualSurroundEffect(IPLhandle, IPLhandle, IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplApplyVirtualSurroundEffect(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Resets any internal state maintained by a Virtual Surround Effect object. This is useful if the Virtual
     * Surround Effect object is going to be disabled/unused for a few frames; resetting the internal state will
     * prevent an audible glitch when the Virtual Surround Effect object is re-enabled at a later time.
     *
     * @param effect Handle to a Virtual Surround Effect object.
     *               Original signature : <code>void iplFlushVirtualSurroundEffect(IPLhandle)</code>
     */
    void iplFlushVirtualSurroundEffect(Pointer effect);

    /**
     * Creates an Ambisonics Panning Effect object. This can be used to render higher-order Ambisonics data using
     * standard panning algorithms.
     *
     * @param renderer     Handle to a Binaural Renderer object.
     * @param inputFormat  The format of the audio buffers that will be passed as input to this effect. All
     *                     subsequent calls to ::iplApplyAmbisonicsPanningEffect for this effect object must
     *                     use IPLAudioBuffer objects with the same format as specified here. The input
     *                     format must be Ambisonics.
     * @param outputFormat The format of the audio buffers which will be used to retrieve the output from this
     *                     effect. All subsequent calls to ::iplApplyAmbisonicsPanningEffect for this
     *                     effect object must use IPLAudioBuffer objects with the same format as specified
     *                     here.
     * @param effect       [out] Handle to the created Ambisonics Panning Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateAmbisonicsPanningEffect(IPLhandle, IPLAudioFormat, IPLAudioFormat, IPLhandle*)</code>
     */
    int iplCreateAmbisonicsPanningEffect(Pointer renderer, AudioFormat inputFormat, AudioFormat outputFormat, PointerByReference effect);

    /**
     * Destroys an Ambisonics Panning Effect object.
     *
     * @param effect [in, out] Address of a handle to the Ambisonics Panning Effect object to destroy.
     *               Original signature : <code>void iplDestroyAmbisonicsPanningEffect(IPLhandle*)</code>
     */
    void iplDestroyAmbisonicsPanningEffect(PointerByReference effect);

    /**
     * Applies a panning-based rendering algorithm to a buffer of Ambisonics audio data. Ambisonics encoders and decoders
     * use many different conventions to store the multiple Ambisonics channels, as well as different normalization
     * schemes. Make sure that you correctly specify these settings when creating the Ambisonics Panning Effect
     * object, otherwise the rendered audio will be incorrect.
     *
     * @param effect           Handle to an Ambisonics Panning Effect object.
     * @param binauralRenderer Handle to a Binaural Renderer object that should be used to apply the ambisonics panning
     *                         effect.
     * @param inputAudio       Audio buffer containing the data to render. The format of
     *                         this buffer must match the inputFormat parameter passed to
     *                         ::iplCreateAmbisonicsPanningEffect.
     * @param outputAudio      Audio buffer that should contain the rendered audio data. The format of this buffer
     *                         must match the outputFormat parameter passed to
     *                         ::iplCreateAmbisonicsPanningEffect.
     *                         Original signature : <code>void iplApplyAmbisonicsPanningEffect(IPLhandle, IPLhandle, IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplApplyAmbisonicsPanningEffect(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Resets any internal state maintained by an Ambisonics Panning Effect object. This is useful if the Ambisonics
     * Panning Effect object is going to be disabled/unused for a few frames; resetting the internal state will
     * prevent an audible glitch when the Ambisonics Panning Effect object is re-enabled at a later time.
     *
     * @param effect Handle to an Ambisonics Panning Effect object.
     *               Original signature : <code>void iplFlushAmbisonicsPanningEffect(IPLhandle)</code>
     */
    void iplFlushAmbisonicsPanningEffect(Pointer effect);

    /**
     * Creates an Ambisonics Binaural Effect object. This can be used to render higher-order Ambisonics data using
     * HRTF-based binaural rendering.
     *
     * @param renderer     Handle to a Binaural Renderer object.
     * @param inputFormat  The format of the audio buffers that will be passed as input to this effect. All
     *                     subsequent calls to ::iplApplyAmbisonicsBinauralEffect for this effect object must
     *                     use IPLAudioBuffer objects with the same format as specified here. The input
     *                     format must be Ambisonics.
     * @param outputFormat The format of the audio buffers which will be used to retrieve the output from this
     *                     effect. All subsequent calls to ::iplApplyAmbisonicsBinauralEffect for this
     *                     effect object must use IPLAudioBuffer objects with the same format as specified
     *                     here. The output format must be stereo (2 channels).
     * @param effect       [out] Handle to the created Ambisonics Binaural Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateAmbisonicsBinauralEffect(IPLhandle, IPLAudioFormat, IPLAudioFormat, IPLhandle*)</code>
     */
    int iplCreateAmbisonicsBinauralEffect(Pointer renderer, AudioFormat inputFormat, AudioFormat outputFormat, PointerByReference effect);

    /**
     * Destroys an Ambisonics Binaural Effect object.
     *
     * @param effect [in, out] Address of a handle to the Ambisonics Binaural Effect object to destroy.
     *               Original signature : <code>void iplDestroyAmbisonicsBinauralEffect(IPLhandle*)</code>
     */
    void iplDestroyAmbisonicsBinauralEffect(PointerByReference effect);

    /**
     * Applies HRTF-based binaural rendering to a buffer of Ambisonics audio data. Ambisonics encoders and decoders
     * use many different conventions to store the multiple Ambisonics channels, as well as different normalization
     * schemes. Make sure that you correctly specify these settings when creating the Ambisonics Binaural Effect
     * object, otherwise the rendered audio will be incorrect.
     *
     * @param effect           Handle to an Ambisonics Binaural Effect object.
     * @param binauralRenderer Handle to a Binaural Renderer object that should be used to apply the ambisonics binaural
     *                         effect. Each Binaural Renderer corresponds to an HRTF (which may be loaded from
     *                         SOFA files); the value of this parameter determines which HRTF is used to
     *                         spatialize the input audio.
     * @param inputAudio       Audio buffer containing the data to render using binaural rendering. The format of
     *                         this buffer must match the inputFormat parameter passed to
     *                         ::iplCreateAmbisonicsBinauralEffect.
     * @param outputAudio      Audio buffer that should contain the rendered audio data. The format of this buffer
     *                         must match the outputFormat parameter passed to
     *                         ::iplCreateAmbisonicsBinauralEffect.
     *                         \remark When using a custom HRTF database, calling this function is not supported.
     *                         Original signature : <code>void iplApplyAmbisonicsBinauralEffect(IPLhandle, IPLhandle, IPLAudioBuffer, IPLAudioBuffer)</code>
     */
    void iplApplyAmbisonicsBinauralEffect(Pointer effect, Pointer binauralRenderer, AudioBuffer inputAudio, AudioBuffer outputAudio);

    /**
     * Resets any internal state maintained by an Ambisonics Binaural Effect object. This is useful if the Ambisonics
     * Binaural Effect object is going to be disabled/unused for a few frames; resetting the internal state will
     * prevent an audible glitch when the Ambisonics Binaural Effect object is re-enabled at a later time.
     *
     * @param effect Handle to an Ambisonics Binaural Effect object.
     *               Original signature : <code>void iplFlushAmbisonicsBinauralEffect(IPLhandle)</code>
     */
    void iplFlushAmbisonicsBinauralEffect(Pointer effect);

    /**
     * Creates an Environmental Renderer object.
     *
     * @param context               The Context object used by the audio engine.
     * @param environment           Handle to an Environment object provided by the game engine. It is up to your
     *                              application to pass this handle from the game engine to the audio engine.
     * @param renderingSettings     An IPLRenderingSettings object describing the audio pipeline's DSP processing
     *                              parameters. These properties must remain constant throughout the lifetime of your
     *                              application.
     * @param outputFormat          The audio format of the output buffers passed to any subsequent call to
     *                              ::iplGetMixedEnvironmentalAudio. This format must not be changed once it is set
     *                              during the call to this function.
     * @param threadCreateCallback  Pointer to a function that will be called when the internal simulation thread
     *                              is created. May be NULL.
     * @param threadDestroyCallback Pointer to a function that will be called when the internal simulation thread
     *                              is destroyed. May be NULL.
     * @param renderer              [out] Handle to the created Environmental Renderer object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateEnvironmentalRenderer(IPLhandle, IPLhandle, IPLRenderingSettings, IPLAudioFormat, IPLSimulationThreadCreateCallback, IPLSimulationThreadDestroyCallback, IPLhandle*)</code>
     */
    int iplCreateEnvironmentalRenderer(Pointer context, Pointer environment, RenderingSettings renderingSettings, AudioFormat outputFormat, Phonon.SimulationThreadCreateCallback threadCreateCallback, Phonon.SimulationThreadDestroyCallback threadDestroyCallback, PointerByReference renderer);

    /**
     * Destroys an Environmental Renderer object. If any other API objects are still referencing the Environmental
     * Renderer object, the object will not be destroyed; it will only be destroyed once its reference count reaches
     * zero.
     *
     * @param renderer [in, out] Address of a handle to the Environmental Renderer object to destroy.
     *                 Original signature : <code>void iplDestroyEnvironmentalRenderer(IPLhandle*)</code>
     */
    void iplDestroyEnvironmentalRenderer(PointerByReference renderer);

    /**
     * Original signature : <code>IPLhandle iplGetEnvironmentForRenderer(IPLhandle)</code>
     */
    Pointer iplGetEnvironmentForRenderer(Pointer renderer);

    /**
     * Calculates direct sound path parameters for a single source. It is up to the audio engine to perform audio
     * processing that uses the information returned by this function.
     *
     * @param environment      Handle to an Environment object.
     * @param listenerPosition World-space position of the listener.
     * @param listenerAhead    Unit vector pointing in the direction in which the listener is looking.
     * @param listenerUp       Unit vector pointing upwards from the listener.
     * @param source           Position, orientation, and directivity of the source.
     * @param sourceRadius     Radius of the sphere defined around the source, for use with DirectOcclusionMode.VOLUMETRIC only.
     * @param numSamples       Number of rays to trace, for use with DirectOcclusionMode.VOLUMETRIC only.
     *                         Increasing this value results in smoother occlusion, but also increases CPU cost.
     *                         This value must be a positive integer less than the maxNumOcclusionSamples value
     *                         of the IPLSimulationSettings struct passed to iplCreateEnvironment.
     * @param occlusionMode    Confuguring the occlusion mode for direct path.
     * @param occlusionMethod  Algorithm to use for checking for direct path occlusion.
     * @return Parameters of the direct path from the source to the listener.
     * Original signature : <code>IPLDirectSoundPath iplGetDirectSoundPath(IPLhandle, IPLVector3, IPLVector3, IPLVector3, IPLSource, IPLfloat32, IPLint32, IPLDirectOcclusionMode, IPLDirectOcclusionMethod)</code>
     */
    DirectSoundPath.ByValue iplGetDirectSoundPath(Pointer environment, Vector3 listenerPosition, Vector3 listenerAhead, Vector3 listenerUp, Source source, float sourceRadius, int numSamples, DirectOcclusionMode occlusionMode, DirectOcclusionMethod occlusionMethod);

    /**
     * Creates a Direct Sound Effect object.
     *
     * @param inputFormat       The format of the audio buffers that will be passed as input to this effect. All
     *                          subsequent calls to ::iplApplyDirectSoundEffect for this effect object must use
     *                          IPLAudioBuffer objects with the same format as specified here.
     * @param outputFormat      The format of the audio buffers which will be used to retrieve the output from this
     *                          effect. All subsequent calls to ::iplApplyDirectSoundEffect for this effect
     *                          object must use IPLAudioBuffer objects with the same format as specified here.
     * @param renderingSettings An IPLRenderingSettings object describing the audio pipeline's DSP processing
     *                          parameters. These properties must remain constant throughout the lifetime of your
     *                          application.
     * @param effect            [out] Handle to the created Direct Sound Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateDirectSoundEffect(IPLAudioFormat, IPLAudioFormat, IPLRenderingSettings, IPLhandle*)</code>
     */
    int iplCreateDirectSoundEffect(AudioFormat inputFormat, AudioFormat outputFormat, RenderingSettings renderingSettings, PointerByReference effect);

    /**
     * Destroys a Direct Sound Effect object.
     *
     * @param effect [in, out] Address of a handle to the Direct Sound Effect object to destroy.
     *               Original signature : <code>void iplDestroyDirectSoundEffect(IPLhandle*)</code>
     */
    void iplDestroyDirectSoundEffect(PointerByReference effect);

    /**
     * Applies various parameters in IPLDirectSoundPath to a buffer of audio data.
     *
     * @param effect          Handle to a Direct Sound Effect object.
     * @param inputAudio      Audio buffer containing the dry audio data. The format of this buffer must match the
     *                        inputFormat parameter passed to ::iplCreateDirectSoundEffect.
     * @param directSoundPath Parameters of the direct path from the source to the listener.
     * @param options         Specifies which parameters from IPLDirectSoundPath should be processed by
     *                        the Direct Sound Effect.
     * @param outputAudio     Audio buffer that should contain the wet audio data. The format of this buffer must
     *                        match the outputFormat parameter passed to ::iplCreateDirectSoundEffect.
     *                        Original signature : <code>void iplApplyDirectSoundEffect(IPLhandle, IPLAudioBuffer, IPLDirectSoundPath, IPLDirectSoundEffectOptions, IPLAudioBuffer)</code>
     */
    void iplApplyDirectSoundEffect(Pointer effect, AudioBuffer inputAudio, DirectSoundPath directSoundPath, DirectSoundEffectOptions options, AudioBuffer outputAudio);

    /**
     * Resets any internal state maintained by a Direct Sound Effect object. This is useful if the
     * Direct Sound Effect object is going to be disabled/unused for a few frames; resetting the internal
     * state will prevent an audible glitch when the Direct Sound Effect object is re-enabled at a later
     * time.
     *
     * @param effect Handle to a Direct Sound Effect object.
     *               Original signature : <code>void iplFlushDirectSoundEffect(IPLhandle)</code>
     */
    void iplFlushDirectSoundEffect(Pointer effect);

    /**
     * Creates a Convolution Effect object.
     *
     * @param renderer       Handle to an Environmental Renderer object.
     * @param identifier     Unique identifier of the corresponding source, as defined in the baked data
     *                       exported by the game engine. Each Convolution Effect object may have an identifier,
     *                       which is used only if the Environment object provided by the game engine uses baked
     *                       data for sound propagation. If so, the identifier of the Convolution Effect is used
     *                       to look up the appropriate information from the baked data. Multiple Convolution
     *                       Effect objects may be created with the same identifier; in that case they will use
     *                       the same baked data.
     * @param simulationType Whether this Convolution Effect object should use baked data or real-time simulation.
     * @param inputFormat    Format of all audio buffers passed as input to
     *                       ::iplSetDryAudioForConvolutionEffect.
     * @param outputFormat   Format of all output audio buffers passed to ::iplGetWetAudioForConvolutionEffect.
     * @param effect         [out] Handle to the created Convolution Effect object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateConvolutionEffect(IPLhandle, IPLBakedDataIdentifier, IPLSimulationType, IPLAudioFormat, IPLAudioFormat, IPLhandle*)</code>
     */
    int iplCreateConvolutionEffect(Pointer renderer, BakedDataIdentifier identifier, SimulationType simulationType, AudioFormat inputFormat, AudioFormat outputFormat, PointerByReference effect);

    /**
     * Destroys a Convolution Effect object.
     *
     * @param effect [in, out] Address of a handle to the Convolution Effect object to destroy.
     *               Original signature : <code>void iplDestroyConvolutionEffect(IPLhandle*)</code>
     */
    void iplDestroyConvolutionEffect(PointerByReference effect);

    /**
     * Changes the identifier associated with a Convolution Effect object. This is useful when using a static listener
     * bake, where you may want to teleport the listener between two or more locations for which baked data has
     * been generated.
     *
     * @param effect     Handle to a Convolution Effect object.
     * @param identifier The new identifier of the Convolution Effect object.
     *                   Original signature : <code>void iplSetConvolutionEffectIdentifier(IPLhandle, IPLBakedDataIdentifier)</code>
     */
    void iplSetConvolutionEffectIdentifier(Pointer effect, BakedDataIdentifier identifier);

    /**
     * Specifies a frame of dry audio for a Convolution Effect object. This is the audio data to which sound
     * propagation effects should be applied.
     *
     * @param effect   Handle to a Convolution Effect object.
     * @param source   Position, orientation, and directivity of the sound source emitting the dry audio.
     * @param dryAudio Audio buffer containing the dry audio data.
     *                 Original signature : <code>void iplSetDryAudioForConvolutionEffect(IPLhandle, IPLSource, IPLAudioBuffer)</code>
     */
    void iplSetDryAudioForConvolutionEffect(Pointer effect, Source source, AudioBuffer dryAudio);

    /**
     * Retrieves a frame of wet audio from a Convolution Effect object. This is the result of applying sound
     * propagation effects to the dry audio previously specified using ::iplSetDryAudioForConvolutionEffect.
     *
     * @param effect           Handle to a Convolution Effect object.
     * @param listenerPosition World-space position of the listener.
     * @param listenerAhead    Unit vector in the direction in which the listener is looking.
     * @param listenerUp       Unit vector pointing upwards from the listener.
     * @param wetAudio         Audio buffer which will be populated with the wet audio data.
     *                         Original signature : <code>void iplGetWetAudioForConvolutionEffect(IPLhandle, IPLVector3, IPLVector3, IPLVector3, IPLAudioBuffer)</code>
     */
    void iplGetWetAudioForConvolutionEffect(Pointer effect, Vector3 listenerPosition, Vector3 listenerAhead, Vector3 listenerUp, AudioBuffer wetAudio);

    /**
     * Retrieves a mixed frame of wet audio. This is the sum of all wet audio data from all Convolution Effect
     * objects that were created using the given Environmental Renderer object. Unless using TrueAudio Next for
     * convolution, this is likely to provide a significant performance boost to the audio thread as compared to
     * calling ::iplGetWetAudioForConvolutionEffect for each Convolution Effect separately. On the other hand, doing
     * so makes it impossible to apply additional DSP effects for specific sources before mixing.
     *
     * @param renderer         Handle to an Environmental Renderer object.
     * @param listenerPosition World-space position of the listener.
     * @param listenerAhead    Unit vector in the direction in which the listener is looking.
     * @param listenerUp       Unit vector pointing upwards from the listener.
     * @param mixedWetAudio    Audio buffer which will be populated with the wet audio data.
     *                         Original signature : <code>void iplGetMixedEnvironmentalAudio(IPLhandle, IPLVector3, IPLVector3, IPLVector3, IPLAudioBuffer)</code>
     */
    void iplGetMixedEnvironmentalAudio(Pointer renderer, Vector3 listenerPosition, Vector3 listenerAhead, Vector3 listenerUp, AudioBuffer mixedWetAudio);

    /**
     * Resets any internal state maintained by a Convolution Effect object. This is useful if the Convolution Effect
     * object is going to be disabled/unused for a few frames; resetting the internal state will prevent an audible
     * glitch when the Convolution Effect object is re-enabled at a later time.
     *
     * @param effect Handle to a Convolution Effect object.
     *               Original signature : <code>void iplFlushConvolutionEffect(IPLhandle)</code>
     */
    void iplFlushConvolutionEffect(Pointer effect);

    /**
     * Generates probes within a box. This function should typically be called from the game engine's editor, in
     * response to the user indicating that they want to generate probes in the scene.
     *
     * @param context                  Handle to the Context object used by the game engine.
     * @param scene                    Handle to the Scene object.
     * @param boxLocalToWorldTransform 4x4 local to world transform matrix laid out in column-major format.
     * @param placementParams          Parameters specifying how probes should be generated.
     * @param progressCallback         Pointer to a function that reports the percentage of this function's
     *                                 work that has been completed. May be NULL.
     * @param probeBox                 [out] Handle to the created Probe Box object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateProbeBox(IPLhandle, IPLhandle, IPLfloat32*, IPLProbePlacementParams, IPLProbePlacementProgressCallback, IPLhandle*)</code>
     * @deprecated use the safer methods {@link #iplCreateProbeBox(Pointer, Pointer, FloatBuffer, ProbePlacementParams, Phonon.ProbePlacementProgressCallback, PointerByReference)} and {@link #iplCreateProbeBox(Pointer, Pointer, FloatByReference, ProbePlacementParams, Phonon.ProbePlacementProgressCallback, PointerByReference)} instead
     */
    @Deprecated
    int iplCreateProbeBox(Pointer context, Pointer scene, FloatByReference boxLocalToWorldTransform, ProbePlacementParams placementParams, Phonon.ProbePlacementProgressCallback progressCallback, PointerByReference probeBox);

    /**
     * Generates probes within a box. This function should typically be called from the game engine's editor, in
     * response to the user indicating that they want to generate probes in the scene.
     *
     * @param context                  Handle to the Context object used by the game engine.
     * @param scene                    Handle to the Scene object.
     * @param boxLocalToWorldTransform 4x4 local to world transform matrix laid out in column-major format.
     * @param placementParams          Parameters specifying how probes should be generated.
     * @param progressCallback         Pointer to a function that reports the percentage of this function's
     *                                 work that has been completed. May be NULL.
     * @param probeBox                 [out] Handle to the created Probe Box object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateProbeBox(IPLhandle, IPLhandle, IPLfloat32*, IPLProbePlacementParams, IPLProbePlacementProgressCallback, IPLhandle*)</code>
     */
    int iplCreateProbeBox(Pointer context, Pointer scene, FloatBuffer boxLocalToWorldTransform, ProbePlacementParams placementParams, Phonon.ProbePlacementProgressCallback progressCallback, PointerByReference probeBox);

    /**
     * Destroys a Probe Box object.
     *
     * @param probeBox [in, out] Address of a handle to the Probe Box object to destroy.
     *                 Original signature : <code>void iplDestroyProbeBox(IPLhandle*)</code>
     */
    void iplDestroyProbeBox(PointerByReference probeBox);

    /**
     * Retrieves spheres describing the positions and influence radii of all probes in the Probe Box object. This
     * function should typically be called from the game engine's editor, and the retrieved spheres should be used
     * for visualization.
     *
     * @param probeBox     Handle to a Probe Box object.
     * @param probeSpheres [out] Array into which information about the probe spheres is returned. It is the
     *                     the caller's responsibility to manage memory for this array. The array must be
     *                     large enough to hold all the spheres in the Probe Box object. May be NULL, in
     *                     which case no spheres are returned; this is useful when finding out the number of
     *                     probes in the Probe Box object.
     * @return The number of probes in the Probe Box object.
     * Original signature : <code>IPLint32 iplGetProbeSpheres(IPLhandle, IPLSphere*)</code>
     */
    int iplGetProbeSpheres(Pointer probeBox, Sphere probeSpheres);

    /**
     * Serializes a Probe Box object to a byte array. This is typically called by the game engine's editor in order
     * to save the Probe Box object's data to disk.
     *
     * @param probeBox Handle to a Probe Box object.
     * @param data     [out] Byte array into which the Probe Box object will be serialized. It is the
     *                 caller's responsibility to manage memory for this array. The array must be large
     *                 enough to hold all the data in the Probe Box object. May be NULL, in which case
     *                 no data is returned; this is useful when finding out the size of the data stored
     *                 in the Probe Box object.
     * @return Size (in bytes) of the serialized data.
     * Original signature : <code>IPLint32 iplSaveProbeBox(IPLhandle, IPLbyte*)</code>
     * @deprecated use the safer methods {@link #iplSaveProbeBox(Pointer, ByteBuffer)} and {@link #iplSaveProbeBox(Pointer, Pointer)} instead
     */
    @Deprecated
    int iplSaveProbeBox(Pointer probeBox, Pointer data);

    /**
     * Serializes a Probe Box object to a byte array. This is typically called by the game engine's editor in order
     * to save the Probe Box object's data to disk.
     *
     * @param probeBox Handle to a Probe Box object.
     * @param data     [out] Byte array into which the Probe Box object will be serialized. It is the
     *                 caller's responsibility to manage memory for this array. The array must be large
     *                 enough to hold all the data in the Probe Box object. May be NULL, in which case
     *                 no data is returned; this is useful when finding out the size of the data stored
     *                 in the Probe Box object.
     * @return Size (in bytes) of the serialized data.
     * Original signature : <code>IPLint32 iplSaveProbeBox(IPLhandle, IPLbyte*)</code>
     */
    int iplSaveProbeBox(Pointer probeBox, ByteBuffer data);

    /**
     * Deserializes a Probe Box object from a byte array. This is typically called by the game engine's editor when
     * loading a Probe Box object from disk.
     *
     * @param context  Handle to the Context object used by the game engine.
     * @param data     Byte array containing the serialized representation of the Probe Box object. Must
     *                 not be NULL.
     * @param size     Size (in bytes) of the serialized data.
     * @param probeBox [out] Handle to the created Probe Box object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplLoadProbeBox(IPLhandle, IPLbyte*, IPLint32, IPLhandle*)</code>
     * @deprecated use the safer methods {@link #iplLoadProbeBox(Pointer, ByteBuffer, int, PointerByReference)} and {@link #iplLoadProbeBox(Pointer, Pointer, int, PointerByReference)} instead
     */
    @Deprecated
    int iplLoadProbeBox(Pointer context, Pointer data, int size, PointerByReference probeBox);

    /**
     * Deserializes a Probe Box object from a byte array. This is typically called by the game engine's editor when
     * loading a Probe Box object from disk.
     *
     * @param context  Handle to the Context object used by the game engine.
     * @param data     Byte array containing the serialized representation of the Probe Box object. Must
     *                 not be NULL.
     * @param size     Size (in bytes) of the serialized data.
     * @param probeBox [out] Handle to the created Probe Box object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplLoadProbeBox(IPLhandle, IPLbyte*, IPLint32, IPLhandle*)</code>
     */
    int iplLoadProbeBox(Pointer context, ByteBuffer data, int size, PointerByReference probeBox);

    /**
     * Creates a Probe Batch object. A Probe Batch object represents a set of probes that are loaded and unloaded
     * from memory as a unit when the game is played. A Probe Batch may contain probes from multiple Probe Boxes;
     * multiple Probe Batches may contain probes from the same Probe Box. At run-time, Phonon does not use Probe
     * Boxes, it only needs Probe Batches. The typical workflow is as follows:
     * 1.  Using the editor, the designer creates Probe Boxes to sample the scene.
     * 2.  Using the editor, the designer specifies Probe Batches, and decides which probes are part of each Probe
     * Batch.
     * 3.  The editor saves the Probe Batches along with the rest of the scene data for use at run-time.
     * 4.  At run-time, Phonon uses the Probe Batches to retrieve baked data.
     *
     * @param context    Handle to the Context object used by the game engine.
     * @param probeBatch [out] Handle to the created Probe Batch object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateProbeBatch(IPLhandle, IPLhandle*)</code>
     */
    int iplCreateProbeBatch(Pointer context, PointerByReference probeBatch);

    /**
     * Destroys a Probe Batch object.
     *
     * @param probeBatch [in, out] Address of a handle to the Probe Batch object to destroy.
     *                   Original signature : <code>void iplDestroyProbeBatch(IPLhandle*)</code>
     */
    void iplDestroyProbeBatch(PointerByReference probeBatch);

    /**
     * Adds a specific probe from a Probe Box to a Probe Batch. Once all probes in a Probe Box have been assigned to
     * their respective Probe Batches, you can destroy the Probe Box object; the baked data for the probes will
     * be retained by the Probe Batch.
     *
     * @param probeBatch Handle to a Probe Batch object into which the probe should be added.
     * @param probeBox   Handle to a Probe Box object from which the probe should be added.
     * @param probeIndex Index of the probe to add. The index is defined relative to the array of probes
     *                   returned by ::iplGetProbeSpheres.
     *                   Original signature : <code>void iplAddProbeToBatch(IPLhandle, IPLhandle, IPLint32)</code>
     */
    void iplAddProbeToBatch(Pointer probeBatch, Pointer probeBox, int probeIndex);

    /**
     * Finalizes the set of probes that comprise a Probe Batch. Calling this function builds internal data
     * structures that are used to rapidly determine which probes influence any given point in 3D space. You may
     * not call ::iplAddProbeToBatch after calling this function. You must call this function before calling
     * ::iplAddProbeBatch to add this Probe Batch object to a Probe Manager object.
     *
     * @param probeBatch Handle to a ProbeBatch object.
     *                   Original signature : <code>void iplFinalizeProbeBatch(IPLhandle)</code>
     */
    void iplFinalizeProbeBatch(Pointer probeBatch);

    /**
     * Serializes a Probe Batch object to a byte array. This is typically called by the game engine's editor in order
     * to save the Probe Batch object's data to disk.
     *
     * @param probeBatch Handle to a Probe Batch object.
     * @param data       [out] Byte array into which the Probe Batch object will be serialized. It is the
     *                   caller's responsibility to manage memory for this array. The array must be large
     *                   enough to hold all the data in the Probe Batch object. May be NULL, in which
     *                   case no data is returned; this is useful when finding out the size of the data
     *                   stored in the Probe Batch object.
     * @return Size (in bytes) of the serialized data.
     * Original signature : <code>IPLint32 iplSaveProbeBatch(IPLhandle, IPLbyte*)</code>
     * @deprecated use the safer methods {@link #iplSaveProbeBatch(Pointer, ByteBuffer)} and {@link #iplSaveProbeBatch(Pointer, Pointer)} instead
     */
    @Deprecated
    int iplSaveProbeBatch(Pointer probeBatch, Pointer data);

    /**
     * Serializes a Probe Batch object to a byte array. This is typically called by the game engine's editor in order
     * to save the Probe Batch object's data to disk.
     *
     * @param probeBatch Handle to a Probe Batch object.
     * @param data       [out] Byte array into which the Probe Batch object will be serialized. It is the
     *                   caller's responsibility to manage memory for this array. The array must be large
     *                   enough to hold all the data in the Probe Batch object. May be NULL, in which
     *                   case no data is returned; this is useful when finding out the size of the data
     *                   stored in the Probe Batch object.
     * @return Size (in bytes) of the serialized data.
     * Original signature : <code>IPLint32 iplSaveProbeBatch(IPLhandle, IPLbyte*)</code>
     */
    int iplSaveProbeBatch(Pointer probeBatch, ByteBuffer data);

    /**
     * Deserializes a Probe Batch object from a byte array. This is typically called by the game engine's editor when
     * loading a Probe Batch object from disk. Calling this function implicitly calls ::iplFinalizeProbeBatch, so
     * you do not need to call it explicitly.
     *
     * @param context    Handle to the Context object used by the game engine.
     * @param data       Byte array containing the serialized representation of the Probe Batch object. Must
     *                   not be NULL.
     * @param size       Size (in bytes) of the serialized data.
     * @param probeBatch [out] Handle to the created Probe Batch object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplLoadProbeBatch(IPLhandle, IPLbyte*, IPLint32, IPLhandle*)</code>
     * @deprecated use the safer methods {@link #iplLoadProbeBatch(Pointer, ByteBuffer, int, PointerByReference)} and {@link #iplLoadProbeBatch(Pointer, Pointer, int, PointerByReference)} instead
     */
    @Deprecated
    int iplLoadProbeBatch(Pointer context, Pointer data, int size, PointerByReference probeBatch);

    /**
     * Deserializes a Probe Batch object from a byte array. This is typically called by the game engine's editor when
     * loading a Probe Batch object from disk. Calling this function implicitly calls ::iplFinalizeProbeBatch, so
     * you do not need to call it explicitly.
     *
     * @param context    Handle to the Context object used by the game engine.
     * @param data       Byte array containing the serialized representation of the Probe Batch object. Must
     *                   not be NULL.
     * @param size       Size (in bytes) of the serialized data.
     * @param probeBatch [out] Handle to the created Probe Batch object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplLoadProbeBatch(IPLhandle, IPLbyte*, IPLint32, IPLhandle*)</code>
     */
    int iplLoadProbeBatch(Pointer context, ByteBuffer data, int size, PointerByReference probeBatch);

    /**
     * Creates a Probe Manager object. A Probe Manager object manages a set of Probe Batch objects are runtime.
     * It is typically exported from the game engine to the audio engine via an Environment object. Probe Batch
     * objects can be dynamically added to or removed from a Probe Manager object.
     *
     * @param context      Handle to the Context object used by the game engine.
     * @param probeManager [out] Handle to the created Probe Manager object.
     * @return Status code indicating whether or not the operation succeeded.
     * Original signature : <code>IPLerror iplCreateProbeManager(IPLhandle, IPLhandle*)</code>
     */
    int iplCreateProbeManager(Pointer context, PointerByReference probeManager);

    /**
     * Destroys a Probe Manager object.
     *
     * @param probeManager [in, out] Address of a handle to the Probe Manager object to destroy.
     *                     Original signature : <code>void iplDestroyProbeManager(IPLhandle*)</code>
     */
    void iplDestroyProbeManager(PointerByReference probeManager);

    /**
     * Adds a Probe Batch to a Probe Manager object. Once this function returns, probes in the Probe Batch will be
     * used to calculate sound propagation effects.
     *
     * @param probeManager Handle to a Probe Manager object.
     * @param probeBatch   Handle to the Probe Batch object to add.
     *                     Original signature : <code>void iplAddProbeBatch(IPLhandle, IPLhandle)</code>
     */
    void iplAddProbeBatch(Pointer probeManager, Pointer probeBatch);

    /**
     * Removes a Probe Batch from a Probe Manager object. Once this function returns, probes in the Probe Batch will
     * no longer be used to calculate sound propagation effects.
     *
     * @param probeManager Handle to a Probe Manager object.
     * @param probeBatch   Handle to the Probe Batch object to remove.
     *                     Original signature : <code>void iplRemoveProbeBatch(IPLhandle, IPLhandle)</code>
     */
    void iplRemoveProbeBatch(Pointer probeManager, Pointer probeBatch);

    /**
     * Bakes reverb at all probes in a Probe Box. Phonon defines reverb as the indirect sound received at a probe
     * when a source is placed at the probe's location. This is a time-consuming operation, and should typically be
     * called from the game engine's editor. The numThreads set on the IPLSimulationSettings structure passed
     * when calling ::iplCreateEnvironment to create the Environment object are used for multi-threaded baking.
     *
     * @param environment      Handle to an Environment object.
     * @param probeBox         Handle to the Probe Box containing the probes for which to bake reverb.
     * @param bakingSettings   The kind of acoustic responses to bake.
     * @param progressCallback Pointer to a function that reports the percentage of this function's work that
     *                         has been completed. May be NULL.
     *                         Original signature : <code>void iplBakeReverb(IPLhandle, IPLhandle, IPLBakingSettings, IPLBakeProgressCallback)</code>
     */
    void iplBakeReverb(Pointer environment, Pointer probeBox, BakingSettings bakingSettings, Phonon.BakeProgressCallback progressCallback);

    /**
     * Bakes propagation effects from a specified source to all probes in a Probe Box. Sources are defined in terms
     * of a position and a sphere of influence; all probes in the Probe Box that lie within the sphere of influence
     * are processed by this function. This is a time-consuming operation, and should typically be called from the
     * game engine's editor. The numThreads set on the IPLSimulationSettings structure passed when calling
     * ::iplCreateEnvironment to create the Environment object are used for multi-threaded baking.
     *
     * @param environment      Handle to an Environment object.
     * @param probeBox         Handle to the Probe Box containing the probes for which to bake reverb.
     * @param sourceInfluence  Sphere defined by the source position (at its center) and its radius of
     *                         influence.
     * @param sourceIdentifier Identifier of the source. At run-time, a Convolution Effect object can use this
     *                         identifier to look up the correct impulse response information.
     * @param bakingSettings   The kind of acoustic responses to bake.
     * @param progressCallback Pointer to a function that reports the percentage of this function's work that
     *                         has been completed. May be NULL.
     *                         Original signature : <code>void iplBakePropagation(IPLhandle, IPLhandle, IPLSphere, IPLBakedDataIdentifier, IPLBakingSettings, IPLBakeProgressCallback)</code>
     */
    void iplBakePropagation(Pointer environment, Pointer probeBox, Sphere sourceInfluence, BakedDataIdentifier sourceIdentifier, BakingSettings bakingSettings, Phonon.BakeProgressCallback progressCallback);

    /**
     * Bakes propagation effects from all probes in a Probe Box to a specified listener. Listeners are defined
     * solely by their position; their orientation may freely change at run-time. This is a time-consuming
     * operation, and should typically be called from the game engine's editor. The numThreads set on the
     * IPLSimulationSettings structure passed when calling ::iplCreateEnvironment to create the Environment
     * object are used for multi-threaded baking.
     *
     * @param environment        Handle to an Environment object.
     * @param probeBox           Handle to the Probe Box containing the probes for which to bake reverb.
     * @param listenerInfluence  Position and influence radius of the listener.
     * @param listenerIdentifier Identifier of the listener. At run-time, a Convolution Effect object can use this
     *                           identifier to look up the correct impulse response information.
     * @param bakingSettings     The kind of acoustic responses to bake.
     * @param progressCallback   Pointer to a function that reports the percentage of this function's work that
     *                           has been completed. May be NULL.
     *                           Original signature : <code>void iplBakeStaticListener(IPLhandle, IPLhandle, IPLSphere, IPLBakedDataIdentifier, IPLBakingSettings, IPLBakeProgressCallback)</code>
     */
    void iplBakeStaticListener(Pointer environment, Pointer probeBox, Sphere listenerInfluence, BakedDataIdentifier listenerIdentifier, BakingSettings bakingSettings, Phonon.BakeProgressCallback progressCallback);

    /**
     * Cancels any bake operations that may be in progress. Typically, an application will call ::iplBakeReverb
     * or ::iplBakePropagation in a separate thread from the editor's GUI thread, to keep the GUI responsive.
     * This function can be called from the GUI thread to safely and prematurely terminate execution of any
     * of these functions.
     * Original signature : <code>void iplCancelBake()</code>
     */
    void iplCancelBake();

    /**
     * Deletes all baked data in a Probe Box that is associated with a given source. If no such baked data
     * exists, this function does nothing.
     *
     * @param probeBox   Handle to a Probe Box object.
     * @param identifier Identifier of the source whose baked data is to be deleted.
     *                   Original signature : <code>void iplDeleteBakedDataByIdentifier(IPLhandle, IPLBakedDataIdentifier)</code>
     */
    void iplDeleteBakedDataByIdentifier(Pointer probeBox, BakedDataIdentifier identifier);

    /**
     * Returns the size (in bytes) of the baked data stored in a Probe Box corresponding to a given source.
     * This is useful for displaying statistics in the editor's GUI.
     *
     * @param probeBox   Handle to a Probe Box object.
     * @param identifier Identifier of the source whose baked data size is to be returned.
     * @return Size (in bytes) of the baked data stored in the Probe Box corresponding to the source.
     * Original signature : <code>IPLint32 iplGetBakedDataSizeByIdentifier(IPLhandle, IPLBakedDataIdentifier)</code>
     */
    int iplGetBakedDataSizeByIdentifier(Pointer probeBox, BakedDataIdentifier identifier);

    interface Error {
        /**
         * The operation completed successfully.
         */
        int STATUS_SUCCESS = 0;
        /**
         * An unspecified error occurred.
         */
        int STATUS_FAILURE = 1;
        /**
         * The system ran out of memory.
         */
        int STATUS_OUTOFMEMORY = 2;
        /**
         * An error occurred while initializing an external dependency.
         */
        int STATUS_INITIALIZATION = 3;
    }

    interface LogFunction extends Callback {
        void apply(String message);
    }


    interface AllocateFunction extends Callback {
        /**
         * Prototype of a callback that allocates memory.
         * This is usually specified to let Phonon use a custom memory allocator. The default behavior is to use the OS-dependent aligned version of malloc.
         *
         * @param size      The number of bytes to allocate.
         * @param alignment The alignment (in bytes) of the start address of the allocated memory.
         */
        Pointer apply(NativeLong size, NativeLong alignment);
    }

    interface FreeFunction extends Callback {
        /**
         * Prototype of a callback that frees a block of memory.
         * This is usually specified when using a custom memory allocator with Phonon. The default behavior is to use the OS-dependent aligned version of free.
         *
         * @param memoryBlock Pointer to the block of memory.
         */
        void apply(Pointer memoryBlock);
    }

    interface LoadSceneProgressCallback extends Callback {
        void apply(float progress);
    }

    interface FinalizeSceneProgressCallback extends Callback {
        void apply(float progress);
    }

    interface ClosestHitCallback extends Callback {
        void apply(FloatByReference origin, FloatByReference direction, float minDistance, float maxDistance, FloatByReference hitDistance, FloatByReference hitNormal, PointerByReference hitMaterial, Pointer userData);
    }

    interface AnyHitCallback extends Callback {
        void apply(FloatByReference origin, FloatByReference direction, float minDistance, float maxDistance, IntByReference hitExists, Pointer userData);
    }

    interface BatchedClosestHitCallback extends Callback {
        void apply(int numRays, Vector3 origins, Vector3 directions, int rayStride, FloatByReference minDistances, FloatByReference maxDistances, FloatByReference hitDistances, Vector3 hitNormals, PointerByReference hitMaterials, int hitStride, Pointer userData);
    }

    interface BatchedAnyHitCallback extends Callback {
        void apply(int numRays, Vector3 origins, Vector3 directions, int rayStride, FloatByReference minDistances, FloatByReference maxDistances, Pointer hitExists, Pointer userData);
    }

    interface SimulationThreadCreateCallback extends Callback {
        void apply();
    }

    interface SimulationThreadDestroyCallback extends Callback {
        void apply();
    }

    interface DistanceAttenuationCallback extends Callback {
        float apply(float distance, Pointer userData);
    }

    interface AirAbsorptionCallback extends Callback {
        float apply(float distance, int band, Pointer userData);
    }

    interface DirectivityCallback extends Callback {
        float apply(Vector3 direction, Pointer userData);
    }

    interface ProbePlacementProgressCallback extends Callback {
        void apply(float progress);
    }

    interface BakeProgressCallback extends Callback {
        void apply(float progress);
    }

}
