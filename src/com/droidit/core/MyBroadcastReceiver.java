package com.droidit.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;
import com.droidit.activity.TexttospeechActivity;
public class MyBroadcastReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
      String s=intent.getStringExtra("testString");
      Toast.makeText(context, "Don't panic but your time is up!!!!."+ s, Toast.LENGTH_LONG).show();
      Intent i = new Intent(context, TexttospeechActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      Bundle bundle = new Bundle();
      bundle.putString("title",s);
      i.putExtras(bundle);
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);

      // Vibrate the mobile phone
      Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
      vibrator.vibrate(2000);
      //context.unregisterReceiver(this);
      //this.clearAbortBroadcast();
  }

} 