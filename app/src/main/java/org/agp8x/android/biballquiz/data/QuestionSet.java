package org.agp8x.android.biballquiz.data;

import java.util.List;

/**
 * Created by clemensk on 01.10.16.
 */

public class QuestionSet implements DBObject {
    private int id;
    private String name;
    private List<Question> questions;

    public QuestionSet(String name, List<Question> questions) {
        this.name = name;
        this.questions = questions;
    }

    public QuestionSet(int id, String name, List<Question> questions) {
        this.id = id;
        this.name = name;
        this.questions = questions;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Question> getQuestions() {
        return questions;
    }
}
