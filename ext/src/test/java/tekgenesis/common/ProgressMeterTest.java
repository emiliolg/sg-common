
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import tekgenesis.common.core.Strings;
import tekgenesis.common.util.ProgressMeter;

import static java.lang.Long.parseLong;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class ProgressMeterTest {

    //~ Methods ......................................................................................................................................

    @Test public void noPhases() {
        final String[] beginMsg = { "" };
        final String[] endMsg   = { "" };

        final ProgressMeter pm = new ProgressMeter.Builder("Test Task").onBegin(p -> beginMsg[0] = p.getCurrentPhaseName()).onEnd(p ->
                    endMsg[0] = p.getCurrentPhaseName()).build();

        assertThat(beginMsg[0]).isEqualTo("Test Task");
        assertThat(endMsg[0]).isEqualTo("");
        assertThat(pm.getPhaseAdvance()).isZero();
        assertThat(pm.getTotalAdvance()).isZero();

        pm.setItemsToProcess(50);
        for (int i = 1; i <= 15; i++)
            pm.advance();
        assertThat(pm.getPhaseAdvance()).isEqualTo(30);
        assertThat(pm.getTotalAdvance()).isEqualTo(30);
        pm.advance();
        assertThat(pm.getPhaseAdvance()).isEqualTo(32);

        final long time = pm.getTime(0);
        assertThat(time).isGreaterThan(0L);

        for (int i = 16; i <= 50; i++)
            pm.advance();
        pm.endTask();
        assertThat(pm.getTotalTime()).isGreaterThan(time);
        assertThat(pm.getTotalAdvance()).isEqualTo(100);

        assertThat(beginMsg[0]).isEqualTo("Test Task");
        assertThat(endMsg[0]).isEqualTo("Test Task");
    }

    @SuppressWarnings("OverlyLongMethod")
    @Test public void phases() {
        final Map<Integer, String> begin = new HashMap<>();
        final Map<Integer, String> end   = new HashMap<>();

        final ProgressMeter.Builder builder = new ProgressMeter.Builder("Test Task").addPhase(1, "Prepare", 100)
                                              .addPhase(2, "Process", 800)
                                              .addPhase(3, "Cleanup", 100);

        final ProgressMeter pm = builder.onBegin(p -> begin.put(p.getCurrentPhase(), p.getCurrentPhaseName()))
                                 .onEnd(p ->
                    end.put(p.getCurrentPhase(), p.getCurrentPhaseName()))
                                 .build();

        assertThat(begin).containsEntry(0, "Test Task").doesNotContainKey(1);

        assertThat(pm.getPhaseAdvance()).isZero();
        assertThat(pm.getTotalAdvance()).isZero();

        // Phase 1;
        pm.beginPhase(1);
        assertThat(begin).containsEntry(1, "Prepare");
        assertThat(end).doesNotContainKey(1);

        pm.setItemsToProcess(50);
        for (int i = 1; i <= 15; i++)
            pm.advance();
        assertThat(pm.getPhaseAdvance()).isEqualTo(30);
        assertThat(pm.getTotalAdvance()).isEqualTo(3);

        pm.endPhase();
        assertThat(end).containsEntry(1, "Prepare");
        assertThat(pm.getTotalAdvance()).isEqualTo(10);

        // phase 2;

        pm.beginPhase(2);
        assertThat(begin).containsEntry(2, "Process");
        assertThat(end).doesNotContainKey(2);

        pm.setItemsToProcess(50);
        for (int i = 1; i <= 15; i++)
            pm.advance();
        assertThat(pm.getPhaseAdvance()).isEqualTo(30);
        assertThat(pm.getTotalAdvance()).isEqualTo(10 + 30 * 0.8);
        pm.endPhase();
        assertThat(end).containsEntry(2, "Process");
        assertThat(pm.getTotalAdvance()).isEqualTo(90);

        // phase 3;

        pm.beginPhase(3);
        assertThat(begin).containsEntry(3, "Cleanup");
        assertThat(end).doesNotContainKey(3);

        pm.setItemsToProcess(50);
        for (int i = 1; i <= 15; i++)
            pm.advance();
        assertThat(pm.getPhaseAdvance()).isEqualTo(30);
        assertThat(pm.getTotalAdvance()).isEqualTo(93);
        pm.endPhase();
        assertThat(end).containsEntry(3, "Cleanup");
        assertThat(pm.getTotalAdvance()).isEqualTo(100);

        final List<Long> ts = Strings.split(pm.getTimes(), ',').map(s -> parseLong(s.charAt(1) == ':' ? s.substring(2) : s)).toList();

        assertThat(ts.get(1) + ts.get(2) + ts.get(3)).isEqualTo(ts.get(0));

        // Create a new Meter with the previous one times;
        final ProgressMeter pm2 = builder.withTimes(pm.getTimes()).build();

        for (int i = 1; i <= 3; i++)
            assertThat(pm2.getEstimatedTime(i)).isEqualTo(ts.get(i));
        assertThat(ts.get(0)).isEqualTo(pm2.getTotalEstimatedTime());
    }  // end method phases
}  // end class ProgressMeterTest
