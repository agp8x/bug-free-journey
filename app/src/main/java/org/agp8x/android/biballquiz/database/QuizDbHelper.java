package org.agp8x.android.biballquiz.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.agp8x.android.biballquiz.data.DBObject;
import org.agp8x.android.biballquiz.data.Question;
import org.agp8x.android.biballquiz.data.QuestionSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.agp8x.android.biballquiz.database.QuizContract.*;

/**
 * Created by clemensk on 01.10.16.
 */

public class QuizDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "quiz.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + QuestionEntry.TABLE_NAME + " (" +
                    QuestionEntry._ID + " INTEGER PRIMARY KEY," +
                    QuestionEntry.COLUMN_NAME_QUESTION + TEXT_TYPE + COMMA_SEP +
                    QuestionEntry.COLUMN_NAME_CORRECT + "INTEGER" + COMMA_SEP +
                    QuestionEntry.COLUMN_NAME_DETAILS + TEXT_TYPE + COMMA_SEP +
                    QuestionEntry.COLUMN_NAME_SOLVED + "INTEGER" + COMMA_SEP +
                    QuestionEntry.COLUMN_NAME_FAILED + "INTEGER" + COMMA_SEP +
                    QuestionEntry.COLUMN_NAME_QUESTIONSET + "INTEGER" + COMMA_SEP +
                    " );" +
                    "CREATE TABLE " + QuestionSetEntry.TABLE_NAME + " (" +
                    QuestionSetEntry._ID + " INTEGER PRIMARY KEY," +
                    QuestionSetEntry.COLUMN_NAME_QUESTIONSET + TEXT_TYPE +
                    " );";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + QuestionEntry.TABLE_NAME +
                    "; DROP TABLE IF EXISTS " + QuestionSetEntry.TABLE_NAME;


    private Creator<Question> questionCreator;
    private final String[] questionProjection;
    private final String[] questionSetProjection;
    private Creator<QuestionSet> questionSetCreator;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        questionProjection = new String[]{
                QuestionEntry._ID,
                QuestionEntry.COLUMN_NAME_QUESTION,
                QuestionEntry.COLUMN_NAME_CORRECT,
                QuestionEntry.COLUMN_NAME_DETAILS,
                QuestionEntry.COLUMN_NAME_SOLVED,
                QuestionEntry.COLUMN_NAME_FAILED,
                QuestionEntry.COLUMN_NAME_QUESTIONSET
        };
        questionCreator = new Creator<Question>() {
            @Override
            public Question create(Cursor c) {
                int id = c.getInt(c.getColumnIndex(QuestionEntry._ID));
                boolean correct = c.getInt(c.getColumnIndex(QuestionEntry.COLUMN_NAME_CORRECT)) == 1;
                String questionText = c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_QUESTION));
                String details = c.getString(c.getColumnIndex(QuestionEntry.COLUMN_NAME_DETAILS));
                int solved = c.getInt(c.getColumnIndex(QuestionEntry.COLUMN_NAME_SOLVED));
                int failed = c.getInt(c.getColumnIndex(QuestionEntry.COLUMN_NAME_FAILED));
                return new Question(id, correct, questionText, details);
            }
        };

        questionSetProjection = new String[]{
                QuestionSetEntry._ID,
                QuestionSetEntry.COLUMN_NAME_QUESTIONSET
        };
        questionSetCreator = new Creator<QuestionSet>() {
            @Override
            public QuestionSet create(Cursor c) {
                int id = c.getInt(c.getColumnIndex(QuestionSetEntry._ID));
                String name = c.getString(c.getColumnIndex(QuestionSetEntry.COLUMN_NAME_QUESTIONSET));
                List<Question> questions = getQuestions(id);
                return new QuestionSet(id, name, questions);
            }
        };
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        //TODO
    }

    public long insertQuestion(Question question, QuestionSet questionSet) {
        return insertQuestion(question, questionSet.getId());
    }

    public long insertQuestion(Question question) {
        return insertQuestion(question, -1);
    }

    public long insertQuestion(Question question, long questionSet) {
        ContentValues values = new ContentValues();
        values.put(QuestionEntry.COLUMN_NAME_QUESTION, question.getQuestion());
        int correct = (question.isCorrect()) ? 1 : 0;
        values.put(QuestionEntry.COLUMN_NAME_CORRECT, correct);
        values.put(QuestionEntry.COLUMN_NAME_DETAILS, question.getDetails());
        values.put(QuestionEntry.COLUMN_NAME_SOLVED, 0);
        values.put(QuestionEntry.COLUMN_NAME_FAILED, 0);
        values.put(QuestionEntry.COLUMN_NAME_QUESTIONSET, questionSet);
        return getWritableDatabase().insert(QuestionEntry.TABLE_NAME, null, values);
    }

    public List<Question> getAllQuestions() {
        String selection = "";
        String[] selectionArgs = {};
        String sortOrder = "";
        return getter(questionCreator, QuestionEntry.TABLE_NAME, questionProjection, selection, selectionArgs, sortOrder);
    }

    public List<Question> getQuestions(QuestionSet questionSet) {
        return getQuestions(questionSet.getId());
    }

    private List<Question> getQuestions(long questionSet) {
        String selection = QuestionEntry.COLUMN_NAME_QUESTIONSET + " = ? ";
        String[] selectionArgs = {String.valueOf(questionSet)};
        String sortOrder = "";
        return getter(questionCreator, QuestionEntry.TABLE_NAME, questionProjection, selection, selectionArgs, sortOrder);
    }

    public boolean updateQuestion(long id, String key, int count) {
        ContentValues values = new ContentValues();
        values.put(key, count);
        String selection = QuestionEntry._ID + " =?";
        String[] selectionArgs = {String.valueOf(id)};
        int affected = getReadableDatabase().update(QuestionEntry.TABLE_NAME, values, selection, selectionArgs);
        return affected > 0;
    }

    public long insertQuestionSet(QuestionSet questionSet) {
        ContentValues values = new ContentValues();
        values.put(QuestionSetEntry.COLUMN_NAME_QUESTIONSET, questionSet.getName());
        return getWritableDatabase().insert(QuestionSetEntry.TABLE_NAME, null, values);
    }

    public List<QuestionSet> getAllQuestionSets() {
        String selection = "";
        String[] selectionArgs = {};
        String sortOrder = "";
        return getter(questionSetCreator, QuestionSetEntry.TABLE_NAME, questionSetProjection, selection, selectionArgs, sortOrder);
    }


    private <E extends DBObject> List<E> getter(Creator<E> creator, String tablename, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = getReadableDatabase().query(tablename, projection, selection, selectionArgs, null, null, sortOrder);
        c.moveToFirst();
        List<E> objects = new ArrayList<>();
        while (c.isFirst() || c.moveToNext()) {
            objects.add(creator.create(c));
        }
        return objects;
    }

    private interface Creator<E extends DBObject> {
        E create(Cursor c);
    }
}
