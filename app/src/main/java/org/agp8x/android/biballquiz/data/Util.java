package org.agp8x.android.biballquiz.data;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by clemensk on 19.09.16.
 */
public class Util {
    public static List<Question> loadQuestions(String path, Context context) {
        List<Question> questions = new LinkedList<>();
        try {
            InputStream is = context.getAssets().open(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String input;
            boolean init = true;
            while ((input = br.readLine()) != null) {
                if (input.length() < 0 || init) {
                    init = false;
                    continue;
                }
                String[] substrings = TextUtils.split(input, "\",\"");
                String[] meta = TextUtils.split(substrings[0], ",");
                int id = Integer.parseInt(meta[0]);
                boolean correct = (Integer.parseInt(meta[1]) == 0);
                String question = substrings[0].split("\"")[1];
                String details = substrings[1].substring(0, substrings[1].length() - 1);
                questions.add(new Question(id, correct, question, details));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(questions);
    }
}
