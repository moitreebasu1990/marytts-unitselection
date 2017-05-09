package marytts.unitselection.io;

import marytts.util.data.Datagram;
import java.io.PrintWriter;

/**
 * Created by pradipta on 05/04/17.
 */
public class BasenameTimelineReader {

    public static void readBasenameTimeline(String timelineDir, String basenamesOutputDir){
        /*
         *  Setting wavfile offset, sample rate
         */
        long[] offset = new long[4];

        try {
            /*
             *  Creates a timeline file reader for the basenames and a .csv file writer to dump the contents os the basename file.
             */
            TimelineReaderAndWriter bsnTimelineReader = new TimelineReaderAndWriter(timelineDir + "/timeline_basenames.mry");
            PrintWriter writer = new PrintWriter(basenamesOutputDir + "/basenames.csv", "US-ASCII");
            StringBuilder sb = new StringBuilder();

             /*
             * Getting the datagrams from the Timeline file using the timeline reader.
             *
             * Writing the contents of each datagram unit in the string builder
             */

            int sampleRate =  bsnTimelineReader.getSampleRate();
            Datagram[] dg = bsnTimelineReader.getDatagrams(0, (int) bsnTimelineReader.getNumDatagrams(), sampleRate, offset);

            sb.append("Basename");
            sb.append(",");
            sb.append("Duration in Seconds");
            sb.append('\n');
            sb.append('\n');

            for (Datagram b : dg) {
                sb.append(new String(b.getData(), "US-ASCII"));
                sb.append(",");
                sb.append(String.format("%.4g", (float)b.getDuration()/sampleRate));
                sb.append('\n');
            }

            /*
             * Dumping the string content to .csv file.
             */
            writer.write(sb.toString());
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


   /* public static void main(String[] args) {

        BasenameTimelineReader bsnReader = new BasenameTimelineReader();
        String timelineDir = "";
        String basenamesOutputDir = "";
        bsnReader.readBasenameTimeline(timelineDir,basenamesOutputDir);

    }*/
}
