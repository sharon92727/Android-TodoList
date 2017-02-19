package com.droidit.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.droidit.R;
import com.droidit.persistence.TodoProvider;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ravikumar
 * Date: 9/26/12
 * Time: 1:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class ViewTodoDetailActivity extends Activity {
    public static final String APP_TAG = "com.droidit.android";
    private ListView taskView;
    private TodoProvider provider;
    public String received_id;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ui_view_todo_detail);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        provider = new TodoProvider(this);
        taskView = (ListView) findViewById(R.id.tasklist);
        //renderTodos();
        render_description();
    }

    private void render_description() {
        Intent i=getIntent();
        Bundle bundle1=i.getExtras();
        String val=bundle1.getString("title");
        Log.v("ViewTodoDetailActivity","Received from ViewActivity --->"+val);

        /*Long id_val=Long.parseLong(received_id);
        long val= id_val.longValue();*/

        List<String> beans = provider.findSpecific(val);
        if (!beans.isEmpty()) {
            Log.d(APP_TAG, String.format("%d beans found", beans.size()));
            // render the list
            taskView.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_expandable_list_item_1, beans
                    .toArray(new String[] {})));

            // dumb item deletion onclick
            taskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Log.d(APP_TAG, String.format(
                            "item with id: %d and position: %d", id, position));
                    //TextView v = (TextView) view;
                    //provider.deleteTask(v.getText().toString());
                    //renderTodos();


                }
            });
        } else {
            Log.d(APP_TAG, "no tasks found");
        }
    }
}
