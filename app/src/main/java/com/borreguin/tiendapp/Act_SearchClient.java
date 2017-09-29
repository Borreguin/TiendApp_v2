package com.borreguin.tiendapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import com.borreguin.tiendapp.Class.Client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.DB_Handlers.DBHandler_Clients;
import com.borreguin.tiendapp.Utilities.ChildRow;
import com.borreguin.tiendapp.Utilities.ClientListAdapter;
import com.borreguin.tiendapp.Utilities.ParentRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


public class Act_SearchClient extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SearchView.OnClickListener{

    // Search list:
    private SearchManager searchManager;
    android.widget.SearchView searchView;
    private ClientListAdapter listAdapter;
    private ExpandableListView myList;
    private ArrayList<ParentRow> parentList = new ArrayList<>();
    private ArrayList<ParentRow> showTheseParentList = new ArrayList<>();
    private MenuItem searchItem;
    // Manage of clients
    private DBHandler_Clients db_client = new DBHandler_Clients(this);

    //Data of clients
    List<Client> clients;
    private SortedSet capLetters = new TreeSet<>();
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

        setContentView(R.layout.activity_search_client);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent NextPage = new Intent(Act_SearchClient.this, Act_NewClient.class);
                startActivity(NextPage);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // *****************************************
        // Search clients:
        searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        parentList = new ArrayList<>();
        showTheseParentList = new ArrayList<>();


        // The app will crash if display list is not called here
        displayList();

        // This expands the List of contents
        expandAll();

        // This create buttons
        super.onCreate(savedInstanceState);
    }



    // Consulta la base de datos de los usuarios
    // Permite la organizacion de manera alfabetica
    private void loadData(){
        // mapping letters and positions
        Map<Character, Integer> map_KV = new HashMap<>();
        char letter;
        int idx = 0;

        // Data from DataBase
        clients = db_client.getAllClients();
        if(clients.size() ==0 ){return;}

        // Id = 0 is a client for testing
        clients.remove(0);

        // Obtaining the capital letter for each name
        for(Client client : clients){
            capLetters.add(getCapitalLetter(client.getName()));
        }

        for(Object capLetter : capLetters){
            map_KV.put((Character) capLetter,idx);
            idx++;
        }

        // Structure for search list for each capital letter
        ArrayList<ChildRow>[] childRows = (ArrayList<ChildRow>[])new ArrayList[capLetters.size()+1];
        ParentRow parentRow = null;

        for(int idx_x=0;idx_x<capLetters.size();idx_x++){
            childRows[idx_x] = new ArrayList<>();
        }

        for(Client client : clients){
            letter = getCapitalLetter(client.getName());
            idx =  map_KV.get(Character.toUpperCase(letter));
            childRows[idx].add(new ChildRow(
                    R.mipmap.generic_icon,      // Client icon
                    client.getName(),           // Name client
                    client.getDescription(),    // client Description
                    String.valueOf(client.getToPay()),  //Debt to pay
                    client.isToRely()) //is rely?);
            );
        }

        int idx_x = 0;
        for(Object capLetter : capLetters){
            parentRow = new ParentRow(capLetter.toString(),childRows[idx_x++]);
            parentList.add(parentRow);
        }

/* this is a simple test:
            // Adding members
            for(Client client : clients){
                childRows[0].add(new ChildRow(
                        R.mipmap.generic_icon,  // Client icon
                        client.getName(),       // Name client
                        String.valueOf(client.getToPay()),  //Debt to pay
                        client.isToRely())   //is rely?
                );
            }
        childRows = new ArrayList<ChildRow>();
        childRows.add(new ChildRow(R.mipmap.generic_icon, "icon 5"));
        childRows.add(new ChildRow(R.mipmap.generic_icon, "icon 6"));
        parentRow = new ParentRow("Second Group",childRows);
        parentList.add(parentRow);
*/

    }

    protected Character getCapitalLetter(String clientName){
        String [] aux = clientName.split(" ");
        char letter;
        if(aux.length > 1)
            letter = aux[1].charAt(0);
        else
            letter = clientName.charAt(0);
        return Character.toUpperCase(letter);
    }

    private void expandAll(){
        int count = listAdapter.getGroupCount();
        for (int i =0; i <count; i++){
            myList.expandGroup(i);
        }
    }

    private void displayList(){
        loadData();
        listAdapter = new ClientListAdapter(Act_SearchClient.this, parentList);
        myList = (ExpandableListView) findViewById(R.id.expListView);
        myList.setAdapter(listAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo
                (searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        //searchView.setOnCloseListener(this);
        searchView.requestFocus();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onClose() {
        listAdapter.filterData("");
        expandAll();
        return false;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);
        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        listAdapter.filterData(newText);
        expandAll();
        return false;
    }

    @Override
    protected void onStop() {
        db_client.close();
        super.onStop();
    }

}
