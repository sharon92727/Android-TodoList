package com.droidit.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.droidit.R;
import com.droidit.persistence.TodoProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


public class EdittaskActivity extends Activity {
	
	 public static final String APP_TAG = "com.droidit.android";
	    private TodoProvider provider;
	    public String received_id;
	    private String str_datetime="";
	    
	    private Button btUpdateTask;
	    private Button btCancelTask;
	    private Button btSetDate;
	    private Button btSetTime;
	    private EditText etNewTask,etNewDescription;
	    private RatingBar rbNewPriority;
        private Spinner spnGroup;
        private ArrayAdapter<String> spinner_adapter;
    Calendar myCalendar = Calendar.getInstance();
	    

	 @Override
	    public void onCreate(Bundle bundle) {
	        super.onCreate(bundle);
	        setContentView(R.layout.ui_edittask);
	        provider = new TodoProvider(this);
	        btUpdateTask = (Button) findViewById(R.id.btn_update_task);
	        btCancelTask=(Button)findViewById(R.id.btn_cancel);
	        btSetDate=(Button)findViewById(R.id.btn_set_date);
	        btSetTime=(Button)findViewById(R.id.btn_set_time);
	        etNewTask = (EditText) findViewById(R.id.editText1);
	        etNewDescription = (EditText) findViewById(R.id.editText2);
	        rbNewPriority=(RatingBar)findViewById(R.id.ratingBar1);
            spnGroup=(Spinner)findViewById(R.id.groups_spinner);
	        btUpdateTask.setOnClickListener(handleupdateTaskEvent);



         //Retrieve all the Groups from database and add to the spinner
         List<String> groups=provider.getGroups();
         if (!groups.isEmpty()) {
             spinner_adapter= new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groups );
             spinner_adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
             spnGroup.setAdapter(spinner_adapter);

         }


	        //Cancel Task
	        btCancelTask.setOnClickListener(new OnClickListener(){
	            public void onClick(View view){
	                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
	                startActivityForResult(myIntent, 0);
	            }
	        });
	        // Date action Listener
	        // Date Format :  yyyy-mm-dd
	        btSetDate.setOnClickListener(
	                new OnClickListener(){
	                    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
	                        public void onDateSet(DatePicker view, int year, int monthOfYear,
	                                              int dayOfMonth) {
	                            myCalendar.set(Calendar.YEAR, year);
	                            myCalendar.set(Calendar.MONTH, monthOfYear);
	                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	                            String formatted = format.format(myCalendar.getTime());
	                            str_datetime =formatted;
	                            Log.v("SimpleTodoActivity Date----->", formatted);
	                        }
	                    };

	                    @Override
	                    public void onClick(View v) {
	                        //Code to handle date time picker
	                        new DatePickerDialog(EdittaskActivity.this, d, myCalendar
	                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
	                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
	                    }
	        });

	        //Time action Listener
	        // Time Format :  hh:mm:ss
	        btSetTime.setOnClickListener(new OnClickListener() {

	            TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
	                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
	                    myCalendar.set(Calendar.MINUTE, minute);
	                    //updateLabel();
	                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
	                    String formatted = format.format(myCalendar.getTime());
	                    str_datetime +=" "+formatted;
	                    Log.v("SimpleTodoActivity time----->",hourOfDay+" "+minute+" ");
	                }
	            };

	            public  void onClick(View v) {
	                new TimePickerDialog(EdittaskActivity.this, t, myCalendar
	                        .get(Calendar.HOUR_OF_DAY), myCalendar
	                        .get(Calendar.MINUTE), true).show();
	            }
	        });

	        //To clear the textview after Error occurs
	        etNewTask.setOnFocusChangeListener(new View.OnFocusChangeListener() {
	            @Override
	            public void onFocusChange(View v, boolean hasFocus) {
	                if (hasFocus) {
	                    if (etNewTask.getText().toString().length() > 0) {
	                        //text_box.setText("");
	                        etNewTask.setError(null);
	                    }
	                }
	            }
	        });

         render_description();
	 }



	 private void render_description() {
	        Intent i=getIntent();
	        final Bundle bundle1=i.getExtras();
	        received_id=bundle1.getString("id");
	        Log.v("ViewTodoDetailActivity","Received from ViewActivity"+received_id);

	        Long id_val=Long.parseLong(received_id);
	        final long val= id_val.longValue();
	        List<String> beans = provider.finddetails(val+1);
	        etNewTask.setText(beans.get(0)); 
	        etNewDescription.setText(beans.get(1));
	        rbNewPriority.setRating(Integer.parseInt(beans.get(2)));
         //

         //Set the alredy selected group's spinner position
            String mystring=beans.get(3);
            Log.v("Group title",mystring);
            int spinnerPosition = spinner_adapter.getPosition(mystring);
            //set the default according to value
            spnGroup.setSelection(spinnerPosition);
	        
	    }

	 
	 
     //Actual -> private OnClickListener handleNewTaskEvent = new OnClickListener()
	    private OnClickListener handleupdateTaskEvent = new OnClickListener() {
	        @Override
	        public void onClick(View view) {
	            Log.d(APP_TAG, "add task click received");
	            String title=etNewTask.getText().toString();
	            String description=etNewDescription.getText().toString();
	            int priority=(int)(rbNewPriority.getRating());
                String group_name=spnGroup.getSelectedItem().toString();

	            if(title.length()==0)
	            {
	                etNewTask.setError("Title cannot be left blank");
	            }
	            else
	            {
	                //Call to Add a Task to database
	                Long id_val=Long.parseLong(received_id);
	    	        final long val= id_val.longValue();
	                provider.update_Task(val,title,description,priority,group_name,str_datetime);

	                //Display user Notification using Toast + Image
	                Toast toast = Toast.makeText(getApplicationContext(),
	                        "The Task Updated Successfully!", Toast.LENGTH_LONG);
	                toast.setGravity(Gravity.CENTER, 0, 0);
	                LinearLayout toastView = (LinearLayout) toast.getView();
	                ImageView imageCodeProject = new ImageView(getApplicationContext());
	                imageCodeProject.setImageResource(R.drawable.todo);
	                toastView.addView(imageCodeProject, 0);
	                toast.show();

	                //Redirect to the MainActivity
	                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
	                startActivityForResult(myIntent, 0);
                    finish();
	            }

	        }
	    };

	 
	 
}
