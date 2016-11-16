package marytts.unitselection.io;

import org.apache.commons.io.FilenameUtils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by moitree on 07/11/16.
 */
public class test {

    public ArrayList createArrayListWav(String wavDir) {

        ArrayList<String> wavFileList = new ArrayList<String>();
        File[] files = new File(wavDir).listFiles();

        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile() && FilenameUtils.getExtension(file.getAbsolutePath()).equalsIgnoreCase("wav")) {
                System.out.println(file.getAbsolutePath());
                wavFileList.add(file.getAbsolutePath());
            }
        }

        return wavFileList;

    }

    public ArrayList readWavFiles(ArrayList wavFileList) {

        int totalFramesRead = 0;
        ArrayList<byte[]> waveFiles = new ArrayList<>();


        for (int file = 0; file < wavFileList.size(); file++) {
            File fileIn = new File((String) wavFileList.get(file));
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileIn);
                System.out.println(audioInputStream.getFormat());
                int bytesPerFrame = audioInputStream.getFormat().getFrameSize();
                if (bytesPerFrame == AudioSystem.NOT_SPECIFIED) {
                    bytesPerFrame = 1;
                }
                //System.out.println("Frame size is:" + bytesPerFrame);


                // Set an arbitrary buffer size of 1024.
                int numBytes = 512 * bytesPerFrame;

                System.out.println("Buffer size is:" + numBytes);

                byte[] audioBytes = new byte[numBytes];

                ArrayList<byte[]> waveFile = new ArrayList<>();

                try {
                    int numBytesRead = 0;
                    int numFramesRead = 0;
                    int counter = 0;

                    // Try to read numBytes bytes from the file.
                    while ((numBytesRead = audioInputStream.read(audioBytes)) != -1) {
                        counter++;

                        // Calculate the number of frames actually read.
                        numFramesRead = numBytesRead / bytesPerFrame;
                        totalFramesRead += numFramesRead;
                        /*for (byte b : audioBytes) {
                            waveFile.add(b);
                        }*/
                    }
                    //System.out.println(totalFramesRead);R
                    //System.out.println(audioBytes.length);
                    //System.out.println(waveFile);
                    waveFiles.add(audioBytes);
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        //System.out.println(waveFiles.size());
        return waveFiles;

    }

    public static void main(String[] args) {

        test wavReader = new test();
        ArrayList<String> wavFileList = wavReader.createArrayListWav("/Users/pradipta/workspace/dfki/files");
        ArrayList<Object> wavefiles = wavReader.readWavFiles(wavFileList);

        System.out.println("Contents of the .wav file is :"+ "\n");
        byte [] x = (byte[]) wavefiles.get(0);
        for(byte b : x){
            System.out.print(b);
        }
    }
}


