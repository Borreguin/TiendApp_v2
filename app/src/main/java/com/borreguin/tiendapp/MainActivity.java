package com.borreguin.tiendapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.Utilities.App_icon;
import com.borreguin.tiendapp.Utilities.CustomGrid_icons;

public class MainActivity extends AppCompatActivity {
    // Creating a grid of Apps:
    GridView grid;
    // Creating a Grid of Icons
    // icons from https://icons8.com/icon/set/notes/all
    App_icon app_icon;
    String[] web;
    int[] imageId;
    View view ;

    // global constants and functions
    Global global = new Global();

    // Button btn_manage_client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app_icon = new App_icon();
        //app_icon.Add_App_icon("Anotar Fiado", R.drawable.icons8_add_notes, 0);
        //app_icon.Add_App_icon("Precios", R.drawable.icons8_us_dollar, 1);
        app_icon.Add_App_icon("Buscar", R.drawable.icons8_find_user, 0);
        app_icon.Add_App_icon("Clientes",R.drawable.icons8_add_user, 1);

        web = app_icon.getWeb();
        imageId = app_icon.getImageId();

        // connecting Layout with controls
        // btn_manage_client = (Button)findViewById(R.id.btn_manage_client);
        CustomGrid_icons adapter = new CustomGrid_icons(MainActivity.this,web,imageId);

        grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "Ingresando a "
                        +web[+ position], Toast.LENGTH_LONG).show();
                gotoNewActivity( position);
            }
        });
    }

    // NAVIGATION BUTTONS
    // NavigateButton -> Open menu for Manage of clients

    public void gotoNewActivity(int activity_view){

        Intent NextPage;
        view = new View(this);
        switch (activity_view){
            case 0:
                global.goto_SearchClient(view);
                break;
            case 1:
                global.goto_CreateClient(view);
                break;

            // Search Clients
            case 2:
                //global.goto_SearchClient(view);
                break;

            // Create Clients
            case 3:
                //global.goto_CreateClient(view);
                break;
        }

    }


}
