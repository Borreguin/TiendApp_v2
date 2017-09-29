package com.borreguin.tiendapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;

public class Act_New_Account extends AppCompatActivity {

    private TextView clientName;
    private EditText clientDebt;
    Button btnEditClient, btnSearchClient, btnCancel;

    // variables for data
    private Client client;
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    // variables and functions that are global
    private Global global = new Global();

    // overwriting the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                global.goto_clientAccount(Act_New_Account.this, client);
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
        setContentView(R.layout.activity_act_new_account);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // connecting with layout
        clientName = (TextView) findViewById(R.id.txt_clientName);
        btnEditClient = (Button)findViewById(R.id.btnEditClient);
        btnSearchClient = (Button)findViewById(R.id.btnSearchClient);
        btnCancel = (Button)findViewById(R.id.btnMainActivity);
        clientDebt = (EditText) findViewById(R.id.dte_editDebt);

        // load information about client
        LoadClient();

        // creating functions for click:
        btnEditClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                create_account();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.goto_MainMenu(v);
            }
        });
        btnSearchClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.goto_SearchClient(v);
            }
        });

    }

    protected void LoadClient(){
        Bundle bundle = getIntent().getExtras();
        client = db_client.getClient(bundle.getString("clientName"));
        clientName.setText(client.getName());
        clientDebt.setText(String.valueOf(client.getToPay()));
    }

    public void clean_debt(View view){clientDebt.setText("");}

    protected void create_account(){

        db_account.delete_Accounts(client,client.get_Account_toDelete());
        client.newAccount();
        db_client.updateClient(client);
        db_account.addNote(new Note(
                Float.valueOf(clientDebt.getText().toString()),
                "Nueva cuenta creada"),client);

        Toast.makeText(Act_New_Account.this,
                getString(R.string.title_activity_act__new__account) + " " +
                getString(R.string.Successfully),
                Toast.LENGTH_LONG).show();
        global.goto_clientAccount(Act_New_Account.this, client);
    }
}
