package local.hal.st42.android.todo75039;


import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    static final int MODE_INSERT = 1;
    static final int MODE_EDIT = 2;
    private ListView _lvTaskList;
    private DatabaseHelper _helper;
    private int _task_flg = 2;  // flag == sql.done => done = 0 / done = 1
    private Map<Integer,String> map_flag = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _lvTaskList = findViewById(R.id.lvTaskList);
        _lvTaskList.setOnItemClickListener(new ListItemClickListener());

        _helper = new DatabaseHelper(getApplicationContext());
    }

    @Override
    protected void onResume(){
        super.onResume();
        SQLiteDatabase db = _helper.getWritableDatabase();
        Cursor cursor = DataAccess.findAll(db, _task_flg);
        TextView _tvTaskStatus = findViewById(R.id.tvTaskStatus);

        map_flag.put(2, getString(R.string.menu_task_all));
        map_flag.put(1, getString(R.string.menu_task_complete));
        map_flag.put(0, getString(R.string.menu_task_incomplete));

        _tvTaskStatus.setText(map_flag.get(_task_flg));

        String[] from = {"name", "deadline", "done", "deadline"};
        int[] to = {R.id.lvTaskName, R.id.lvDeadline, R.id.cbDoneCheck, R.id.lvDatePass};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.row, cursor, from, to, 0);
        adapter.setViewBinder(new CustomViewBinder());
        _lvTaskList.setAdapter(adapter);
    }

    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            String strDeadline = cursor.getString(cursor.getColumnIndex("deadline"));
            String tmpDeadline = strDeadline.replace("年", "").replace("月", "").replace("日", "");
            int intDeadline = Integer.parseInt(tmpDeadline);
            int intToday = Integer.parseInt(getToday());

            switch(view.getId()){
                case R.id.lvDatePass:
                    TextView lvDatePass = (TextView) view;

                    if (intDeadline == intToday) {
                        lvDatePass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_watch_later_blue_24dp, 0, 0, 0);
                    } else if (intDeadline > intToday) {
                        lvDatePass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_today_green_24dp, 0, 0, 0);
                    } else {
                        lvDatePass.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_update_red_24dp, 0, 0, 0);
                    }
                    return true;

                case R.id.lvDeadline:
                    TextView lvDeadline = (TextView) view;
                    if (intDeadline == intToday) {
                        strDeadline = "期限: 今日";
                        lvDeadline.setTextColor(Color.parseColor("#003CFF"));
                    }
                    lvDeadline.setText(strDeadline);
                    return true;

                case R.id.cbDoneCheck:
                    long id = cursor.getLong(cursor.getColumnIndex("_id"));
                    CheckBox cbDoneCheck = (CheckBox) view;

                    boolean isChecked = (cursor.getInt(columnIndex) == 1);

                    LayoutBackgroundColorChanger(cbDoneCheck, isChecked);

                    cbDoneCheck.setChecked(isChecked);
                    cbDoneCheck.setTag(id);
                    cbDoneCheck.setOnClickListener(new OnCheckBoxClickListener());
                    return true;
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return true;
    }

    public void onAddButtonClick(MenuItem item){
        Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
        intent.putExtra("mode", MODE_INSERT);
        startActivity(intent);
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            Cursor item = (Cursor) parent.getItemAtPosition(position);
            int itemId = item.getColumnIndex("_id");
            long idNo = item.getLong(itemId);

            Intent intent = new Intent(getApplicationContext(), ToDoEditActivity.class);
            intent.putExtra("mode", MODE_EDIT);
            intent.putExtra("idNo", idNo);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.menuTaskAll:
                _task_flg = 2;
                break;
            case R.id.menuTaskComplete:
                _task_flg = 1;
                 break;
            case R.id.menuTaskIncomplete:
                _task_flg = 0;
                break;
        }

        onResume();

        return super.onOptionsItemSelected(item);
    }

    private String getToday() {
        Date date = new Date();
        SimpleDateFormat today = new SimpleDateFormat("yyyyMMdd");
        return today.format(date);
    }

    private class OnCheckBoxClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            CheckBox cbDoneCheck = (CheckBox) view;

            boolean isChecked = cbDoneCheck.isChecked();

            LayoutBackgroundColorChanger(cbDoneCheck, isChecked);

            long id = (Long) cbDoneCheck.getTag();
            SQLiteDatabase db = _helper.getWritableDatabase();
            DataAccess.updateCheckboxChecked(db, id, isChecked);
        }
    }

    private void LayoutBackgroundColorChanger(CheckBox checkbox, boolean isChecked) {
        LinearLayout llBackgroundColor = (LinearLayout) checkbox.getParent();

        if (isChecked) {
            llBackgroundColor.setBackgroundColor(getResources().getColor(R.color.rowDark));
        }
        else {
            llBackgroundColor.setBackgroundColor(getResources().getColor(R.color.rowPrimary));
        }
    }
}