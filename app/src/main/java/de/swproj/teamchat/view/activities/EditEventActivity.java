package de.swproj.teamchat.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import de.swproj.teamchat.connection.database.DBStatements;
import de.swproj.teamchat.connection.firebase.FirebaseConnection;
import de.swproj.teamchat.R;
import de.swproj.teamchat.datamodell.chat.Event;
import de.swproj.teamchat.datamodell.chat.FirebaseActions;
import de.swproj.teamchat.datamodell.chat.FirebaseTypes;
import de.swproj.teamchat.helper.FormatHelper;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class EditEventActivity extends AppCompatActivity {

    private Event event;
    private String msgId;
    private String chatID;
    private GregorianCalendar cal;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private int selectedHour;
    private int selectedMinute;


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

        // Connect the Layout Components
        tv_selectDate = (TextView) findViewById(R.id.edit_event_tv_select_date);
        tv_selectTime = (TextView) findViewById(R.id.edit_event_tv_select_time);
        et_title = (EditText) findViewById(R.id.edit_event_et_title);
        et_description = (EditText) findViewById(R.id.edit_event_et_description);

        // get the own intent of the Activity
        Intent ownIntent = getIntent();
        msgId = ownIntent.getStringExtra("ID");
        chatID = ownIntent.getStringExtra("chatID");

        if (!msgId.equals("0")) {  // If it`s not a new event
            event = DBStatements.getEvent(msgId);
            et_title.setText(event.getMessage());
            et_title.setEnabled(false);
            et_description.setText(event.getDescription());
            cal = event.getDate();

        } else {
            // Initialize the Calendar
            cal = (GregorianCalendar) GregorianCalendar.getInstance();
        }

        tv_selectDate.setText(FormatHelper.formatDate(cal));
        tv_selectTime.setText(FormatHelper.formatTime(cal));

        // Initialize the int values for the time and date
        selectedYear = cal.get(Calendar.YEAR);
        selectedMonth = cal.get(Calendar.MONTH);
        selectedDay = cal.get(Calendar.DAY_OF_MONTH);
        selectedHour = cal.get(Calendar.HOUR_OF_DAY);
        selectedMinute = cal.get(Calendar.MINUTE);

        // Call the Listener Method to intitalize them
        dateTimeOnClickListener();
    }

    /*
     * Private Method to set all Listener on the Date, Time and the Listener on the Dialogs
     */
    private void dateTimeOnClickListener() {
        // Listener for the Date TextView
        tv_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create the DatePicker Dialog
                DatePickerDialog dateDialog = new DatePickerDialog(
                        EditEventActivity.this,
                        android.R.style.Theme_DeviceDefault_Dialog_MinWidth,
                        dateSetListener,
                        selectedYear, selectedMonth, selectedDay);

                dateDialog.show();
            }
        });

        // Listener for the DatePickerDialog
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "." + (month+1) + "." + year;
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
                // Create the TimePicker Dialog
                TimePickerDialog timeDialog = new TimePickerDialog(
                        EditEventActivity.this,
                        timeSetListener,
                        selectedHour, selectedMinute, true);

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
                if (hourOfDay < 10)
                    hour = "0" + hourOfDay;
                else
                    hour = "" + hourOfDay;
                if (minute < 10)
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

    public void onClickSaveChanges(View view) {
        GregorianCalendar date = new GregorianCalendar(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute);
        if (msgId.equals("0")) {
            // New created event, automatically status = 0
            int status = 0;
            try {
                // Check if event date is in future
                if (checkDateFuture(date)) {


                    event = new Event(GregorianCalendar.getInstance().getTime(),
                            et_title.getText().toString(), msgId, true, FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            date, et_description.getText().toString(), chatID, status);
                    //Push Event to Firebase
                    firebaseConnection.addToFirestore(event, FirebaseTypes.Message.getValue(), FirebaseActions.ADD.getValue());

                    finish();

                } else {
                    // send Toast as Info, that Time has to be in Future
                    Toast infoToast = Toast.makeText(getApplicationContext(), R.string.datefuture, Toast.LENGTH_SHORT);
                    infoToast.show();
                }

            } catch (NullPointerException npe) {

                npe.printStackTrace();
            }
        } else {
            // Update of a existing Event
            event.setDescription(et_description.getText().toString());
            event.setDate(date);

            firebaseConnection.addToFirestore(event,
                    FirebaseTypes.Message.getValue(), FirebaseActions.UPDATE.getValue());
            finish();
        }

    }

    public void onClickCancel(View view) {
        // Just go back to the previous Activity, safe nothing
        finish();
    }

    // Private Helper Method to check, if Date is in Future -> no Event creating in the past
    private boolean checkDateFuture(GregorianCalendar eventDate) {
        return GregorianCalendar.getInstance().compareTo(eventDate) < 0;
    }
}
