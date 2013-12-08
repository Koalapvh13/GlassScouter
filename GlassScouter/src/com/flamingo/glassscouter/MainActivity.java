package com.flamingo.glassscouter;

import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * Simple Scouter application for the hell of it. It's extremely rudimentary, just a random number generator with some generic DBZ-style Scouter screen layout.
 * A good extension of this would be using the Microphone allowing people having their "power level" read increased by screaming and having the Device pick up on the volume of the screaming.
 * I don't really have the time for that, so...
 * 
 * Use this code however you deem fit. I really don't care.
 * 
 * @author Chris Eggison @50hzgamer http://flamin.co
 *
 */
public class MainActivity extends Activity {

	private Handler handler;
	private TextView scouterOutput;
	private SurfaceView svCamera;
	private SurfaceHolder svHolder;
	
	private final int STEP_INTERVAL = 100;
	private final int MAX_POWER_LEVEL = 10000;
	
	private int powerLevel = 0;
	private int currentlyDisplayedPowerLevel = 0;
	private int powerLevelStep = 10;
	
	private boolean isScouting = false;
	
	private Camera camera;
	private final int cameraId = 0;
	
	private boolean tryExperimentalCamera = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        scouterOutput = ((TextView) findViewById(R.id.tvScouterOutput));
        scouterOutput.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isScouting) {
					performScouting();
					isScouting = true;
				}
			}
		});
        
        svCamera = (SurfaceView) findViewById(R.id.svCamera);
        svHolder = svCamera.getHolder();
    }
    
    public void onResume() {
    	super.onResume();
    	
    	if (tryExperimentalCamera) {
	    	try {
	        	camera = Camera.open(cameraId);
	        	camera.setPreviewDisplay(svHolder);
	        	camera.startPreview();
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
    	}
    }
    
    public void onPause() {
    	super.onPause();
    	if (tryExperimentalCamera) {
	    	try {
	    		camera.release();
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    }
    
    /**
     * Generates a random power level.
     */
    private void performScouting() {
    	currentlyDisplayedPowerLevel = 0;
    	powerLevel = (int) Math.ceil(Math.random() * MAX_POWER_LEVEL);
    	
    	if (powerLevel < 10) {
    		powerLevel = 10;
    	} else if (powerLevel < 100) {
    		powerLevelStep = 50;
    	} else {
    		powerLevelStep = powerLevel / 10;
    	}
    	
    	handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (currentlyDisplayedPowerLevel <= powerLevel) {
					currentlyDisplayedPowerLevel += powerLevelStep;
					if (currentlyDisplayedPowerLevel > powerLevel) {
						currentlyDisplayedPowerLevel = powerLevel;
					}
				}
				
				scouterOutput.setText(formatScouterOutput(currentlyDisplayedPowerLevel));
				
				if (currentlyDisplayedPowerLevel != powerLevel) {
					handler.postDelayed(this, STEP_INTERVAL);
					isScouting = false;
				} else {
					playSound(R.raw.scouter);
				}
			}
    	}, STEP_INTERVAL);
    }
    
    /**
     * Formats the input number so it has preceeding -'s if less than 100
     * @param input		Power level
     * @return			Formatted power level
     */
    private String formatScouterOutput(int input) {
    	String out = input+"";
    	
    	if (input < 100) {
    		out = "-" + input;
    	}
    	
    	if (input < 10) {
    		out = "--" + input;
    	}
    	
    	return out;
    }
    
    private void playSound(int resid) {
    	MediaPlayer mp = MediaPlayer.create(this, resid);
    	mp.start();
    }
    
}
