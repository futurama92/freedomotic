/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins.devices.mqttclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author ricca
 */
public class SplitPayload {
    List<Integer> value_integer = new ArrayList();
    List<String> behavior_integer = new ArrayList();
    List<String> value_string = new ArrayList();
    List<String> behavior_string = new ArrayList();
    String payload = null;
    String[] record;
    public SplitPayload(String payload) {
        this.payload = payload;
    }
    
    public void workAsInteger() {
        record = payload.split(";");
        for (int i = 0; i < record.length; i++) {
            value_integer.add(Integer.parseInt(record[i].split(":")[1]));
            behavior_integer.add(record[i].split(":")[0]);
        }
    }
    
    public void workAsString() {
        record = payload.split(";");
        for (int i = 0; i < record.length; i++) {
            value_string.add(record[i].split(":")[1]);
            behavior_string.add(record[i].split(":")[0]);
        }
    }
    
    public List getValueInteger() {
        return value_integer;
    }
    
    public List getBehaviorInteger() {
        return behavior_integer;
    }
    
    public List getValueString() {
        return value_string;
    }
    
    public List getBehaviorString() {
        return behavior_string;
    }
    
    public int getSizerecord() {
        return record.length;
    }
}
