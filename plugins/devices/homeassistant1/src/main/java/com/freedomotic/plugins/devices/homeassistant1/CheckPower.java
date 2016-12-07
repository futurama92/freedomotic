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
        this.list = list;
        //totalConsumption = 0;
    }
    
    protected void countConsumption(){
        totalConsumption = 0;
        for (EnvObjectLogic list1 : list) {            
            try{
            LOG.info(list1.getBehavior("simuleted_consumption").getValueAsString());
            totalConsumption += Integer.parseInt(list1.getBehavior("simuleted_consumption").getValueAsString());  
            } catch (NullPointerException e){
                LOG.info("Non considerato");
            }
        }
        LOG.info("Consumo totale: " + totalConsumption + " kW");

        for (EnvObjectLogic list1 : list) {
            if(list1.getPojo().getName().equals("PowerMeter")){
                ElectricDevice powerMeter = (ElectricDevice)list1;
                Config params = new Config();
                powerMeter.executeSetPowerConsumption(totalConsumption, params);
                LOG.info("IN TEORIA HO SETTATO");
            }      
        }
    }
    
}
