package marytts.unitselection.io;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by moitree on 07/11/16.
 */
public class test {

    public static ArrayList createArrayListWav(String wavDir) {

        ArrayList<String> wavFileList = new ArrayList<String>();
        File[] files = new File(wavDir).listFiles();

        //If this pathname does not denote a directory, then listFiles() returns null.

        for (File file : files) {
            if (file.isFile()) {
                System.out.println(file.getAbsolutePath());
                wavFileList.add(file.getAbsolutePath());
            }
        }

        return wavFileList;

    }

    public static ArrayList readWavFiles(ArrayList wavFileList) {

        int totalFramesRead = 0;
        ArrayList<Object> waveFiles = new ArrayList<>();


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
                ArrayList<Byte> waveFile = new ArrayList<Byte>();

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
                        for (byte b : audioBytes) {
                            waveFile.add(b);
                        }
                    }
                    //System.out.println(totalFramesRead);
                    //System.out.println(audioBytes.length);
                    //System.out.println(waveFile);
                    waveFiles.add(waveFile);
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
        ArrayList<String> wavFileList = createArrayListWav("/Users/moitree/Desktop/Test");
        ArrayList<Object> wavefiles = readWavFiles(wavFileList);

        System.out.println(wavefiles.get(2));
    }
}


