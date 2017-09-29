package com.borreguin.tiendapp.Utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.borreguin.tiendapp.Act_ClientAccount;
import com.borreguin.tiendapp.Act_Delete_Client;
import com.borreguin.tiendapp.Act_Edit_client;
import com.borreguin.tiendapp.Class.Global;
import com.borreguin.tiendapp.R;

import java.io.IOError;
import java.util.ArrayList;

/**
 * Created by Roberto on 8/31/2017.
 * com.borreguin.tiendapp.Utilities
 * Adapter for Searchable Menu
 */

public class ClientListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;
    private Intent NextPage;
    private Global global = new Global();

    public ClientListAdapter(Context context
            , ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }

    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parentRowList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentRow parentRow = (ParentRow) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.parent_text);

        heading.setText(parentRow.getName().trim());
        return convertView;
    }

    //////////////////////////////////////////////////////////////////////////////////
    //  HERE WE CONSTRUCT EACH USER FOR THE BIG LIST
    //  create the view for each client
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ChildRow childRow = (ChildRow) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row, null);
        }

        // Creating and updating each client:
        ImageView childIcon = (ImageView) convertView.findViewById(R.id.child_icon);
        final TextView childText = (TextView) convertView.findViewById(R.id.child_text);
        final TextView debtText = (TextView) convertView.findViewById(R.id.txt_debt);
        final TextView relyText = (TextView) convertView.findViewById(R.id.txt_rely);

        float client_deft = global.parseStringToFloat(childRow.getDebt());
        childIcon.setImageResource(childRow.getIcon());
        childText.setText(childRow.getClientName().trim());
        debtText.setText(String.format("%.2f",client_deft));
        relyText.setText(childRow.getRely());


        final View finalConvertView = convertView;
        childText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(finalConvertView.getContext()
                        , childText.getText()
                        , Toast.LENGTH_SHORT).show();

                gotoClientDetails(childText.getText().toString());


            }
        });

        // Creating Delete Button; assigning action in button
        final Button deleteClient = (Button) convertView.findViewById(R.id.btnDeleteClient);
        deleteClient.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Toast.makeText(finalConvertView.getContext()
                        , "Eliminar " + childText.getText()
                        , Toast.LENGTH_SHORT).show();
                gotoDeleteClient(childText.getText().toString());
            }

        });

        // Creating Delete Button; assigning action in button
        final Button editClient = (Button) convertView.findViewById(R.id.btnEditClient);
        editClient.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Toast.makeText(finalConvertView.getContext()
                        , "Editar " + childText.getText()
                        , Toast.LENGTH_SHORT).show();
                gotoEditClient(childText.getText().toString());
            }

        });

        return convertView;
    }

    //////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        parentRowList.clear();

        if (query.isEmpty()) {
            parentRowList.addAll(originalList);
        }
        else {
            for (ParentRow parentRow : originalList) {
                ArrayList<ChildRow> childList = parentRow.getChildList();
                ArrayList<ChildRow> newList = new ArrayList<>();

                // MAKING THE SEARCHING ACTIVITY:
                for (ChildRow childRow: childList) {
                    if (childRow.getClientName().toLowerCase().contains(query)
                            || childRow.getClientDescription().toLowerCase().contains(query)
                            ) {
                        newList.add(childRow);
                    }
                } // end for (com.example.user.searchviewexpandablelistview.ChildRow childRow: childList)
                if (newList.size() > 0) {
                    ParentRow nParentRow = new ParentRow(parentRow.getName(), newList);
                    parentRowList.add(nParentRow);
                }
            } // end or (com.example.user.searchviewexpandablelistview.ParentRow parentRow : originalList)
        } // end else

        notifyDataSetChanged();
    }

    private void gotoDeleteClient(String childText){

        // Pass information to the next view:
        NextPage = new Intent(context, Act_Delete_Client.class);
        NextPage.putExtras(SaveInBundle(childText));
        context.startActivity(NextPage); //Fire that second activity
    }

    private void gotoEditClient(String childText){

        // Pass information to the next view:
        NextPage = new Intent(context, Act_Edit_client.class);
        NextPage.putExtras(SaveInBundle(childText));
        context.startActivity(NextPage); //Fire that second activity
    }


    private void gotoClientDetails(String childText){

        try {
            // Pass information to the next view:
            NextPage = new Intent(context, Act_ClientAccount.class);
            NextPage.putExtras(SaveInBundle(childText));
            context.startActivity(NextPage); //Fire that second activity
        }catch (IOError e){
            e.printStackTrace();
        }catch (UnknownError e1){
            e1.printStackTrace();
        }

    }

    private Bundle SaveInBundle(String childText){
        //Create the bundle
        Bundle bundle = new Bundle();
        //Adding data to bundle
        bundle.putString("clientName",childText);
        return bundle;
    }

}
