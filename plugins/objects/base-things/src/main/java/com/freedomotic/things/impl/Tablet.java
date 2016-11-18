/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.events.ObjectReceiveClick;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;

/**
 *
 * @author futurama92
 */
public class Tablet extends ElectricDevice {
    
    private RangedIntBehaviorLogic brightness;
    private int brightnessStoredValue = 0;
    protected final static String BEHAVIOR_BRIGHTNESS = "brightness";
    
    @Override
    public void init(){
    
        
        brightness = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_BRIGHTNESS));
        brightness.setValue(brightnessStoredValue);
        brightness.addListener(new RangedIntBehaviorLogic.Listener() {
            
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                brightnessStoredValue = brightness.getMin();
                executePowerOff(params);
            }
            
            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                brightnessStoredValue = brightness.getMax();
                executePowerOn(params);
            }
            
            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                executeBrightness(rangeValue, params);
            }
        });
        //register this behavior to the superclass to make it visible to it
        registerBehavior(brightness);
        super.init();
    }
    
    @Override
    public void executePowerOff(Config params) {
        // when a light is "powered off" its brightness is set to the minValue but the current value is stored
        brightness.setValue(brightness.getMin());
        // executeCommand the body of the super implementation. The super call
        // must be the last call as it executes setChanged(true)
        super.executePowerOff(params);
    }
    
    @Override
    public void executePowerOn(Config params) {
        // when a light is "powered on" its brightness is set to the stored value if this is greater than the minValue
        if (brightnessStoredValue > brightness.getMin()) {
            brightness.setValue(brightnessStoredValue);
        } else {
            brightness.setValue(brightness.getMax());
        }
        // executeCommand the body of the super implementation. The super call
        // must be the last call as it executes setChanged(true)
        super.executePowerOn(params);
    }
    
    public void executeBrightness(int rangeValue, Config params) {
        boolean executed = executeCommand("set brightness", params); //executes the developer level command associated with 'set brightness' action

        if (executed) {
            powered.setValue(true);
            brightness.setValue(rangeValue);
            brightnessStoredValue = brightness.getValue();
            //set the light graphical representation
            getPojo().setCurrentRepresentation(1); //points to the second element in the XML views array (light on image)
            setChanged(true);
        }
    }
    
    
    protected void CreateCommands(){
        
        Command brigh_up = new Command();
        brigh_up.setName("Increase " + getPojo().getName() + " brightness");
        brigh_up.setDescription("increases " + getPojo().getName() + " brightness of one step");
        brigh_up.setReceiver("app.events.sensors.behavior.request.objects");
        brigh_up.setProperty("object",
                getPojo().getName());
        brigh_up.setProperty("behavior", BEHAVIOR_BRIGHTNESS);
        brigh_up.setProperty("value", Behavior.VALUE_NEXT);
        
        Command brigh_down = new Command();
        brigh_down.setName("Decrease " + getPojo().getName() + " brightness");
        brigh_down.setDescription("decreases " + getPojo().getName() + " brightness of one step");
        brigh_down.setReceiver("app.events.sensors.behavior.request.objects");
        brigh_down.setProperty("object",
                getPojo().getName());
        brigh_down.setProperty("behavior", BEHAVIOR_BRIGHTNESS);
        brigh_down.setProperty("value", Behavior.VALUE_PREVIOUS);
        
        commandRepository.create(brigh_up);
        commandRepository.create(brigh_down);
    }
    
    
    @Override
    protected void createTriggers(){
        
        Trigger clicked = new Trigger();
        clicked.setName("When " + this.getPojo().getName() + " is clicked");
        clicked.setChannel("app.event.sensor.object.behavior.clicked");
        clicked.getPayload().addStatement("object.name",
                this.getPojo().getName());
        clicked.getPayload().addStatement("click", ObjectReceiveClick.SINGLE_CLICK);
        clicked.setPersistence(false);
        triggerRepository.create(clicked);
    }
    
}
