package ru.klaw.steamaudio.computedevice;

import com.sun.jna.ptr.PointerByReference;
import org.junit.jupiter.api.Test;
import ru.klaw.steamaudio.PhononTest;
import ru.klaw.steamaudio.Phonon;

import static org.junit.jupiter.api.Assertions.*;

class ComputeDeviceFilterTest extends PhononTest {

    @Test
    void createComputeDevice(){
        ComputeDeviceFilter computeDeviceFilter = new ComputeDeviceFilter(ComputeDeviceType.GPU,1,1);
        PointerByReference device = new PointerByReference();
        int status = phonon.iplCreateComputeDevice(context.getValue(), computeDeviceFilter, device);
        assertEquals(Phonon.Error.STATUS_SUCCESS,status);
        phonon.iplDestroyComputeDevice(device);
    }

}