package com.example.mode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		String key = LocationManager.KEY_PROXIMITY_ENTERING;
		Boolean entering = intent.getBooleanExtra(key, false);
		//AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		
		if(entering){
			//Toast.makeText(context, "목표지점에 접근하는 중...", Toast.LENGTH_LONG).show();
			//audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
		else {
			//Toast.makeText(context, "목표지점에서 벗어나는 중...", Toast.LENGTH_LONG).show();
		}
	}
}