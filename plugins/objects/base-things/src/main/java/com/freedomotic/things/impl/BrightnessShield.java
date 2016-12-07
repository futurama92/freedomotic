/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;
import static com.freedomotic.things.impl.Light.BEHAVIOR_BRIGHTNESS;

/**
 *
 * @author futurama92
 */
public class BrightnessShield extends ElectricDevice {
    
    private RangedIntBehaviorLogic shield;
    private int shieldValue = 0;
    private static final String BEHAVIOR_SHIELD = "shield";
    
    @Override
    public void init(){
        
        shield = new RangedIntBehaviorLogic((RangedIntBehavior)getPojo().getBehavior(BEHAVIOR_SHIELD));
        shield.setValue(shieldValue);
        shield.addListener(new RangedIntBehaviorLogic.Listener() {
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
               shieldValue = shield.getMin();
               executePowerOff(params);
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
               shieldValue = shield.getMax();
               executePowerOn(params);
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                executeShield(rangeValue, params);
            }
        });
        
        registerBehavior(shield);
        super.init();
        
    }
    
    @Override
    public void executePowerOff(Config params){
        
        shield.setValue(shield.getMin());
        super.executePowerOff(params);
        
    }
    
    @Override
    public void executePowerOn(Config params){
        
        if (shieldValue > shield.getMin())
            shield.setValue(shieldValue);
        else
            shield.setValue(shield.getMax());
        
        super.executePowerOn(params);
        
    }
    
    public void executeShield(int rangeValue, Config params){
        
        boolean executed = executeCommand("set shield", params); //executes the developer level command associated with 'set brightness' action

        if (executed) {
            powered.setValue(true);
            shield.setValue(rangeValue);
            shieldValue = shield.getValue();
            setChanged(true);
        }

    }
    
    @Override
    protected void createCommands(){
        
        super.createCommands();
        
        Command shield_50 = new Command();
        shield_50.setName("Set " + getPojo().getName() + " shield to 50%");
        shield_50.setDescription("Set " + getPojo().getName() + " shield valure to 50%");
        shield_50.setReceiver("app.events.sensors.behavior.request.objects");
        shield_50.setProperty("object", getPojo().getName());
        shield_50.setProperty("behavior", BEHAVIOR_SHIELD);
        shield_50.setProperty("value", "50");
        
        Command shield_0 = new Command();
        shield_0.setName("Set " + getPojo().getName() + " shield to 0%");
        shield_0.setDescription("Set " + getPojo().getName() + " shield valure to 0%");
        shield_0.setReceiver("app.events.sensors.behavior.request.objects");
        shield_0.setProperty("object", getPojo().getName());
        shield_0.setProperty("behavior", BEHAVIOR_SHIELD);
        shield_0.setProperty("value", "0");
        
        Command increase_shield = new Command();
        increase_shield.setName("Increase " + getPojo().getName() + " shield value");
        increase_shield.setDescription("Increase " + getPojo().getName() + " to next value");
        increase_shield.setReceiver("app.events.sensors.behavior.request.objects");
        increase_shield.setProperty("object", getPojo().getName());
        increase_shield.setProperty("object", getPojo().getName());
        increase_shield.setProperty("behavior", BEHAVIOR_SHIELD);
        increase_shield.setProperty("value", Behavior.VALUE_NEXT);
        
        Command decrease_shield = new Command();
        decrease_shield.setName("Decrease " + getPojo().getName() + " shield value");
        decrease_shield.setDescription("Decrease " + getPojo().getName() + " to previous value");
        decrease_shield.setReceiver("app.events.sensors.behavior.request.objects");
        decrease_shield.setProperty("object", getPojo().getName());
        decrease_shield.setProperty("object", getPojo().getName());
        decrease_shield.setProperty("behavior", BEHAVIOR_SHIELD);
        decrease_shield.setProperty("value", Behavior.VALUE_PREVIOUS);
        
        Command g = new Command();
        g.setName("Set " + getPojo().getName() + " value to the value in the event");
        g.setDescription("Set " + getPojo().getName() + " value to the value in the event");
        g.setReceiver("app.events.sensors.behavior.request.objects");
        g.setProperty("object", "@event.object.name");
        g.setProperty("behavior", BEHAVIOR_BRIGHTNESS);
        g.setProperty("value", "@event.value");
        
        commandRepository.create(g);
        commandRepository.create(decrease_shield);
        commandRepository.create(increase_shield);
        commandRepository.create(shield_50);
        commandRepository.create(shield_0);
    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
    
    
    
}
