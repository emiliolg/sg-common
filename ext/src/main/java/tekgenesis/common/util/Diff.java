
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.core.Strings;

import static tekgenesis.common.Predefined.createToStringBuilder;
import static tekgenesis.common.collections.Colls.emptyList;
import static tekgenesis.common.collections.Colls.immutable;
import static tekgenesis.common.util.Diff.Type.*;

/**
 * A Difference generator (Like the Shell diff command).
 *
 * @param  <T>  The type of objects to compare
 */
public class Diff<T> {

    //~ Instance Fields ..............................................................................................................................

    @NotNull protected final Equals<T> equalsComparator;

    //~ Constructors .................................................................................................................................

    /**
     * Creates a instance of the Diff class.
     *
     * @param  equalsComparator  The Strategy used for considering the lines equals
     */
    private Diff(@NotNull final Equals<T> equalsComparator) {
        this.equalsComparator = equalsComparator;
    }

    //~ Methods ......................................................................................................................................

    /**
     * Computes the difference between the 2 sequences and returns it as a List of Delta objects.
     */
    public final ImmutableList<Delta<T>> diff(List<T> a, List<T> b) {
        final PathNode path = buildPath(a, b);
        return buildRevision(path, a, b);
    }

    protected List<T> extractSubList(final List<T> a, final int from, final int to) {
        return a.subList(from, to);
    }

    private PathNode buildPath(@NotNull List<T> a, @NotNull List<T> b) {
        // these are local constants
        final int aSize = a.size();
        final int bSize = b.size();

        final int        max      = aSize + bSize + 1;
        final int        size     = 1 + 2 * max;
        final int        middle   = size / 2;
        final PathNode[] diagonal = new PathNode[size];

        diagonal[middle + 1] = PathNode.snake(0, -1, null);
        for (int d = 0; d < max; d++) {
            for (int k = -d; k <= d; k += 2) {
                final int      kMiddle = middle + k;
                final int      kPlus   = kMiddle + 1;
                final int      kMinus  = kMiddle - 1;
                final PathNode prev;

                int i;
                if ((k == -d) || (k != d && diagonal[kMinus].i < diagonal[kPlus].i)) {
                    i    = diagonal[kPlus].i;
                    prev = diagonal[kPlus];
                }
                else {
                    i    = diagonal[kMinus].i + 1;
                    prev = diagonal[kMinus];
                }

                int j = i - k;

                PathNode node = PathNode.diffNode(i, j, prev);

                // orig and rev are zero-based
                // but the algorithm is one-based
                // that's why there's no +1 when indexing the sequences
                while (i < aSize && j < bSize && equalsComparator.equal(a.get(i), b.get(j))) {
                    i++;
                    j++;
                }
                if (i > node.i) node = PathNode.snake(i, j, node);

                diagonal[kMiddle] = node;

                if (i >= aSize && j >= bSize) return diagonal[kMiddle];
            }
        }
        return null;
    }  // end method buildPath

    /** Constructs a List of Deltas from a difference path. */
    @NotNull
    @SuppressWarnings("TooBroadScope")
    private ImmutableList<Delta<T>> buildRevision(final PathNode originalPath, @NotNull List<T> a, @NotNull List<T> b) {
        final List<Delta<T>> patch = new ArrayList<>();

        PathNode path = originalPath == null ? null : originalPath.isSnake() ? originalPath.prev : originalPath;

        while (path != null && path.prev != null && path.prev.j >= 0) {
            if (path.isSnake()) throw new IllegalStateException();

            final int i = path.i;
            final int j = path.j;

            path = path.prev;

            final List<T> aSubList = extractSubList(a, path.i, i);
            final List<T> bSubList = extractSubList(b, path.j, j);
            if (!aSubList.isEmpty() || !bSubList.isEmpty()) patch.add(0, new Delta<>(path.i, aSubList, path.j, bSubList));

            if (path.isSnake()) path = path.prev;
        }
        return immutable(patch);
    }

    //~ Methods ......................................................................................................................................

    /** Creates a case insensitive diff for Strings. */
    public static Diff.Str caseInsensitive() {
        return new Str(STRING_CASE_INSENSITIVE, false);
    }

    /** Creates a case sensitive diff for Strings. */
    public static Diff.Str caseSensitive() {
        return new Str(STRING_EQUALS, false);
    }

    /** Create a differ with the specified comparator. */
    public static <T> Diff<T> differ(final Equals<T> comparator) {
        return new Diff<>(comparator);
    }

    /** Creates a diff Strings that ignore all space. */
    public static Diff.Str ignoreAllSpace() {
        return new Str(STRING_EQUALS, true).ignoreSpaces();
    }

    /** Convert all the list of deltas to a single string. */
    public static <T> String makeString(Iterable<Delta<T>> deltas) {
        final StringWriter writer = new StringWriter();
        final PrintWriter  out    = new PrintWriter(writer);
        for (final Delta<T> delta : deltas)
            delta.print(out);
        return writer.toString();
    }

    /** Create a differ with the specified String comparator. */
    public static Str stringDiffer(final Equals<String> comparator) {
        return new Str(comparator, false);
    }

    /** Creates a case sensitive diff that trim Strings, before comparing them. */
    public static Str trimming() {
        return new Str(TRIM_COMPARATOR, false);
    }

    //~ Static Fields ................................................................................................................................

    private static final Equals<String> STRING_CASE_INSENSITIVE = new Equals<String>() {
            @Override public boolean doEqualComparison(@NotNull final String a, @NotNull final String b) {
                return a.equalsIgnoreCase(b);
            }
        };

    private static final Equals<String> STRING_EQUALS = new Equals<>();

    private static final Diff.Equals<String> TRIM_COMPARATOR = new Diff.Equals<String>() {
            @Override public boolean doEqualComparison(String a, String b) {
                return a.trim().equals(b.trim());
            }
        };

    //~ Enums ........................................................................................................................................

    enum Type {
        ADD("a"), DELETE("d"), CHANGE("c");

        private final String str;

        Type(final String str) {
            this.str = str;
        }

        public String toString() {
            return str;
        }
    }

    //~ Inner Classes ................................................................................................................................

    public static class Delta<T> {
        private final List<T> aLines;
        private final int     aPosition;
        private final List<T> bLines;
        private final int     bPosition;
        private final Type    type;

        private Delta(final int aPos, final List<T> aLines, final int bPos, final List<T> bLines) {
            type        = aLines.isEmpty() ? ADD : bLines.isEmpty() ? DELETE : CHANGE;
            aPosition   = aPos;
            bPosition   = bPos;
            this.aLines = immutable(aLines);
            this.bLines = immutable(bLines);
        }

        /**
         * Print the Delta using the standard Diff format.
         *
         * @param  out  The Print Stream to print the delta to
         */
        public void print(PrintWriter out) {
            if (type == ADD) out.print(aPosition);
            else printRange(out, aPosition, aLines);
            out.print(type);
            if (type == DELETE) out.print(bPosition);
            else printRange(out, bPosition, bLines);
            out.println();

            for (final T l : aLines)
                out.printf("< %s\n", l);
            if (type == CHANGE) out.println("---");
            for (final T l : bLines)
                out.printf("> %s\n", l);
        }

        public String toString() {
            return createToStringBuilder("Diff.Delta").add(aPosition).add(type).add(bPosition).add(aLines).add(bLines).build();
        }

        private void printRange(final PrintWriter out, final int pos, final Collection<T> lines) {
            out.print(pos + 1);
            final int n = lines.size();
            if (n > 1) {
                out.print(',');
                out.print(pos + n);
            }
        }
    }  // end class Delta

    public static class Equals<T> {
        /**
         * Method that perform the actual comparison for equal between the 2 values You can override
         * this method to implement other type of comparisons (i.e. case insensitive, ignore spaces,
         * etc)
         */
        public boolean doEqualComparison(final T a, final T b) {
            return a.equals(b);
        }
        private boolean equal(final T a, final T b) {
            return a == b || b != null && doEqualComparison(a, b);
        }
    }

    private static class IgnoreSpace extends Equals<String> {
        private final Equals<String> cmp;

        public IgnoreSpace(final Equals<String> cmp) {
            this.cmp = cmp;
        }

        @Override public boolean doEqualComparison(final String a, final String b) {
            return cmp.doEqualComparison(removeSpaces(a), removeSpaces(b));
        }
        private static String removeSpaces(final String a) {
            final StringBuilder result = new StringBuilder();
            for (int i = 0; i < a.length(); i++) {
                final char c = a.charAt(i);
                if (!Character.isWhitespace(c)) result.append(c);
            }
            return result.toString();
        }
    }

    private static class PathNode {
        private final int      i;
        private final int      j;
        private final PathNode prev;
        private boolean        snake;

        PathNode(final int i, final int j, final PathNode prev) {
            this.i    = i;
            this.j    = j;
            this.prev = prev;
        }

        public boolean isSnake() {
            return snake;
        }

        private PathNode previousSnake() {
            return i < 0 || j < 0 ? null : !snake && prev != null ? prev.previousSnake() : this;
        }

        static PathNode diffNode(int i, int j, PathNode prev) {
            return new PathNode(i, j, (prev == null ? null : prev.previousSnake()));
        }

        static PathNode snake(int i, int j, @Nullable final PathNode prev) {
            final PathNode node = new PathNode(i, j, prev);
            node.snake = true;
            return node;
        }
    }

    public static class Str extends Diff<String> {
        private final boolean ignoreEmptyLines;

        /** Creates a instance of the Diff class. */
        private Str(@NotNull final Equals<String> equalsComparator, final boolean ignoreEmptyLines) {
            super(equalsComparator);
            this.ignoreEmptyLines = ignoreEmptyLines;
        }

        /**
         * Computes the difference between the 2 strings split by end of line and returns it as a
         * List of Delta objects.
         */
        public List<Delta<String>> diff(final String a, final String b) {
            return diff(Strings.lines(a), Strings.lines(b));
        }
        /**
         * Computes the difference between the 2 Readers and returns it as a List of Delta objects.
         */
        public final ImmutableList<Delta<String>> diff(Reader a, Reader b) {
            return diff(Files.readLines(a), Files.readLines(b));
        }

        /**
         * Computes the difference between the 2 Files and returns it as a List of Delta objects.
         */
        public List<Delta<String>> diff(final File a, final File b) {
            return diff(Files.readLines(a), Files.readLines(b));
        }

        /** Ignore empty lines. */
        public Str ignoreEmptyLines() {
            return new Str(equalsComparator, true);
        }

        /** Ignore empty lines. */
        public Str ignoreSpaces() {
            return new Str(new IgnoreSpace(equalsComparator), ignoreEmptyLines);
        }

        protected List<String> extractSubList(final List<String> a, final int from, final int to) {
            final List<String> ts = super.extractSubList(a, from, to);
            if (ignoreEmptyLines) {
                for (final String t : ts)
                    if (!t.isEmpty()) return ts;
                return emptyList();
            }
            return ts;
        }
    }  // end class Str
}  // end class Diff
