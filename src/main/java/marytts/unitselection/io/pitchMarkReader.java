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
				/* - For each frame in the WAV file: */
        for (int f = 0; f < pitchReader.getNumFrames(); f++) {

					/* Locate the corresponding segment in the wave file */
            frameStart = frameEnd;
            frameEnd = (int) ((double) pitchReader.getTime(f) * (double) (globSampleRate));
            assert frameEnd <= wave.length : "Frame ends after end of wave data: " + frameEnd + " > " + wave.length;

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

                System.out.println("=====================\n\nContents of the datagram no:" + numDatagrams + " is :");
                for (byte b : tempDatagram.getData()) {

                    System.out.print(b + "\t");

                }

            }catch (Exception e){
                e.printStackTrace();
            }



        }




        /*Datagram waveDatagram = new Datagram(1, x);

        System.out.println("=====================\n\nContents of the datagram is :");
        for (byte b : waveDatagram.getData()) {

            System.out.print(b + "\t");

        }*/


    }
}

/* PmRead pmread = new PmRead();
        ArrayList pmFileList = pmread.getPmFileList();
        pmread.readPitchMarkFile(pmFileList);
        System.out.println("");
        }
        public static void call(String[] args){

            //run();
            System.out.println("");

        }

    public void readPitchMarkFile(ArrayList pmFileList) {

        for (int fileCounter = 0; fileCounter < pmFileList.size(); fileCounter++) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public ArrayList getPmFileList(String path) {

        ArrayList<String> pmFileList = new ArrayList<String>();
        File[] files = new File(path).listFiles();

        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile() && FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("pm")) {
                System.out.println(file.getAbsolutePath());
                pmFileList.add(file.getAbsolutePath());
            }
        }

        return pmFileList;
}*/
