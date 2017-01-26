package marytts.unitselection.io

import groovy.json.JsonSlurper
import org.testng.annotations.*

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
