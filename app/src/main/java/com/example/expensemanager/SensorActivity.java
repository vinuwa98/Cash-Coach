package com.example.expensemanager;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TextView temperatureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Initialize TextView to display temperature
        temperatureTextView = findViewById(R.id.temperatureTextView);

        // Initialize SensorManager to access device sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Get the ambient temperature sensor from the sensor manager
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        // Check if ambient temperature sensor is available on the device
        if (temperatureSensor == null) {
            temperatureTextView.setText("Ambient Temperature Sensor not available!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register sensor listener when activity is resumed
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister sensor listener when activity is paused to save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // This method is called when sensor values change
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            // Retrieve ambient temperature from the sensor event
            float ambientTemperature = event.values[0];

            // Update the TextView to display the ambient temperature in Celsius
            temperatureTextView.setText("Ambient Temperature: " + ambientTemperature + " °C");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // This method is called when sensor accuracy changes (rarely used for ambient temperature)
        // You can handle sensor accuracy changes here if needed
    }
}
