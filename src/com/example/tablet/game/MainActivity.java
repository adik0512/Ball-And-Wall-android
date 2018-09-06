package com.example.tablet.game;


import android.os.Bundle;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class MainActivity extends Activity implements SensorEventListener{
    

    

	
	
	GameView mGameView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // getActionBar().hide();
        mGameView = new GameView(this);
        setContentView(mGameView);
        
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    	
    }
    
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
    
    
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO W tym miejscu automatycznie generowany szkielet 
		
	}
	/*
	 * dwie tablice przechowuja wartosci
	* motoda obiektu klasy SensorManager odczytuje polozenie urzadzenia

	 * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
	 */

	@Override
	public void onSensorChanged(SensorEvent event) {

	//	if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			float R[] = new float[9];
		float orientation[] = new float[3];
		SensorManager.getOrientation(R, orientation);
	//	for(int i=0; i<9; i++){
	//		orientation[i]=event.values[i];
	//	}
		
		
		
		mGameView.processOrientationEvent(orientation);
		
	//	Log.d("azymut",Float.toString(orientation[0]));
		//Log.d("pochylenie",Float.toString(orientation[1]));
		//Log.d("przechylenie",Float.toString(orientation[2]));
		
	// 	}

	}
	
	
    
	
	
	

}
