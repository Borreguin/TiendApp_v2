package com.borreguin.tiendapp.Class;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Roberto on 9/8/2017.
 * com.borreguin.tiendapp.Class
 */

public class Account {
    private int idAccount;
    private ArrayList<Note> notes = new ArrayList<>();

    public Account(){
        this.idAccount = 0;
        this.notes = new ArrayList<>();
    }

    public Account(int idAccount, ArrayList<Note> notes) {
        this.idAccount = idAccount;
        this.notes = notes;
    }

    public void addNote(Note note){
        notes.add(note);
    }

    public int getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(int idAccount) {
        this.idAccount = idAccount;
    }

    public ArrayList<Note> getNotes() {
        //Collections.reverse(notes);
        return notes;
    }


    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public float getTotal(){
        float ans = 0;
        for (Note note : notes){
            ans += note.getValue();
        }
        return ans;
    }

    public Note getNote(int position){
        if(notes.size() !=0 )
            return notes.get(position);
        else
            return new Note(0.f);
    }

}
