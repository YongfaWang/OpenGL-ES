package com.opengl.myapplication;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

    GLSurfaceView sv;
    boolean isNewSV = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sv = new GLSurfaceView(this);
        sv.setEGLContextClientVersion(3);
        sv.setRenderer(new FRenderer());

        isNewSV = true;
        setContentView(sv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isNewSV)
        {
            sv.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isNewSV)
        {
            sv.onResume();
        }
    }
}
