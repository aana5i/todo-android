package local.hal.st42.android.todo75039;

import android.app.DatePickerDialog;
import android.app.FragmentManager;

import java.util.Date;
import java.text.SimpleDateFormat;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.TextView;

public class ToDoEditActivity extends AppCompatActivity {

    private int _mode = MainActivity.MODE_INSERT;
    private long _idNo = 0;
    private DatabaseHelper _helper;
    private String deadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_edit);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        _helper = new DatabaseHelper(getApplicationContext());

        Intent intent = getIntent();
        _mode = intent.getIntExtra("mode", MainActivity.MODE_INSERT);

        if(_mode == MainActivity.MODE_INSERT){
            deadline = getToday();
            TextView tvTaskEdit = findViewById(R.id.tvTaskEdit);
            tvTaskEdit.setText(R.string.tv_title_register);
            TextView deadlineDay = findViewById(R.id.tvDeadlineDay);
            deadlineDay.setText(deadline);
        } else {
            TextView tvTaskEdit = findViewById(R.id.tvTaskEdit);
            tvTaskEdit.setText(R.string.tv_title_edit);

            _idNo = intent.getLongExtra("idNo", 0);
            SQLiteDatabase db = _helper.getWritableDatabase();
            Task taskData = DataAccess.findByPK(db, _idNo);

            EditText edTaskName = findViewById(R.id.edTaskName);
            edTaskName.setText(taskData.getName());

            TextView tvDeadlineDay = findViewById(R.id.tvDeadlineDay);
            tvDeadlineDay.setText(taskData.getDeadline());

            Switch swTaskDone = findViewById(R.id.swTaskDone);
            if(taskData.getDone() == 1){
                swTaskDone.setChecked(true);
            }

            EditText edTaskText = findViewById(R.id.edTaskText);
            edTaskText.setText(taskData.getNote());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(_mode == MainActivity.MODE_INSERT){
            inflater.inflate(R.menu.menu_options_regist, menu);
        }else{
            inflater.inflate(R.menu.menu_options_edit, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onSaveButtonClick(MenuItem item){
        EditText edTaskName = findViewById(R.id.edTaskName);
        String taskName = edTaskName.getText().toString();
        if(taskName.equals("")){
            Toast.makeText(ToDoEditActivity.this, R.string.text_name,Toast.LENGTH_SHORT).show();
        }else{
            EditText edTaskText = findViewById(R.id.edTaskText);
            String taskDetail = edTaskText.getText().toString();
            SQLiteDatabase db = _helper.getWritableDatabase();
            if(_mode == MainActivity.MODE_INSERT){
                DataAccess.insert(db, taskName, deadline, taskDetail);
            }else{
                long done;
                Switch swTaskDone = findViewById(R.id.swTaskDone);
                TextView deadlineDay = findViewById(R.id.tvDeadlineDay);
                deadline = deadlineDay.getText().toString();
                if(swTaskDone.isChecked()){
                    done = 1;
                }else{
                    done = 0;
                }
                DataAccess.update(db, _idNo, taskName, deadline, done, taskDetail);
            }
            finish();
        }
    }

    public void onDeleteButtonClick(MenuItem item){
        FullDialogFragment dialog = new FullDialogFragment();
        FragmentManager manager = getFragmentManager();
        dialog.show(manager, "FullDialogFragment");
    }

    public void showDatePickerDialog(View view){
        TextView tvDeadlineDay = findViewById(R.id.tvDeadlineDay);
        String dateMsg = tvDeadlineDay.getText().toString();

        String[] dateParts = dateMsg.split("年|月|日");

        DatePickerDialog dialog = new DatePickerDialog(ToDoEditActivity.this, new DatePickerDialogOnDateSetListener(), Integer.parseInt(dateParts[0]),
                Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[2]));
        dialog.show();
    }

    private class DatePickerDialogOnDateSetListener implements DatePickerDialog.OnDateSetListener{
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
            String strMonth = convertDate(month, 1);
            String strDay = convertDate(dayOfMonth, 0);

            deadline = year + "年" + strMonth + "月" + strDay + "日";
            TextView deadlineDay = findViewById(R.id.tvDeadlineDay);
            deadlineDay.setText(deadline);
        }
    }

    private String convertDate(int value, int flg) {
        String strResult;
        if (flg == 1) { value = value + 1; }
        if (value < 10){
            strResult = "0" + value;
        } else{
            strResult = String.valueOf(value);
        }
        return strResult;
    }

    private String getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    @Override
    protected void onDestroy() {
        _helper.close();
        super.onDestroy();
    }

    public void  listDelete(){
        SQLiteDatabase db = _helper.getWritableDatabase();
        DataAccess.delete(db, _idNo);
        finish();
    }
}
