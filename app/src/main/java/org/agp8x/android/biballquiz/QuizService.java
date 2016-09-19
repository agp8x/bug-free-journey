package org.agp8x.android.biballquiz;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.agp8x.android.biballquiz.data.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clemensk on 19.09.16.
 */
public class QuizService extends Service {
    private final IBinder binder = new LocalBinder();
    private final List<Question> questions = new ArrayList<>();
    int i = -1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        questions.add(new Question(1, "false", false, "asdf"));
        questions.add(new Question(2, "true", true, "asdf"));
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        QuizService getService() {
            return QuizService.this;
        }
    }

    public Question getNext() {
        i += 1;
        if (i >= questions.size()) {
            i = 0;
        }
        return questions.get(i);
    }
}
