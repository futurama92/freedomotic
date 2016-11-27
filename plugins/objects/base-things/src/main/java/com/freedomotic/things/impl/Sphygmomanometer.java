/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;

/**
 *
 * @author ricca
 */
public class Sphygmomanometer extends ElectricDevice {
    
    private RangedIntBehaviorLogic maximumPressure;
    private RangedIntBehaviorLogic minimumPressure;
    private RangedIntBehaviorLogic batteryLevel;
    private int maximumValue = 60;
    private int minimumValue = 40;
    private int batteryValue = 90;
    private final static String BEHAVIOR_MAXIMUM = "maximumPressure";
    private final static String BEHAVIOR_MINIMUM = "minimumPressure";
    private final static String BEHAVIOR_BATTERY = "batteryLevel";

    @Override
    public void init() {
        
        maximumPressure = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_MAXIMUM));
        maximumPressure.setValue(maximumValue);
        maximumPressure.addListener(new RangedIntBehaviorLogic.Listener() {
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                maximumValue = maximumPressure.getMax();
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                maximumValue = maximumPressure.getMin();
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

        minimumPressure = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_MINIMUM));
        minimumPressure.setValue(minimumValue);
        minimumPressure.addListener(new RangedIntBehaviorLogic.Listener() {
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        batteryLevel = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_BATTERY));
        batteryLevel.setValue(batteryValue);
        batteryLevel.addListener(new RangedIntBehaviorLogic.Listener() {
            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        registerBehavior(batteryLevel);
        registerBehavior(maximumPressure);
        registerBehavior(minimumPressure);
        super.init();
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
    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
        Trigger low_battery = new Trigger();
        low_battery.setName("When " + this.getPojo().getName() + " battery is too low");
        low_battery.setChannel("app.event.sensor.object.behavior.change");
        low_battery.getPayload().addStatement("object.name", this.getPojo().getName());
        low_battery.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_BATTERY, "LESS_THAN", "5");
        low_battery.setPersistence(false);
        triggerRepository.create(low_battery);
        
        Trigger anomaly_pressure = new Trigger();
        anomaly_pressure.setName("When " + this.getPojo().getName() + " value is anomalous");
        anomaly_pressure.setChannel("app.event.sensor.object.behavior.change");
        anomaly_pressure.getPayload().addStatement("object.name", this.getPojo().getName());
        anomaly_pressure.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_MAXIMUM, "GREATER_THEN", "130");
        anomaly_pressure.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_MAXIMUM, "LESS_THEN", "90");
        anomaly_pressure.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_MINIMUM, "GREATER_THEN", "85");
        anomaly_pressure.getPayload().addStatement("OR", "object.behavior." + BEHAVIOR_MINIMUM, "LESS_THEN", "60");
        anomaly_pressure.setPersistence(false);
        triggerRepository.create(anomaly_pressure);
        
    }
}

    

