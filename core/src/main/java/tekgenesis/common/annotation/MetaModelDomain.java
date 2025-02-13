
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.annotation;

/**
 * The annotation implemented by metamodel domains.
 */

public @interface MetaModelDomain {
    /**
     * Metamodel directory relative to sources directory.
     */
    String metaModelDir() default "../mm";
    /**
     * Generated sources directory relative to class output directory.
     */
    String generatedSourcesDir() default "../generated-sources/mm";
}
