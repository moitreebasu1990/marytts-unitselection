package marytts.unitselection.io;

import marytts.util.data.ESTTrackReader;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by moitree on 02/01/17.
 */
public class PitchmarkWriter {

    public static void main(String[] args) {

        PitchmarkWriter newWriter = new PitchmarkWriter();
        newWriter.write();

    }

    public void write(){

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

        double wavDurationCounter = 0.0;
        DecimalFormat df = new DecimalFormat("#.#########");

        try {
            PrintWriter pmwriter = new PrintWriter("./resourceFiles/generated/pmFile_Generated.pm", "UTF-8");
            pmwriter.write("EST_File Track\n" +
                    "DataType ascii\n" +
                    "NumFrames 535\n" +
                    "NumChannels 0\n" +
                    "NumAuxChannels 0\n" +
                    "EqualSpace 0\n" +
                    "BreaksPresent true\n" +
                    "CommentChar ;\n" +
                    "EST_Header_End\n");

            for (int i = 0; i < pmFileList.size(); i++) {
                File wavFile = new File(wavFileList.get(i));
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
                AudioFormat format = audioInputStream.getFormat();
                long audioFileLength = wavFile.length();
                int frameSize = format.getFrameSize();
                float frameRate = format.getFrameRate();
                float durationInSeconds = (audioFileLength / (frameSize * frameRate));


                ESTTrackReader pitchReader = new ESTTrackReader(pmFileList.get(i));

                for (int f = 0; f < pitchReader.getNumFrames(); f++) {

                    double timestamp = (double) pitchReader.getTime(f);
                    if (i != 0) {
                        timestamp+=wavDurationCounter;

                    }
                    pmwriter.write( Double.valueOf(df.format(timestamp))+"\t1\n");
                }
                wavDurationCounter+=durationInSeconds;
                // System.out.println(wavDurationCounter);



            }

            pmwriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
