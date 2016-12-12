package marytts.unitselection.io;

import marytts.unitselection.data.TimelineReader;
import marytts.util.data.Datagram;
import org.apache.commons.io.FilenameUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by moitree on 07/11/16.
 */
public class datagramGenerator {

    public static void main(String[] args) {

        try {
            TimelineReader treader = new TimelineReader("./src/test/resources/cmu_time_awb/timeline_waveforms.mry");
            System.out.println(treader.getNumDatagrams());

            Datagram dg = treader.getDatagram(05);
            System.out.println(dg.getData());

            WavWriter newWavWriter = new WavWriter();
            newWavWriter.writeWavFile("./out_wav.wav", dg.getData());

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


