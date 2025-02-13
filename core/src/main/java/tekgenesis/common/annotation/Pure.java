
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation to indicate a method as pure.
 *
 * <p>A pure method is a method where the return value is only determined by its input values,
 * without observable side effects. This is how functions in math work: Math.cos(x) will, for the
 * same value of x, always return the same result. Computing it does not change x. It does not write
 * to log files, do network requests, ask for user input, or change program state. It’s a coffee
 * grinder: beans go in, powder comes out, end of story.</p>
 *
 * <p>When a method performs any other “action”, apart from calculating its return value, the method
 * is impure. It follows that a method which calls an impure method is impure as well. Impurity is
 * contagious. A given invocation of a pure method can always be replaced by its result. There’s no
 * difference between Math.cos(Math::PI) and -1; we can always replace the first with the second.
 * This property is called referential transparency.</p>
 *
 * <p>A pure method can only access what you pass it, so it’s easy to see its dependencies. When a
 * method accesses some other program state, such as an instance or global variable, it is no longer
 * pure.</p>
 *
 * <p>Pure methods go hand in hand with immutable values. Together they lead to declarative
 * programs, describing how inputs relate to outputs, without spelling out the steps to get from A
 * to B. This can simplify systems and, in the face of concurrency, referential transparency is a
 * godsend.</p>
 */
@Target(ElementType.METHOD)
public @interface Pure {}
