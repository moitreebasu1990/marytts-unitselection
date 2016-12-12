package marytts.unitselection.io;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by pradipta on 08/11/16.
 */
public class WavWriter {


    public static void main(String[] args) throws IOException {

        double sampleRate = 16000.0;
        double frequency = 440;
        double frequency2 = 90;
        double amplitude = 1.0;
        double seconds = 2.0;
        double twoPiF = 2 * Math.PI * frequency;
        double piF = Math.PI * frequency2;

        float[] buffer = new float[(int) (seconds * sampleRate)];

        for (int sample = 0; sample < buffer.length; sample++) {
            double time = sample / sampleRate;
            buffer[sample] = (float) (amplitude * Math.cos((double) piF * time) * Math.sin(twoPiF * time));
        }


        final byte[] byteBuffer = new byte[buffer.length * 2];

        int bufferIndex = 0;

        for (int i = 0; i < byteBuffer.length; i++) {
            final int x = (int) (buffer[bufferIndex++] * 32767.0);
            byteBuffer[i] = (byte) x;
            i++;
            byteBuffer[i] = (byte) (x >>> 8);
        }

       WavWriter newWavWriter = new WavWriter();

       newWavWriter.writeWavFile("./out.wav",byteBuffer);

    }

    public void writeWavFile(String fileName, byte [] wavContent) throws IOException{

        double sampleRate = 16000.0;
        boolean bigEndian = false;
        boolean signed = true;
        int bits = 16;
        int channels = 1;

        File out = new File(fileName);

        AudioFormat format =  new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                (float)sampleRate,
                bits,
                channels,
                2, // frameSize
                8000f,// frameRate
                bigEndian);
        ByteArrayInputStream bais = new ByteArrayInputStream(wavContent);
        AudioInputStream audioInputStream = new AudioInputStream(bais, format, wavContent.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, out);
        audioInputStream.close();

    }

}
