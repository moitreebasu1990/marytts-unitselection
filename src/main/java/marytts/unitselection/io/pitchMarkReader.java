package marytts.unitselection.io;

import marytts.tools.voiceimport.TimelineWriter;
import marytts.tools.voiceimport.WavReader;
import marytts.util.data.Datagram;
import marytts.util.data.ESTTrackReader;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


/**
 * Created by pradipta on 06/12/16.
 */
public class pitchMarkReader {

    public static void main(String[] args) {
        Datagram tempDatagram;
        ESTTrackReader pitchReader = new ESTTrackReader("/Users/pradipta/workspace/dfki/files/arctic_a0123.pm");

        System.out.print("The time array is : ");
        for (float time : pitchReader.getTimes())
            System.out.print(time + "\t");

        float[][] pitchFrames = pitchReader.getFrames();

        System.out.println("\n\nThe frame array has length: " + pitchReader.getTimeSpan());
        WavReader newWavReader = new WavReader("/Users/pradipta/workspace/dfki/files/arctic_a0123.wav");
        System.out.println(newWavReader.getSampleRate());

        short[] wave = newWavReader.getSamples();
        int globSampleRate = newWavReader.getSampleRate();

        TimelineWriter waveTimeline = new TimelineWriter("x.mry", "\n", globSampleRate, 0.1);

        /* - Reset the frame locations in the local file */
        int frameStart, numDatagrams = 0;
        int frameEnd = 0;
        double totalTime = 0.0;
        double localTime = 0.0;

        for (int f = 0; f < pitchReader.getNumFrames(); f++) {


            frameStart = frameEnd;
            frameEnd = (int) ((double) pitchReader.getTime(f) * (double) (globSampleRate));

            int duration = frameEnd - frameStart;
            ByteArrayOutputStream buff = new ByteArrayOutputStream(2 * duration);
            DataOutputStream subWave = new DataOutputStream(buff);

            try {

                for (int k = 0; k < duration; k++) {
                    subWave.writeShort(wave[frameStart + k]);
                }

                tempDatagram = new Datagram(duration, buff.toByteArray());

                waveTimeline.feed(tempDatagram, globSampleRate);

                totalTime += duration;
                localTime += duration;
                numDatagrams++;

                /*System.out.println("=====================\n\nContents of the datagram no:" + numDatagrams + " is :");
                for (byte b : tempDatagram.getData()) {

                    System.out.print(b + "\t");

                }*/

            }catch (Exception e){
                e.printStackTrace();
            }



        }

    }
}