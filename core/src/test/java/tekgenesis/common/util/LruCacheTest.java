
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.util.function.Function;

import org.junit.Test;

import tekgenesis.common.core.Strings;
import tekgenesis.common.util.LruCache;

import static org.assertj.core.api.Assertions.*;

/**
 * User: emilio; Date: 12/16/11; Time: 12:47 PM;
 */

@SuppressWarnings({ "JavaDoc", "DuplicateStringLiteralInspection", "MagicNumber" })
public class LruCacheTest {

    //~ Instance Fields ..............................................................................................................................

    long tick;

    //~ Methods ......................................................................................................................................

    @Test public void cache() {
        final LruCache<Integer, Integer> cache = LruCache.createLruCache(5);
        final Function<Integer, Integer> load  = key -> key * 2;

        for (int i = 1; i <= 10; i++)
            assertThat(cache.find(i, load)).isEqualTo(i * 2);
        assertThat(cache.hitCount()).isZero();

        for (int i = 10; i >= 5; i--)
            assertThat(cache.find(i, load)).isEqualTo(i * 2);

        assertThat(cache.hitCount()).isEqualTo(5);
        assertThat(cache.loadedCount()).isEqualTo(11);
        assertThat(cache.missCount()).isEqualTo(11);
        assertThat(cache.evictionCount()).isEqualTo(6);
        assertThat(cache.putCount()).isEqualTo(0);
        assertThat(cache.getMaxWeight()).isEqualTo(5);
    }

    @Test public void cacheExpiration() {
        final LruCache<Integer, String> cache = new LruCache.Builder<Integer, String>().withExpiration(String::length)
                                                .ticker(this::getTick)
                                                .maxWeight(100)
                                                .build();

        for (int i = 1; i <= 10; i++)
            findAndTest(cache, i);
        assertThat(cache.hitCount()).isZero();

        for (int i = 1; i <= 10; i++)
            findAndTest(cache, i);

        assertThat(cache.hitCount()).isEqualTo(10);
        cache.trimToWeight(9);
        assertThat(cache.size()).isEqualTo(9);
        tick = 5;

        for (int i = 1; i <= 10; i++)
            findAndTest(cache, i);

        assertThat(cache.hitCount()).isEqualTo(15);
        assertThat(cache.size()).isEqualTo(10);
        assertThat(cache.getWeight()).isEqualTo(10);
        tick = 10;
        cache.trimToWeight(8);
        assertThat(cache.size()).isEqualTo(0);
    }

    @Test public void cacheWeighted() {
        final LruCache<Integer, String> cache = new LruCache.Builder<Integer, String>().weigher(String::length).maxWeight(50).build();

        for (int i = 1; i <= 10; i++)
            findAndTest(cache, i);
        assertThat(cache.hitCount()).isZero();

        for (int i = 10; i >= 1; i--)
            findAndTest(cache, i);

        assertThat(cache.hitCount()).isEqualTo(7);
        assertThat(cache.loadedCount()).isEqualTo(13);
        assertThat(cache.missCount()).isEqualTo(13);
        assertThat(cache.evictionCount()).isEqualTo(4);
        assertThat(cache.putCount()).isEqualTo(0);
        assertThat(cache.getMaxWeight()).isEqualTo(50);
    }

    public void findAndTest(final LruCache<Integer, String> cache, final int i) {
        assertThat(cache.find(i, Strings::spaces)).isEqualTo(Strings.spaces(i));
    }

    public long getTick() {
        return tick;
    }
}  // end class LruCacheTest
