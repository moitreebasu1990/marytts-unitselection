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
            TimelineReader treader = new TimelineReader("./arctic_a0001_Generated.mry");
            String fileName = "./arctic_a0001_Generated.mry";

            //test(fileName);

            //UnitSelectionTimelineReader treader = new UnitSelectionTimelineReader(fileName);
            System.out.println(treader.getNumDatagrams());


            long[] offset = new long[4];
            //RandomAccessFile wavFile = new RandomAccessFile("./arctic_a0001_Generated.wav","rw");

            int sampleRate = treader.getSampleRate();

            Datagram[] dg = treader.getDatagrams(0, treader.getNumDatagrams(), sampleRate, offset);

            for (Datagram b : dg) {
                dataGramSize += b.getData().length;
            }

            ByteBuffer bb = ByteBuffer.allocate(dataGramSize);



            for (Datagram b : dg) {
                bb.put(b.getData());
                //b.write(wavFile);
            }

            for (byte b : bb.array()){
                System.out.println(b);
            }

            //wavFile.close();
            WavWriter newWavWriter = new WavWriter();
            newWavWriter.writeWavFile("./arctic_a0001_Generated.wav", bb.array());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


