/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.plugins.devices.homeassistant1;
import com.freedomotic.model.ds.Config;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.things.impl.ElectricDevice;
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
    int totalConsumption;
    
    public CheckPower(List <EnvObjectLogic> list){
        LOG.info("HO FATTO LA CREAZIONE DALLA CLASSE CONSUMPTION");
        this.list = list;
        totalConsumption = 0;
    }
    
    protected void countConsumption(){
        
        LOG.info("NUMERO OGGETTI ELETTRICI: " + list.size());
        for (EnvObjectLogic list1 : list) {
            LOG.info(list1.getBehavior("simuleted_consumption").getValueAsString());
            totalConsumption += Integer.parseInt(list1.getBehavior("simuleted_consumption").getValueAsString());
        }
        LOG.info("Consumo totale: " + totalConsumption + " kW");
    }
    
    public int getCountConsumption(){
        return totalConsumption;
    }
    
    public void setConsumptionPowerMeter(){

            
        for (EnvObjectLogic list1 : list) {
            if(list1.getPojo().getName().equals("PowerMeter")){
                ElectricDevice powerMeter = (ElectricDevice)list1;
                powerMeter.setConsumptionValue(getCountConsumption(), true);
                LOG.info("IN TEORIA HO SETTATO");
            }      
        }
    }
    
}
