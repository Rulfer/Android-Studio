package com.makingview.mvlauncher;

import android.app.Activity;
import android.content.Intent;
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

    public void showApps(View view){
        Intent i = new Intent(this, AppListActivity.class);
        startActivity(i);
    }
}
