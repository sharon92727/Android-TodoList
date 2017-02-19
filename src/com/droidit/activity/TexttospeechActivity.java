package com.droidit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.droidit.R;
import com.droidit.persistence.TodoProvider;
public class TexttospeechActivity extends Activity implements OnInitListener{
        /** Called when the activity is first created. */
        private int MY_DATA_CHECK_CODE = 0;

        private TextToSpeech tts;

        private EditText inputText;
        private Button speakButton;
        private TodoProvider provider;
        private static String view_task_title="";
        String text="You have a task to do";
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            Intent i=getIntent();
            Bundle bundle1=i.getExtras();
            view_task_title=bundle1.getString("title");
            //text=view_task_title;
           //long value=Long.parseLong(val);
           Log.v("value",view_task_title);
            Intent checkIntent = new Intent();
            checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

       }

       protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                        // success, create the TTS instance
                        tts = new TextToSpeech(this, this);

                }
                else {
                        // missing data, install it
                        Intent installIntent = new Intent();
                        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installIntent);
                }
         }
       }

        @Override
        public void onInit(int status) {               
          if (status == TextToSpeech.SUCCESS) {
                Toast.makeText(TexttospeechActivity.this, "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
                tts.speak(text, TextToSpeech.QUEUE_ADD, null);

                Intent i = new Intent(getBaseContext(), ViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title",view_task_title);
                i.putExtras(bundle);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

          }
          else if (status == TextToSpeech.ERROR) {
                Toast.makeText(TexttospeechActivity.this, "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
          }
        }
}