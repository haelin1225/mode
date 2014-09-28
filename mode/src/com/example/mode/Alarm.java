package com.example.mode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		//String mode = null;
		String mode = intent.getStringExtra("sound");
		Log.e("XXXX","mode : "+ mode);
		
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(mode.toString().equals("���Ҹ�")) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			Log.e("XXXX","Change RingerMode Normal");
			Toast.makeText(context, "���Ҹ�", Toast.LENGTH_LONG).show();
			mode = null;
		}
		else if(mode.toString().equals("����")) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			Log.e("XXXX","Change RingerMode Vibrate");
			Toast.makeText(context, "����", Toast.LENGTH_LONG).show();
			mode = null;
		}
		else if(mode.toString().equals("����")){
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Log.e("XXXX","Change RingerMode Silent");
			Toast.makeText(context, "����", Toast.LENGTH_LONG).show();
			mode = null;
		}
	}

}