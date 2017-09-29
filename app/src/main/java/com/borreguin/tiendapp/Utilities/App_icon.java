package com.borreguin.tiendapp.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roberto on 8/29/2017.
 * com.borreguin.tiendapp.Utilities
 */

public class App_icon {
    private ArrayList<String> web;
    private ArrayList<Integer> imageId;
    private ArrayList<Integer> order;

    public App_icon(){
        this.web = new ArrayList<String>();
        this.imageId = new ArrayList<Integer>();
        this.order = new ArrayList<Integer>();
    }
    public App_icon(String web, int imageId, int order){
        this.web = new ArrayList<String>();
        this.imageId = new ArrayList<Integer>();
        this.order = new ArrayList<Integer>();

        this.web.add(web);
        this.imageId.add(imageId);
        this.order.add(order);
    }

    public void Add_App_icon(String web, int imageId, int order){
        this.web.add(web);
        this.imageId.add(imageId);
        this.order.add(order);
    }

    public String[] getWeb() {
        String[] ans = new String[web.size()];
        int i=0;
        for(Integer idx : order){
            ans[i] = web.get(idx);
            i++;
        }
        return ans;
    }

    public int[] getImageId() {
        int[] ans = new int[web.size()];
        int i=0;
        for(Integer idx : order){
            ans[i] = imageId.get(idx);
            i++;
        }
        return ans;
    }

}
