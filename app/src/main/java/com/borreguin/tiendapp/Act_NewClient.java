package com.borreguin.tiendapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;

import java.util.List;

public class Act_NewClient extends AppCompatActivity {

    private TextView mTextMessage;
    private CheckedTextView checkText;
    private List<Client> clients;
    private EditText clientName;
    private EditText description;
    private EditText clientDebt;
    private DBHandler_Clients db = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);

    //private Client SelectedClient;
    Button btnNewClient;
    Button btnSearchClient;
    Global global = new Global();


    // Validating a new client
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // here: the validation of EditText

            clients = db.getAllClients();
            db.close();
            checkClient(clients,clientName.getText().toString());
        }
    };

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

        setContentView(R.layout.activity_act_new_client);
        // PART OF THE MENU
        // ------------------------------------------------
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // -------------------------------------------------
        // Read all clients:

        // db.addClient(new Client("ya pues", ""));
        clients = db.getAllClients();
        db.close();
        //**************************************************
        //**************************************************

        // Validate Text
        clientName = (EditText)findViewById(R.id.clientName);
        clientName.addTextChangedListener(textWatcher);
        description = (EditText)findViewById(R.id.clientDescription);
        clientDebt = (EditText)findViewById(R.id.editDebt);

        // Init checkedTextView
        checkText = (CheckedTextView)findViewById(R.id.checkText);
        checkText.setCheckMarkDrawable(R.drawable.empty);

        //***************CREATE BUTTONS****************
        btnNewClient = (Button)findViewById(R.id.btnNewClient);
        btnSearchClient = (Button)findViewById(R.id.btnSearchClient);
        //***************************************************

        isThereTemporalClient();
        super.onCreate(savedInstanceState);
    }

    // check if the user is already created
    public void checkClient(List<Client> clients, String NameClient){

        Boolean Found = false;

        if(NameClient.isEmpty()){
            checkText.setCheckMarkDrawable(R.drawable.wrong);
            checkText.setChecked(false);
            checkText.setText(R.string.empty);
            return;
        }

        // Check if the user is already created
        String NameClient_ax = NameClient.replace(" ", "").toLowerCase();
        for(Client client : clients){
            String client_ax = client.getName().replace(" ", "").toLowerCase();

            if(client_ax.equals(NameClient_ax)){
                Found = true;
            }
        }

        if(Found){
            checkText.setCheckMarkDrawable(R.drawable.wrong);
            checkText.setChecked(false);
            checkText.setText(R.string.already_created);
        }else{
            checkText.setCheckMarkDrawable(R.drawable.checked);
            checkText.setChecked(true);
            checkText.setText(R.string.accepted);
        }
    }

    public void addNewClient(View view) {

        checkClient(clients,clientName.getText().toString());

        if(clientName.getText().length() == 0){
            Toast.makeText(Act_NewClient.this,
                    getString(R.string.NewClient) + " " +
                    getString(R.string.empty) , Toast.LENGTH_LONG).show();
            return;
        }
        if(!checkText.isChecked()){
            Toast.makeText(Act_NewClient.this,
                    getString(R.string.already_created) + ": " +
                    clientName.getText(), Toast.LENGTH_LONG).show();
        }else{

            // creating a new client to save in DB -------------------
            Client new_client = new Client(
                    clientName.getText().toString().trim(),
                    description.getText().toString(),
                    clientDebt.getText().toString()
            );
            db.addClient(new_client);
            cleanTemporalClient();
            Toast message = Toast.makeText(Act_NewClient.this,
                    getString(R.string.Successfully) + "\n" +
                    getString(R.string.newClientCreated) + "\t" +
                            clientName.getText() , Toast.LENGTH_LONG);
            message.setGravity(0, 0, -140);
            message.show();

            checkText.setCheckMarkDrawable(R.drawable.ok);
            db.close();
            // End creating a new client to save in DB -------------------

            // Create account for client
            db_account.addNote(new Note(
                    global.parseStringToFloat(clientDebt.getText().toString())),
                    new_client
            );

            // got to the main activity
            global.goto_clientAccount(Act_NewClient.this, new_client);
        }
    }


    // Buttons for navigation:
    public void gotoSearchClient(View view){
        saveTemporalClient();
        global.goto_SearchClient(view);
    }

    // Create temporal client for testing or any other purpose
    public void saveTemporalClient(){

       Client temporalClient = new Client( global.id_temp,
                global.prefix + clientName.getText().toString(),
                description.getText().toString(),
                global.parseStringToFloat(clientDebt.getText().toString()));
        db.updateClient(temporalClient);
        clients = db.getAllClients();
        db.close();
    }

    public void cleanTemporalClient(){

        Client temporalClient = new Client(global.id_temp, global.prefix + "", "", 0);
        db.updateClient(temporalClient);
        db.close();
    }

    public Boolean isThereTemporalClient() {

        Client tempClient = db.getClient(global.id_temp);
        if (tempClient.isEmpty()) {
            db.addClient(new Client(global.id_temp, global.prefix, "", 0));
            db.close();
            return false;
        }

        if (tempClient.getName().equals(global.prefix)) {
            db.close();
            return false;
        }
        else {
            clientName.setText(tempClient.getName().replace(global.prefix, ""));
            description.setText(tempClient.getDescription());
            clientDebt.setText(String.valueOf(tempClient.getToPay()));
            return true;
        }
    }

    public void clean_clientName(View view){
        clientName.setText("");
    }
    public void clean_description(View view){
        description.setText("");
    }
    public void clean_debt(View view){clientDebt.setText("");}

    @Override
    protected void onStop() {
        db.close();
        db_account.close();
        super.onStop();
    }
}
