
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.util;

import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.ImmutableList;
import tekgenesis.common.collections.Seq;
import tekgenesis.common.collections.Stack;
import tekgenesis.common.core.Tuple;

import static java.lang.Character.isJavaIdentifierPart;
import static java.lang.Character.isJavaIdentifierStart;
import static java.lang.String.format;

import static tekgenesis.common.Predefined.notNull;
import static tekgenesis.common.core.Strings.isLowerCase;
import static tekgenesis.common.core.Strings.isUpperCase;
import static tekgenesis.common.core.Tuple.tuple;
import static tekgenesis.common.util.Preprocessor.Directive.EMPTY;
import static tekgenesis.common.util.Preprocessor.Directive.NONE;
import static tekgenesis.common.util.Preprocessor.State.*;

/**
 * A Simple Line reader {@link Reader} with certain pre-processing capabilities (Like a very simple
 * cpp).
 */
public class Preprocessor {

    //~ Instance Fields ..............................................................................................................................

    private final Map<String, Macro> defines;
    private Escape                   escapeMode;
    @SuppressWarnings("BooleanVariableAlwaysNegated")
    private boolean                  expandStrings;
    private final LinkedList<String> lastWords;

    private int lineNo;

    @NotNull private Pattern                          pattern;
    private final Map<String, Macro>                  preDefines;
    @NotNull private final Stack<ProcessedDirective>  states = Stack.createStack();

    //~ Constructors .................................................................................................................................

    /** Construct the Preprocessor. */
    public Preprocessor() {
        reset();
        defines       = new HashMap<>();
        pattern       = compilePattern("#");
        lineNo        = 0;
        escapeMode    = Escape.NONE;
        expandStrings = false;
        lastWords     = new LinkedList<>();
        preDefines    = Predefined.initPredefined(this);
    }

    //~ Methods ......................................................................................................................................

    /** Define the symbol as TRUE. */
    @SuppressWarnings("UnusedReturnValue")
    public Preprocessor define(String name) {
        defines.put(name, new Macro(Boolean.TRUE.toString()));
        return this;
    }

    /** Define the symbol with the specified value. */
    @SuppressWarnings("UnusedReturnValue")
    public Preprocessor define(String name, String value) {
        defines.put(name, new Macro(value));
        return this;
    }

    /** Define the symbol with the specified value. */
    @SuppressWarnings("UnusedReturnValue")
    public Preprocessor define(String name, Function<List<String>, String> function) {
        defines.put(name, new Macro(function));
        return this;
    }

    /** Define all the symbol in the specified map. */
    public Preprocessor defineAll(HashMap<String, Macro> values) {
        for (final Map.Entry<String, Macro> e : values.entrySet())
            defines.put(e.getKey(), e.getValue());
        return this;
    }

    /** The preprocessor will escape ids using the indicated ESCAPE MODE. */
    @SuppressWarnings("UnusedReturnValue")
    public Preprocessor escapeIds(Escape escape) {
        escapeMode = escape;
        return this;
    }

    /** Expand macros inside strings. */
    @SuppressWarnings("UnusedReturnValue")
    public Preprocessor expandStrings() {
        expandStrings = true;
        return this;
    }

    /** Process a Reader and return a Seq with the preprocessed lines. */
    @NotNull public Seq<String> process(Reader reader) {
        return process(Files.readLines(reader));
    }

    /** Process a set of lines and return a Seq with the preprocessed lines. */
    @NotNull public Seq<String> process(@NotNull Iterable<String> lines) {
        reset();
        final ImmutableList.Builder<String> result = ImmutableList.builder();
        for (final String line : lines) {
            lineNo++;
            result.add(processLine(line));
        }

        if (states.size() > 1) {
            final ProcessedDirective dir = states.peek();
            throw new Exception(format("Reached end of processing with an unclosed '%s' directive declared at line '%d'.", dir.directive, dir.line));
        }
        return result.build();
    }
    /** PreProcess a line. */
    @NotNull public String process(@NotNull String line) {
        reset();
        lineNo = 1;
        return notNull(processLine(line));
    }

    /** Reset the preprocessor to clean State. */
    @SuppressWarnings("WeakerAccess")
    public void reset() {
        states.clear();
        states.push(new ProcessedDirective());
    }

    /** Un Define the symbol with the specified value. */
    @SuppressWarnings({ "WeakerAccess", "UnusedReturnValue" })
    public Preprocessor undefine(String name) {
        defines.remove(name);
        return this;
    }

    /** Sets the string to be used to identify preprocessor directives. */
    @SuppressWarnings({ "WeakerAccess", "UnusedReturnValue" })
    public Preprocessor withMarker(String marker) {
        pattern = compilePattern(marker);
        return this;
    }

    @SuppressWarnings("WeakerAccess")
    boolean evaluate(String ops) {
        final OpIterator it = new OpIterator(ops);
        while (it.hasNext()) {
            final String  op       = it.next();
            final boolean b        = op.charAt(0) != '!' ? isTrue(op) : !isTrue(op.length() > 1 ? op.substring(1) : it.next());
            final String  operator = it.next();
            if (operator.isEmpty()) return b;
            if (operator.charAt(0) == '|') {
                if (b) return true;
            }
            else if (!b) return false;
        }
        return true;
    }

    private void _define(String operands) {
        final Matcher m      = DEFINE_PATTERN.matcher(operands);
        String        symbol = "";
        String        value  = "";
        if (m.find()) {
            symbol = m.group(1);
            value  = m.group(2);
        }
        if (symbol.isEmpty()) throw new Exception(format("Illegal Define: '%s' at line '%d'", operands, lineNo));
        if (value.isEmpty()) define(symbol);
        else define(symbol, value);
    }

    private void _undefine(String operands) {
        final Matcher m      = WORD_PATTERN.matcher(operands);
        final String  symbol = m.find() ? m.group(1) : "";
        if (symbol.isEmpty()) throw new Exception(format("Illegal Undefine: '%s' at line '%d'", operands, lineNo));
        undefine(symbol);
    }

    private String applyMacros(String line) {
        final StringBuilder result = new StringBuilder();

        int i = 0;
        while (i < line.length()) {
            final char c = line.charAt(i);
            if (c == '"' && !expandStrings || c == '\'' || c == '`') i = processString(result, line, i, c);
            else if (isJavaIdentifierStart(c)) i = processWord(result, line, i);
            else {
                result.append(c);
                i++;
            }
        }
        return result.toString();
    }  // end method applyMacros

    private State currentState() {
        return states.peek().state;
    }

    private Directive directive(String line) {
        final Matcher matcher = pattern.matcher(line);
        if (!matcher.find()) return NONE;
        final Directive op = Directive.find(matcher.group(1));
        if (op == NONE) return EMPTY;
        process(op, matcher.group(2));
        return op;
    }

    private State invalid(Directive op) {
        throw new Exception(format("Illegal '%s' directive at line '%d' without previous 'if'.", op, lineNo));
    }

    private void process(Directive op, String operands) {
        State pushState = null;
        switch (op) {
        case IF:
            pushState = evaluate(operands) ? TRUE : FALSE;
            break;
        case ELSIF: {
            final State state = states.pop().state;
            pushState = state == TRUE ? SKIP : state != FALSE ? invalid(op) : evaluate(operands) ? TRUE : FALSE;
        }
        break;
        case ELSE: {
            final State state = states.pop().state;
            pushState = state == TRUE || state == SKIP ? SKIP_END : state == FALSE ? ELSE : invalid(op);
        }
        break;
        case END:
            if (states.pop().state == START) invalid(op);
            break;
        case DEFINE:
            _define(operands);
            break;
        case UNDEFINE:
            _undefine(operands);
            break;
        default:
            break;
        }
        if (pushState != null) states.push(new ProcessedDirective(pushState, op));
    }

    @Nullable private String processLine(String line) {
        if (line == null) return null;

        final Directive directive = directive(line);
        return directive == EMPTY ? line : directive != NONE || currentState().skip() ? "" : applyMacros(line);
    }

    private int processWord(StringBuilder result, String line, int i) {
        final StringBuilder w = new StringBuilder();
        int                 j = i;
        while (j < line.length()) {
            final char c = line.charAt(j);
            if (!isJavaIdentifierPart(c)) break;
            w.append(c);
            j++;
        }
        final String word   = w.toString();
        final Macro  preDef = preDefines.get(word);
        final Macro  def    = preDef != null ? preDef : defines.remove(word);
        if (def == null) {
            lastWords.addFirst(word);
            if (lastWords.size() >= 10) lastWords.removeLast();
            result.append(escapeMode.escape(word));
            return j;
        }
        final Tuple<Integer, List<String>> t = extractArguments(line, j);
        result.append(applyMacros(def.expand(t.second(), lastWords)));
        if (preDef == null) defines.put(word, def);
        return t.first();
    }

    private boolean isTrue(String symbol) {
        final Macro macro = defines.get(symbol);
        return macro != null && macro.isTrue();
    }

    //~ Methods ......................................................................................................................................

    private static Pattern compilePattern(String marker) {
        return Pattern.compile("^\\s*" + marker + "\\s*(\\w*)\\s*(.*)");
    }

    private static Tuple<Integer, List<String>> extractArguments(String line, int argsStart) {
        int i = argsStart;
        if (i >= line.length() || line.charAt(i) != '(') return tuple(i, Collections.<String>emptyList());

        final int          n    = line.length();
        final List<String> args = new ArrayList<>();

        int paren    = 1;
        int argStart = ++i;
        // noinspection BooleanVariableAlwaysNegated
        boolean inQuotes = false;
        while (i < n) {
            final char c = line.charAt(i);
            if (c == '\'') inQuotes = !inQuotes;
            else if (!inQuotes) {
                if (c == '(') paren++;
                else if (paren == 1) {
                    if (c == ')' || c == ',') {
                        String arg = line.substring(argStart, i).trim();
                        // Special case check if the argument is escaped with '\(' '\)' and removed them
                        if (arg.startsWith("\\(") && arg.endsWith("\\)")) arg = arg.substring(2, arg.length() - 2);
                        if (!arg.isEmpty()) args.add(arg);
                        if (c == ')') return tuple(i + 1, args);
                        argStart = i + 1;
                    }
                }
                else if (c == ')') paren--;
            }
            i++;
        }
        return tuple(i, args);
    }  // end method extractArguments

    private static int processString(StringBuilder result, String line, int i, char c) {
        result.append(c);
        int j = i + 1;
        while (j < line.length()) {
            final char c1 = line.charAt(j);
            if (c1 == c) {
                j++;
                if (j == line.length() || line.charAt(j) != c) break;
                result.append(c1);
            }
            result.append(c1);
            j++;
        }
        result.append(c);
        return j;
    }

    //~ Static Fields ................................................................................................................................

    private static final String  WORD_CHAR       = "[\\$\\w]";
    private static final Pattern SPLIT_BY_SPACES = Pattern.compile("\\s+");
    private static final Pattern DEFINE_PATTERN  = Pattern.compile("(" + WORD_CHAR + "*)\\s*(.*)");
    private static final Pattern WORD_PATTERN    = Pattern.compile("(" + WORD_CHAR + "*)");

    private static final Pattern ARGUMENT = Pattern.compile("(\\$-?[0-9]+)");

    //~ Enums ........................................................................................................................................

    enum State {
        /** Base Initial State. */
        START(true),
        /** And if (or elsif) condition was true. */
        TRUE(true),
        /** And if (or elsif) condition was false. */
        FALSE(false),
        /** Skip to elsif, else or end. */
        SKIP(false),
        /** Skip to end. */
        SKIP_END(false),
        /** accept else block until end. */
        ELSE(true);

        @SuppressWarnings("BooleanVariableAlwaysNegated")
        private final boolean accept;

        State(boolean accept) {
            this.accept = accept;
        }

        public boolean skip() {
            return !accept;
        }
    }

    enum Directive {
        NONE, EMPTY, IF, ELSIF, ELSE, END, DEFINE, UNDEFINE;

        @Override public String toString() {
            return super.toString().toLowerCase();
        }

        static Directive find(String name) {
            try {
                return valueOf(name.toUpperCase());
            }
            catch (final IllegalArgumentException e) {
                return EMPTY;
            }
        }
    }

    public enum Escape {
        NONE,
        QUOTE_UPPER_CASE { @Override public String escape(String word) { return isUpperCase(word) ? '"' + word + '"' : word; } },
        QUOTE_LOWER_CASE { @Override public String escape(String word) { return isLowerCase(word) ? '"' + word + '"' : word; } };

        /** Escape the word. */
        public String escape(String word) {
            return word;
        }
    }

    enum Predefined {
        If {
            @Override String evaluate(final Preprocessor p, final List<String> arg) {
                return p.evaluate(arg.get(0)) ? arg.get(1) : arg.size() == 3 ? arg.get(2) : "";
            }};

        abstract String evaluate(final Preprocessor p, final List<String> arg);

        private static Map<String, Macro> initPredefined(final Preprocessor preprocessor) {
            final Map<String, Macro> map = new HashMap<>();

            for (final Predefined p : Predefined.values())
                map.put(p.name(), new Macro(arg -> p.evaluate(preprocessor, arg)));
            return map;
        }
    }

    //~ Inner Classes ................................................................................................................................

    public static class Exception extends RuntimeException {
        /** Create a Preprocessor.Exception. */
        public Exception(String message) {
            super(message);
        }

        private static final long serialVersionUID = 2543775881038324711L;
    }

    public static class Macro {
        public final Function<List<String>, String> function;
        private final String                        str;

        /** Create a Macro from an String. */
        public Macro(final String str) {
            this.str = str;
            function = null;
        }

        /** Create a Macro from a function. */
        public Macro(final Function<List<String>, String> function) {
            this.function = function;
            str           = null;
        }

        /** Returns the value as an String. */
        public String asString() {
            return notNull(str);
        }

        boolean isTrue() {
            return str != null && !str.isEmpty();
        }

        private String expand(List<String> args, final LinkedList<String> last) {
            if (function != null) return function.apply(args);
            if (str == null) return "";

            final StringBuilder result = new StringBuilder();
            final Matcher       m      = ARGUMENT.matcher(str);

            int prev = 0;
            while (m.find()) {
                final String word = m.group();
                result.append(str.substring(prev, m.start()));
                prev = m.end();

                final int n = Integer.parseInt(word.substring(1)) - 1;
                if (n >= 0 && n < args.size()) result.append(args.get(n));
                if (n < 0) result.append(last.get(-n - 2));
            }
            result.append(str.substring(prev));
            return result.toString();
        }
    }  // end class Macro

    private static class OpIterator {
        int            i;
        final int      n;
        final String[] operands;

        OpIterator(String ops) {
            operands = SPLIT_BY_SPACES.split(ops);
            n        = operands.length;
            i        = 0;
        }

        boolean hasNext() {
            return i < n;
        }
        String next() {
            return hasNext() ? operands[i++] : "";
        }
    }

    class ProcessedDirective {
        Directive directive;
        int       line;
        State     state;

        ProcessedDirective() {
            this(START, NONE);
        }

        ProcessedDirective(State state, Directive directive) {
            this.state     = state;
            this.directive = directive;
            line           = lineNo;
        }
    }
}  // end class Preprocessor
