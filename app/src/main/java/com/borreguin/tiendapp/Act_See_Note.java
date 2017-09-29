package com.borreguin.tiendapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;
import com.borreguin.tiendapp.Utilities.LinedEditText;

public class Act_See_Note extends AppCompatActivity {

    Global global = new Global();
    Client client;
    Note note;
    TextView clientName, txt_Deft, edt_date;
    LinedEditText txt_description;

    // variables for data:
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    // overwriting the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goto_client_all_Account(Act_See_Note.this,client);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_note);
        // Navigation button
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // connecting layout and code
        clientName = (TextView) findViewById(R.id.clientName);
        txt_Deft = (TextView) findViewById(R.id.txt_Deft);
        txt_description = (LinedEditText)findViewById(R.id.txt_notes);
        edt_date = (TextView) findViewById(R.id.edt_date);


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

    }

    public void goto_client_all_Account(Context context, Client client){
        // Pass information to the next view:
        Intent NextPage = new Intent(context, Act_Details_all_accounts.class);
        //Add the bundle to the intent
        NextPage.putExtras(BundleWithInfo(client));
        //Fire that second activity
        context.startActivity(NextPage);
    }

    public Bundle BundleWithInfo(Client client){
        //Create the bundle
        Bundle bundle = new Bundle();
        //Adding data to bundle
        bundle.putString("clientName", client.getName());
        return bundle;
    }
}
