/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and takeThePills the template in the editor.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.BooleanBehaviorLogic;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ricca
 */
public class Glucometer extends ElectricDevice {
    private static final Logger LOG = LoggerFactory.getLogger(Glucometer.class.getName());
    private RangedIntBehaviorLogic glicemia;
    private BooleanBehaviorLogic takeThePills;
    private int glicemiaStoredValue = 80;
    private final static String BEHAVIOR_GLICEMIA = "glicemia";
    private final static String BEHAVIOR_TAKETHEPILLS =  "takeThePills";
    @Override
    public void init() {
        
        glicemia = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_GLICEMIA));
        glicemia.setValue(glicemiaStoredValue);
        glicemia.addListener(new RangedIntBehaviorLogic.Listener() {
            
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                glicemiaStoredValue = glicemia.getMin();
                executePowerOff(params);
            }
            
            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                glicemiaStoredValue = glicemia.getMax();
                executePowerOn(params);
            }
            
            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                executeGlicemia(rangeValue, params);
            }
        });
        //register this behavior to the superclass to make it visible to it
        registerBehavior(glicemia);
        
        takeThePills = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_TAKETHEPILLS));
        takeThePills.addListener(new BooleanBehaviorLogic.Listener() {
            @Override
            public void onTrue(Config params, boolean fireCommand) {
                //takeThePills = true
                setOn(params);
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                //takeThePills = false -> not takeThePills
                setOff(params);
            }
        });
        
        registerBehavior(takeThePills);
        super.init();
    }
    
    
    protected void setOff(Config params) {
        boolean executed = executeCommand("take pill", params); //executes the developer level command associated with 'set brightness' action

        if (executed) {
            takeThePills.setValue(false);
            setChanged(true);
        }
    }

    protected void setOn(Config params) {
        boolean executed = executeCommand("don't take pill", params); //executes the developer level command associated with 'set brightness' action
        if (executed) {
            takeThePills.setValue(true);
            setChanged(true);
        }
    }
    @Override
    public void executePowerOff(Config params) {
        // when a light is "powered off" its glicemia is set to the minValue but the current value is stored
        glicemia.setValue(glicemia.getMin());
        // executeCommand the body of the super implementation. The super call
        // must be the last call as it executes setChanged(true)
        super.executePowerOff(params);
    }
    
    @Override
    public void executePowerOn(Config params) {
        // when a light is "powered on" its glicemia is set to the stored value if this is greater than the minValue
        if (glicemiaStoredValue > glicemia.getMin()) {
            glicemia.setValue(glicemiaStoredValue);
        } else {
            glicemia.setValue(glicemia.getMax());
        }
        // executeCommand the body of the super implementation. The super call
        // must be the last call as it executes setChanged(true)
        super.executePowerOn(params);
    }
    
    public void executeGlicemia(int rangeValue, Config params) {
        boolean executed = executeCommand("set glicemia", params); //executes the developer level command associated with 'set glicemia' action

        if (executed) {
            powered.setValue(true);
            glicemia.setValue(rangeValue);
            glicemiaStoredValue = glicemia.getValue();
            setChanged(true);
        }
    }
    @Override
    protected void createCommands() {
        super.createCommands();
        Command turnOff = new Command();
        turnOff.setName("Turn " + getPojo().getName() + " off");
        turnOff.setDescription("The " + getPojo().getName() + " turns off");
        turnOff.setReceiver("app.events.sensors.behavior.request.objects");
        turnOff.setProperty("object", getPojo().getName());
        turnOff.setProperty("behavior", "powered");
        turnOff.setProperty("value", "false");
        commandRepository.create(turnOff);
        
        Command pillTrue = new Command();
        pillTrue.setName("Turn " + getPojo().getName() + " sensors on");
        pillTrue.setDescription("Take the pill");
        pillTrue.setReceiver("app.events.sensors.behavior.request.objects");
        pillTrue.setProperty("object", getPojo().getName());
        pillTrue.setProperty("behavior", BEHAVIOR_TAKETHEPILLS);
        pillTrue.setProperty("value", "true");
        commandRepository.create(pillTrue);
        
        Command pillFalse = new Command();
        pillFalse.setName("Turn " + getPojo().getName() + " sensors off");
        pillFalse.setDescription("Don't Take the pill");
        pillFalse.setReceiver("app.events.sensors.behavior.request.objects");
        pillFalse.setProperty("object", getPojo().getName());
        pillFalse.setProperty("behavior", BEHAVIOR_TAKETHEPILLS);
        pillFalse.setProperty("value", "true");
        commandRepository.create(pillFalse);
    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
        
        Trigger anomaly_glicemia = new Trigger();
        anomaly_glicemia.setName("When " + this.getPojo().getName() + " value is anomalous");
        anomaly_glicemia.setChannel("app.event.sensor.object.behavior.change");
        anomaly_glicemia.getPayload().addStatement("object.name", this.getPojo().getName());
        anomaly_glicemia.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_GLICEMIA, "GREATER_THAN", "100");
        anomaly_glicemia.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_GLICEMIA, "LESS_THAN", "60");
        anomaly_glicemia.setPersistence(false);
        triggerRepository.create(anomaly_glicemia);
        
        Trigger correct_glicemia = new Trigger();
        correct_glicemia.setName("When " + this.getPojo().getName() + " value is ok");
        correct_glicemia.setChannel("app.event.sensor.object.behavior.change");
        correct_glicemia.getPayload().addStatement("object.name", this.getPojo().getName());
        correct_glicemia.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_GLICEMIA, "GREATER_THAN", "60");
        correct_glicemia.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_GLICEMIA, "LESS_THAN", "100");
        correct_glicemia.setPersistence(false);
        triggerRepository.create(correct_glicemia);
        
    }
}

    

