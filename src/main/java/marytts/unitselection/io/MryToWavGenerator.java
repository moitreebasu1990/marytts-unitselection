/**
 * Copyright 2004-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p>
 * This file is part of MARY TTS.
 * <p>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package marytts.unitselection.io;

import marytts.util.data.Datagram;
import marytts.util.data.ESTTrackReader;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Moitree Basu on 07/11/16.
 */
public class MryToWavGenerator {

    public static void main(String[] args) {

        MryToWavGenerator newGenerator = new MryToWavGenerator();
        String timelineDir = "./resourceFiles/generated";
        String pmDir = "./resourceFiles/pm";
        newGenerator.compute(timelineDir,pmDir);

    }

    /**
     * Outputs the timeline data in wav format.
     *
     * @param timelineDir
     *            The directory path for the timeline file.
     */

    public void compute(String timelineDir, String pmDir) {

        int dataGramSize = 0;

        try {

            /*
             *  Creates a timeline file reader
             */
            TimelineReadWrite treader = new TimelineReadWrite(timelineDir + "/Timeline.mry", false);

            /*
             *  Setting wavfile offset, sample rate
             */

            long[] offset = new long[4];
            int sampleRate = treader.getSampleRate();


            /*
             * Getting the datagrams from the Timeline file using the timeline reader.
             *
             * Calculating the size of all the datagram to allocate appropriate amount of buffer
             *
             * Putting each individual datagram in the buffer.
             *
             */

            Datagram[] dg = treader.getDatagrams(0, (int) treader.getNumDatagrams(), sampleRate, offset);

            for (Datagram b : dg) {
                dataGramSize += b.getData().length;
            }

            ByteBuffer bb = ByteBuffer.allocate(dataGramSize);

            for (Datagram b : dg) {
                bb.put(b.getData());
            }

            /*
             *  Writing the bb array content to disk using wav writer.
             */

            WavReadWrite wavWriter = new WavReadWrite();

            wavWriter.export(timelineDir + "/Timeline.wav", sampleRate, bb.array());


            /************************************8********************/

            ArrayList<String> pmFileList = new ArrayList<String>();
            File[] pmfiles = new File(pmDir).listFiles();
            Arrays.sort(pmfiles);

            for (File file : pmfiles) {
                if (file.isFile()) {
                    pmFileList.add(file.getPath());
                }
            }


            int counter = 0;

            ESTTrackReader pitchReader = null;

            for (int i = 0; i < pmFileList.size(); i++) {
                pitchReader = new ESTTrackReader(pmFileList.get(i));

                int bufferAllocation = 0;
                int dataGramStartPoint = counter;
                int dataGramCount = dataGramStartPoint + pitchReader.getNumFrames();

                while (counter < dataGramCount) {
                    bufferAllocation += dg[counter].getData().length;
                    counter++;
                }

                counter = dataGramStartPoint;
                ByteBuffer bb_temp = ByteBuffer.allocate(bufferAllocation);

                while (counter < dataGramCount) {
                    bb_temp.put(dg[counter].getData());
                    counter++;
                }

                wavWriter.export(timelineDir + "/wav/Timeline" + (i+1) + ".wav", sampleRate, bb_temp.array());


            }


            System.out.println("Regeneration Completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


