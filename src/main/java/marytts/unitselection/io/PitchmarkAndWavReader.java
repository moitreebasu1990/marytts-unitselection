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

import marytts.util.data.Datagram;
import marytts.util.data.ESTTrackReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by Pradipta Deb on 06/12/16.
 */
public class PitchmarkAndWavReader {

   /* public static void main(String[] args){

        PitchmarkAndWavReader newReader = new PitchmarkAndWavReader();
        String timelineDir = "./resourceFiles/generated";
        String wavDir = "./resourceFiles/wav";
        String pmDir = "./resourceFiles/pm";
        newReader.createTimeline(timelineDir, wavDir, pmDir);

    }*/

    /**
     * Reads each (.wav + .pm) file and writes tha data in .mry format to disk.
     *
     * @param timelineDir The directory path for the timeline file.
     * @param wavDir      The directory path for the wav files.
     * @param pmDir       The directory path for the pitchmark files.
     */

    public void createTimeline(String timelineDir, String wavDir, String pmDir) {

        try {

            /**
             * Setting global sample rate, creating Timeline writer
             * & Creating the array of filenames in pitchmark and wavfile directory.
             */

            int globSampleRate = 16000;
            TimelineReaderAndWriter waveTimeline = new TimelineReaderAndWriter(timelineDir+"/Timeline.mry", "\n", globSampleRate, 1);

            ArrayList<String> pmFileList = new ArrayList<String>();
            ArrayList<String> wavFileList = new ArrayList<String>();
            File[] pmfiles = new File(pmDir).listFiles();
            File[] wavfiles = new File(wavDir).listFiles();
            Arrays.sort(pmfiles);
            Arrays.sort(wavfiles);

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


            /*
               Read each pitchmark file and corresponding wav file, cut the wav byte into datagrams using timestamps, write the datagrams to disk.
             */
            for (int i = 0; i < pmFileList.size(); i++) {

                ESTTrackReader pitchReader = new ESTTrackReader(pmFileList.get(i));
                WavReaderAndWriter newWavReader = new WavReaderAndWriter();
                newWavReader.read(wavFileList.get(i));
                short[] wave = newWavReader.getSamples();

                int frameStart, frameEnd = 0;
                /*int numDatagrams = 0;
                double totalTime = 0.0, localTime = 0.0;*/

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

                    waveTimeline.writeTimeline(new Datagram(duration, buff.toByteArray()), globSampleRate);

                    /*totalTime += (double) duration;
                    localTime += (double) duration;
                    numDatagrams++;*/

                }

            }

            waveTimeline.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}