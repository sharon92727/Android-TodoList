package com.droidit.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.droidit.activity.SimpleTodoActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TodoProvider {
    private static final String DB_NAME = "tasks";
    private static final String TABLE_NAME = "tasks";
    private static final int DB_VERSION = 1;
    public static final String KEY_ID="id";
    public static final String KEY_TITLE="title";
    public static final String KEY_DESCRIPTION="description";
    public static final String KEY_PRIORITY="priority";
    public static final String KEY_TASKDATE="task_date";
    public static final String KEY_COMPLETED="completed";
    public static final String KEY_GROUP_NAME="group_name";
    public static final String KEY_LOCATION="location";
    private static final String GROUP_TABLE_NAME = "groups";


    private static final String DB_CREATE_GROUP = "CREATE TABLE " + GROUP_TABLE_NAME
            + " ( group_name text primary key, location text " + " );" ;


    private static final String DB_CREATE_QUERY = "CREATE TABLE " + TABLE_NAME
            + " ("+KEY_ID+" integer primary key autoincrement, "
            +KEY_TITLE+" text not null, "
            +KEY_DESCRIPTION+" text ,"
            +KEY_PRIORITY+" integer not null default 2,"
            +KEY_GROUP_NAME+" text not null, "
            +KEY_TASKDATE+" datetime,"
            +KEY_COMPLETED+" bool default false  );" ;

    private SQLiteDatabase storage;
    private SQLiteOpenHelper helper;

    public TodoProvider(Context ctx) {
        helper = new SQLiteOpenHelper(ctx, DB_NAME, null, DB_VERSION) {
            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion,
                                  int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE_NAME);
                onCreate(db);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                Log.v("TodoProvider","Query"+DB_CREATE_QUERY);
                db.execSQL(DB_CREATE_QUERY);
                db.execSQL(DB_CREATE_GROUP);
                Log.i("TodoProvider","Databases(tasks,groups) Created successfully");
            }
        };
        storage = helper.getReadableDatabase();

    }

    //Required to close the Db connection and avoid errors
    public void close() {
        if (helper != null) {
            helper.close();
        }
    }

  //Required for Edit task
    public List<String> finddetails(long id ) {
         id=id-1;
        Log.d(SimpleTodoActivity.APP_TAG, "findspecific triggered with ID----->"+id);
        List<String> tasks = new ArrayList<String>();

        try{
            String query = "SELECT * FROM "+TABLE_NAME+" WHERE id=" + id +";";

            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex(KEY_TITLE));
                    String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                    String priority=c.getString(c.getColumnIndex(KEY_PRIORITY));
                    String task_date=c.getString(c.getColumnIndex(KEY_TASKDATE));
                    String task_group=c.getString(c.getColumnIndex(KEY_GROUP_NAME));
                    tasks.add(title);
                    tasks.add(description);
                    tasks.add(priority);
                    tasks.add(task_group);
                    tasks.add(task_date);
                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return tasks;
    }

    public List<String> findAll() {
        Log.d(SimpleTodoActivity.APP_TAG, "findAll triggered");
        List<String> tasks = new ArrayList<String>();
        Cursor c = storage.query(TABLE_NAME, new String[] { KEY_TITLE,KEY_DESCRIPTION }, null,
                null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                tasks.add(c.getString(0));   //title
                //tasks.add(c.getString(1)); //description
                c.moveToNext();
            }
            c.close();
        }
        return tasks;
    }

    public List<String> findSpecific(String title ) {

        Log.d(SimpleTodoActivity.APP_TAG, "findspecific triggered with Task Title----->"+title);
        List<String> tasks = new ArrayList<String>();

        try{
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_TITLE+"='" + title +"';";

            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    title = c.getString(c.getColumnIndex(KEY_TITLE));
                    String description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                    String priority=c.getString(c.getColumnIndex(KEY_PRIORITY));
                    String task_date=c.getString(c.getColumnIndex(KEY_TASKDATE));
                    String task_status=c.getString(c.getColumnIndex(KEY_COMPLETED));
                    String group_name=c.getString(c.getColumnIndex(KEY_GROUP_NAME));
                    Log.v("ToDOProvider title       ---->",title);
                    Log.v("ToDOProvider description ---->",description);
                    Log.v("ToDOProvider priority    ---->",priority);
                    Log.v("ToDOProvider Task Date   ---->",task_date);
                    Log.v("ToDOProvider Task status ---->",task_status);
                    Log.v("ToDOProvider Task Group ---->",group_name);
                    priority=convert_int_priority_to_string(priority);
                    boolean status=Boolean.parseBoolean(task_status);
                    task_status= status ?"Yes":"No" ;
                    tasks.add(title);
                    tasks.add(description);
                    tasks.add("Priority  "+priority);
                    tasks.add("Task Date "+task_date);
                    tasks.add("Group :"+group_name);
                    tasks.add("Completed? "+task_status);

                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return tasks;
    }


    public List<String> getGroups() {


        Log.d(SimpleTodoActivity.APP_TAG, "getGroups triggered ");
        List<String> groups = new ArrayList<String>();

        try{
            String query = "SELECT * FROM "+GROUP_TABLE_NAME+";";

            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    String groupname = c.getString(c.getColumnIndex(KEY_GROUP_NAME));
                    Log.v("ToDOProvider Group name      ---->",groupname);
                    groups.add(groupname);

                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return groups;
    }

    private String convert_int_priority_to_string(String priority) {
        int val=Integer.parseInt(priority);
        switch(val)
        {
            case 1: return "Low";
            case 2: return "Normal";
            case 3: return "High";
        }
        return null;
    }

    public void addTask(String title, String description, int priority, String group_name, String datetime) {
        ContentValues data = new ContentValues();
        Log.v("TodoProvider","-------------Inside Add task----------");
        data.put(KEY_TITLE, title);
        data.put(KEY_DESCRIPTION, description);
        data.put(KEY_PRIORITY, priority);
        data.put(KEY_GROUP_NAME,group_name);
        data.put(KEY_TASKDATE, datetime);

        storage.insert(TABLE_NAME, null, data);
        Log.v("TodoProvider","-------------Task Added Successfully----------");
    }

    public void addGroup(String group_name,String location) {

        Log.v("TodoProvider","-------------Inside AddGroup function----------");
        ContentValues data = new ContentValues();
        data.put(KEY_GROUP_NAME, group_name);
        data.put(KEY_LOCATION, location);
        storage.insert(GROUP_TABLE_NAME, null, data);
        Log.v("TodoProvider","-------------Group Added Successfully----------");
    }

    public void deleteGroup(String txt_group_name) {
        storage.delete(GROUP_TABLE_NAME, "group_name='" + txt_group_name + "'", null);
    }

    public void deleteTask(String title) {
        storage.delete(TABLE_NAME, "title='" + title + "'", null);
    }

    //Code to Mark the Task as completed
    public void completeTask(String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_COMPLETED, "true");
        storage.update(TABLE_NAME, args, KEY_TITLE + "='" + title+"'", null);
    }

    //Code to Mark the Task as Not completed
    public void uncompleteTask(String title) {
        ContentValues args = new ContentValues();
        args.put(KEY_COMPLETED, "false");
        storage.update(TABLE_NAME, args, KEY_TITLE + "='" + title+"'", null);
    }

    public void deleteTask(long id) {
        storage.delete(TABLE_NAME, "id=" + id, null);
    }


    public List<String> findCompleted() {
        Log.d(SimpleTodoActivity.APP_TAG, "findCompleted Tasks triggered" );
        List<String> tasks = new ArrayList<String>();

        try{
            String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_COMPLETED+"='true';";
            Log.v("Todoprovider",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex(KEY_TITLE));
                    tasks.add(title);
                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<String> findUnCompleted() {

        List<String> tasks = new ArrayList<String>();

        try{
            String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_COMPLETED+"='false';";
            Log.v("Todoprovider",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex(KEY_TITLE));
                    tasks.add(title);
                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<String> findTodoByDate() {

        List<String> tasks = new ArrayList<String>();

        try{
            //select * from tasks where completed='false' order by task_date asc;
            String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_COMPLETED+"='false' " +
                    "order by "+KEY_TASKDATE+" asc ;";
            Log.v("Todoprovider",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex(KEY_TITLE));
                    tasks.add(title);
                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<String> findTodoByPriority() {
        Log.d(SimpleTodoActivity.APP_TAG, "findTodos by priority triggered" );
        List<String> tasks = new ArrayList<String>();

        try{
            String query = "SELECT * FROM "+TABLE_NAME+" WHERE "+KEY_COMPLETED+"='false' " +
                    "order by "+KEY_PRIORITY+" desc ;";
            Log.v("Todoprovider",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    String title = c.getString(c.getColumnIndex(KEY_TITLE));
                    tasks.add(title);
                }while(c.moveToNext());
            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tasks;
    }


    public void update_Task(Long id,String title,String description, int priority,String group,String datetime) {
        ContentValues data = new ContentValues();
        Log.v("TodoProvider","-------------Inside Add task----------");
        data.put("title", title);
        data.put("description", description);
        data.put("priority", priority);
        data.put("task_date", datetime);
        String query = "UPDATE "+TABLE_NAME+" SET title='"+title+"', description='"+description+"', " +
                " "+KEY_PRIORITY+"="+priority+
                ", "+KEY_GROUP_NAME+"='"+group+
                "', "+KEY_TASKDATE+"='"+datetime+
                "'  WHERE id=" + id +";";
        storage.execSQL(query);
        Log.v("TodoProvider","-------------Task updated Successfully----------");
    }

    public long getID(String edit_task_title) {
        long id=0;
        try{
            String query = "SELECT "+KEY_ID+" FROM "+TABLE_NAME+" WHERE "+KEY_TITLE+"='"+edit_task_title+"';";
            Log.v("Todoprovider",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){

                    id= Long.parseLong(c.getString(c.getColumnIndex(KEY_ID)));
                    Log.v("Todoprovider-GetId->method", id + "");

            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return id;  //To change body of created methods use File | Settings | File Templates.
    }

    //
    public String getLatestDate()
    {
        String date="";
        try{
            String query="SELECT * FROM "+TABLE_NAME+" where completed='false' " +
                    "order by "+KEY_TASKDATE+" asc;" ;
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{

                    String date1=c.getString(c.getColumnIndex(KEY_TASKDATE));
                    SimpleDateFormat formatter ;
                    formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date lasttime;
                    try {
                        lasttime = (Date) formatter.parse(date1);
                        long time = lasttime.getTime();
                        System.out.println(System.currentTimeMillis());
                        System.out.println(time);
                        long timeDiff = time-System.currentTimeMillis();
                         if(timeDiff<1)
                         {
                             //continue
                         }
                        else
                         {
                             //Now extract
                             date= c.getString(c.getColumnIndex(KEY_TASKDATE));
                             Log.v("Todoprovider-GetDate --->",date);
                             break;
                         }

                } catch (ParseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                    while(c.moveToNext());
            }
            c.close();
    }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return date;
    }

    public List<String> findTasksByLocation(float cur_latitude,float cur_longitude) {
        Log.d("TodoProvider", "Find Tasks by Location Triggered with GPS Latitude "+cur_latitude+"GPS Longitude"+cur_longitude );
        String groupname[]=new String[50];
        String location[]=new String[50];
        int i=0;
        List<String> tasks = new ArrayList<String>();

        try{
            String query = "SELECT * FROM "+GROUP_TABLE_NAME+"; ";
            Log.v("Retrieves all Groupnames and their location",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){
                do{
                    //Now extract
                    groupname[i] = c.getString(c.getColumnIndex(KEY_GROUP_NAME));
                    location[i]=c.getString(c.getColumnIndex(KEY_LOCATION));
                    i+=1;
                }while(c.moveToNext());
            }
            c.close();



                query = "SELECT * FROM "+TABLE_NAME+"; ";
                Log.v("Retrieves all tasks and now check their location",""+query);
                c = storage.rawQuery(query, null);
                if (c.moveToFirst()){
                    do{
                        float latitude_difference,longitude_difference;
                        float flt_latitude,flt_longitude;
                        //Get the title and Groupname from the tasks table
                        String title = c.getString(c.getColumnIndex(KEY_TITLE));
                        String col_groupname=c.getString(c.getColumnIndex(KEY_GROUP_NAME));
                        String col_location = "";
                        Log.v("Todoprovider","Totally "+(i)+" found");
                        for(int j=0;j<i;j++)
                        {
                             if(groupname[j].contains(col_groupname))
                             {
                                 Log.v("TodoProvider","Group matched"+col_groupname);
                                 //Group matched
                                 col_location=location[j];
                                 Log.v("TodoProvider","Location retrieved from table"+col_location);
                                 String[] location_array=col_location.split(" ");
                                 flt_latitude=Float.parseFloat(location_array[0]);
                                 flt_longitude=Float.parseFloat(location_array[1]);
                                 latitude_difference=cur_latitude-flt_latitude;
                                 longitude_difference=cur_longitude-flt_longitude;
                                 if( latitude_difference>-3 && latitude_difference<3 )
                                 {
                                     if(longitude_difference>-3 && longitude_difference<3 )
                                     {

                                         //Task matched the Location criteria
                                         Log.v("Todoprovider ","The Task matched the criteria "+title);
                                         tasks.add(title);
                                     }
                                 }

                             }
                        }

                    }while(c.moveToNext());
                }
                c.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return tasks;
    }

    public String getMYtitle(String sample) {

        String return_title="";
        try{
            String query = "SELECT "+KEY_TITLE+" FROM "+TABLE_NAME+" WHERE "+KEY_TASKDATE+"='"+sample+"';";
            Log.v("Todoprovider",""+query);
            Cursor c = storage.rawQuery(query, null);
            if (c.moveToFirst()){

                return_title= c.getString(c.getColumnIndex(KEY_TITLE));
                Log.v("Todoprovider-GetMYtitle-", return_title );

            }
            c.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return return_title;
    }
}