package com.droidit.activity;

/**
 * Created with IntelliJ IDEA.
 * User: ravikumar
 * Date: 9/24/12
 * Time: 8:49 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.droidit.R;
import com.droidit.core.MyLocationListener;
import com.droidit.persistence.TodoProvider;

import java.util.List;





public class ViewActivity extends Activity {



    public static final String APP_TAG = "com.droidit.android";
    private ListView taskView;
    private TodoProvider provider;
    private String MENU_COMPLETED="Completed";
    private String MENU_UNCOMPLETED="Not Completed";
    private String MENU_EDIT_TASK="Edit Task";
    private String MENU_DELETE_TASK="Delete Task";



    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_view);
        provider = new TodoProvider(this);
        taskView = (ListView) findViewById(R.id.tasklist);
        registerForContextMenu(taskView);
        //renderTodos();
        render_uncompleted_tasks();

    }


    /*
     *   Context Menu to be displayed on LongPress
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == taskView.getId()) {
            //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            //Address address = (Address) taskView().getItemAtPosition(info.position);
            menu.setHeaderTitle("Task Operations");
            menu.add(0, taskView.getId(), 0, MENU_COMPLETED);
            menu.add(1, taskView.getId(), 0, MENU_UNCOMPLETED);
            menu.add(2, taskView.getId(), 0, MENU_EDIT_TASK);
            menu.add(3, taskView.getId(), 0, MENU_DELETE_TASK);
        }
    }


    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        /*SubMenu sub_priority=menu.addSubMenu(2, Menu.FIRST, Menu.NONE, "View by Priority");
        sub_priority.add(2, 3, Menu.NONE, "High");
        sub_priority.add(2, 2, Menu.NONE, "Normal");
        sub_priority.add(2, 1, Menu.NONE, "Low");
        menuInflater.inflate(R.layout.menu, sub_priority);*/
        return true;
    }


    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            // Single menu item is selected do something
            // Ex: launching new activity/screen or show alert message
            case R.id.menu_sort_by_date:
                Toast.makeText(ViewActivity.this, "Sort by Date", Toast.LENGTH_SHORT).show();
                render_sort_by_date();
                return true;

            case R.id.menu_completed_tasks:
                //Toast.makeText(ViewActivity.this, "Completed Task", Toast.LENGTH_SHORT).show();
                render_completed_tasks();
                return true;

            case R.id.menu_sort_by_priority:
                Toast.makeText(ViewActivity.this, "Sort by Priority", Toast.LENGTH_SHORT).show();
                render_sort_by_priority();
                return true;

            case R.id.menu_show_tasks:
                //Toast.makeText(ViewActivity.this, "Pending Tasks", Toast.LENGTH_SHORT).show();
                render_uncompleted_tasks();
                return true;

            case R.id.menu_show_all_tasks:
                //Retrieve High priority tasks
                Log.v("ViewActivity","Show All tasks Request received");
                renderTodos();
                return true;

            case R.id.menu_find_tasks_by_location:
                //Retrieve tasks by location
                Log.v("ViewActivity","Retrieve tasks by location");
                renderbyLocation();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void renderbyLocation() {
        //Get the Current Location
        LocationManager mlocManager;
        LocationListener mlocListener;
        String latitude,longitude,location = "";
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||mlocManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            mlocManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER , 10, 1000, mlocListener);
            latitude=MyLocationListener.latitude+"" ;
            longitude=MyLocationListener.longitude+"";
            location=latitude+" "+longitude;
        }
        float flt_latitude,flt_longitude;
        String[] location_array=location.split(" ");
        flt_latitude=Float.parseFloat(location_array[0]);
        flt_longitude=Float.parseFloat(location_array[1]);
        //Dummy data to be set
        //flt_latitude=90.43f;
        //flt_longitude=34.1f;

        List<String> beans = provider.findTasksByLocation(flt_latitude,flt_longitude);

        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, beans
                    .toArray(new String[beans.size()])));


            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    String view_task_title=taskView.getItemAtPosition(position).toString();
                    Intent i = new Intent(getBaseContext(), ViewTodoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",view_task_title+"");
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
            });
        } else {
            //Log.d(APP_TAG, "No Completed tasks found");
            Toast.makeText(ViewActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void render_sort_by_priority() {
        List<String> beans = provider.findTodoByPriority();
        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, beans
                    .toArray(new String[beans.size()])));


            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    String view_task_title=taskView.getItemAtPosition(position).toString();
                    Intent i = new Intent(getBaseContext(), ViewTodoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",view_task_title+"");
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
            });
        } else {
            //Log.d(APP_TAG, "No Completed tasks found");
            Toast.makeText(ViewActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void render_sort_by_date() {

        List<String> beans = provider.findTodoByDate();
        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, beans
                    .toArray(new String[beans.size()])));


            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    String view_task_title=taskView.getItemAtPosition(position).toString();
                    Intent i = new Intent(getBaseContext(), ViewTodoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",view_task_title+"");
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
            });
        } else {
            //Log.d(APP_TAG, "No Completed tasks found");
            Toast.makeText(ViewActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }

    }

    private void render_uncompleted_tasks() {
        List<String> beans = provider.findUnCompleted();
        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, beans
                    .toArray(new String[beans.size()])));


            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    String view_task_title=taskView.getItemAtPosition(position).toString();
                    Intent i = new Intent(getBaseContext(), ViewTodoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",view_task_title+"");
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);

                }
            });
        } else {
            //Log.d(APP_TAG, "No Completed tasks found");
            Toast.makeText(ViewActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void render_completed_tasks() {

        List<String> beans = provider.findCompleted();
        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, beans
                    .toArray(new String[beans.size()])));


            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    String view_task_title=taskView.getItemAtPosition(position).toString();
                    Intent i = new Intent(getBaseContext(), ViewTodoDetailActivity.class);
                    //Create the bundle
                    Bundle bundle = new Bundle();
                    //Add your data to bundle
                    // ID should let you select from a database or however you are pulling data
                    // any other data you need to pass on to your view here.
                    bundle.putString("title",view_task_title+"");
                    //Add the bundle to the intent
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);


                }
            });
        } else {
            Log.d(APP_TAG, "No Completed tasks found");
            Toast.makeText(ViewActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }


    //Context Menu selection
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Log.v("ViewActivity----->","Position --> "+menuInfo.position);


        if (item.getTitle() == MENU_DELETE_TASK) {
            //Code To Handle deletion
            Log.v("ViewActivity--->","User Pressed Delete Task");
            //TodoProvider atodo=(TodoProvider)taskView.getAdapter().getItem(menuInfo.position);
            Log.v("ViewActivity----->","Position "+menuInfo.position);

            //code to remove the task
            String del_task_title=taskView.getItemAtPosition(menuInfo.position).toString();
            Log.v("Task to delete ",""+del_task_title);
            provider.deleteTask(del_task_title);

            //Refresh the display
            renderTodos();
            Log.v("ViewActivity--->","Task was deleted successfully");
        }
        else if (item.getTitle() == MENU_COMPLETED) {
                //Code To Handle the task is completed
                Log.v("ViewActivity--->","User Pressed Complete Task Option");

                //code to update the task info
                String complete_task_title=taskView.getItemAtPosition(menuInfo.position).toString();
                Log.v("Task to delete ",""+complete_task_title);
                provider.completeTask(complete_task_title);
                renderTodos();
                Log.v("ViewActivity--->","Task was Completed");
            }
        else if (item.getTitle() == MENU_UNCOMPLETED) {
            //Code To Handle the task is completed
            Log.v("ViewActivity--->","User Pressed Not Completed Task Option");

            //code to update the task info
            String uncomplete_task_title=taskView.getItemAtPosition(menuInfo.position).toString();
            Log.v("Task to delete ",""+uncomplete_task_title);
            provider.uncompleteTask(uncomplete_task_title);
            renderTodos();
            Log.v("ViewActivity--->","Task was Completed");
        }
        else if(item.getTitle() == MENU_EDIT_TASK)
        {
            //Pass the Id of the Task and invoke Edittaskactivity
            String edit_task_title=taskView.getItemAtPosition(menuInfo.position).toString();
            Log.v("Task to Edit ",""+edit_task_title);
            long taskid=provider.getID(edit_task_title);
            Intent i = new Intent(getBaseContext(), EdittaskActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id",taskid+"");
            //bundle.putString("title",edit_task_title+"");
            i.putExtras(bundle);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        }
        else {
            return false;
        }
        return true;
    }


    /**
     * renders the task list
     */
    private void renderTodos() {
        List<String> beans = provider.findAll();
        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, beans
                    .toArray(new String[beans.size()])));

            // dumb item deletion onclick
            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    String view_task_title=taskView.getItemAtPosition(position).toString();
                    Intent i = new Intent(getBaseContext(), ViewTodoDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title",view_task_title+"");
                    i.putExtras(bundle);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    startActivity(i);


                }
            });
        } else {
            Log.d(APP_TAG, "no tasks found");
            Toast.makeText(ViewActivity.this, "Nothing Found", Toast.LENGTH_SHORT).show();
        }
    }



}
