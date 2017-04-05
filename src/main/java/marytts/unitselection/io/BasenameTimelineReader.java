package marytts.unitselection.io;

import marytts.util.data.Datagram;
import java.io.PrintWriter;

/**
 * Created by pradipta on 05/04/17.
 */
public class BasenameTimelineReader {

    public static void main(String[] args) {

        BasenameTimelineReader bsnReader = new BasenameTimelineReader();
        String timelineDir = "/Users/pradipta/workspace/dfki/marytts-unitselection/build/resources/test/cmu_time_awb";
        String basenamesOutputDir = "/Users/pradipta/workspace/dfki/marytts-unitselection/build/resources/test/cmu_time_awb/timeline";
        bsnReader.readBasenameTimeline(timelineDir,basenamesOutputDir);

    }

    public static void readBasenameTimeline(String timelineDir, String basenamesOutputDir){

        try {
            long[] offset = new long[4];
            TimelineReadWrite bsnTimelineReader = new TimelineReadWrite(timelineDir + "/timeline_basenames.mry", true);
            PrintWriter writer = new PrintWriter(basenamesOutputDir + "/basenames.txt", "US-ASCII");

            int sampleRate =  bsnTimelineReader.getSampleRate();
            Datagram[] dg = bsnTimelineReader.getDatagrams(0, (int) bsnTimelineReader.getNumDatagrams(), sampleRate, offset);

            for (Datagram b : dg) {
                writer.println(new String(b.getData(), "US-ASCII")+" , Duration: "+ Math.round((float)b.getDuration()/sampleRate) +" secs");
            }
            writer.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
