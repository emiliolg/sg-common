
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.env.logging;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.LogManager;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import ch.qos.logback.classic.jmx.MBeanUtil;
import ch.qos.logback.classic.log4j.XMLLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;

import me.moocar.logbackgelf.GelfAppender;

import org.jetbrains.annotations.NotNull;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.bridge.SLF4JBridgeHandler;

import tekgenesis.common.core.Constants;
import tekgenesis.common.core.Option;
import tekgenesis.common.env.Environment;
import tekgenesis.common.env.properties.LoggingProps;

import static ch.qos.logback.classic.ClassicConstants.*;

import static tekgenesis.common.core.Constants.LIFECYCLE_KEY;
import static tekgenesis.common.core.Constants.REQUEST_METHOD;
import static tekgenesis.common.core.Constants.REQ_UUID;
import static tekgenesis.common.env.context.Context.getEnvironment;

/**
 * Initializes the logger configurations.
 */
@SuppressWarnings("WeakerAccess")
public class LogConfig {

    //~ Instance Fields ..............................................................................................................................

    private LoggerContext context;

    private String                                      fullLogFileNamePattern;
    private boolean                                     initialized;
    private final Map<String, LoggingProps.LoggerProps> loggerPropsMap;

    private LoggingProps props;

    private final Set<SizeAndTimeBasedFNATP<ILoggingEvent>> sizeAndTimeBasedFNATPs;

    private final Set<TimeBasedRollingPolicy<ILoggingEvent>> timeBasedRollingPolicies;

    //~ Constructors .................................................................................................................................

    private LogConfig() {
        context                = null;
        fullLogFileNamePattern = null;
        initialized            = false;
        loggerPropsMap         = new HashMap<>();

        sizeAndTimeBasedFNATPs = new HashSet<>();

        timeBasedRollingPolicies = new HashSet<>();
        props                    = getEnvironment().get(LoggingProps.class, new LoggingPropsListener());
    }

    //~ Methods ......................................................................................................................................

    /**
     * Returns the logging properties.
     *
     * @return  logging properties
     */
    public LoggingProps getProps() {
        return props;
    }

    private void addAppender() {
        timeBasedRollingPolicies.clear();
        sizeAndTimeBasedFNATPs.clear();

        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

        // CONSOLE APPENDER
        if (props.consoleOutput) root.addAppender(buildConsoleAppender());

        // FILE APPENDER
        if (props.fileOutput) root.addAppender(buildFileAppender());

        // XML APPENDER
        if (props.xmlOutput) root.addAppender(buildXmlAppender());

        // GELF APPENDER
        if (props.gelfOutput) root.addAppender(createGelfAppender());
    }

    private ConsoleAppender<ILoggingEvent> buildConsoleAppender() {
        final ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setName(CONSOLE);
        consoleAppender.setContext(context);
        consoleAppender.setWithJansi(true);
        consoleAppender.setEncoder(patternLayout());
        consoleAppender.start();
        return consoleAppender;
    }

    private RollingFileAppender<ILoggingEvent> buildFileAppender() {
        final RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setName("FILE");
        fileAppender.setContext(context);
        fileAppender.setFile(fullLogFileNamePattern + ".log");
        fileAppender.setEncoder(patternLayout());

        final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setFileNamePattern(fullLogFileNamePattern + ".%d.%i.log");
        rollingPolicy.setMaxHistory(props.maxDays);
        rollingPolicy.setParent(fileAppender);
        timeBasedRollingPolicies.add(rollingPolicy);
        fileAppender.setRollingPolicy(rollingPolicy);

        final SizeAndTimeBasedFNATP<ILoggingEvent> sizeAndTimeBasedFNATP = createSizeAndTimeBasedFNATP(rollingPolicy);

        rollingPolicy.start();
        fileAppender.start();
        sizeAndTimeBasedFNATP.start();

        return fileAppender;
    }

    private Map<String, String> buildGelfMDCMap() {
        final Map<String, String> result = new HashMap<>();
        for (final String key : MDC_KEYS)
            result.put(key, "_" + key);  // Name starting with _ to comply with gelf standard
        return result;
    }

    @NotNull
    @SuppressWarnings("OverlyLongMethod")
    private RollingFileAppender<ILoggingEvent> buildXmlAppender() {
        //
        final RollingFileAppender<ILoggingEvent> fileAppenderXml = new RollingFileAppender<>();
        fileAppenderXml.setName("XML");
        fileAppenderXml.setContext(context);
        fileAppenderXml.setFile(fullLogFileNamePattern + ".xml");

        //
        final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicyXml = new TimeBasedRollingPolicy<>();
        rollingPolicyXml.setContext(context);
        rollingPolicyXml.setFileNamePattern(fullLogFileNamePattern + "%i.%d.xml");
        rollingPolicyXml.setMaxHistory(props.maxDays);
        rollingPolicyXml.setParent(fileAppenderXml);
        timeBasedRollingPolicies.add(rollingPolicyXml);
        //
        final SizeAndTimeBasedFNATP<ILoggingEvent> sizeAndTimeBasedFNATPXml = createSizeAndTimeBasedFNATP(rollingPolicyXml);
        fileAppenderXml.setRollingPolicy(rollingPolicyXml);

        final LayoutWrappingEncoder<ILoggingEvent> layoutWrappingEncoder = new LayoutWrappingEncoder<>();
        layoutWrappingEncoder.setContext(context);
        final XMLLayout xmlLayout = new XMLLayout();
        xmlLayout.setContext(context);
        xmlLayout.setLocationInfo(true);
        xmlLayout.setProperties(true);
        layoutWrappingEncoder.setLayout(xmlLayout);
        fileAppenderXml.setEncoder(layoutWrappingEncoder);

        fileAppenderXml.start();
        rollingPolicyXml.start();
        sizeAndTimeBasedFNATPXml.start();
        layoutWrappingEncoder.start();
        xmlLayout.start();
        return fileAppenderXml;
    }

    private void configureJmx(@NotNull LoggerContext ctx) {
        if (props.jmxEnabled) {
            final String      contextName     = ctx.getName();
            final String      objectNameAsStr = MBeanUtil.getObjectNameFor(contextName, JMXConfigurator.class);
            final ObjectName  jmxBeanName     = MBeanUtil.string2ObjectName(ctx, this, objectNameAsStr);
            final MBeanServer mbs             = ManagementFactory.getPlatformMBeanServer();
            if (!MBeanUtil.isRegistered(mbs, jmxBeanName)) {
                final JMXConfigurator jmxConfigurator = new JMXConfigurator(ctx, mbs, jmxBeanName);
                try {
                    mbs.registerMBean(jmxConfigurator, jmxBeanName);
                }
                catch (final Exception e) {
                    e.printStackTrace(System.err);
                }
            }
        }
    }

    private void configureLoggers() {
        if (initialized) {
            // ROOT LOGGER
            ((Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)).setLevel(props.rootLoggerLevel);

            // CONFIGURE LOGGERS
            configurePredefinedLoggers();

            // CONFIGURE PLUGGABLE LOGGERS
            configurePluggableLoggers();
        }
    }

    private void configurePluggableLoggers() {
        final String[] loggerList = props.loggers.split(",");
        for (final String logger : loggerList) {
            final Level level = getLoggerProps(logger).level;
            if (level != null) ((Logger) LoggerFactory.getLogger(logger)).setLevel(level);
        }
    }

    private void configurePredefinedLoggers() {
        final boolean debugTekgenesis = props.debugAll || props.debugTekgenesis;

        // TEKGENESIS LOGGERS
        ((Logger) LoggerFactory.getLogger(Constants.TEKGENESIS)).setLevel(debugTekgenesis ? Level.DEBUG : props.rootLoggerLevel);
    }

    @NotNull private GelfAppender createGelfAppender() {
        final GelfAppender appender = new GelfAppender();
        appender.setContext(context);
        appender.setGraylog2ServerHost(props.gelfServer);
        if (props.gelfServerPort > 0) appender.setGraylog2ServerPort(props.gelfServerPort);
        appender.setUseLoggerName(true);
        appender.setUseThreadName(true);
        appender.setFacility(props.gelfFacility);
        appender.setAdditionalFields(buildGelfMDCMap());
        // noinspection MagicNumber
        appender.setChunkThreshold(2000);
        appender.setGraylog2ServerVersion("0.9.6");
        appender.start();
        return appender;
    }

    private SizeAndTimeBasedFNATP<ILoggingEvent> createSizeAndTimeBasedFNATP(TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy) {
        final SizeAndTimeBasedFNATP<ILoggingEvent> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<>();
        sizeAndTimeBasedFNATP.setContext(context);
        sizeAndTimeBasedFNATP.setMaxFileSize(props.maxFileSize);
        sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(rollingPolicy);
        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
        sizeAndTimeBasedFNATPs.add(sizeAndTimeBasedFNATP);
        return sizeAndTimeBasedFNATP;
    }

    private void init() {
        if (!initialized) {
            final int      MAX_RETRIES = 10;
            final int      WAIT_TIME   = 100;
            ILoggerFactory factory;
            int            i           = 0;
            // Delay & retry in case of reentrant behavior
            // See http://bugzilla.slf4j.org/show_bug.cgi?id=106
            do {
                factory = LoggerFactory.getILoggerFactory();
                i++;
                try {
                    Thread.sleep(WAIT_TIME);
                }
                catch (final InterruptedException e) {
                    // ignore
                }
            }
            while (!(factory instanceof LoggerContext) && i <= MAX_RETRIES);

            if (factory instanceof LoggerContext) {
                initialized = true;
                context     = (LoggerContext) factory;
                final String dir = props.logDir;
                assert dir != null;
                final String logDir = dir.endsWith(File.separator) ? dir : (dir + File.separator);

                fullLogFileNamePattern = logDir + props.logFileName;

                context.stop();
                context.reset();  // override default config

                context.setName(props.contextName);

                // noinspection DuplicateStringLiteralInspection
                MDC.put(PRODUCT_VERSION, System.getProperty("product.version", "DEVEL"));

                addAppender();

                // BRIDGE JUL to SLF4J
                bridgeJul();

                // CONFIGURE LOGGERS LEVELS
                configureLoggers();

                // CONTEXT LISTENER TO RECONFIGURE IN CASE OF RESET
                context.addListener(new LoggerContextListener() {
                        @Override public boolean isResetResistant() {
                            return true;
                        }

                        @Override public void onStart(LoggerContext ctx) {}

                        @Override public void onReset(LoggerContext ctx) {
                            reconfigureAll(true);
                        }

                        @Override public void onStop(LoggerContext ctx) {}

                        @Override public void onLevelChange(Logger logger, Level level) {}
                    });

                configureJmx(context);
            }
        }
    }  // end method init

    private PatternLayoutEncoder patternLayout() {
        final PatternLayoutEncoder patternLayoutEncoderFile = new PatternLayoutEncoder();
        patternLayoutEncoderFile.setPattern(PATTERN_BASE);
        patternLayoutEncoderFile.setOutputPatternAsHeader(false);
        patternLayoutEncoderFile.setContext(context);
        patternLayoutEncoderFile.start();
        return patternLayoutEncoderFile;
    }

    private void reconfigureAll(boolean inReset) {
        if (!inReset) {
            context.stop();
            context.reset();  // override default config
        }

        addAppender();

        // BRIDGE JUL to SLF4J
        bridgeJul();

        // CONFIGURE LOGGERS LEVELS
        configureLoggers();

        if (!inReset) context.start();
    }

    private LoggingProps.LoggerProps getLoggerProps(@NotNull String loggerName) {
        return loggerPropsMap.computeIfAbsent(loggerName,
            k -> getEnvironment().get(loggerName, LoggingProps.LoggerProps.class, new LoggerPropsListener()));
    }

    //~ Methods ......................................................................................................................................

    /** Removes all java util logging handlers and plus the slf4j bridge. */
    public static void bridgeJul() {
        final java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        final Handler[]                handlers   = rootLogger.getHandlers();
        for (final Handler handler : handlers)
            rootLogger.removeHandler(handler);
        SLF4JBridgeHandler.install();
    }

    /** Reset all the configs and re configures. */
    public static void reconfigure() {
        getInstance().reconfigureAll(false);
    }

    /** Configures or reconfigures the levels of the loggers. */
    public static void reconfigureLoggers() {
        getInstance().configureLoggers();
    }

    /** Starts the logging subsystem. */
    public static void start() {
        if (!getInstance().initialized) getInstance().init();

        final LoggerContext loggerContext = ourInstance.context;
        if (loggerContext != null && !loggerContext.isStarted()) loggerContext.start();
    }

    /** Stops the Logging subsystem. */
    public static void stop() {
        if (getInstance().initialized && getInstance().context.isStarted()) getInstance().context.stop();
    }

    /**
     * The log config instance.
     *
     * @return  the instance
     */
    public static LogConfig getInstance() {
        return ourInstance;
    }

    //~ Static Fields ................................................................................................................................

    @SuppressWarnings("DuplicateStringLiteralInspection")
    public static final String CONSOLE = "CONSOLE";

    @SuppressWarnings("DuplicateStringLiteralInspection")
    private static final String PRODUCT_VERSION = "productVersion";

    private static final String[] MDC_KEYS = {
        PRODUCT_VERSION,
        USER_MDC_KEY,

        REQUEST_REMOTE_HOST_MDC_KEY,
        REQUEST_METHOD,
        REQUEST_REQUEST_URL,
        REQUEST_REQUEST_URI,
        REQUEST_X_FORWARDED_FOR,
        REQUEST_QUERY_STRING,
        REQUEST_USER_AGENT_MDC_KEY,

        LIFECYCLE_KEY,
        REQ_UUID
    };

    private static final LogConfig ourInstance = new LogConfig();

    private static final String PATTERN_BASE = "%date{ISO8601} %-5level [%thread][%mdc][%class{16}] - %msg%n";

    //~ Inner Classes ................................................................................................................................

    private class LoggerPropsListener implements Environment.Listener<LoggingProps.LoggerProps> {
        @Override public void onChange(@NotNull Option<LoggingProps.LoggerProps> value) {
            if (value.isPresent()) configurePluggableLoggers();
        }
    }

    private class LoggingPropsListener implements Environment.Listener<LoggingProps> {
        @Override public void onChange(@NotNull Option<LoggingProps> value) {
            if (value.isPresent()) {
                props = value.get();

                for (final TimeBasedRollingPolicy<ILoggingEvent> policy : timeBasedRollingPolicies) {
                    policy.stop();
                    policy.setMaxHistory(value.get().maxDays);
                    policy.start();
                }

                for (final SizeAndTimeBasedFNATP<ILoggingEvent> f : sizeAndTimeBasedFNATPs) {
                    f.stop();
                    f.setMaxFileSize(value.get().maxFileSize);
                    f.start();
                }

                configurePredefinedLoggers();
            }
        }
    }
}  // end class LogConfig
