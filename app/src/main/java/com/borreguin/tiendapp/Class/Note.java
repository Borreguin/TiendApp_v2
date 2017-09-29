package com.borreguin.tiendapp.Class;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Roberto on 9/8/2017.
 * com.borreguin.tiendapp.Class
 */

public class Note {
    private int IdNote;
    private float value;
    private String description;
    private Date date_update;

    // global constant and functions
    Global global = new Global();

    public Note(float value){
        this.value = value;
        this.date_update = Calendar.getInstance().getTime();
    }

    public Note(float value, String description) {
        this.value = value;
        this.description = description;
        this.date_update = Calendar.getInstance().getTime();
    }

    public Note(float value, String description, Date date_update) {
        this.value = value;
        this.description = description;
        this.date_update = date_update;
    }

    public Note(int idNote, float value, String description, String date_update) {
        this.IdNote = idNote;
        this.value = value;
        this.description = description;
        try {
            this.date_update = global.formatter.parse(date_update);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getIdNote() {
        return IdNote;
    }

    public void setIdNote(int idNote) {
        IdNote = idNote;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_update_string() {
        return global.formatter.format(this.date_update);
    }

    public String getDate_update_string_2() {
        return global.formatter2.format(this.date_update);
    }

    public void setDateUpdate(String dateUpdate){
        try {
            this.date_update = global.formatter.parse(dateUpdate);
        }
        catch (ParseException e) {
            try {
                this.date_update = global.formatter2.parse(dateUpdate);
            } catch (ParseException e1){
                this.date_update = Calendar.getInstance().getTime();
                e1.printStackTrace();
            }
        }
    }
}
