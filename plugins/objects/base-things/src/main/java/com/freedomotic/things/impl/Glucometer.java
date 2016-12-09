/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import static com.freedomotic.things.impl.ElectricDevice.BEHAVIOR_POWERED;
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
    private int glicemiaValue = 80;
    private final static String BEHAVIOR_GLICEMIA = "glicemia";
    private final static String BEHAVIOR_TAKETHEPILLS =  "takeThePills";
    @Override
    public void init() {
        
        takeThePills = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_TAKETHEPILLS));
        //add a listener to values changes
        takeThePills.addListener(new BooleanBehaviorLogic.Listener() {
            @Override
            public void onTrue(Config params, boolean fireCommand) {
                if (fireCommand) {
                    executePowerOn(params); //executes a turn on command and then sets the object behavior to on
                } else {
                    setOn(); //sets the object behavior to on as a result from a notified value
                }
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                if (fireCommand) {
                    executePowerOff(params); //executes a turn off command and then sets the object behavior to off
                } else {
                    setOff(); //sets the object behavior to off as a result from a notified value
                }
            }
        });
        //register this behavior to the superclass to make it visible to it
        registerBehavior(takeThePills);
        
        glicemia = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_GLICEMIA));
        glicemia.setValue(glicemiaValue);
        glicemia.addListener(new RangedIntBehaviorLogic.Listener() {
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                glicemiaValue = glicemia.getMax();
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                glicemiaValue = glicemia.getMin();
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                executeSet(rangeValue, params, BEHAVIOR_GLICEMIA);
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        registerBehavior(glicemia);
        super.init();
    }
    
    public void executeSet(int rangeValue, Config params, String object) {
        boolean executed = executeCommand("set glicemia", params);
        if (executed) {
            if (object.equals("glicemia")){
                glicemia.setValue(rangeValue);
                glicemiaValue = glicemia.getValue();
                setChanged(true);
            }
        }
    }
    
    @Override
    public void executePowerOn(Config params) {
        boolean executed = executeCommand(ACTION_TURN_ON, params);

        if (executed) {
            setOn();
        }
    }
    
    @Override
    public void executePowerOff(Config params) {
        boolean executed = executeCommand(ACTION_TURN_OFF, params);

        if (executed) {
            setOff();
        }
    }
    
    public void setOn() {
        LOG.info("Setting behavior ''takeThePills'' of object ''{}'' to true", getPojo().getName());

        //if not already on
        if (takeThePills.getValue() != true) {
            takeThePills.setValue(true);
            setChanged(true);
        }
    }

    public void setOff() {
        LOG.info("Setting behavior ''takeThePills'' of object ''{}'' to false", getPojo().getName());

        //if not already off
        if (takeThePills.getValue() != false) {
            takeThePills.setValue(false);
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

    

