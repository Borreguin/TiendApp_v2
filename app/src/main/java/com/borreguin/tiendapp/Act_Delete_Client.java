package com.borreguin.tiendapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;

public class Act_Delete_Client extends AppCompatActivity {

    private TextView mTextMessage;
    Button btnDeleteClient;
    Button btnSearchClient;
    Button btnCancel;
    private TextView clientName;
    private TextView description;
    private TextView clientDebt;
    private DBHandler_Clients db = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    private Client tempClient;

    // Global functions and variables
    Global global = new Global();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_act_delete_client);
        btnDeleteClient = (Button)findViewById(R.id.btnDeleteClient);
        btnSearchClient = (Button)findViewById(R.id.btnSearchClient);
        btnCancel = (Button)findViewById(R.id.btnMainActivity);

        clientName = (TextView)findViewById(R.id.clientName);
        description = (TextView)findViewById(R.id.clientDescription);
        clientDebt = (TextView)findViewById(R.id.debt);

        LoadClient();

        // Assigning functions for buttons ----------------------------
        btnDeleteClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClient(v);
                global.goto_SearchClient(v);
            }
        });
        btnSearchClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.goto_SearchClient(v);
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                global.goto_MainMenu(v);
            }
        });

        //--------------------------------------------------------------

    }

    protected void LoadClient(){

        Bundle bundle = getIntent().getExtras();
        tempClient = db.getClient(bundle.getString("clientName"));
        clientName.setText(tempClient.getName());
        description.setText(tempClient.getDescription());
        clientDebt.setText(String.valueOf(tempClient.getToPay()));
    }

    protected void deleteClient(View v){
        db.deleteClient(tempClient);
        db_account.delete_All_Accounts(tempClient);
        global.goto_SearchClient(v);
    }

    @Override
    protected void onStop() {
        db.close();
        //db_account.close();
        super.onStop();
    }


}
