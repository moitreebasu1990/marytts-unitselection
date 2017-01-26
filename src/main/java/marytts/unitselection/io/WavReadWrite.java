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

import marytts.util.io.General;

import java.io.*;

/**
 * Created by Pradipta Deb on 14/12/16.
 */
public class WavReadWrite {

    private byte[] buf = null;
    private int nBytesPerSample = 0;
    private int numSamples;
    private int sampleRate;
    private short[] samples;
    private int headerSize;
    private static final short RIFF_FORMAT_PCM = 0x0001;

    /**
     * Byte swapping for int values.
     *
     * @param val
     *            val
     * @return (((val & 0xff000000) >>> 24) + ((val & 0x00ff0000) >>> 8) + ((val & 0x0000ff00) << 8) + ((val & 0x000000ff) << 24))
     */
    private static int byteswap(int val) {
        return (((val & 0xff000000) >>> 24) + ((val & 0x00ff0000) >>> 8) + ((val & 0x0000ff00) << 8) + ((val & 0x000000ff) << 24));
    }

    /**
     * Byte swapping for short values.
     *
     * @param val
     *            val
     * @return ((((int) (val) & 0xff00) >>> 8) + (((int) (val) & 0x00ff) << 8))
     */
    private static short byteswap(short val) {
        return ((short) ((((int) (val) & 0xff00) >>> 8) + (((int) (val) & 0x00ff) << 8)));
    }

    /**
     * Outputs the data in wav format.
     *
     * @param fileName
     *            file name
     * @param sampleRate
     *            sample rate
     * @throws IOException
     *             IOException
     */
    private void doWrite(String fileName, int sampleRate) throws IOException {

        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
        dos.writeBytes("RIFF"); // "RIFF" in ascii
        dos.writeInt(byteswap(36 + buf.length)); // Chunk size
        dos.writeBytes("WAVEfmt ");
        dos.writeInt(byteswap(16)); // chunk size, 16 for PCM
        dos.writeShort(byteswap((short) 1)); // PCM format
        dos.writeShort(byteswap((short) 1)); // Mono, one channel
        dos.writeInt(byteswap(sampleRate)); // Samplerate
        dos.writeInt(byteswap(sampleRate * nBytesPerSample)); // Byte-rate
        dos.writeShort(byteswap((short) (nBytesPerSample))); // Nbr of bytes per samples x nbr of channels
        dos.writeShort(byteswap((short) (nBytesPerSample * 8))); // nbr of bits per sample
        dos.writeBytes("data");
        dos.writeInt(byteswap(buf.length));
        dos.write(buf); // <= This buffer should already be byte-swapped at this stage
        dos.close();

    }

    /**
     * Export an array of shorts to a wav file.
     *
     * @param fileName
     *            The name of the wav file.
     * @param sampleRate
     *            The sample rate.
     * @param samples
     *            The array of short samples.
     * @throws IOException
     *             IOException
     */
    public void export(String fileName, int sampleRate, short[] samples) throws IOException {
        nBytesPerSample = 2;
        buf = new byte[samples.length * 2];
        // Cast the samples, and byte-swap them in the same loop
        for (int i = 0; i < samples.length; i++) {
            buf[2 * i] = (byte) ((samples[i] & 0xff00) >>> 8);
            buf[2 * i + 1] = (byte) ((samples[i] & 0x00ff));
        }
        // Do the write
        doWrite(fileName, sampleRate);
    }

    /**
     * Export an array of bytes to a wav file.
     *
     * @param fileName
     *            The name of the wav file.
     * @param sampleRate
     *            The sample rate.
     * @param samples
     *            The array of short samples, given as a byte array (with low and hi bytes separated).
     * @throws IOException
     *             IOException
     */
    public void export(String fileName, int sampleRate, byte[] samples) throws IOException {
        nBytesPerSample = 2;
        buf = new byte[samples.length];
        System.arraycopy(samples, 0, buf, 0, samples.length);

        // Byte-swap the samples
        byte b = 0;
        for (int j = 0; j < buf.length; j += 2) {
            b = buf[j];
            buf[j] = buf[j + 1];
            buf[j + 1] = b;
        }
        // Do the write
        doWrite(fileName, sampleRate);
    }

    public void read (String fileName) {
        try {
			/* Open the file */
            FileInputStream fis = new FileInputStream(fileName);
			/* Stick the file to a DataInputStream to allow easy reading of primitive classes (numbers) */
            DataInputStream dis = new DataInputStream(fis);
			/* Parse the header and load the data */
            loadHeaderAndData(dis);
			/* Close the file */
            fis.close();
        } catch (FileNotFoundException e) {
            throw new Error("WAV file [" + fileName + "] was not found.");
        } catch (SecurityException e) {
            throw new Error("You do not have createTimeline access to the file [" + fileName + "].");
        } catch (IOException e) {
            throw new Error("IO Exception caught when closing file [" + fileName + "]: " + e.getMessage());
        }
    }

    /**
     * Read in a wave from a riff format
     *
     * @param dis
     *            DataInputStream to createTimeline data from
     */
    private void loadHeaderAndData(DataInputStream dis) {
        int numChannels = 1; // Only support mono

        try {
            loadHeader(dis);
            if (dis.skipBytes(headerSize - 16) != (headerSize - 16)) {
                throw new Error("Unexpected error parsing wave file.");
            }

            // Bunch of potential random headers
            while (true) {
                String s = new String(General.readChars(dis, 4));

                if (s.equals("data")) {
                    numSamples = General.readInt(dis, false) / 2;
                    break;
                } else if (s.equals("fact")) {
                    int i = General.readInt(dis, false);
                    if (dis.skipBytes(i) != i) {
                        throw new Error("Unexpected error parsing wave file.");
                    }
                } else {
                    throw new Error("Unsupported wave header chunk type " + s);
                }
            }

            int dataLength = numSamples * numChannels;
            samples = new short[numSamples];

            for (int i = 0; i < dataLength; i++) {
                samples[i] = General.readShort(dis, false);
            }

        } catch (IOException ioe) {
            throw new Error("IO error while parsing wave" + ioe.getMessage());
        }

    }

    private void loadHeader(DataInputStream dis) throws IOException {

        if (!checkChars(dis, "RIFF")) {
            throw new Error("Invalid wave file format.");
        }
        int numBytes = General.readInt(dis, false);
        if (!checkChars(dis, "WAVEfmt ")) {
            throw new Error("Invalid wave file format.");
        }

        headerSize = General.readInt(dis, false);

        if (General.readShort(dis, false) != RIFF_FORMAT_PCM) {
            throw new Error("Invalid wave file format.");
        }

        if (General.readShort(dis, false) != 1) {
            throw new Error("Only mono wave files supported.");
        }

        sampleRate = General.readInt(dis, false);
        General.readInt(dis, false);
        General.readShort(dis, false);
        General.readShort(dis, false);

    }

    /**
     * Make sure that a string of characters appear next in the file
	 *
     * @param dis
	 *            DataInputStream to createTimeline in
	 * @param chars
	 *            a String containing the ascii characters you want the <code>dis</code> to contain.
     *
     * @throws IOException
	 *             ill-formatted input (end of file, for example)
	 */
    private boolean checkChars(DataInputStream dis, String chars) throws IOException {
        char[] carray = chars.toCharArray();
        for (int i = 0; i < carray.length; i++) {
            if ((char) dis.readByte() != carray[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the sample rate for this wave
     *
     * @return sample rate
     */
    public int getSampleRate() {
        return sampleRate;
    }

    /**
     * Get the number of samples for this wave
     *
     * @return number of samples
     */
    public int getNumSamples() {
        return numSamples;
    }

    /**
     * Get the sample data of this wave
     *
     * @return samples
     */
    public short[] getSamples() {
        return samples;
    }

}
