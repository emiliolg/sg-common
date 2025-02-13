
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.properties;

import javax.inject.Named;

import ch.qos.logback.classic.Level;
import ch.qos.logback.core.CoreConstants;

import tekgenesis.common.core.Constants;
import tekgenesis.common.env.Mutable;
import tekgenesis.common.env.Properties;

import static tekgenesis.common.core.Constants.LOCALHOST;

/**
 * Logging properties.
 */
@Mutable
@Named("logging")
@SuppressWarnings("MagicNumber")
public class LoggingProps implements Properties {

    //~ Static Fields ................................................................................................................................

    public static final String LOG_FILE_NAME = "sui-generis";
    public static final String GELF_FACILITY = "SUIGENERIS";

    //~ Inner Classes ................................................................................................................................

    //J-

    // Output types enabled

    public boolean consoleOutput  = true;
    public boolean fileOutput     = true;
    public boolean xmlOutput      = false;
    public boolean gelfOutput     = false;

    // File based output
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public String logDir = System.getProperty(Constants.JAVA_IO_TMPDIR) + "/log";
    public String logFileName = LOG_FILE_NAME;

    // Rolling policy

    public int     maxDays        = 7;
    public String  maxFileSize    = "20MB";

    // Gray Log Stuff

    /** The GrayLog  server host */
    public String gelfServer     = LOCALHOST;

    /** The GrayLog  server port */
    public int    gelfServerPort = 12201;

    /** Name of the GrayLog facility */
    public String gelfFacility = GELF_FACILITY;

    /** Pluggable loggers list (, separated) */
    public String  loggers       = "";

    // Debugging Stuff

    /** enable all internal debugging */
    public boolean debugAll = false;

    /** enable debugging on tekgenesis loggers */
    public boolean debugTekgenesis= false;

    /** Sets the level of the root logger */
    public Level rootLoggerLevel = Level.WARN;

    /** Logconfig management through JMX */
    public boolean jmxEnabled = true;

    /** Add X-Node-Ref header in http responses **/
    public boolean addIdRefResponseHeader = false;

    /** Logger Context name */
    public String contextName = CoreConstants.DEFAULT_CONTEXT_NAME;

    //J+

    /**
     * Pluggable logger properties.
     */
    @Mutable
    @Named("logger")
    @SuppressWarnings("DuplicateStringLiteralInspection")
    public static class LoggerProps implements Properties {
        public Level level = Level.WARN;
    }
}  // end class LoggingProps
