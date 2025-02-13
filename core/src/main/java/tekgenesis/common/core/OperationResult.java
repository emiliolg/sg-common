
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.core;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import tekgenesis.common.collections.Colls;
import tekgenesis.common.exception.ApplicationException;
import tekgenesis.common.util.Message;

/**
 * This class ia a container of the result of an operation.
 */
public class OperationResult<T> {

    //~ Instance Fields ..............................................................................................................................

    private final List<Message> errors = new ArrayList<>();

    @Nullable private final T   value;
    private final List<Message> warnings = new ArrayList<>();

    //~ Constructors .................................................................................................................................

    private OperationResult(@Nullable T value) {
        this.value = value;
    }

    //~ Methods ......................................................................................................................................

    /** Add error message to result. */
    @NotNull public OperationResult<T> addError(@NotNull Message message) {
        errors.add(message);
        return this;
    }

    /** Add error message with arguments to result. */
    @NotNull public OperationResult<T> addError(Enumeration<?, ?> msg, Object... args) {
        errors.add(Message.create(msg, args));
        return this;
    }

    /** Add warning message to result. */
    @NotNull public OperationResult<T> addWarning(@NotNull Message message) {
        warnings.add(message);
        return this;
    }

    /** Add warning message with arguments to result. */
    @NotNull public OperationResult<T> addWarning(Enumeration<?, ?> msg, Object... args) {
        warnings.add(Message.create(msg, args));
        return this;
    }

    /**
     * @param   newValue  the element of the new result
     *
     * @return  a new object withe the new value and a copy of all errors and warnings
     */
    public <R> OperationResult<R> copy(@NotNull R newValue) {
        final OperationResult<R> result = new OperationResult<>(newValue);
        result.errors.addAll(errors);
        result.warnings.addAll(warnings);
        return result;
    }

    /**
     * @return  this element
     *
     * @throws  ApplicationException  if the operation is not valid
     * @throws  NullPointerException  if the element is null
     *
     * @see     #isValid()
     * @see     #getErrors()
     * @see     #none()
     */
    @NotNull public T get()
        throws ApplicationException
    {
        if (isNotValid()) throw new ApplicationException(getErrors().get(0));
        if (value == null) throw new NullPointerException("no value");

        return value;
    }

    /** Returns true if result is not valid. */
    public boolean isNotValid() {
        return !errors.isEmpty();
    }

    /** Returns true if result is valid. */
    public boolean isValid() {
        return errors.isEmpty();
    }

    /** Returns list containing errors. */
    public List<Message> getErrors() {
        return errors;
    }

    /** Returns errors as one String. */
    public String getErrorsAsString() {
        return Colls.mkString(getErrors());
    }

    /** Returns value or null if no value. */
    @Nullable public T getOrNull() {
        return value;
    }

    /** Returns list containing warnings. */
    public List<Message> getWarnings() {
        return warnings;
    }

    /** Returns warnings as one String. */
    public String getWarningsAsString() {
        return Colls.mkString(getWarnings());
    }

    //~ Methods ......................................................................................................................................

    /** Creates an OperationResult with none value. */
    public static <T> OperationResult<T> none() {
        return new OperationResult<>(null);
    }

    /** Creates an OperationResult with some value. */
    public static <T> OperationResult<T> some(@NotNull T element) {
        return new OperationResult<>(element);
    }
}  // end class OperationResult
