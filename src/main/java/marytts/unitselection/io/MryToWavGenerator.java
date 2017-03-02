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

//import marytts.unitselection.data.TimelineReader;
import marytts.util.data.Datagram;
import java.nio.ByteBuffer;

/**
 * Created by Moitree Basu on 07/11/16.
 */
public class MryToWavGenerator {

    public static void main(String[] args) {

        MryToWavGenerator newGenerator = new MryToWavGenerator();
        String timelineDir = "./resourceFiles/generated";
        newGenerator.compute(timelineDir);

    }

    /**
     * Outputs the timeline data in wav format.
     *
     * @param timelineDir
     *            The directory path for the timeline file.
     */

    public void compute(String timelineDir){

        int dataGramSize = 0;

        try {

            /*
             *  Creates a timeline file reader
             */
            TimelineReadWrite treader = new TimelineReadWrite(timelineDir+"/Timeline.mry");

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

            Datagram[] dg = treader.getDatagrams(0, (int)treader.getNumDatagrams(), sampleRate, offset);

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

            wavWriter.export(timelineDir+"/Timeline.wav",sampleRate, bb.array());

            System.out.println("Regeneration Completed.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}


