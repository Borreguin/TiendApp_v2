package com.borreguin.tiendapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.borreguin.tiendapp.Class.Account;
import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;
import com.borreguin.tiendapp.Utilities.LinedEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static java.lang.Math.abs;

public class Act_Edit_Note extends AppCompatActivity {

    private TextView mTextMessage, clientName, txt_Deft;
    private Account clientAccount = new Account();
    private Client client;
    private Note note;

    // Controls for layout:
    LinedEditText txt_description;
    EditText edt_date;
    Button btn_plusNote, btn_minusNote, btn_deleteNote, btn_editNote;
    Intent NextPage;
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    // general variables:
    Global global = new Global();
    Context context;

    // variables for data:
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    // Alert advise:
    AlertDialog.Builder builder;


    // overwriting the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                global.goto_clientAccount(Act_Edit_Note.this,client);
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
        setContentView(R.layout.activity_act_edit_note);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // connecting with layout:
        context = Act_Edit_Note.this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        // connecting layout and code
        clientName = (TextView) findViewById(R.id.clientName);
        txt_Deft = (TextView) findViewById(R.id.txt_Deft);
        txt_description = (LinedEditText)findViewById(R.id.txt_notes);
        edt_date = (EditText)findViewById(R.id.edt_date);
        btn_plusNote = (Button) findViewById(R.id.btn_addDeft);
        btn_minusNote = (Button) findViewById(R.id.btn_lessDeft);
        btn_deleteNote = (Button) findViewById(R.id.btn_deleteNote);
        btn_editNote = (Button) findViewById(R.id.btn_editNote);

        // setting the date picker:
        // setting a date picker:
        edt_date= (EditText) findViewById(R.id.edt_date);
        edt_date.setText(global.formatter2.format(Calendar.getInstance().getTime()));

        btn_minusNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minus_value();
            }
        });
        btn_plusNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plus_value();
            }
        });
        btn_deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_alert();
            }
        });
        btn_editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNote();
            }
        });

        // Getting information from the last view
        try {
            Bundle bundle = getIntent().getExtras();
            int idNote = Integer.parseInt(bundle.getString("idNote"));
            client = db_client.getClient(bundle.getString("clientName"));
            note = db_account.getNote(client, idNote);

            // Populating the layout
            clientName.setText(client.getName());
            txt_Deft.setText(String.format("%.2f", note.getValue()));
            edt_date.setText(note.getDate_update_string_2());
            /*edt_date.setText(note.getDateUpdate());*/
            if(note.getDescription() == null){
                txt_description.setText(global.getlines(8));
            }else{
                txt_description.setText(note.getDescription());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

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
                new DatePickerDialog(Act_Edit_Note.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        super.onCreate(savedInstanceState);
    }

    // Function for updating time in date picker
    private void updateLabel() {
        SimpleDateFormat formatter = global.formatter2;
        edt_date.setText(formatter.format(calendar.getTime()));
    }

    public void plus_value(){
        float value = abs(global.parseStringToFloat(txt_Deft.getText().toString()));
        txt_Deft.setText(String.format("%.2f",value));
    }

    public void minus_value(){
        float value = -abs(global.parseStringToFloat(txt_Deft.getText().toString()));
        txt_Deft.setText(String.format("%.2f",value));
    }

    public void create_alert(){

        builder.setTitle(getString(R.string.delete_note) + "? :")
                .setMessage(getString(R.string.delete_note_dialog))
                .setPositiveButton(R.string.delete_note, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if( db_account.deleteNote(client,note.getIdNote())){
                            Toast.makeText(context, getString(R.string.delete_note) + " "
                                    + getString(R.string.Successfully),
                                    Toast.LENGTH_LONG).show();
                            global.goto_clientAccount(Act_Edit_Note.this,client);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void updateNote(){
        note.setValue(Float.valueOf(txt_Deft.getText().toString()));
        note.setDescription(txt_description.getText().toString());
        note.setDateUpdate(edt_date.getText().toString());
        db_account.updateNote(note);
        global.goto_clientAccount(Act_Edit_Note.this,client);
    }

    @Override
    protected void onStop() {
        db_client.close();
        db_account.close();
        super.onStop();
    }

}
