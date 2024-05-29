package com.example.myapplication.UI;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ExcursionDetails extends AppCompatActivity {
    String name;

    String exDate;
    int excursionID;
    int vacaID;
    EditText editName;

    EditText editNote;
    TextView editDate;
    Repository repository;

    Excursion currentExcursion;

    int numExcursions;
    DatePickerDialog.OnDateSetListener startDate;
    final Calendar myCalendarStart = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        repository = new Repository(getApplication());
        excursionID = getIntent().getIntExtra("id", -1);
        name = getIntent().getStringExtra("name");
        vacaID = getIntent().getIntExtra("vacaID", -1);
        exDate = getIntent().getStringExtra("date");
        editName = findViewById(R.id.excursionname);
        editNote = findViewById(R.id.note);
        editDate = findViewById(R.id.date);
        editName.setText(name);


        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);


        Spinner spinner = findViewById(R.id.action_bar_spinner);
        ArrayList<Vacation> vacationArrayList = new ArrayList<>();
        vacationArrayList.addAll(repository.getmAllVacations());
        ArrayAdapter<Vacation> vacationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vacationArrayList);
        spinner.setAdapter(vacationAdapter);
        spinner.setSelection(0);

        editDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Date date;
                //get value from other screen,but I'm going to hard code it right now
                String info = editDate.getText().toString();
                if (info.equals("")) info = "05/23/24";
                try {
                    myCalendarStart.setTime(sdf.parse(info));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                new DatePickerDialog(ExcursionDetails.this, startDate, myCalendarStart
                        .get(Calendar.YEAR), myCalendarStart.get(Calendar.MONTH),
                        myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        startDate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, monthOfYear);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabelStart();
            }

        };


    }

    private void updateLabelStart() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        editDate.setText(sdf.format(myCalendarStart.getTime()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }

        if (item.getItemId() == R.id.excursionsave) {
            String excursionTitle = editName.getText().toString();
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (myDate != null) {
                Vacation associatedVacation = null;
                for (Vacation vacation : repository.getmAllVacations()) {
                    if (vacation.getVacationID() == vacaID) {
                        associatedVacation = vacation;
                        break;
                    }
                }
                if (associatedVacation != null) {
                    String vacationStartDate = associatedVacation.getStartDate();
                    String vacationEndDate = associatedVacation.getEndDate();

                    try {
                        Date startDate = sdf.parse(vacationStartDate);
                        Date endDate = sdf.parse(vacationEndDate);

                        if (myDate.after(startDate) && myDate.before(endDate)) {
                            Excursion excursion;
                            if (excursionID == -1) {
                                if (repository.getAllExcursions().isEmpty())
                                    excursionID = 1;

                                else
                                    excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                                excursion = new Excursion(excursionID, editName.getText().toString(), editDate.getText().toString(), vacaID);
                                repository.insert(excursion);
                            } else {
                                excursion = new Excursion(excursionID, editName.getText().toString(), editDate.getText().toString(), vacaID);
                                repository.update(excursion);
                            }
                        } else {
                            Toast.makeText(ExcursionDetails.this, "Excursion date must be within the vacation period", Toast.LENGTH_LONG).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ExcursionDetails.this, "Associated vacation not found", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ExcursionDetails.this, "Invalid date", Toast.LENGTH_LONG).show();
            }
            return true;
        }


        if (item.getItemId() == R.id.excursiondelete) {
            if (excursionID != -1) {
                Excursion excursionDelete = null;
                for (Excursion exc : repository.getAllExcursions()) {
                    if (exc.getExcursionID() == excursionID) {
                        excursionDelete = exc;
                        break;
                    }
                }
                if (excursionDelete != null) {
                    repository.delete(excursionDelete);
                    Toast.makeText(ExcursionDetails.this, "Excursion successfully deleted", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ExcursionDetails.this, "Excursion not found", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ExcursionDetails.this, "Invalid excursion ID", Toast.LENGTH_LONG).show();
            }
            return true;
        }


        if (item.getItemId() == R.id.notify) {
            String excursionTitle = editName.getText().toString();
            String dateFromScreen = editDate.getText().toString();
            String myFormat = "MM/dd/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            Date myDate = null;
            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (myDate != null) {
                Vacation associatedVacation = null;
                for (Vacation vacation : repository.getmAllVacations()) {
                    if (vacation.getVacationID() == vacaID) {
                        associatedVacation = vacation;
                        break;
                    }
                }

                if (associatedVacation != null) {
                    String vacationStartDate = associatedVacation.getStartDate();
                    String vacationEndDate = associatedVacation.getEndDate();

                    try {
                        Date startDate = sdf.parse(vacationStartDate);
                        Date endDate = sdf.parse(vacationEndDate);

                        if (myDate.after(startDate) && myDate.before(endDate)) {

                            Long trigger = myDate.getTime();
                            Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                            intent.putExtra("key", "Excursion: " + excursionTitle);
                            PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, ++MainActivity.numAlert, intent, PendingIntent.FLAG_IMMUTABLE);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
                            Toast.makeText(ExcursionDetails.this, "Notifications set for " + name, Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(ExcursionDetails.this, "Excursion date must be within the vacation period", Toast.LENGTH_LONG).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ExcursionDetails.this, "Associated vacation not found", Toast.LENGTH_LONG).show();
                }
            } else {
                    Toast.makeText(ExcursionDetails.this, "Invalid date", Toast.LENGTH_LONG).show();
                }
                    return true;
                }

                return super.onOptionsItemSelected(item);
            }
        }
















