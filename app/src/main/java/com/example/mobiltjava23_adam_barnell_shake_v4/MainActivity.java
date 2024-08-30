package com.example.mobiltjava23_adam_barnell_shake_v4;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private ProgressBar progressBarX;
    private SeekBar seekBarY;
    private ImageView imageViewZ;
    private TextView textView;
    private ProgressBar progressBar;
    private Switch switchZ;

    private final float[] gravity = new float[3];
    private final float[] linear_acceleration = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarX = findViewById(R.id.progressBarX);
        seekBarY = findViewById(R.id.seekBarY);
        imageViewZ = findViewById(R.id.imageViewZ);
        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);
        switchZ = findViewById(R.id.switchZ);
        textView.setText("lumen");

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.5f;
        float dangerSensor = 0.4f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            progressBarX.setProgress((int) ((linear_acceleration[0] + 1) * 5));
            seekBarY.setProgress((int) ((linear_acceleration[1] + 1) * 5));
            progressBar.setProgress((int) ((linear_acceleration[2] + 1) * 5));
            imageRotation(linear_acceleration[2]);


            if (linear_acceleration[2] > dangerSensor) {
                switchZ.setChecked(true);
            } else {
                switchZ.setChecked(false);
            }

            Log.i("X-axis", String.valueOf(linear_acceleration[0]));
            Log.i("Y-axis", String.valueOf(linear_acceleration[1]));
            Log.i("Z-axis", String.valueOf(linear_acceleration[2]));
            if (linear_acceleration[0] > dangerSensor) {
                Toast.makeText(this, "Danger X-axis", Toast.LENGTH_SHORT).show();
                Log.i("adam", "Danger X-axis ");
            } else if (linear_acceleration[1] > dangerSensor) {
                Toast.makeText(this, "Danger Y-axis", Toast.LENGTH_SHORT).show();
                Log.i("adam", "Danger Y-axis ");
            } else if (linear_acceleration[2] > dangerSensor) {
                Toast.makeText(this, "Danger Z-axis", Toast.LENGTH_SHORT).show();
                Log.i("adam", "Danger Z-axis");
            }
        } else if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            adjustColor(lightLevel);
        }
    }
    // multiplied so it makes more movement
    private void imageRotation(float z) {
        imageViewZ.setRotation(z * 10);
    }
    //Changes the color of the text lumen when switching the brightness level in the room
    private void adjustColor(float lightLevel) {
        if (lightLevel < 10) {
            textView.setTextColor(Color.RED);
            textView.setText("Dark");
        } else if (lightLevel < 1000) {
            textView.setTextColor(Color.GRAY);
            textView.setText("Medium light");
        } else {
            textView.setTextColor(Color.GREEN);
            textView.setText("Very bright");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
