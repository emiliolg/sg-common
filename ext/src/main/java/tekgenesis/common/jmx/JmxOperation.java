
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.jmx;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;

import tekgenesis.common.logging.Logger;

import static tekgenesis.common.logging.Logger.getLogger;

/**
 * A JmxOperation can be an update over an attribute or a Method invocation.
 */
public abstract class JmxOperation implements Serializable {

    //~ Instance Fields ..............................................................................................................................

    private JmxEndpoint connection = null;

    private final String objectName;

    //~ Constructors .................................................................................................................................

    protected JmxOperation(@NotNull String objectName) {
        this.objectName = objectName;
    }

    //~ Methods ......................................................................................................................................

    /** Execute a JMX Operation. */
    public void execute() {
        try {
            doExecute();
        }
        catch (final RuntimeException e) {
            logger.error(e);
            throw e;
        }
    }

    /**
     * Set the jmxconnection to use.
     *
     * @param  connection  JmxConnection
     */
    public void setEndpoint(@NotNull JmxEndpoint connection) {
        this.connection = connection;
    }

    protected abstract void doExecute();

    protected JmxEndpoint getEndpoint() {
        if (connection == null) throw new IllegalStateException("JMX connection not set");
        return connection;
    }

    protected String getObjectName() {
        return objectName;
    }

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = -7264023419951042739L;

    private static final Logger logger = getLogger(JmxOperation.class);
}  // end class JmxOperation
