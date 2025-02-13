
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import static tekgenesis.common.Predefined.cast;

/**
 * ObservableObject class.
 */
public abstract class ObservableObject<OO extends ObservableObject<OO, OS>, OS extends ObserverService<OS, OO>> implements Closeable {

    //~ Instance Fields ..............................................................................................................................

    private final ServiceLoader<OS> serviceLoader;

    //~ Constructors .................................................................................................................................

    protected ObservableObject(Class<OS> clazz) {
        serviceLoader = cast(serviceLoaderMap.computeIfAbsent(clazz, ServiceLoader::load));
    }

    //~ Methods ......................................................................................................................................

    @Override public void close() {
        for (final OS service : observers())
            service.onClose(cast(this));
    }
    protected void init() {
        for (final OS service : observers())
            service.onInit(cast(this));
    }

    protected Iterable<OS> observers() {
        return serviceLoader;
    }

    //~ Static Fields ................................................................................................................................

    private static final Map<Class<?>, ServiceLoader<?>> serviceLoaderMap = new HashMap<>();
}
