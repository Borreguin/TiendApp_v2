package com.borreguin.tiendapp.Utilities;

import android.content.Context;
import android.content.res.Resources;

import com.borreguin.tiendapp.R;

import static android.provider.Settings.Global.getString;

/**
 * Created by Roberto on 8/31/2017.
 * com.borreguin.tiendapp.Utilities
 */

public class ChildRow {
    private int icon;
    private String clientName;
    private String clientDescription;
    private String debt;
    private String rely;

    public String getDebt() {
        return debt;
    }

    public void setDebt(String debt) {
        this.debt = debt;
    }

    public String getRely() {
        return rely;
    }

    public void setRely(String rely) {
        this.rely = rely;
    }

    public ChildRow(int icon, String clientName) {
        this.icon = icon;
        this.clientName = clientName;
    }

    public ChildRow(int icon, String clientName, String debt, String rely) {
        this.icon = icon;
        this.clientName = clientName;
        this.debt = debt;
        this.rely = rely;
    }

    public ChildRow(int icon, String clientName, String clientDescription,
                    String debt, boolean rely) {
        this.icon = icon;
        this.clientName = clientName;
        this.debt = debt;
        this.clientDescription = clientDescription;

        if(rely) {
            this.rely = "Si";
        }else{
            this.rely = "No";
        }
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientDescription() {
        return clientDescription;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
