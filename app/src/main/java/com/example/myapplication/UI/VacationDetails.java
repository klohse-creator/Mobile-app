package com.example.myapplication.UI;

import static android.app.ProgressDialog.show;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.database.Repository;
import com.example.myapplication.entities.Excursion;
import com.example.myapplication.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VacationDetails extends AppCompatActivity {
    String name;

    String hotel;

    String startDate;

    String endDate;


    int vacationID;
    EditText editName;
    EditText editHotel;

    EditText editStartDate;

    EditText editEndDate;
    Repository repository;

    Vacation currentVacation;

    int numExcursions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);


        editName = findViewById(R.id.vacationname);
        editHotel = findViewById(R.id.hotel);
        editStartDate = findViewById(R.id.startdate);
        editEndDate = findViewById(R.id.enddate);
        name = getIntent().getStringExtra("name");
        hotel = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("Start Date");
        endDate = getIntent().getStringExtra("End Date");
        editName.setText(name);
        editHotel.setText(hotel);
        editStartDate.setText(startDate);
        editEndDate.setText(endDate);
        vacationID = getIntent().getIntExtra("id", -1);



        if(!isValidDateFormat(startDate) || !isValidDateFormat(endDate)) {
            Toast.makeText(getApplicationContext(), "Not a valid date Format", Toast.LENGTH_SHORT).show();
            return;

        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-YYYY");
        Date startDate = null;
        Date endDate = null;
        Vacation vacation;
        try {
            startDate = dateFormat.parse(getIntent().getStringExtra("Start Date"));
            endDate = dateFormat.parse(getIntent().getStringExtra("End Date"));

            if (startDate != null && endDate != null && startDate.before(endDate)) {
                Vacation updatedVacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(),
                        editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.updateVacation(updatedVacation);
                this.finish();
            } else {
                Toast.makeText(getApplicationContext(), "Invalid date selection", Toast.LENGTH_SHORT).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(VacationDetails.this, ExcursionDetails.class);
                intent.putExtra("vacaID", vacationID);
                startActivity(intent);
            }
        });
        RecyclerView recyclerView = findViewById(R.id.excursionrecyclerview);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        for (Excursion p : repository.getAllExcursions()) {
            if (p.getVacationID() == vacationID) filteredExcursions.add(p);
        }
        excursionAdapter.setExcursions(filteredExcursions);

    }

    private boolean isValidDateFormat(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }



    public boolean OnCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;
            if (vacationID == -1) {
                if (repository.getmAllVacations().size() == 0) vacationID = 1;
                else vacationID = repository.getmAllVacations().get(repository.getmAllVacations().size() - 1).getVacationID() + 1;
                vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.insert(vacation);
                this.finish();
            }
            else
            { vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), editStartDate.getText().toString(), editEndDate.getText().toString());
                repository.update(vacation);
                this.finish();
            }
        }

        if (item.getItemId()==R.id.vacationdelete) {
            for(Vacation vaca:repository.getmAllVacations()) {
                if(vaca.getVacationID()==vacationID)currentVacation=vaca;
            }
            numExcursions=0;
            for (Excursion excursion: repository.getAllExcursions()) {
                if(excursion.getVacationID()==vacationID)++numExcursions;
            }
            if(numExcursions==0){
                repository.delete(currentVacation);
                Toast.makeText(VacationDetails.this, currentVacation.getVacationName() + " was deleted",Toast.LENGTH_LONG).show();
                VacationDetails.this.finish();
            }
            else{
                Toast.makeText(VacationDetails.this, "Can't delete a Vacation with these Excursions",Toast.LENGTH_LONG).show();
            }

        }

        if (item.getItemId()==R.id.notifyvacation) {
            String startDateStr = editStartDate.getText().toString();
            String endDateStr = editEndDate.getText().toString();

           if(!isValidDateFormat(startDateStr) || !isValidDateFormat(endDateStr)) {
               Toast.makeText(getApplicationContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
               return true;
           }
           SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
            try {
                Date startDate = dateFormat.parse(startDateStr);
                Date endDate = dateFormat.parse(endDateStr);

                setAlarm(startDate, "Starting " + editName.getText().toString());
                setAlarm(endDate, "Ending " + editName.getText().toString());

                Toast.makeText(getApplicationContext(), "Alarms set successfully", Toast.LENGTH_SHORT).show();
            } catch (ParseException e) {
                e.printStackTrace();

            }
            }
        return super.onOptionsItemSelected(item);
    }


        private void setAlarm(Date date, String message) {
            try {
                long trigger = date.getTime();
                Intent intent = new Intent(VacationDetails.this, MyReceiver.class);
                intent.putExtra("key", message);
                PendingIntent sender = PendingIntent.getBroadcast(VacationDetails.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


