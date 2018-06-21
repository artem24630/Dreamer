package ru.atproduction.dreamer;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    static int kod = 1;
    SharedPreferences sPref;
    static int kod2 = 0;
    private Calendar dateAndTime1;
    private Calendar dateAndTime2;
    Switch switcher;
    Switch switcher2;
    long tm;
    TextClock tcl;
    TextClock tcl2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        if(savedInstanceState!=null){
//            dateAndTime1.setTimeInMillis(savedInstanceState.getLong("dateAndTime11"));
//            dateAndTime2.setTimeInMillis(savedInstanceState.getLong("dateAndTime22"));
//            SetTime(1);
//            SetTime(2);
//        }

         switcher = (Switch) findViewById(R.id.switch1);
         switcher2 = (Switch) findViewById(R.id.switch2);

        switcher.setOnCheckedChangeListener(this);
        switcher2.setOnCheckedChangeListener(this);
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);

        int dsa = sPref.getInt("kod1",0);
        if(dsa == 1){
            switcher.setChecked(true);

        }
        dsa = sPref.getInt("kod2",0);
        if(dsa == 1){

            tm = sPref.getLong("dateAndTime2", 0L);
            dateAndTime2 = Calendar.getInstance();
            dateAndTime2.setTimeInMillis(tm);

            tm = sPref.getLong("dateAndTime1", 0L);
            dateAndTime1 = Calendar.getInstance();
            dateAndTime1.setTimeInMillis(tm);
            switcher2.setChecked(true);

        }else{

            dateAndTime1 = Calendar.getInstance();
            dateAndTime2 = Calendar.getInstance();
            dateAndTime1.setTimeInMillis(System.currentTimeMillis());

            dateAndTime2.setTimeInMillis(System.currentTimeMillis());

        }




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
       switch(buttonView.getId()) {

        case R.id.switch2:
            if(isChecked){


                TextView tv4 = (TextView) findViewById(R.id.textView4);

                tv4.setVisibility(View.VISIBLE);
                tv4 = findViewById(R.id.textView5);
                tv4.setVisibility(View.VISIBLE);
                 tcl = (TextClock) findViewById(R.id.textClock2);
                tcl.setVisibility(View.VISIBLE);
                tcl2 = findViewById(R.id.textClock3);
                tcl2.setVisibility(View.VISIBLE);
                SetTime(1);
                SetTime(2);
                }
                else{

                TextView tv4 = (TextView) findViewById(R.id.textView4);

                tv4.setVisibility(View.INVISIBLE);
                tv4 = findViewById(R.id.textView5);
                tv4.setVisibility(View.INVISIBLE);
                tcl = (TextClock) findViewById(R.id.textClock2);
                tcl.setVisibility(View.INVISIBLE);
                tcl2 = findViewById(R.id.textClock3);
                tcl2.setVisibility(View.INVISIBLE);
            }
                break;
       }
    }




    public void SetTime(int i){
        if(i==1){
            tcl.setText(DateUtils.formatDateTime(this,
                    dateAndTime1.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }
        else if(i==2){
            tcl2.setText(DateUtils.formatDateTime(this,
                    dateAndTime2.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }
    }

    public void SetClock2(View v){

            new TimePickerDialog(this, t,
                    dateAndTime1.get(Calendar.HOUR_OF_DAY),
                    dateAndTime1.get(Calendar.MINUTE), true)
                    .show();




    }
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime1.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime1.set(Calendar.MINUTE, minute);
            SetTime(1);
            saveDate(1);
        }
    };

    public void SetClock3(View v){
        new TimePickerDialog(this, t2,
                dateAndTime2.get(Calendar.HOUR_OF_DAY),
                dateAndTime2.get(Calendar.MINUTE), true)
                .show();

    }
    TimePickerDialog.OnTimeSetListener t2 = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime2.set(Calendar.MINUTE, minute);
            SetTime(2);
            saveDate(2);

        }
    };

    void saveDate(int i) {
        sPref = getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putLong("dateAndTime"+i,i==1?dateAndTime1.getTimeInMillis():dateAndTime2.getTimeInMillis());
        ed.commit();

    }
    void saveKod(int id,int n){
        sPref = getSharedPreferences("MyPref",MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt("kod"+id,n);
        ed.commit();
//id: 1 - switcher, 2 - switcher2
//n: 0 - not checked,1 - checked
    }

//    void loadDate(){
//        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
//        sad = sPref.getLong("dateAndTime2", 0L);
//        dateAndTime2 = Calendar.getInstance();
//        dateAndTime2.setTimeInMillis(sad);
//
//        sad = sPref.getLong("dateAndTime1", 0L);
//        dateAndTime1 = Calendar.getInstance();
//        dateAndTime1.setTimeInMillis(sad);
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saving();
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void saving() {
        if(switcher.isChecked())
            saveKod(1,1);
        else saveKod(1,0);

        if(switcher2.isChecked()) {
            saveKod(2, 1);
            saveDate(1);
            saveDate(2);
        }else
            saveKod(2,0);

//        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }
}
