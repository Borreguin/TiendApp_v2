package com.borreguin.tiendapp.Utilities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.borreguin.tiendapp.Class.Account;
import com.borreguin.tiendapp.Class.Note;
import com.borreguin.tiendapp.R;

import java.util.ArrayList;

import static java.lang.Math.abs;

/**
 * Created by Roberto on 9/8/2017.
 * com.borreguin.tiendapp.Utilities
 */

public class NoteAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Note> noteList;
    private Intent NextPage;

    public NoteAdapter(Context context, ArrayList<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    public void addNote(Note note){
        this.noteList.add(note);
    }

    @Override
    public int getCount() {
        return noteList.size();
    }

    @Override
    public Object getItem(int position) {
        return noteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Note item_note = (Note) getItem(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_value, null);
        }

        final TextView childValue = (TextView) convertView.findViewById(R.id.flt_child_value);
        final TextView sign = (TextView) convertView.findViewById(R.id.sign);
        childValue.setText(String.format("%.2f", abs(item_note.getValue())));
        if(item_note.getValue() >= 0){
            sign.setText("+");
        }else{
            sign.setText("-");
        }
        if(position == getCount()-1){
            childValue.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        }

        final View finalConvertView = convertView;

       /* childValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(finalConvertView.getContext()
                        , childValue.getText()
                        , Toast.LENGTH_SHORT).show();
            }
        });*/

        return convertView;
    }
}
