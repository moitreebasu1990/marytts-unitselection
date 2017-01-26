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

import java.io.File;

public class Timeline {

    public static Timeline createFrom(File wavDir, File pmDir) {
        Timeline timeline = new Timeline();
        // createTimeline and store data, not yet implemented
        return timeline;
    }

    public void load(File timelineFile) {
        // not yet implemented
    }

    public void saveTo(File timelineFile) {
        // not yet implemented
    }

    public static void main(String[] args) {
        File wavDir;
        File pmDir;
        File timelineFile;
        try {
            wavDir = new File(args[0]);
            pmDir = new File(args[1]);
            timelineFile = new File(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw e;
        }
        Timeline.createFrom(wavDir, pmDir).saveTo(timelineFile);
    }
}
