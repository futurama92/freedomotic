/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.BooleanBehaviorLogic;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.reactions.Trigger;

/**
 *
 * @author ricca
 */
public class HeartSensor extends ElectricDevice{
    
    private RangedIntBehaviorLogic bmpheart;
    private int bmpheartValue = 50;
    protected final static String BEHAVIOR_BMP_HEART = "bmpheart";

    
    @Override
    public void init(){
                //linking this property with the behavior defined in the XML
        bmpheart = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_BMP_HEART));
        bmpheart.setValue(bmpheartValue);
        bmpheart.addListener(new RangedIntBehaviorLogic.Listener() {
            
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                bmpheartValue = bmpheart.getMin();
                executePowerOff(params);
            }
            
            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                bmpheartValue = bmpheart.getMax();
                executePowerOn(params);
            }
            
            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                executeBmp(rangeValue, params);
            }
        });
        registerBehavior(bmpheart);
        

        super.init();
    }
    
    @Override
    public void executePowerOff(Config params) {
        bmpheart.setValue(bmpheart.getMin());
        super.executePowerOff(params);
    }
    
    @Override
    public void executePowerOn(Config params) {
        if (bmpheartValue > bmpheart.getMin()) {
            bmpheart.setValue(bmpheartValue);
        } else {
            bmpheart.setValue(bmpheart.getMax());
        }
        super.executePowerOn(params);
    }
    
    public void executeBmp(int rangeValue, Config params) {
        boolean executed = executeCommand("set bmpheart", params); 
        if (executed) {
            powered.setValue(true);
            bmpheart.setValue(rangeValue);
            bmpheartValue = bmpheart.getValue();
            setChanged(true);
        }
    }
    
    
    @Override
    protected void createTriggers(){
        Trigger ok_bmp = new Trigger();
        ok_bmp.setName("When " + this.getPojo().getName() + " value is ok");
        ok_bmp.setChannel("app.event.sensor.object.behavior.change");
        ok_bmp.getPayload().addStatement("object.name", this.getPojo().getName());
        ok_bmp.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_BMP_HEART, "GREATER_THAN", "40");
        ok_bmp.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_BMP_HEART, "LESS_THAN", "100");
        ok_bmp.setPersistence(false);
        triggerRepository.create(ok_bmp);
        
        Trigger anomaly_bmp = new Trigger();
        anomaly_bmp.setName("When " + this.getPojo().getName() + " value is anomalus");
        anomaly_bmp.setChannel("app.event.sensor.object.behavior.change");
        anomaly_bmp.getPayload().addStatement("object.name", this.getPojo().getName());
        anomaly_bmp.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_BMP_HEART, "GREATER_THAN", "100");
        anomaly_bmp.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_BMP_HEART, "LESS_THAN", "40");
        anomaly_bmp.setPersistence(false);
        triggerRepository.create(anomaly_bmp);
        super.createTriggers();
    }
    
}
