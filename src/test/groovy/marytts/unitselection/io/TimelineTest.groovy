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
        def testTimeLineFile = new File(testResourceDir, 'timeline_waveforms.mry')
        timeline = new Timeline()
        timeline.load(testTimeLineFile)
    }

    @Test(expectedExceptions = AssertionError)
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
    }
}
