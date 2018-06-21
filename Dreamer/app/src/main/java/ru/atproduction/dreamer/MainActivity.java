package ru.atproduction.dreamer;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,LoaderManager.LoaderCallbacks<Cursor> {
//    TextView currentDateTime;
//    Calendar dateAndTime = Calendar.getInstance();
    Spinner spin;
  //  Button btn;
    private AlarmReciever alarm;
    EditText etName;
    private static final int CM_DELETE_ID = 1;
    SimpleCursorAdapter userAdapter;
    DB db;
    android.support.v4.widget.SimpleCursorAdapter scAdapter;
    ListView lvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        btn = (Button) findViewById(R.id.timeButton);
//        btn.setEnabled(false);
        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);




        // проверяем, первый ли раз открывается программа
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {

            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("hasVisited", true);
            ed.putInt("kod1",0);
            ed.putInt("kod2",0);
            ed.commit();
        }








        alarm = new AlarmReciever();

        spin = (Spinner) findViewById(R.id.spinner);

        spin.setSelection(0);
        spin.setOnItemSelectedListener(this);



//        setInitialDateTime();


// открываем подключение к БД
        db = new DB(this);
        db.open();

        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_ID, DB.COLUMN_NAME, DB.COLUMN_TXT};
        int[] to = new int[]{R.id.tvID, R.id.tvName, R.id.tvTime};

        // создаем адаптер и настраиваем список
        scAdapter = new android.support.v4.widget.SimpleCursorAdapter(this, R.layout.item, null, from, to, 0);
        lvData = (ListView) findViewById(R.id.listView);
        lvData.setAdapter(scAdapter);


        // добавляем контекстное меню к списку
        registerForContextMenu(lvData);

        // создаем лоадер для чтения данных
        getSupportLoaderManager().initLoader(0, null, this);


    }

//    private void setInitialDateTime() {
//        currentDateTime.setText(DateUtils.formatDateTime(this,
//                dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
//
//    }


//    public void setTime(View v) {
//        new TimePickerDialog(MainActivity.this, t,
//                dateAndTime.get(Calendar.HOUR_OF_DAY),
//                dateAndTime.get(Calendar.MINUTE), true)
//                .show();
//
//    }
//
//    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//            dateAndTime.set(Calendar.MINUTE, minute);
//            setInitialDateTime();
//        }
//    };



    public void startRepeatingTimer(int id,int a,String nm) {
        Context context = this.getApplicationContext();
        if (alarm != null) {
            alarm.SetAlarm(context, a,nm,id);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelRepeatingTimer(int id) {
        Context context = this.getApplicationContext();
        if (alarm != null) {
            alarm.CancelAlarm(context,id);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void BtnCLick(View view) {
        etName = (EditText) findViewById(R.id.etName);
        String name = etName.getText().toString();



            if(name!="") {
                int timeT = spin.getSelectedItemPosition();


                String tm = "every 15 min";
                switch (timeT) {
                    case 0:
                        tm = "every 15 min";
                        break;
                    case 1:
                        tm = "every 30 min";
                        break;
                    case 2:
                        tm = "every hour";
                        break;
                    case 3:
                        tm = "every 4 hours";
                        break;
                    case 4:
                        tm = "once in 3 days";
                        break;
                }
                int id = (int) db.addRec(tm, name);
                Toast toast2 = Toast.makeText(getApplicationContext(),
                        " "+name, Toast.LENGTH_SHORT);
                toast2.show();
                startRepeatingTimer(id, timeT, name);
                // получаем новый курсор с данными
                getSupportLoaderManager().getLoader(0).forceLoad();

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Alarm has been set up", Toast.LENGTH_SHORT);
                toast.show();
                etName.setText(null);
            }
            else{
                Toast toast2 = Toast.makeText(getApplicationContext(),
                        "Write a name", Toast.LENGTH_SHORT);
                toast2.show();
            }





    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.delete_record);
    }

    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД

            // получаем новый курсор с данными
            TextView tv = (TextView) findViewById(R.id.tvName);
            String nm = tv.getText().toString();

            int ids = db.getIdRow(nm);
            if(ids == -1){
                Toast toast = Toast.makeText(MainActivity.this, "Ошибка ", Toast.LENGTH_LONG);
                toast.show();
            }


            db.delRec(acmi.id);
            getSupportLoaderManager().getLoader(0).forceLoad();
            cancelRepeatingTimer(ids);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items

        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        return true;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    static class MyCursorLoader extends CursorLoader {

        DB db;

        public MyCursorLoader(Context context, DB db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            Cursor cursor = db.getAllData();
            try {
                TimeUnit.SECONDS.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return cursor;
        }
    }


}