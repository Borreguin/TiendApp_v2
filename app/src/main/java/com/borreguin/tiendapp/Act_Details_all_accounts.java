package com.borreguin.tiendapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Accounts;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;
import com.borreguin.tiendapp.Utilities.NoteAdapter;

public class Act_Details_all_accounts extends AppCompatActivity {

    Intent NextPage;
    Client client;
    Global global = new Global();
    TextView clientName, debtTotal;
    GridView grid;

    // variables for data:
    private DBHandler_Clients db_client = new DBHandler_Clients(this);
    private DBHandler_Accounts db_account = new DBHandler_Accounts(this);
    private Account clientAccount = new Account();

    // overwriting the back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                global.goto_clientAccount(Act_Details_all_accounts.this, client);
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
        setContentView(R.layout.activity_details_all_accounts);

        // Navigation button
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // connecting members of activity layout
        clientName =(TextView) findViewById(R.id.clientName);
        debtTotal = (TextView)findViewById(R.id.txt_total);
        grid=(GridView)findViewById(R.id.grid_item_values);

        // loading data of the client:
        if(LoadClient()) //if the user is not loaded then scape
        {
            update_grid();
        }
        // add listeners to the grid:
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(Act_Details_all_accounts.this, "Ingresando a "
                                + clientAccount.getNote(position).getValue(),
                        Toast.LENGTH_LONG).show();
                gotoSeeNote(view, position);
            }
        });
    }


    private Boolean LoadClient(){

        // loading data from the last view
        try {
            Bundle bundle = getIntent().getExtras();
            client = db_client.getClient(bundle.getString("clientName"));
            clientName.setText(client.getName());
            debtTotal.setText(String.format("%.2f",clientAccount.getTotal()));
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void update_grid(){
        clientAccount = db_account.getClientAccount(client, client.get_last_AccountID());
        NoteAdapter adapter = new NoteAdapter(Act_Details_all_accounts.this, clientAccount.getNotes());
        grid.setAdapter(adapter);
        debtTotal.setText(String.format("%.2f",clientAccount.getTotal()));
    }

    public void gotoSeeNote(View v, int position){

        // Pass information to the next view:
        NextPage = new Intent(Act_Details_all_accounts.this, Act_See_Note.class);
        //Add the bundle to the intent
        NextPage.putExtras(BundleWithInfo(position));
        //Fire that second activity
        startActivity(NextPage);
    }

    public Bundle BundleWithInfo(int position){
        //Create the bundle
        Bundle bundle = new Bundle();
        int idDeft = clientAccount.getNote(position).getIdNote();
        //Adding data to bundle
        bundle.putString("clientName", client.getName());
        bundle.putString("idNote", String.valueOf(idDeft));
        return bundle;
    }

}
