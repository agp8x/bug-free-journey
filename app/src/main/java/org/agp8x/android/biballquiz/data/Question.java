package org.agp8x.android.biballquiz.data;

import java.io.Serializable;

/**
 * Created by clemensk on 19.09.16.
 */
public class Question implements Serializable {
    private final int id;
    private final String question;
    private final boolean correct;
    private final String details;

    public Question(int id, boolean correct, String question, String details) {
        this.id = id;
        this.question = question;
        this.correct = correct;
        this.details = details;
    }

    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getDetails() {
        return details;
    }
}
