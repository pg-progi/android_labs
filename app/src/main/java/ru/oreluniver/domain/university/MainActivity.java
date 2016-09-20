package ru.oreluniver.domain.university;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private TextView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1
        Button btn = (Button) findViewById(R.id.lifecycle);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LifeCycle.class);
                startActivity(intent);
            }
        });

        //2
        view = (TextView) findViewById(R.id.coords);

        //4
        final EditText edit = (EditText) findViewById(R.id.edit);
        Button editBtn = (Button) findViewById(R.id.text_click);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setText(edit.getText());
            }
        });

        //5
        new SensorHandler(this);

        //6
        AssetManager manager = getAssets();
        try {
            InputStream stream = manager.open("text.txt");
            BufferedReader buf = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = buf.readLine()) != null) {
                Log.d(TAG, "File text " + line);
            }
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //7
        AndroidFileIO afio = new AndroidFileIO(manager);
        try {
            PrintWriter writer = new PrintWriter(afio.writeFile("temp.txt"), true);
            writer.write("Now you see me 2");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //8
        MyDBHelper helper = new MyDBHelper(this);
        SQLiteDatabase wdb = helper.getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("name", "It's me!!!");
        wdb.insert("mytable", null, vals);

        SQLiteDatabase rdb = helper.getReadableDatabase();
        Cursor myDB = rdb.query("mytable", new String[]{"name"}, null, null, null, null, null);
        while (myDB.moveToNext())
            Log.d(TAG, "From db " + myDB.getString(0));

        //9
        final AndroidAudio audio = new AndroidAudio(this);
        final AndroidSound sound = audio.newSound("Dj Chris Parker - Typhoon.mp3");
        Button mPlay = (Button) findViewById(R.id.play_music);
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.play(sound);
            }
        });
        Button mStop = (Button) findViewById(R.id.stop_music);
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audio.stop(sound);
            }
        });

        final VideoView vView = (VideoView) findViewById(R.id.video_view);
        vView.setVideoURI(Uri.parse("android.resource://ru.oreluniver.domain.university/"+R.raw.rolik_yougid));

        Button vPlay = (Button) findViewById(R.id.play_video);
        vPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vView.requestFocus();
                vView.start();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        String coords = "";
        for (int i = 0; i < event.getPointerCount(); i++) {
            coords += "Pointer - " + (i+1) + " X " + event.getX(i) + " Y " + event.getY(i) + " ";
        }
        view.setText(coords);
        return true;
    }

    class SensorHandler implements SensorEventListener {
        float accelX;
        float accelY;
        float accelZ;
        public SensorHandler(Context context) {
            SensorManager manager = (SensorManager) context
                    .getSystemService(Context.SENSOR_SERVICE);
            if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
                Sensor accelerometer = manager.getSensorList(
                        Sensor.TYPE_ACCELEROMETER).get(0);
                manager.registerListener(this, accelerometer,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];
            Log.d(TAG, "Accel " + accelX + " " + accelY + " " + accelZ);
        }
        public float getAccelX() {
            return accelX;
        }
        public float getAccelY() {
            return accelY;
        }
        public float getAccelZ() {
            return accelZ;
        }
    }

    class AndroidFileIO {
        AssetManager assets;
        String externalStoragePath;
        public AndroidFileIO(AssetManager assets) {
            this.assets = assets;
            this.externalStoragePath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator;
        }

        public OutputStream writeFile(String fileName) throws IOException {
            return new FileOutputStream(externalStoragePath + fileName);
        }
    }

    class MyDBHelper extends SQLiteOpenHelper {

        MyDBHelper(Context context) {
            super(context, "MyDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable (id integer primary key autoincrement, name text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    class AndroidAudio {
        AssetManager manager;
        SoundPool pool;

        public AndroidAudio(Activity activity) {
            activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            this.manager = activity.getAssets();
            this.pool = new SoundPool(10, AudioManager.STREAM_MUSIC, 100);
        }


        public AndroidSound newSound(String filename) {
            try {
                AssetFileDescriptor assetDescriptor = manager.openFd(filename);
                int soundId = pool.load(assetDescriptor, 0);
                return new AndroidSound(pool, soundId);
            } catch (IOException e) {
                throw new RuntimeException("Невозможно загрузить звук '" +
                        filename + "'");
            }
        }

        public void play(AndroidSound sound) {
            sound.play(0.5f);
        }

        public void stop(AndroidSound sound) {
            sound.dispose();
        }
    }

    class AndroidSound {
        int soundId;
        SoundPool soundPool;

        public AndroidSound(SoundPool soundPool, int soundId) {
            this.soundId = soundId;
            this.soundPool = soundPool;
        }

        public void play(float volume) {
            soundPool.play(soundId, volume, volume, 0, 0, 1);
        }


        public void dispose() {
            soundPool.unload(soundId);
        }
    }
}
