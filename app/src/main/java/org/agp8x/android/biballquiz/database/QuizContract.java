package org.agp8x.android.biballquiz.database;

import android.provider.BaseColumns;

/**
 * Created by clemensk on 01.10.16.
 */

public class QuizContract {
    private QuizContract() {
    }

    public static class QuestionEntry implements BaseColumns {
        public static final String TABLE_NAME = "question";
        public static final String COLUMN_NAME_QUESTION = "question";
        public static final String COLUMN_NAME_CORRECT = "correct";
        public static final String COLUMN_NAME_DETAILS = "details";
        public static final String COLUMN_NAME_SOLVED = "solved";
        public static final String COLUMN_NAME_FAILED = "failed";
        public static final String COLUMN_NAME_QUESTIONSET = "questionset";
    }

    public static class QuestionSetEntry implements BaseColumns {
        public static final String TABLE_NAME = "questionset";
        public static final String COLUMN_NAME_QUESTIONSET = "name";
    }
}
