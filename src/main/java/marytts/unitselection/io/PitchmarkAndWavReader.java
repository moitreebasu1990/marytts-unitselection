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

import marytts.tools.voiceimport.TimelineWriter;
import marytts.tools.voiceimport.WavReader;
import marytts.util.data.Datagram;
import marytts.util.data.ESTTrackReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;


/**
 * Created by Pradipta Deb on 06/12/16.
 */
public class PitchmarkAndWavReader {

    public static void main(String[] args){

        PitchmarkAndWavReader newReader = new PitchmarkAndWavReader();
        String timelineDir = "./resourceFiles/generated";
        String wavDir = "./resourceFiles/wav";
        String pmDir = "./resourceFiles/pm";
        newReader.read(timelineDir, wavDir, pmDir);

    }

    public void read(String timelineDir, String wavDir, String pmDir) {

        try {
            Datagram tempDatagram;
            int globSampleRate = 16000;
            TimelineWriter waveTimeline = new TimelineWriter(timelineDir+"/Timeline.mry", "\n", globSampleRate, 1);

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