package ru.klaw.steamaudio.directsound;

import com.sun.jna.Memory;
import com.sun.jna.ptr.PointerByReference;
import lombok.var;
import org.junit.jupiter.api.Test;
import ru.klaw.steamaudio.Phonon;
import ru.klaw.steamaudio.PhononTest;
import ru.klaw.steamaudio.audiobuffers.AudioBuffer;
import ru.klaw.steamaudio.audiobuffers.AudioFormat;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelLayout;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelLayoutType;
import ru.klaw.steamaudio.audiobuffers.enums.ChannelOrder;
import ru.klaw.steamaudio.directsound.enums.DirectOcclusionMethod;
import ru.klaw.steamaudio.directsound.enums.DirectOcclusionMode;
import ru.klaw.steamaudio.geometry.Vector3;
import ru.klaw.steamaudio.scene.Material;
import ru.klaw.steamaudio.scene.Materials;
import ru.klaw.steamaudio.simulation.SceneType;
import ru.klaw.steamaudio.simulation.SimulationSettings;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DirectSoundTest extends PhononTest {

    @Test
    void createDirectSoundEffect() throws IOException {
        Material[] materials = (Material[]) Materials.toWood(new Material()).toArray(1);

        PointerByReference scene = new PointerByReference();
        int status = phonon.iplCreateScene(
                context.getValue(), null,
                SceneType.PHONON,
                materials.length, materials,
                null, null, null, null, null,
                scene
        );
        assertEquals(Phonon.Error.STATUS_SUCCESS,status);

        var simulationSettings = new SimulationSettings(SceneType.PHONON,
                256,
                1024,
                2048,
                3,
                6,
                2f,
                2,
                3,
                0,
                0.5f);
        PointerByReference envirement = new PointerByReference();
        status = phonon.iplCreateEnvironment(context.getValue(), null, simulationSettings, scene.getValue(), null, envirement);
        assertEquals(Phonon.Error.STATUS_SUCCESS,status);
        //Create directSoundPath
        Source source = new Source(
                new Vector3(10,2,0),
                new Vector3(10,3,0),
                new Vector3(10,2,1),
                new Vector3(9,2,0),
                new Directivity(0,0,null,null),
                DistanceAttenuationModel.getDefault(),
                AirAbsorptionModel.getDefault());
        DirectSoundPath.ByValue directSoundPath = phonon.iplGetDirectSoundPath(envirement.getValue(),
                new Vector3(),
                new Vector3(1,0,0),
                new Vector3(0,0,1),
                source,
                20f,
                1024,
                DirectOcclusionMode.NONE,
                DirectOcclusionMethod.RAYCAST);

        var inputFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.MONO, ChannelOrder.INTERLEAVED);
        var outputFormat = new AudioFormat(ChannelLayoutType.SPEAKERS, ChannelLayout.STEREO, ChannelOrder.INTERLEAVED);
        var dsEffect = new PointerByReference();
        status=phonon.iplCreateDirectSoundEffect(inputFormat,outputFormat,renderingSettings,dsEffect);
        assertEquals(Phonon.Error.STATUS_SUCCESS,status);

        var input = new AudioBuffer(AudioFormat.mono(),frameSize,new Memory(frameSize*byteToFloat));
        var ouput = new AudioBuffer(AudioFormat.stereo(),frameSize,new Memory(frameSize*byteToFloat*2));
        var option = new DirectSoundEffectOptions(true,true,true,DirectOcclusionMode.NONE);
        phonon.iplApplyDirectSoundEffect(dsEffect.getValue(),input,directSoundPath,option,ouput);

        //Cleanup
        phonon.iplDestroyDirectSoundEffect(dsEffect);
        phonon.iplDestroyEnvironment(envirement);
        phonon.iplDestroyScene(scene);
    }


}