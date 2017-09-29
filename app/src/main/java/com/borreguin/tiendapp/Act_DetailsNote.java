package com.borreguin.tiendapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;
import com.borreguin.tiendapp.Utilities.LinedEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Math.abs;

public class Act_DetailsNote extends AppCompatActivity {

    Calendar calendar = Calendar.getInstance();
    Global global = new Global();
    EditText edt_date, putDebt, txt_notes;
    TextView clientName;
    DatePickerDialog.OnDateSetListener date;
    LinedEditText linedText;
    Button btn_plusNote, btn_minusNote;
    Client client;
    Intent NextPage;

    // Manage Databases:
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    // overwriting the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                gotoClientAccount(getWindow().getCurrentFocus(), client.getName());
                break;
        }
        return true;
    }

    // Navigation Button:
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return global.switch_BottomNavigationView(item.getItemId(),getCurrentFocus());
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Navigation button
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        clientName = (TextView) findViewById(R.id.clientName);
        putDebt = (EditText) findViewById(R.id.txt_enterDeft);
        linedText = (LinedEditText) findViewById(R.id.txt_notes);
        btn_plusNote = (Button) findViewById(R.id.btn_addDeft);
        btn_minusNote = (Button) findViewById(R.id.btn_lessDeft);

        // Getting information from the last view
        try {
            Bundle bundle = getIntent().getExtras();
            clientName.setText(bundle.getString("clientName"));
            putDebt.setText(bundle.getString("putDeft"));
            // loading data of temporal client
            client = db_client.getClient(bundle.getString("clientName"));
        }catch (Exception e){
            client = new Client(global.id_temp,global.prefix,"",0);
            e.printStackTrace();
        }

        // setting a date picker:
        edt_date= (EditText) findViewById(R.id.edt_date);
        edt_date.setText(global.formatter2.format(Calendar.getInstance().getTime()));
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(Act_DetailsNote.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        linedText.setText(global.getlines(10));

        // Adding functions to Buttons:
        btn_plusNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusNoteToClient();
                gotoClientAccount(v, client.getName().toString());
            }
        });
        btn_minusNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusNoteToClient();
                gotoClientAccount(v, client.getName().toString());
            }
        });

        super.onCreate(savedInstanceState);
    }

    // Function for updating time in date picker
    private void updateLabel() {
        SimpleDateFormat formatter = global.formatter2;
        edt_date.setText(formatter.format(calendar.getTime()));
    }

    // functions for buttons

    private void plusNoteToClient(){
        float value = abs(global.parseStringToFloat(putDebt.getText().toString()));
        try {
            db_account.addNote(new Note(value,
                            linedText.getText().toString(),
                            global.formatter2.parse(edt_date.getText().toString())
                            ), client);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private void minusNoteToClient(){
        float value = -abs(global.parseStringToFloat(putDebt.getText().toString()));
        try {
            db_account.addNote(new Note(value,
                    linedText.getText().toString(),
                    global.formatter2.parse(edt_date.getText().toString())
            ), client);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    public void gotoClientAccount(View v, String childText){

        // Pass information to the next view:
        NextPage = new Intent(Act_DetailsNote.this, Act_ClientAccount.class);

        //Create the bundle
        Bundle bundle = new Bundle();

        //Adding data to bundle
        bundle.putString("clientName",childText);

        //Add the bundle to the intent
        NextPage.putExtras(bundle);

        //Fire that second activity
        startActivity(NextPage);
    }


}
