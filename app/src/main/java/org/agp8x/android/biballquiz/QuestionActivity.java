package org.agp8x.android.biballquiz;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.agp8x.android.biballquiz.data.Question;

public class QuestionActivity extends AppCompatActivity {
    private Button buttonTrue;
    private Button buttonFalse;
    protected QuizService quizService;
    protected boolean serviceBound = false;
    private ServiceConnection serviceconnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            quizService = ((QuizService.LocalBinder) iBinder).getService();
            serviceBound = true;
            QuestionActivity.this.setupQuestion();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };
    private Question question;
    private TextView text;
    private TextView explanation;

    protected void setupQuestion() {
        question = quizService.getNext();
        text.setBackgroundColor(Color.TRANSPARENT);
        text.setText(question.getQuestion());
        explanation.setText("");
        buttonTrue.setEnabled(true);
        buttonFalse.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        text = (TextView) findViewById(R.id.text_question);
        explanation = (TextView) findViewById(R.id.text_explanation);
        buttonFalse = (Button) findViewById(R.id.button_false);
        buttonFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (question.isCorrect()) {
                    text.setBackgroundColor(Color.RED);
                    Toast.makeText(QuestionActivity.this, "WRONG!!!!", Toast.LENGTH_SHORT).show();
                    explanation.setText(question.getDetails());
                } else {
                    text.setBackgroundColor(Color.GREEN);
                    Toast.makeText(QuestionActivity.this, "RIGHT!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonTrue = (Button) findViewById(R.id.button_true);
        buttonTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (question.isCorrect()) {
                    text.setBackgroundColor(Color.GREEN);
                    Toast.makeText(QuestionActivity.this, "RIGHT!", Toast.LENGTH_SHORT).show();
                } else {
                    text.setBackgroundColor(Color.RED);
                    Toast.makeText(QuestionActivity.this, "WRONG!!!!", Toast.LENGTH_SHORT).show();
                    explanation.setText(question.getDetails());
                }
            }
        });
        Button buttonNext= (Button) findViewById(R.id.button_next);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupQuestion();
            }
        });
        Intent intent = new Intent(this, QuizService.class);
        bindService(intent, serviceconnection, Context.BIND_AUTO_CREATE);
    }
}
