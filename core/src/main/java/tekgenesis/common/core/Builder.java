
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

/**
 * <p>The Builder interface is designed to designate a class as a <em>builder</em> object in the
 * Builder design pattern. Builders are capable of creating and configuring objects or results that
 * normally take multiple steps to construct or are very complex to derive.</p>
 *
 * <p>The builder interface defines a single method, {@link #build()}, that classes must implement.
 * The result of this method should be the final configured object or result after all building
 * operations are performed.</p>
 *
 * <p>It is a recommended practice that the methods supplied to configure the object or result being
 * built return a reference to {@code this} so that method calls can be chained together.</p>
 *
 * <p>Example Builder:</p>
 *
 * <pre><code>
   class FontBuilder implements Builder&lt;Font&gt; {
       private Font font;

       public FontBuilder(String fontName) {
           this.font = new Font(fontName, Font.PLAIN, 12);
       }

       public FontBuilder bold() {
           this.font = this.font.deriveFont(Font.BOLD);
           return this; // Reference returned so calls can be chained
       }

       public FontBuilder size(float pointSize) {
           this.font = this.font.deriveFont(pointSize);
           return this; // Reference returned so calls can be chained
       }

       // Other Font construction methods

       public Font build() {
           return this.font;
       }
   }
   </code></pre>
 *
 * <p>Example Builder Usage:</p>
 *
 * <pre><code>
   Font bold14ptSansSerifFont = new FontBuilder(Font.SANS_SERIF).bold()
                                                                .size(14.0f)
                                                                .build();
   </code></pre>
 *
 * @param  <T>  the type of object that the builder will construct or compute.
 */
public interface Builder<T> {

    //~ Methods ......................................................................................................................................

    /** Builds the target Object. */
    T build();
}
