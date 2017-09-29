package com.borreguin.tiendapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;

import java.util.List;

public class Act_Edit_client extends AppCompatActivity {

    // variables for layout
    Button btnEditClient, btnSearchClient, btnCancel;
    private EditText clientName, description, clientDebt;
    private Switch swRelyClient;

    // variables for data
    private List<Client> clients;
    private Client tempClient, client;
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);
    private Boolean uniqueClient = false;

    // variables and functions that are global
    private Global global = new Global();

    // Navigation Button:
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return global.switch_BottomNavigationView(item.getItemId(),getCurrentFocus());
        }

    };

    // Validating the edition of the client
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            // here: the validation of EditText
            clients = db_client.getAllClients();
            uniqueClient = checkClient(getBaseContext(),
                    clients,clientName.getText().toString(),
                    clientName);
            if(clientName.toString().equals(tempClient.getName())){
                uniqueClient = true;
                clientName.setTextColor(ContextCompat.getColor(getBaseContext(),R.color.fine));
            }
        }
    };

    public Boolean checkClient(Context context, List<Client> clients, String NameClient, EditText checkText){

        Boolean unique = global.checkClient(clients, NameClient);

        if(unique){
            checkText.setTextColor(ContextCompat.getColor(context, R.color.fine));
        }else{
            checkText.setTextColor(ContextCompat.getColor(context, R.color.wrong));
        }
        return unique;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create new buttons and linking with the layout
        setContentView(R.layout.activity_act_edit_client);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // navigation bottom
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnEditClient = (Button)findViewById(R.id.btnEditClient);
        btnSearchClient = (Button)findViewById(R.id.btnSearchClient);
        btnCancel = (Button)findViewById(R.id.btnMainActivity);

        clientName = (EditText) findViewById(R.id.dte_clientName);
        description = (EditText)findViewById(R.id.dte_clientDescription);
        clientDebt = (EditText)findViewById(R.id.dte_editDebt);
        swRelyClient = (Switch)findViewById(R.id.swRelyClient);

        btnEditClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editClient(v);
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

        // loading data from the last view
        Bundle bundle = getIntent().getExtras();
        clientName.setText(bundle.getString("clientName"));
        // loading data of temporal client
        clients = db_client.getAllClients();
        LoadClient(bundle.getString("clientName"));

        // checking client name
        clientName.addTextChangedListener(textWatcher);
    }


    protected void LoadClient(String nameClient){
        tempClient = db_client.getClient(nameClient);
        client = db_client.getClient(nameClient);
        clientName.setText(tempClient.getName());
        description.setText(tempClient.getDescription());
        clientDebt.setText(String.valueOf(tempClient.getToPay()));
        swRelyClient.setChecked(tempClient.isToRely());
    }

    protected void updateTemporalClient(){
        tempClient.setName(clientName.getText().toString());
        tempClient.setDescription(description.getText().toString());
        tempClient.setToPay(clientDebt.getText().toString());
        tempClient.setToRely(swRelyClient.isChecked());
    }

    protected void editClient(View v){

        uniqueClient = checkClient(Act_Edit_client.this,
                clients, clientName.getText().toString(),
                clientName);

        String edt_client = clientName.getText().toString().toLowerCase();
        String aux_client = tempClient.getName().toLowerCase();

        if(uniqueClient || edt_client.equals(aux_client) ) {
            updateTemporalClient();

            if(!tempClient.getName().equals(client.getName())){
                db_account.updateNameClient(client.getName(), tempClient.getName());
            }

            if(tempClient.getToPay() != client.getToPay()){
                db_account.delete_Accounts(client,client.get_Account_toDelete());
                tempClient.newAccount();
                db_account.addNote(new Note(
                        Float.valueOf(clientDebt.getText().toString()),
                        "Nueva cuenta creada"),tempClient);
            }
            db_client.updateClient(tempClient);
            db_client.close();
            global.goto_SearchClient(v);
        }else{
            Toast.makeText(Act_Edit_client.this,
                    getString(R.string.already_created) + " ",
                    Toast.LENGTH_LONG).show();
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
        db_client.close();
        super.onStop();
    }
}
