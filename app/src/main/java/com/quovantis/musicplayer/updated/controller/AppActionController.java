package com.quovantis.musicplayer.updated.controller;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * This handles starting of activity and services.
 */

public class AppActionController {
    private final Context mContext;
    private Class mTargetClass;
    private int mRequestCode;
    private Bundle mBundle;
    private Activity mActivity;
    private boolean mIsStartActivityForResult;
    private boolean mIsStartService;
    private String mIntentAction;

    private AppActionController(Builder builder) {
        this.mContext = builder.mContext;
        this.mTargetClass = builder.mTargetClass;
        this.mRequestCode = builder.mRequestCode;
        this.mBundle = builder.mBundle;
        this.mActivity = builder.mActivity;
        this.mIsStartActivityForResult = builder.mIsStartActivityForResult;
        this.mIsStartService = builder.mIsStartService;
        this.mIntentAction = builder.mIntentAction;
    }

    public void execute() {
        Intent intent = new Intent(mActivity, mTargetClass);
        if (mBundle != null) {
            intent.putExtras(mBundle);
        }
        if (mIntentAction != null) {
            intent.setAction(mIntentAction);
        }
        if (mIsStartActivityForResult) {
            mActivity.startActivityForResult(intent, mRequestCode);
        } else if (mIsStartService) {
            mContext.startService(intent);
        } else {
            mContext.startActivity(intent);
        }
    }

    public static class Builder {
        private final Context mContext;
        private Class mTargetClass;
        private int mRequestCode;
        private Bundle mBundle;
        private Activity mActivity;
        private boolean mIsStartActivityForResult;
        private boolean mIsStartService;
        private String mIntentAction;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder from(Activity activity) {
            this.mActivity = activity;
            return this;
        }

        public Builder setTargetActivity(Class<? extends Activity> mTargetClass) {
            this.mTargetClass = mTargetClass;
            mIsStartActivityForResult = false;
            mIsStartService = false;
            return this;
        }

        public Builder setTargetActivityForResult(Class<? extends Activity> mTargetClass, int mRequestCode) {
            this.mTargetClass = mTargetClass;
            this.mRequestCode = mRequestCode;
            mIsStartActivityForResult = true;
            mIsStartService = false;
            return this;
        }

        public Builder setTargetService(Class<? extends Service> mTargetClass) {
            this.mTargetClass = mTargetClass;
            mIsStartService = true;
            mIsStartActivityForResult = false;
            return this;
        }

        public Builder setIntentAction(String intentAction) {
            this.mIntentAction = intentAction;
            return this;
        }

        public Builder setBundle(Bundle mBundle) {
            this.mBundle = mBundle;
            return this;
        }

        public AppActionController build() {
            return new AppActionController(this);
        }
    }
}
