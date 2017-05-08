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
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Moitree Basu on 07/11/16.
 */
public class MryToWavGenerator {

    /*public static void main(String[] args) {

        MryToWavGenerator newGenerator = new MryToWavGenerator();
        String timelineDir = "/Users/pradipta/Desktop/issue735";
        String bsnTimelineDir = "/Users/pradipta/workspace/dfki/marytts-unitselection/build/resources/test/cmu_time_awb/";
        String pmDir = "/Users/pradipta/Desktop/issue735/pm";
        String basenamesOutputDir = "/Users/pradipta/workspace/dfki/marytts-unitselection/build/resources/test/cmu_time_awb/";
        newGenerator.compute(timelineDir,bsnTimelineDir,pmDir,basenamesOutputDir);

    }*/

    /**
     * Outputs the timeline data in wav format.
     *
     * @param timelineDir
     *            The directory path for the timeline file.
     */

    public void compute(String timelineDir, String bsnTimelineDir, String pmDir, String basenamesOutputDir) {

        int dataGramSize = 0;
        long[] offset = new long[4];
        ArrayList<String> dataGramNames = new ArrayList<String>();

        try {

            /*
             *  Creates a timeline file reader for the basenames and a .csv file writer to dump the contents os the basename file.
             */
            TimelineReaderAndWriter bsnTimelineReader = new TimelineReaderAndWriter(bsnTimelineDir + "/timeline_basenames.mry");
            PrintWriter writer = new PrintWriter(basenamesOutputDir + "/basenames.csv", "US-ASCII");
            StringBuilder sb = new StringBuilder();

             /*
             * Getting the datagrams from the Timeline file using the timeline reader.
             *
             * Writing the contents of each datagram unit in the string builder
             */

            int bsnSampleRate =  bsnTimelineReader.getSampleRate();
            Datagram[] bsDataGramArray = bsnTimelineReader.getDatagrams(0, (int) bsnTimelineReader.getNumDatagrams(), bsnSampleRate, offset);

            sb.append("Basename");
            sb.append(",");
            sb.append("Duration in Seconds");
            sb.append('\n');
            sb.append('\n');

            for (Datagram b : bsDataGramArray) {
                sb.append(new String(b.getData(), "US-ASCII"));
                dataGramNames.add(new String(b.getData(), "US-ASCII"));
                sb.append(",");
                sb.append(String.format("%.4g", (float)b.getDuration()/bsnSampleRate));
                sb.append('\n');
            }

            /*
             * Dumping the string content to .csv file.
             */
            writer.write(sb.toString());
            writer.close();


            /*
             *  Creates a timeline file reader
             */
            TimelineReaderAndWriter treader = new TimelineReaderAndWriter(timelineDir + "/timeline.mry");

            /*
             *  Setting wavfile offset, sample rate
             */

            int timelineSampleRate = treader.getSampleRate();


            /*
             * Getting the datagrams from the Timeline file using the timeline reader.
             *
             * Calculating the size of all the datagram to allocate appropriate amount of buffer
             *
             * Putting each individual datagram in the buffer.
             *
             */

            Datagram[] dg = treader.getDatagrams(0, (int) treader.getNumDatagrams(), timelineSampleRate, offset);

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

            WavReaderAndWriter wavWriter = new WavReaderAndWriter();

            wavWriter.export(timelineDir + "/Timeline.wav", timelineSampleRate, bb.array());


            /*
             * This part corresponds to saving the wav files from the datagrams according to their number of frames.
             *
             */

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

                wavWriter.export(timelineDir + "/wav/" + dataGramNames.get(i) + ".wav", timelineSampleRate, bb_temp.array());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


