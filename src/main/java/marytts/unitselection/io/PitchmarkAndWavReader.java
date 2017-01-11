package marytts.unitselection.io;

import marytts.tools.voiceimport.TimelineWriter;
import marytts.tools.voiceimport.WavReader;
import marytts.util.data.Datagram;
import marytts.util.data.ESTTrackReader;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;


/**
 * Created by pradipta on 06/12/16.
 */
public class PitchmarkAndWavReader {

    public static void main(String[] args){

        PitchmarkAndWavReader newReader = new PitchmarkAndWavReader();
        newReader.read();

    }

    public void read() {

        try {
            Datagram tempDatagram;
            int globSampleRate = 16000;
            TimelineWriter waveTimeline = new TimelineWriter("./resourceFiles/generated/arctic_Generated.mry", "\n", globSampleRate, 1);

            ArrayList<String> pmFileList = new ArrayList<String>();
            ArrayList<String> wavFileList = new ArrayList<String>();
            File[] pmfiles = new File("./resourceFiles/pm").listFiles();
            File[] wavfiles = new File("./resourceFiles/wav").listFiles();

            for (File file : pmfiles) {
                if (file.isFile()) {
                    pmFileList.add(file.getPath());
                }
            }

            for (File file : wavfiles) {
                if (file.isFile()) {
                    wavFileList.add(file.getPath());
                }
            }

            for (int i = 0; i < pmFileList.size(); i++) {

                ESTTrackReader pitchReader = new ESTTrackReader(pmFileList.get(i));
                WavReader newWavReader = new WavReader(wavFileList.get(i));


                short[] wave = newWavReader.getSamples();

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


                    for (int k = 0; k < duration; k++) {
                        subWave.writeShort(wave[frameStart + k]);
                    }

                    tempDatagram = new Datagram(duration, buff.toByteArray());

                    waveTimeline.feed(tempDatagram, globSampleRate);

                    totalTime += duration;
                    localTime += duration;
                    numDatagrams++;

                }

            }

            waveTimeline.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}