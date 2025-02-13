
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.core.StrBuilder;
import tekgenesis.common.logging.Logger;

import static java.lang.String.format;

import static javax.xml.bind.DatatypeConverter.parseInt;
import static javax.xml.bind.DatatypeConverter.parseLong;

import static tekgenesis.common.core.Times.SECONDS_MINUTE;

/**
 * This class implements a Progress Meter with phases It's useful to show progress in batch tasks.
 * You can define several phases and assign them an estimated number of time-units.
 */
@SuppressWarnings("WeakerAccess")
public class ProgressMeter {

    //~ Instance Fields ..............................................................................................................................

    private int                           currentPhase;
    private int                           currentPhaseItems;
    private long                          currentPhaseStartTime;
    private Long                          currentPhaseUnits;
    private int                           itemsProcessed;
    private final Logger                  logger;
    private final Consumer<ProgressMeter> onBegin;
    private final Consumer<ProgressMeter> onEnd;

    private final SortedMap<Integer, Phase> phases;

    private final String taskName;
    private long         totalTime;
    private final long   totalUnits;
    private int          unitsProcessed;

    //~ Constructors .................................................................................................................................

    private ProgressMeter(String taskName, SortedMap<Integer, Phase> phases, long totalUnits, Logger logger, Consumer<ProgressMeter> onBegin,
                          Consumer<ProgressMeter> onEnd) {
        this.taskName     = taskName;
        this.phases       = phases;
        this.totalUnits   = totalUnits;
        this.logger       = logger;
        this.onBegin      = onBegin;
        this.onEnd        = onEnd;
        unitsProcessed    = 0;
        currentPhaseUnits = 0L;
        totalTime         = 0;
        beginPhase(0);
    }

    //~ Methods ......................................................................................................................................

    /** Mark an item as processed. */
    public void advance() {
        itemsProcessed++;
    }

    /** Start the specified phase. */
    public void beginPhase(int phaseId) {
        currentPhaseStartTime = System.nanoTime();
        currentPhase          = phaseId;
        if (phaseId != 0) {
            final Phase phase = retrievePhase(phaseId);
            phase.executionTime = 0L;
            currentPhaseUnits   = phase.estimatedTime;
        }
        fireBegin(phaseId);
        itemsProcessed = 0;
    }
    /** Start the specified phase. Mar items to be processed */
    public void beginPhase(int phaseId, int n) {
        beginPhase(phaseId);
        setItemsToProcess(n);
    }

    /** End the current phase. */
    public void endPhase() {
        if (currentPhase == 0) endTask();
        final long elapsed = System.nanoTime() - currentPhaseStartTime;
        retrievePhase(currentPhase).executionTime =  elapsed;
        unitsProcessed                            += currentPhaseUnits;
        fireEnd(currentPhase, elapsed);
        if (allexecuted()) endTask();
        currentPhase = 0;
    }

    /**
     * End the task, it will be invoked automatically from endPhase once the last phase is
     * completed.
     */
    public void endTask() {
        totalTime = phases.isEmpty() ? System.nanoTime() - currentPhaseStartTime : sumExecuted();
        fireEnd(0, totalTime);
    }

    /** Get current phase id. */
    public int getCurrentPhase() {
        return currentPhase;
    }

    /** Get current phase name. */
    public String getCurrentPhaseName() {
        return getName(currentPhase);
    }

    /** return the estimated execution time for a given phase in nanoseconds. */
    public long getEstimatedTime(int phaseId) {
        return phaseId == 0 ? getTotalEstimatedTime() : retrievePhase(phaseId).estimatedTime;
    }

    /** Set the number of items to process. */
    public void setItemsToProcess(int n) {
        currentPhaseItems = n;
    }

    /** return the name fo a given phase. */
    public String getName(int phaseId) {
        return phaseId == 0 ? taskName : retrievePhase(phaseId).name;
    }

    /** Get current phase advance in %. */
    public double getPhaseAdvance() {
        return currentPhaseItems == 0 || itemsProcessed == 0 ? 0.0 : (ONE_HUNDRED * itemsProcessed) / currentPhaseItems;
    }

    /** return the execution time for a given phase in nanoseconds. */
    public long getTime(int phaseId) {
        return phaseId == currentPhase ? System.nanoTime() - currentPhaseStartTime : retrievePhase(phaseId).executionTime;
    }

    /** Return a String representation of the times by phase. */
    public String getTimes() {
        final StrBuilder builder = new StrBuilder();
        builder.appendElement(totalTime);
        for (final Map.Entry<Integer, Phase> t : phases.entrySet())
            builder.appendElement(t.getKey() + ":" + t.getValue().executionTime);
        return builder.toString();
    }

    /** Get Total advance in %. */
    public double getTotalAdvance() {
        if (totalTime > 0) return ONE_HUNDRED;
        if (phases.isEmpty()) return getPhaseAdvance();
        if (totalUnits == 0) return 0.0;

        final double base = unitsProcessed == 0 ? 0.0 : (ONE_HUNDRED * unitsProcessed) / totalUnits;
        if (currentPhase == 0) return base;
        return base + getPhaseAdvance() * currentPhaseUnits / totalUnits;
    }

    /** Get total estimation time. */
    public long getTotalEstimatedTime() {
        return totalUnits;
    }

    /** return the total execution time in nanoseconds. */
    public long getTotalTime() {
        return totalTime;
    }

    private boolean allexecuted() {
        for (final Phase phase : phases.values()) {
            if (phase.executionTime == -1) return false;
        }
        return true;
    }

    private void fireBegin(int phaseId) {
        if (logger != null) logger.info(msgPrefix(phaseId) + " started.");
        if (onBegin != null) onBegin.accept(this);
    }

    private void fireEnd(int phaseId, long elapsed) {
        if (logger != null) {
            double    secs    = elapsed / (1000 * MILLION);
            final int minutes = (int) (secs / SECONDS_MINUTE);
            secs -= minutes * SECONDS_MINUTE;
            logger.info(
                format("%s completed in %s%2.3f secs.", msgPrefix(phaseId), minutes == 0 ? "" : minutes == 1 ? "1 min " : minutes + " mins ", secs));
        }
        if (onEnd != null) onEnd.accept(this);
    }

    private IllegalArgumentException invalidPhaseId(int phaseId) {
        return new IllegalArgumentException("Invalid phase id: " + phaseId);
    }

    private String msgPrefix(int phaseId) {
        return phaseId == 0 ? "Task " + taskName : "    Phase " + phases.get(phaseId).name;
    }

    @NotNull private Phase retrievePhase(int phaseId) {
        final Phase phase = phases.get(phaseId);
        if (phase == null) throw invalidPhaseId(phaseId);
        return phase;
    }

    private long sumExecuted() {
        long tot = 0;
        for (final Phase t : phases.values())
            tot += t.executionTime;
        return tot;
    }

    //~ Static Fields ................................................................................................................................

    private static final double MILLION     = 1000000.0;
    private static final double ONE_HUNDRED = 100.0;

    //~ Inner Classes ................................................................................................................................

    /**
     * Progress Meter builder class.
     */
    public static class Builder {
        final SortedMap<Integer, Phase> phases;
        private Logger                  logger;
        private Consumer<ProgressMeter> onBegin;
        private Consumer<ProgressMeter> onEnd;

        private final String taskName;
        private long         totalUnits;

        /** Create the builder and specify a task name. */
        public Builder(String taskName) {
            this.taskName = taskName;
            phases        = new TreeMap<>();
            logger        = null;
            onBegin       = null;
            onEnd         = null;
            totalUnits    = 0L;
        }

        /** Add a Phase to the builder. Id 0 is reserved for the whole execution */
        public Builder addPhase(int id, String name, long timeUnits) {
            if (phases.containsKey(id)) throw new IllegalArgumentException("Id " + id + " already added.");
            phases.put(id, new Phase(name, timeUnits));
            totalUnits += timeUnits;
            return this;
        }

        /** Build the meter. */
        public ProgressMeter build() {
            return new ProgressMeter(taskName, phases, totalUnits, logger, onBegin, onEnd);
        }

        /** Register an onStart callback. */
        public Builder onBegin(Consumer<ProgressMeter> f) {
            onBegin = f;
            return this;
        }
        /**
         * Register an onEnd callback, phase id, phase name and nanoseconds will be passed to the
         * callback function.
         */
        public Builder onEnd(Consumer<ProgressMeter> f) {
            onEnd = f;
            return this;
        }

        /** Use the specified logger to log begin-end messages. */
        public Builder withLogger(Logger l) {
            logger = l;
            return this;
        }

        /** Use previous times to set Estimations. */
        public Builder withTimes(String times) {
            final String[] ts = times.split(",");
            totalUnits = ts.length == 1 ? parseLong(ts[0]) : 0;
            for (int i = 1; i < ts.length; i++) {
                final String[] t       = ts[i].split(":");
                final int      phaseId = parseInt(t[0]);
                final long     units   = parseLong(t[1]);
                final Phase    phase   = phases.get(phaseId);
                if (phase != null) {
                    phase.estimatedTime =  units;
                    totalUnits          += units;
                }
            }
            return this;
        }
    }  // end class Builder

    static class Phase {
        long   estimatedTime;
        long   executionTime;
        String name;

        public Phase(String name, long estimatedTime) {
            this.name          = name;
            this.estimatedTime = estimatedTime;
            executionTime      = -1;
        }
    }
}  // end class ProgressMeter
