package de.swproj.teamchat.view.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.swproj.teamchat.R;
import de.swproj.teamchat.view.activities.ViewEventActivity;

public class ReasonDialog extends Dialog implements
        android.view.View.OnClickListener {


    public Activity a;

    private Button send, cancle;
    public EditText etReason;


    public ReasonDialog(Activity a){
        super(a);
        this.a=a;
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reason);
        setTitle("Enter Reason");


        etReason= findViewById(R.id.dialog_reason_etreason);
        send = findViewById(R.id.dialog_reason_btnsend);
        cancle =findViewById(R.id.dialog_reason_btncancle);

        cancle.setOnClickListener(this);
        send.setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_reason_btnsend:
                if(etReason.getText().toString().equals("")){
                    Context context = a.getApplicationContext();
                    CharSequence text = "Please enter reason";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                }else {
                    ViewEventActivity vea = (ViewEventActivity) a;
                    vea.cancleState(etReason.getText().toString());
                    dismiss();
                }
                break;
            case R.id.dialog_reason_btncancle:
                dismiss();
                break;
            default:
                break;
        }



    }
}

