package marytts.unitselection.io;

import marytts.exceptions.MaryConfigurationException;
import marytts.unitselection.data.TimelineReader;
import marytts.util.MaryUtils;
import marytts.util.data.Datagram;
import marytts.util.data.MaryHeader;
import org.apache.commons.io.FilenameUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by moitree on 07/11/16.
 */
public class datagramGenerator {

    public static void main(String[] args) {

        int dataGramSize = 0;

        try {
            TimelineReader treader = new TimelineReader("./resourceFiles/generated/arctic_a0001_Generated.mry");
            System.out.println(treader.getNumDatagrams());


            long[] offset = new long[4];

            int sampleRate = treader.getSampleRate();

            Datagram[] dg = treader.getDatagrams(0, (int)treader.getNumDatagrams(), sampleRate, offset);

            for (Datagram b : dg) {
                dataGramSize += b.getData().length;
            }

            ByteBuffer bb = ByteBuffer.allocate(dataGramSize);

            for (Datagram b : dg) {
                bb.put(b.getData());
            }

            ArrayList<Byte> WavData = new ArrayList<Byte>();

            for (int i = 0; i<bb.array().length; i++){
                if( i % 2 != 0) {
                    WavData.add(bb.get(i));
                }
            }

            System.out.println(bb.array().length);

            Wav wavWriter = new Wav();

           wavWriter.export("./resourceFiles/generated/arctic_a0001_Generated.wav",sampleRate, bb.array());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


