
// ...............................................................................................................................
//
// (C) Copyright  2011/2017 TekGenesis.  All Rights Reserved
// THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF TekGenesis.
// The copyright notice above does not evidence any actual or intended
// publication of such source code.
//
// ...............................................................................................................................

package tekgenesis.common.exception;

/**
 * SuiGeneris exception.
 */
@SuppressWarnings("GwtInconsistentSerializableClass")
public class SuiGenerisException extends RuntimeException {

    //~ Static Fields ................................................................................................................................

    private static final long serialVersionUID = 3706228766929986689L;

    //~ Inner Classes ................................................................................................................................

    public static class Default extends SuiGenerisException {
        private final String message;

        /** Default SuiGeneris Exception with a message. */
        public Default(String message) {
            this.message = message;
        }

        @Override public String getMessage() {
            return message;
        }

        private static final long serialVersionUID = -8084754170108022013L;
    }
}
