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
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;

/**
 *
 * @author ricca
 */
public class Oximeter extends ElectricDevice{
    
    private RangedIntBehaviorLogic oximeter;
    private int oximeterValue = 50;
    protected final static String BEHAVIOR_OXIMETER = "oximeter";
    private BooleanBehaviorLogic takeOxi;
    private final static String BEHAVIOR_TAKEOXI =  "takeOxi";
    
    @Override
    public void init(){
                //linking this property with the behavior defined in the XML
        oximeter = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_OXIMETER));
        oximeter.setValue(oximeterValue);
        oximeter.addListener(new RangedIntBehaviorLogic.Listener() {
            
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                oximeterValue = oximeter.getMin();
                executePowerOff(params);
            }
            
            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                oximeterValue = oximeter.getMax();
                executePowerOn(params);
            }
            
            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                executeOxi(rangeValue, params);
            }
        });
        //register this behavior to the superclass to make it visible to it
        registerBehavior(oximeter);
        
                takeOxi = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_TAKEOXI));
        takeOxi.addListener(new BooleanBehaviorLogic.Listener() {
            @Override
            public void onTrue(Config params, boolean fireCommand) {
                //takeOxi = true
                setOn(params);
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                //takeOxi = false -> not takeOxi
                setOff(params);
            }
        });
        registerBehavior(takeOxi);
        super.init();
    }
    
    protected void setOff(Config params) {
        boolean executed = executeCommand("don't take pill", params); //executes the developer level command associated with 'set brightness' action

        if (executed) {
            takeOxi.setValue(false);
            setChanged(true);
        }
    }

    protected void setOn(Config params) {
        boolean executed = executeCommand("take pill", params); //executes the developer level command associated with 'set brightness' action
        if (executed) {
            takeOxi.setValue(true);
            setChanged(true);
        }
    }
    
    @Override
    public void executePowerOff(Config params) {
        oximeter.setValue(oximeter.getMin());
        super.executePowerOff(params);
    }
    
    @Override
    public void executePowerOn(Config params) {
        if (oximeterValue > oximeter.getMin()) {
            oximeter.setValue(oximeterValue);
        } else {
            oximeter.setValue(oximeter.getMax());
        }
        super.executePowerOn(params);
    }
    
    public void executeOxi(int rangeValue, Config params) {
        boolean executed = executeCommand("set oximeter", params); 
        if (executed) {
            powered.setValue(true);
            oximeter.setValue(rangeValue);
            oximeterValue = oximeter.getValue();
            setChanged(true);
        }
    }
    
    
    @Override
    protected void createCommands(){
        
        Command oxiTrue = new Command();
        oxiTrue.setName("Take " + getPojo().getName() + " oxigen on");
        oxiTrue.setDescription("Take the oxi");
        oxiTrue.setReceiver("app.events.sensors.behavior.request.objects");
        oxiTrue.setProperty("object", getPojo().getName());
        oxiTrue.setProperty("behavior", BEHAVIOR_TAKEOXI);
        oxiTrue.setProperty("value", "true");
        commandRepository.create(oxiTrue);
        
        Command oxiFalse = new Command();
        oxiFalse.setName("Take " + getPojo().getName() + " oxigen off");
        oxiFalse.setDescription("Don't Take the oxi");
        oxiFalse.setReceiver("app.events.sensors.behavior.request.objects");
        oxiFalse.setProperty("object", getPojo().getName());
        oxiFalse.setProperty("behavior", BEHAVIOR_TAKEOXI);
        oxiFalse.setProperty("value", "false");
        commandRepository.create(oxiFalse);
        
        super.createCommands();
        
        
    }
    
    @Override
    protected void createTriggers(){
        Trigger ok_oxi = new Trigger();
        ok_oxi.setName("When " + this.getPojo().getName() + " value is ok");
        ok_oxi.setChannel("app.event.sensor.object.behavior.change");
        ok_oxi.getPayload().addStatement("object.name", this.getPojo().getName());
        ok_oxi.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_OXIMETER, "GREATER_THAN", "96");
        ok_oxi.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_OXIMETER, "LESS_THAN", "101");
        ok_oxi.setPersistence(false);
        triggerRepository.create(ok_oxi);
        
        Trigger anomaly_oxi = new Trigger();
        anomaly_oxi.setName("When " + this.getPojo().getName() + " value is anomalus");
        anomaly_oxi.setChannel("app.event.sensor.object.behavior.change");
        anomaly_oxi.getPayload().addStatement("object.name", this.getPojo().getName());
        anomaly_oxi.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_OXIMETER, "GREATER_THAN", "89");
        anomaly_oxi.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_OXIMETER, "LESS_THAN", "96");
        anomaly_oxi.setPersistence(false);
        triggerRepository.create(anomaly_oxi);
        
        Trigger very_anomaly_oxi = new Trigger();
        very_anomaly_oxi.setName("When " + this.getPojo().getName() + " value is anomalus 2");
        very_anomaly_oxi.setChannel("app.event.sensor.object.behavior.change");
        very_anomaly_oxi.getPayload().addStatement("object.name", this.getPojo().getName());
        very_anomaly_oxi.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_OXIMETER, "LESS_THAN", "90");
        very_anomaly_oxi.setPersistence(false);
        triggerRepository.create(very_anomaly_oxi);
        super.createTriggers();
    }
    
}
