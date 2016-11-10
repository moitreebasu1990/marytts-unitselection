package marytts.unitselection.io;

/**
 * Created by pradipta on 08/11/16.
 */
public class PmRead {

    /**
    public static void main(Strings[] args) {

        PmRead pmread = new PmRead();
        ArrayList pmFileList = pmread.getPmFileList();
        pmread.readPitchMarkFile(pmFileList);
        System.out.println("");

    }

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
