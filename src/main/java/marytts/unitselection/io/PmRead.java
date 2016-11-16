package marytts.unitselection.io;


import marytts.util.data.Datagram;
import marytts.util.io.BasenameList;

import java.util.ArrayList;

/**
 * Created by pradipta on 08/11/16.
 */
public class PmRead {


    public static void run() {

        ESTTrackReader pitchReader = new ESTTrackReader("/Users/pradipta/workspace/dfki/files/arctic_a0123.pm");

        System.out.print("The time array is : ");
        for (float time: pitchReader.getTimes())
            System.out.print(time +"\t");

        float[][] pitchFrames = pitchReader.getFrames();

        System.out.println("\n\nThe frame array has length: "+pitchFrames.length);

        test wavReader = new test();

        ArrayList wavFilesList = wavReader.createArrayListWav("/Users/pradipta/workspace/dfki/files/");
        ArrayList wavFiles = wavReader.readWavFiles(wavFilesList);

        byte[] x = (byte[]) wavFiles.get(0);



        Datagram waveDatagram = new Datagram(12,x);

        System.out.println("=====================\n\nContents of the datagram is :");
        for (byte b: waveDatagram.getData()) {

            System.out.print(b+"\t");

        }

        //System.out.println(dg.getDuration());
        /*PmRead pmread = new PmRead();
        ArrayList pmFileList = pmread.getPmFileList();
        pmread.readPitchMarkFile(pmFileList);
        System.out.println("");*/

        BasenameList bstList = new BasenameList("/Users/pradipta/workspace/dfki/files", "wav") ;
        try {
            bstList.write("/Users/pradipta/workspace/dfki/files/basename.lst");
            System.out.println(bstList.getListAsArray().length);
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

    public static void main(String[] args){

        run();

    }

    /**
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

    }
     */

}
