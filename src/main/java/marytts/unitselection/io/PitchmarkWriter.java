/**
 * Copyright 2004-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of MARY TTS.
 *
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

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
 * Created by Moitree Basu on 02/01/17.
 */
public class PitchmarkWriter {

    public static void main(String[] args) {

        PitchmarkWriter newWriter = new PitchmarkWriter();
        String timelineDir = "./resourceFiles/generated";
        String wavDir = "./resourceFiles/wav";
        String pmDir = "./resourceFiles/pm";
        newWriter.write(timelineDir, wavDir, pmDir);

    }

    /**
     * Triggers the writing of the pitchmark file to the disk.
     *
     * @param timelineDir
     *            The directory path for the timeline file.
     * @param wavDir
     *            The directory path for the wav files.
     * @param pmDir
     *            The directory path for the pitchmark files.
     */

    public void write(String timelineDir, String wavDir, String pmDir){

        ArrayList<String> pmFileList = new ArrayList<String>();
        ArrayList<String> wavFileList = new ArrayList<String>();


        File[] pmfiles = new File(pmDir).listFiles();
        File[] wavfiles = new File(wavDir).listFiles();

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
            PrintWriter pmWriter = new PrintWriter(timelineDir+"/Timeline.pm", "UTF-8");

            pmWriter.write("EST_File Track\n" +
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
                    pmWriter.write( Double.valueOf(df.format(timestamp))+"\t1\n");
                }
                wavDurationCounter+=durationInSeconds;
                // System.out.println(wavDurationCounter);
            }

            pmWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
