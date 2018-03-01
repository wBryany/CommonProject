package com.project.common_basic.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.AnimRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.project.common_basic.R;
import com.project.common_basic.exception.NearLogger;
import com.project.common_basic.mvp.NearInteraction;
import com.project.common_basic.widget.AppBar;

/**
 * Essential模块基础Activity
 *
 * @author yamlee
 */
public class NearActivity extends AppCompatActivity implements
        NearFragment.BackHandlerInterface, NearInteraction {
    protected AppBar appBar;
    private View rootLayoutView;
    private FrameLayout layoutContent;
    private NearFragment selectedFragment;
    protected boolean isPaused = false;
    protected FragmentManager supportFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            // Activity was brought to front and not created,
            // Thus finishing this will get us to the last viewed activity
            finish();
            return;
        }
        supportFragmentManager = getSupportFragmentManager();
        rootLayoutView = LayoutInflater.from(this).inflate(R.layout.activity_base_content, null);
        super.setContentView(rootLayoutView);
        layoutContent = (FrameLayout) rootLayoutView.findViewById(R.id.layout_content);
        appBar = (AppBar) rootLayoutView.findViewById(R.id.appbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPaused = true;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(layoutResID, layoutContent);
        super.setContentView(rootLayoutView);
    }

    @Override
    public void setContentView(View view) {
        layoutContent.removeAllViews();
        layoutContent.addView(view);
        super.setContentView(rootLayoutView);
    }

    @Override
    public void startNearActivity(Intent intent, Class<? extends Activity> activityClass) {
        try {
            if (intent != null) {
                intent.setClass(this, activityClass);
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            NearLogger.log(e);
        }
    }

    @Override
    public void startNearActivity(Intent intent) {
        try {
            //去除intent.resolveActivity的判断，防止那些由action构建的Intent无法启动
            if (intent != null) {
                intent.setPackage(getPackageName());
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.have_some_problem_please_contacts_user_service, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            NearLogger.log(e);
            Toast.makeText(getApplicationContext(),
                    R.string.have_some_problem_please_contacts_user_service, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startOutsideActivity(Intent intent) {
        try {
            if (intent != null) {
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(),
                        R.string.have_some_problem_please_contacts_user_service, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            NearLogger.log(e);
            Toast.makeText(getApplicationContext(),
                    R.string.have_some_problem_please_contacts_user_service, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startNearActivityForResult(Intent intent, int requestCode) {
        if (intent == null) return;
        try {
            startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            NearLogger.log(e);
        }
    }

    @Override
    public void startNearActivityForResult(Intent intent, int requestCode, Class<? extends Activity> activityClass) {
        try {
            if (intent != null) {
                intent.setClass(this, activityClass);
            }
            startNearActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException e) {
            NearLogger.log(e);
        }
    }

    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void finishActivityDelay(long millis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, millis);
    }

    @Override
    public void finishActivityWithAnim() {
        finish();
        overridePendingTransition(R.anim.activity_animation_right_left,
                R.anim.activity_animation_right_left_exit);
    }

    @Override
    public void finishActivityWithAnim(@AnimRes int enterAnim, @AnimRes int exitAnim) {
        finish();
        overridePendingTransition(enterAnim, exitAnim);
    }

    @Override
    public void setActivityResult(int resultCode, Intent data) {
        setResult(resultCode, data);
    }


    @Override
    public void setSelectedFragment(NearFragment baseFragment) {
        this.selectedFragment = baseFragment;
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment == null || !selectedFragment.onFragmentBackPressed()) {
            // Selected fragment did not consume the back press event.
            if (!isPaused) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void addFragment(NearFragment fragment) {
        if (fragment.getArguments() == null) {
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
        }
        if (supportFragmentManager == null){
            return;
        }
        FragmentTransaction ft = supportFragmentManager.beginTransaction();
        ft.add(R.id.layout_content, fragment, fragment.getClass().getName());
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void popFragment() {
        if (!isPaused) {
            super.onBackPressed();
        }
    }

    @Override
    public void hideFragment(NearFragment fragment) {
        if (supportFragmentManager == null){
            return;
        }
        FragmentTransaction ft = supportFragmentManager.beginTransaction();
        if (fragment != null && fragment.isAdded()) {
            ft.hide(fragment);
        }
        ft.commitAllowingStateLoss();
    }

    @Override
    public void showFragment(NearFragment fragment) {
        if (supportFragmentManager == null){
            return;
        }
        FragmentTransaction ft = supportFragmentManager.beginTransaction();
        if (fragment != null && fragment.isAdded()) {
            ft.show(fragment);
        }
        ft.commitAllowingStateLoss();
    }


    @Override
    public void initFragment(NearFragment f) {
        changeFragment(f, true);
    }

    @Override
    public void changeFragment(NearFragment f) {
        changeFragment(f, false);
    }

    private void changeFragment(Fragment f, boolean init) {
        if (f.getArguments() == null) {
            Bundle bundle = new Bundle();
            f.setArguments(bundle);
        }
        if (supportFragmentManager == null){
            return;
        }
        FragmentTransaction ft = supportFragmentManager.beginTransaction();
        ft.replace(R.id.layout_content, f, f.getClass().getName());
        if (!init)
            ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    public void setHasAppBar(boolean hasAppBar) {
        if (!hasAppBar) {
            appBar.setVisibility(View.GONE);
        } else {
            appBar.setVisibility(View.VISIBLE);
        }
    }

    public AppBar getAppBar() {
        return appBar;
    }


}
