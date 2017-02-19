package com.droidit.activity;

/**
 * Created with IntelliJ IDEA.
 * User: ravikumar
 * Date: 9/24/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.droidit.R;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        //create task button object
        Button btn_create=(Button)findViewById(R.id.btn_create);
        btn_create.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent myIntent = new Intent(view.getContext(), SimpleTodoActivity.class);
                startActivityForResult(myIntent, 0);
            }
        });

        //View Tasks
        Button btn_view=(Button)findViewById(R.id.btn_view_tasks);
        btn_view.setOnClickListener(new OnClickListener(){
            public void onClick(View view){
                Intent myIntent = new Intent(view.getContext(), ViewActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });

        //Create a New Group
        Button btn_group=(Button)findViewById(R.id.btn_create_group);
        btn_group.setOnClickListener(new OnClickListener(){
            public void onClick(View view){
                Intent myIntent = new Intent(view.getContext(), GroupActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }
        });

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            final String intentAction = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
                //Log.w(TAG, "Main Activity is not the root. Finishing Main Activity instead of launching.");
                finish();
                return;
            }

    }
  }
}