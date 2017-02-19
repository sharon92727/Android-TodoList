package com.droidit.activity;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.droidit.R;
import com.droidit.core.MyBroadcastReceiver;
import com.droidit.persistence.TodoProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SimpleTodoActivity extends Activity {
    public static final String APP_TAG = "com.droidit.android";
    private String str_datetime ="";
    private ListView taskView;
    private Button btNewTask;
    private Button btCancelTask;
    private Button btSetDate;
    private Spinner spnGroup;
    private EditText etNewTask,etNewDescription;
    private RatingBar rbNewPriority;
    private TodoProvider provider;
    Calendar myCalendar = Calendar.getInstance();
    //DateFormat fmtDateAndTime = DateFormat.getDateTimeInstance();

    /*
      * (non-Javadoc)
      *
      * @see android.app.Activity#onCreate(android.os.Bundle)
      */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_create);
        provider = new TodoProvider(this);
        btNewTask = (Button) findViewById(R.id.btn_new_task);
        btCancelTask=(Button)findViewById(R.id.btn_cancel);
        btSetDate=(Button)findViewById(R.id.btn_set_date);
        Button btSetTime = (Button) findViewById(R.id.btn_set_time);
        etNewTask = (EditText) findViewById(R.id.editText1);
        etNewDescription = (EditText) findViewById(R.id.editText2);
        rbNewPriority=(RatingBar)findViewById(R.id.ratingBar1);
        spnGroup=(Spinner)findViewById(R.id.groups_spinner);
        btNewTask.setOnClickListener(handleNewTaskEvent);

        //Retrieve all the Groups from database and add to the spinner
       List<String> groups=provider.getGroups();
        if (!groups.isEmpty()) {


            ArrayAdapter<String> aa = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,groups );
            aa.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
            spnGroup.setAdapter(aa);

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
                        new DatePickerDialog(SimpleTodoActivity.this, d, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
        });

        //Time action Listener
        // Time Format :  hh:mm:ss
        btSetTime.setOnClickListener(new View.OnClickListener() {

            TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    myCalendar.set(Calendar.MINUTE, minute);
                    //updateLabel();
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    String formatted = format.format(myCalendar.getTime());
                    str_datetime += " " + formatted;
                    Log.v("SimpleTodoActivity time----->", hourOfDay + " " + minute + " ");
                }
            };

            public void onClick(View v) {
                new TimePickerDialog(SimpleTodoActivity.this, t, myCalendar
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

    }

        //Actual -> private OnClickListener handleNewTaskEvent = new OnClickListener()
    private OnClickListener handleNewTaskEvent = new OnClickListener() {
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
                Log.v("SimpleTodoActivity Title----------->",title);
                Log.v("SimpleTodoActivity Description----------->",description);
                Log.v("SimpleTodoActivity Priority----------->",priority+"");
                Log.v("SimpleTodoActivity Task Date----------->", str_datetime);
                Log.v("SimpleTodoActivity Task Group----------->", group_name);
                //Call to Add a Task to database
                provider.addTask(title,description,priority,group_name,str_datetime);
                //####################################################################
                /*long when = str_datetime.getTime();
                String contentTitle = title;
                String contentText = description;
                AlarmManager mgr = (AlarmManager) view.getContext().getSystemService(Context.ALARM_SERVICE);
                Intent notificationIntent = new Intent(view.getContext(), ReminderAlarm.class);
                notificationIntent.putExtra("Name",contentTitle );
                notificationIntent.putExtra("Description",contentText );
                notificationIntent.putExtra("NotifyCount",notificationCount );
                PendingIntent pi = PendingIntent.getBroadcast(mContext, notificationCount, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                mgr.set(AlarmManager.RTC_WAKEUP,when, pi);
                Toast.makeText(mContext, "Your Reminder Activated", Toast.LENGTH_LONG).show();
                contentTitle = "";
                contentText = "";
                descEdit.setText("");
                nameEdit.setText("");*/
                //####################################################################
                //Display user Notification using Toast + Image
                Toast toast = Toast.makeText(getApplicationContext(),
                        "The Task Was added Successfully!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                LinearLayout toastView = (LinearLayout) toast.getView();
                ImageView imageCodeProject = new ImageView(getApplicationContext());
                imageCodeProject.setImageResource(R.drawable.todo);
                toastView.addView(imageCodeProject, 0);
                toast.show();

                //notification alerts
                //String sample="2012-10-09 19:10:46";
                String sample=provider.getLatestDate();
                System.out.println(sample);
                  SimpleDateFormat formatter ;
                //String sample="2012-10-10 00:05:00";
                formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date lasttime;
                try {
                    lasttime = (Date) formatter.parse(sample);
                    long time = lasttime.getTime();
                    System.out.println(System.currentTimeMillis());
                    System.out.println(time);
                    long timeDiff = time-System.currentTimeMillis();

                    Toast.makeText(view.getContext(), "Notification set in " + timeDiff/1000 + " seconds", Toast.LENGTH_LONG).show();
                   //System.out.println(timeDiff+" "+(timeDiff/1000) + "seconds");
                    Intent intent = new Intent(view.getContext(), MyBroadcastReceiver.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    String task_title=provider.getMYtitle(sample);
                    intent.putExtra("testString", task_title);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(view.getContext().getApplicationContext(), 234324243, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+ timeDiff, pendingIntent);


                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                //Redirect to the MainActivity
                Intent myIntent = new Intent(view.getContext(), MainActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        }
    };

}