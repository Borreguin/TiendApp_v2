package com.borreguin.tiendapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import com.borreguin.tiendapp.Class.Account;
import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;
import com.borreguin.tiendapp.Utilities.NoteAdapter;

import static java.lang.Math.abs;

public class Act_ClientAccount extends AppCompatActivity {

    private Account clientAccount = new Account();

    // variables for data:
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    //local variables:
    GridView grid;
    TextView clientName, description, debtTotal;
    EditText putDebt;
    Button btn_plusNote, btn_minusNote, btn_addDetails, btn_pastDetails;
    View view;
    Client client;
    Intent NextPage;

    // global variables and functions
    Global global = new Global();

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

        setContentView(R.layout.activity_act_client_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNewAccount();
            }
        });
        // Navigation button
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // connecting members of activity layout
        clientName =(TextView) findViewById(R.id.clientName);
        description = (TextView) findViewById(R.id.description);
        debtTotal = (TextView)findViewById(R.id.txt_total);
        putDebt = (EditText) findViewById(R.id.txt_enterDeft);
        btn_plusNote = (Button) findViewById(R.id.btn_addDeft);
        btn_minusNote = (Button) findViewById(R.id.btn_lessDeft);
        btn_addDetails = (Button) findViewById(R.id.btn_addDetails);
        btn_pastDetails = (Button) findViewById(R.id.btn_pastDetails);
        grid=(GridView)findViewById(R.id.grid_item_values);

        // setting functions for each button
        btn_plusNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusNoteToClient();
            }
        });
        btn_minusNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusNoteToClient();
            }
        });

        // loading data of the client:
        if(LoadClient()) //if the user is not loaded then scape
        {
            update_grid();
            btn_addDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoNoteDetails(v);
                }
            });
        }

        // adding a click listener to each child
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(Act_ClientAccount.this, "Ingresando a "
                                + clientAccount.getNote(position).getValue(),
                        Toast.LENGTH_LONG).show();
                gotoEditNote(position);
            }
        });

        btn_pastDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDetails_all_accounts();
            }
        });

        // test creates some notes:
        // db_account.addNote(new Note(0,12.3f,"Test 1"), client);
        // db_account.addNote(new Note(1,2.3f,"Test 2"), client);
    }

    private Boolean LoadClient(){

        // loading data from the last view
        try {
            Bundle bundle = getIntent().getExtras();
            client = db_client.getClient(bundle.getString("clientName"));
            clientName.setText(client.getName());
            description.setText(client.getDescription());
            debtTotal.setText(String.format("%.2f",clientAccount.getTotal()));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void plusNoteToClient(){
        float value = abs(global.parseStringToFloat(putDebt.getText().toString()));
        if(value > 0.00f){
            db_account.addNote(new Note(value), client);
            putDebt.setText("");
            update_grid();
        }
    }
    private void minusNoteToClient(){
        float value = -abs(global.parseStringToFloat(putDebt.getText().toString()));
        if (value < 0.00f){
            db_account.addNote(new Note(value), client);
            putDebt.setText("");
            update_grid();
        }
    }

    private void update_grid(){
        clientAccount = db_account.getClientAccount(client);
        NoteAdapter adapter = new NoteAdapter(Act_ClientAccount.this, clientAccount.getNotes());
        grid.setAdapter(adapter);
        debtTotal.setText(String.format("%.2f",clientAccount.getTotal()));

        // ----- updating debt of the user: ------
        client.setToPay(clientAccount.getTotal());
        db_client.updateClient(client);
        // ---------------------------------------
    }

    public void gotoEditNote(int position){

        // Pass information to the next view:
        NextPage = new Intent(Act_ClientAccount.this, Act_Edit_Note.class);
        //Add the bundle to the intent
        NextPage.putExtras(BundleWithInfo(position));
        //Fire that second activity
        startActivity(NextPage);
    }

    public void gotoNoteDetails(View v){

        // Pass information to the next view:
        NextPage = new Intent(Act_ClientAccount.this, Act_DetailsNote.class);
        //Add the bundle to the intent
        NextPage.putExtras(BundleWithInfo(0));
        //Fire that second activity
        startActivity(NextPage);
    }

    public Bundle BundleWithInfo(int position){
        //Create the bundle
        Bundle bundle = new Bundle();
        int idDeft = clientAccount.getNote(position).getIdNote();
        //Adding data to bundle
        bundle.putString("clientName", client.getName());
        bundle.putString("putDeft", putDebt.getText().toString() );
        bundle.putString("idNote", String.valueOf(idDeft));
        return bundle;
    }

    public void gotoDetails_all_accounts(){
        // Pass information to the next view:
        NextPage = new Intent(Act_ClientAccount.this, Act_Details_all_accounts.class);
        //Add the bundle to the intent
        NextPage.putExtras(BundleWithInfo(0));
        //Fire that second activity
        startActivity(NextPage);
    }

    public void gotoNewAccount(){
        // Pass information to the next view:
        NextPage = new Intent(Act_ClientAccount.this, Act_New_Account.class);
        //Add the bundle to the intent
        NextPage.putExtras(BundleWithInfo(0));
        //Fire that second activity
        startActivity(NextPage);
    }

    @Override
    protected void onStop() {
        db_client.close();
        db_account.close();
        super.onStop();
    }
}
