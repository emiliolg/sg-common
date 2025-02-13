
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.invoker;

import java.util.List;

/**
 * Enum for MultiHostInvoker strategy.
 */
public enum Strategy {

    //~ Enum constants ...............................................................................................................................

    MASTER_SLAVE { @Override public MultiHostStrategy create(List<HttpInvoker> invokerList) { return new MasterSlaveStrategy(invokerList); } },
    ROUND_ROBIN { @Override MultiHostStrategy create(List<HttpInvoker> invokerList) { return new RoundRobinStrategy(invokerList); } },
    RANDOM { @Override MultiHostStrategy create(List<HttpInvoker> invokerList) { return new RandomStrategy(invokerList); } };

    //~ Methods ......................................................................................................................................

    abstract MultiHostStrategy create(List<HttpInvoker> invokers);
}
