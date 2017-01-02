package marytts.unitselection.io;

import marytts.tools.voiceimport.TimelineWriter;
import marytts.tools.voiceimport.WavReader;
import marytts.util.data.Datagram;
import marytts.util.data.ESTTrackReader;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by pradipta on 06/12/16.
 */
public class pitchMarkReader {

    public static void main(String[] args) throws IOException {
        Datagram tempDatagram;
        ESTTrackReader pitchReader = new ESTTrackReader("./resourceFiles/pm//arctic_a0001.pm");
        WavReader newWavReader = new WavReader("./resourceFiles/wav/arctic_a0001.wav");


        short[] wave = newWavReader.getSamples();
        int globSampleRate = newWavReader.getSampleRate();

        TimelineWriter waveTimeline = new TimelineWriter("./resourceFiles/generated/arctic_a0001_Generated.mry", "\n", globSampleRate, 1);

        /* - Reset the frame locations in the local file */
        int frameStart, numDatagrams = 0, frameEnd = 0;
        double totalTime = 0.0, localTime = 0.0;

        for (int f = 0; f < pitchReader.getNumFrames(); f++) {


            frameStart = frameEnd;
            frameEnd = (int) ((double) pitchReader.getTime(f) * (double) (globSampleRate));


            int duration = frameEnd - frameStart;

            assert frameEnd <= wave.length : "Frame ends after end of wave data: " + frameEnd + " > " + wave.length;
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(totalTime);

        waveTimeline.close();
    }
}