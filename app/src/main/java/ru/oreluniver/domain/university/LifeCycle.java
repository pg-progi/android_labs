package ru.oreluniver.domain.university;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.shapes.OvalShape;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class LifeCycle extends AppCompatActivity {
    public static final String TAG = "LifeCycle";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_life_cycle);
        TextView view = (TextView) findViewById(R.id.font_test);
        Typeface font = Typeface.createFromAsset(getAssets(), "Magneto-Bold.ttf");
        view.setTypeface(font);
        Log.d(TAG, "OnCreate call");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart call");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume call");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop call");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OnDestory call");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "OnRestart call");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "OnPause call");
    }
}
