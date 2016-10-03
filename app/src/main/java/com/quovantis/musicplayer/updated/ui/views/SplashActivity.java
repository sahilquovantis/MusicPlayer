package com.quovantis.musicplayer.updated.ui.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.quovantis.musicplayer.R;
import com.quovantis.musicplayer.updated.constants.AppKeys;
import com.quovantis.musicplayer.updated.helper.PermissionHelper;
import com.quovantis.musicplayer.updated.ui.views.home.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (PermissionHelper.checkPermission(SplashActivity.this)) {
            initHandler();
        }
    }

    private void initHandler() {
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 2000);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppKeys.MY_PERMISSIONS_REQUEST_READ_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initHandler();
            } else {
                Toast.makeText(SplashActivity.this, "Read Storage Permission Needed", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        super.onBackPressed();
    }
}
