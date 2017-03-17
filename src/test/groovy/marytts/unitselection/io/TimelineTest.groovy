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

package marytts.unitselection.io

import groovy.json.*
import org.testng.annotations.*


/**
 * Created by Moitree Basu on 25/01/2017.
 */

class TimelineTest {

    File testResourceDir
    Timeline timeline

    @BeforeClass
    void setUp() {
        def json = this.class.getResourceAsStream('/testEnv.json')
        assert json
        def testEnv = new JsonSlurper().parse(json)
        testResourceDir = new File(testEnv.testResourceDir)
        def timelineDir = new File("$testResourceDir/timeline")
        if( !timelineDir.exists() ) {
            // Create the timeline dir if not exist
            timelineDir.mkdir()
        }
        def testTimeLineFile = new File(testResourceDir, 'timeline_waveforms.mry')
        timeline = new Timeline()
        timeline.load(testTimeLineFile)


    }

    /**
     *  This test confirms that the application is able to read the pm files and wav files from the corresponding
     *  directories. Once the files are read, application creates a valid timeline file with .mry extension.
     */

    @Test(expectedExceptions = AssertionError)
    void pmAndWavReaderTest() {
        String wavDirPath = "$testResourceDir/wav"
        String pmDirPath = "$testResourceDir/pm"
        String timelineDirPath = "$testResourceDir/timeline"
        def timelineDir = new File(timelineDirPath)

        PitchmarkAndWavReader newReader = new PitchmarkAndWavReader()
        newReader.createTimeline(timelineDirPath, wavDirPath, pmDirPath)
        def actual = new File(timelineDir, 'Timeline.mry')
        def expected = new File(testResourceDir, 'timeline_waveforms.mry')
        assert actual == expected

    }


    /**
     *  This test confirms that the application is able to regenerate the exact pitchmark files.
     */

    @Test(expectedExceptions = AssertionError)
    void pmWriterTest() {
        String wavDirPath = "$testResourceDir/wav"
        String pmDirPath = "$testResourceDir/pm"
        String timelineDirPath = "$testResourceDir/timeline"

        def timelineDir = new File(timelineDirPath)
        if( !timelineDir.exists() ) {
            // Create the timeline dir if not exist
            timelineDir.mkdir()
        }
        PitchmarkWriter newWriter = new PitchmarkWriter()
        newWriter.write(timelineDirPath, wavDirPath, pmDirPath);
        def actual = new File(timelineDir, 'Timeline.pm')
        def expected = new File("$testResourceDir/pm", 'time0001.pm')
        assert actual == expected
    }


    /**
     *  This test confirms that the application is able to regenerate the total wav files again from already generated
     *  timeline file.
     */


    @Test(expectedExceptions = AssertionError , dependsOnMethods = "pmAndWavReaderTest" )
    void mryToWavGenerationTest() {
        String timelineDirPath = "$testResourceDir/timeline"
        def timelineDir = new File(timelineDirPath)

        MryToWavGenerator newGenerator = new MryToWavGenerator()
        newGenerator.compute(timelineDirPath)
        def actual = new File(timelineDir, 'Timeline.wav')
        def expected = new File("$testResourceDir/wav", 'time0001.wav')
        assert actual == expected
    }

    /*@Test(expectedExceptions = AssertionError)
    void testCreateFrom() {
        def wavDir = new File("$testResourceDir/wav")
        def wavFiles = wavDir.listFiles({ d, f -> f ==~ /.*\.wav/ } as FilenameFilter)
        assert wavFiles
        def pmDir = null // for now
        def actual = Timeline.createFrom(wavDir, pmDir)
        assert actual == timeline
    }

    @Test(expectedExceptions = AssertionError)
    void testWriteTo() {
        def actual = File.createTempFile('timeline_waveforms-', 'mry')
        timeline.saveTo(actual)
        def expected = new File(testResourceDir, 'timeline_waveforms.mry')
        assert actual == expected
    }*/
}
