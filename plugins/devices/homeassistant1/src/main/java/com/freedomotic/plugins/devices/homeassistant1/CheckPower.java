/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins.devices.homeassistant1;

import com.freedomotic.things.EnvObjectLogic;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ricca
 */
public class CheckPower {
    List <EnvObjectLogic> list;
    private static final Logger LOG = LoggerFactory.getLogger(CheckPower.class.getName());
    int totalConsumption = 0;
    
    public CheckPower(List <EnvObjectLogic> list){
        LOG.info("HO FATTO LA CREAZIONE DALLA CLASSE CONSUMPTION");
        this.list = list;
    }
    
    protected void countConsumption(){
        
        LOG.info("NUMERO OGGETTI ELETTRICI: " + list.size());
        for (EnvObjectLogic list1 : list) {
            totalConsumption += Integer.parseInt(list1.getBehavior("simuleted_consumption").getValueAsString());
        }
        
        LOG.info("Consumo totale: " + totalConsumption + "kW");
    }
    
    public int getCountConsumption(){
        return totalConsumption;
    }
    
}
