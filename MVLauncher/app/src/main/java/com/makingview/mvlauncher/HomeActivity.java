package com.makingview.mvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Bård on 08.11.2016.
 */

public class HomeActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void openMovieMenu(View view) {
        PackageManager manager = getPackageManager();

        Intent i = manager.getLaunchIntentForPackage("com.MakingView.movieMenu");

        if (i != null) {
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(i);
        }
    }
}
