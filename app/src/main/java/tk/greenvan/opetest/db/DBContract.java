package tk.greenvan.opetest.db;

import android.provider.BaseColumns;

public final class DBContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private DBContract() {
    }

    /**
     * Inner class that defines constant values for the user_tests database table.
     * Each entry in the table represents an answer to a question.
     */
    public static final class AnswerEntry implements BaseColumns {

        /**
         * Name of database table for user answers
         */
        public final static String TABLE_NAME = "user_answers";

        /**
         * Unique ID number for the pet (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the user.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_USER_NAME = "username";

        /**
         * Test Id.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_TEST_ID = "testID";

        /**
         * Question Id.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_QUESTION_ID = "questionID";

        /**
         * State of the answer.
         * <p>
         * The only possible values are {@link #NO_ANSWER}, {@link #RIGHT_ANSWER},
         * or {@link #WRONG_ANSWER}.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_ANSWER_STATE = "answerState";


        /**
         * Last accessed.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_LAST_ACCESS = "lastAcess";

        /**
         * Selection (A, B, C, D).
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_SELECTION = "selection";

        /**
         * Possible values for the answer state of the question.
         */
        public static final int NO_ANSWER = 0;
        public static final int RIGHT_ANSWER = 1;
        public static final int WRONG_ANSWER = 2;
    }

}
