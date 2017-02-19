package com.droidit.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.droidit.R;
import com.droidit.core.MyLocationListener;
import com.droidit.persistence.TodoProvider;

/**
 * Created with IntelliJ IDEA.
 * User: ravikumar
 * Date: 9/30/12
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroupActivity extends Activity {
    private EditText etNewGroup;
    private TodoProvider provider;
    private String location=null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_create_group);
        //Create objects for the textfields
        etNewGroup=(EditText) findViewById(R.id.editText1);
        provider = new TodoProvider(this);


        //create task button object
        Button btn_create=(Button)findViewById(R.id.btn_create_group);
        Button btn_set_location=(Button)findViewById(R.id.btn_set_location);
        Button btn_cancel=(Button)findViewById(R.id.btn_group_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
            }});

        btn_create.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                /*if(location==null)
                    location="92.43 34.23" ;
*/
                Log.d("GroupActivity", "add group click received");
                String group_name=etNewGroup.getText().toString();


                if(group_name.length()==0)
                {
                    etNewGroup.setError("Title cannot be left blank");
                }
                if(location.length()==0)
                {
                    Toast.makeText(GroupActivity.this,
                            "Location Must be set",
                            Toast.LENGTH_LONG).show();
                }
                else
                {


                    Log.v("GroupActivity Title----------->",group_name);
                    Log.v("GroupActivity location----------->",location);

                    //Call to Add a Task to database
                    provider.addGroup(group_name,location);

                    //Display user Notification using Toast + Image
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "A New Group Was Created Successfully!", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout toastView = (LinearLayout) toast.getView();
                    ImageView imageCodeProject = new ImageView(getApplicationContext());
                    imageCodeProject.setImageResource(R.drawable.todo);
                    toastView.addView(imageCodeProject, 0);
                    toast.show();

                    //Redirect to the MainActivity
                    Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                    startActivityForResult(myIntent, 0);
                }
            }
        });

        //Code to get the current location for the group
        btn_set_location.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {

                LocationManager mlocManager;
                LocationListener mlocListener;
                String latitude=null,longitude=null;
                mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                mlocListener = new MyLocationListener();

                if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER , 10, 1000, mlocListener);

                    Toast.makeText(GroupActivity.this,
                            "Wait. GPS Location tracking is in Progress",
                            Toast.LENGTH_LONG).show();

                    latitude=MyLocationListener.latitude+"" ;
                    longitude=MyLocationListener.longitude+"";

                    location=latitude+" "+longitude;

                    Toast.makeText(GroupActivity.this,
                            location,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(GroupActivity.this,
                            "GPS is not turned on",
                            Toast.LENGTH_LONG).show();

                }

            }
        });

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (provider != null) {
            provider.close();
        }

    }
}
