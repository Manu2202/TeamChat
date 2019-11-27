package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class EditEventActivity extends AppCompatActivity {

    private Event event;
    private String msgId;
    private String chatID;
    private Calendar cal;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMinute;
    private DBStatements dbStatements;

    private TextView tv_selectDate;
    private TextView tv_selectTime;
    private EditText et_title;
    private EditText et_description;

    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TimePickerDialog.OnTimeSetListener timeSetListener;
    private FirebaseConnection firebaseConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        // Connect Firebase
        firebaseConnection = new FirebaseConnection();

        // Initialize Local Database Statements
        dbStatements = new DBStatements(EditEventActivity.this);

        // Connect the Layout Components
        tv_selectDate = (TextView)findViewById(R.id.edit_event_tv_select_date);
        tv_selectTime = (TextView)findViewById(R.id.edit_event_tv_select_time);
        et_title = (EditText)findViewById(R.id.edit_event_et_title);
        et_description = (EditText)findViewById(R.id.edit_event_et_description);

        // Call the Listener Method to intitalize them
        dateTimeOnClickListener();

        // get the own intent of the Activity
        Intent ownIntent = getIntent();
        msgId = ownIntent.getStringExtra("ID");
        chatID = ownIntent.getStringExtra("chatID");
    }

    /*
     * Private Method to set all Listener on the Date, Time and the Listener on the Dialogs
     */
    private void dateTimeOnClickListener(){
        // Initialize the Calendar
        cal = Calendar.getInstance();

        // Listener for the Date TextView
        tv_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initialize the Calendar
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                // Create the DatePicker Dialog
                DatePickerDialog dateDialog = new DatePickerDialog(
                        EditEventActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dateDialog.show();
            }
        });

        // Listener for the DatePickerDialog
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;  // Month starts at '0'
                String date = dayOfMonth + "." + month + "." + year;
                tv_selectDate.setText(date);

                // Write the selected Date in the variables
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
            }
        };

        // Listener for the Time TextView
        tv_selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                // Create the TimePicker Dialog
                TimePickerDialog timeDialog = new TimePickerDialog(
                        EditEventActivity.this,
                        timeSetListener,
                        hour, minute, true);
                timeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                timeDialog.show();
            }
        });

        // Listener for the TimePickerDialog
        timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hour;
                String min;
                // Format the hour and min
                if(hourOfDay < 10)
                    hour = "0" + hourOfDay;
                else
                    hour = "" + hourOfDay;
                if(minute < 10)
                    min = "0" + minute;
                else
                    min = "" + minute;

                String time = hour + ":" + min;
                tv_selectTime.setText(time);

                // Write the time in the class variables
                selectedHour = hourOfDay;
                selectedMinute = minute;
            }
        };
    }



    public void onClickSaveChanges(View view){
        if (msgId.equals("0")){

            // Own created Event -> User automatically accepted
            Byte status = 2;
            try {
                GregorianCalendar date = new GregorianCalendar(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
                event = new Event(new Time(System.currentTimeMillis()),
                        et_title.getText().toString(), msgId, true, "dummyUser",
                        date, et_description.getText().toString(), chatID, status);

                HashMap<String, Object> eventMap = convertToMap(event);

                event.setId(firebaseConnection.addToFirestore("message", eventMap));

                finishActivity(0);

            }catch(NullPointerException npe){
                // TODO: Info ausgeben, dass Werte nicht eingetragen sind z.B. Toast
            }
        }else{
            // TODO: Update eines existierenden Events
        }
    }

    public void onClickCancel(View view){
        // Just go back to the previous Activity, safe nothing
        finishActivity(1);
    }

    private HashMap<String, Object> convertToMap(Event event){
        HashMap<String, Object> eventMap = new HashMap<>();
        eventMap.put("Timestamp", event.getTimeStamp());
        eventMap.put("Titel", event.getMessage());
        eventMap.put("MessageID", event.getId());
        eventMap.put("IsEvent", event.isEvent());
        eventMap.put("CreatorID", event.getCreator());
        eventMap.put("Date", event.getDate());
        eventMap.put("Description", event.getDescription());
        eventMap.put("ChatID", event.getChatid());
        eventMap.put("Status", event.getStatus());

        return eventMap;
    }
}
