package org.agp8x.android.biballquiz.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.agp8x.android.biballquiz.QuizService;
import org.agp8x.android.biballquiz.Util;

import java.util.ArrayList;
import java.util.List;

public class FilesActivity extends AppCompatActivity {

    private int requestCode = 1337;
    private List<Uri> backlog = new ArrayList<>();
    protected QuizService quizService;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            quizService = ((QuizService.LocalBinder) iBinder).getService();
            handleIntent();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, QuizService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void handleIntent() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e(Util.TAG, "GIMME PERMISSION!");
                //TODO: rationale
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            }
        }
        if (getIntent().getAction().equals(Intent.ACTION_SEND)) {
            Parcelable payload = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
            backlog.add((Uri) payload);
        } else if (getIntent().getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
            List<Parcelable> payloads = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            for (Parcelable p : payloads) {
                backlog.add((Uri) p);
            }
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            handle();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handle();
            } else {
                Log.d(Util.TAG, "FU!");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handle() {
        for (Uri uri : backlog) {
            quizService.load(uri.getPath());
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
