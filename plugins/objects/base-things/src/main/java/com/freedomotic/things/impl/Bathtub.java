/**
 *
 * Copyright (c) 2009-2016 Freedomotic team http://freedomotic.com
 *
 * This file is part of Freedomotic
 *
 * This Program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * This Program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Freedomotic; see the file COPYING. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.ListBehaviorLogic;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.object.ListBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;
import com.freedomotic.things.EnvObjectLogic;

/**
 *
 * @author Enrico Nicoletti
 */
public class Bathtub extends EnvObjectLogic {
    
    private ListBehaviorLogic waterType;
    private RangedIntBehaviorLogic waterLevel;
    private int waterLevelValue = 0;
    protected final static String BEHAVIOR_WATER = "waterLevel";
    protected final static String BEHAVIOR_WATERTYPE = "waterType";
    @Override
    public void init() {
        //linking this property with the behavior defined in the XML
//linking this property with the behavior defined in the XML
        
        waterLevel = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_WATER));
        waterLevel.setValue(waterLevelValue);
        waterLevel.addListener(new RangedIntBehaviorLogic.Listener() {

            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                waterLevelValue = waterLevel.getMin();
                executePowerOff(params);
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                waterLevelValue = waterLevel.getMax();
                executePowerOn(params);
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                    executeSetWaterLevel(rangeValue, params);
            }
        });
        
        waterType = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_WATERTYPE));
        waterType.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setWaterType(params.getProperty("value"), params, fireCommand);
            }
        });
        
        registerBehavior(waterType);
        registerBehavior(waterLevel);
        super.init();
    }
    
    
    public void setWaterType(String selectedCondition, Config params, boolean fireCommand) {
        if (fireCommand) {
            if (executeCommand("set conditions", params)) {
                //Executed succesfully, update the value
                waterType.setSelected(selectedCondition);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            waterType.setSelected(selectedCondition);
            setChanged(true);
        }
    }
    
    public void executeSetWaterLevel(int rangeValue, Config params) {
        boolean executed = executeCommand("set waterLevel", params);
        if (executed) {
            waterLevel.setValue(rangeValue);
            waterLevelValue = waterLevel.getValue();
            setChanged(true);
        }
    }
    
    public void executePowerOff(Config params) {
        // when a light is "powered off" its brightness is set to the minValue but the current value is stored
        waterLevel.setValue(waterLevel.getMin());
        // executeCommand the body of the super implementation. The super call
        // must be the last call as it executes setChanged(true)
    }
    
    public void executePowerOn(Config params) {
        // when a light is "powered on" its brightness is set to the stored value if this is greater than the minValue
        if (waterLevelValue > waterLevel.getMin()) {
            waterLevel.setValue(waterLevelValue);
        } else {
            waterLevel.setValue(waterLevel.getMax());
        }
        // executeCommand the body of the super implementation. The super call
        // must be the last call as it executes setChanged(true)
    }

    
    @Override
    protected void createCommands() {
        super.createCommands();
        
        
        Command b = new Command();
        b.setName("Increase " + getPojo().getName() + " waterLevel");
        b.setDescription("increases " + getPojo().getName() + " waterLevel of one step");
        b.setReceiver("app.events.sensors.behavior.request.objects");
        b.setProperty("object", getPojo().getName());
        b.setProperty("behavior", BEHAVIOR_WATER);
        b.setProperty("value", Behavior.VALUE_NEXT);
        
        Command c = new Command();
        c.setName("Decrease " + getPojo().getName() + " waterLevel");
        c.setDescription("decreases " + getPojo().getName() + " waterLevel of one step");
        c.setReceiver("app.events.sensors.behavior.request.objects");
        c.setProperty("object", getPojo().getName());
        c.setProperty("behavior", BEHAVIOR_WATER);
        c.setProperty("value", Behavior.VALUE_PREVIOUS);
        commandRepository.create(b);
        commandRepository.create(c);
        
        Command d = new Command();
        d.setName("Turn " + getPojo().getName() + " off");
        d.setDescription("Stop " + getPojo().getName() + " waterLevel");
        d.setReceiver("app.events.sensors.behavior.request.objects");
        d.setProperty("object", getPojo().getName());
        d.setProperty("behavior", "powered");
        d.setProperty("value", "false");
        commandRepository.create(d);
        
        Command block_water = new Command();
        block_water.setName("Turn " + getPojo().getName() + " off");
        block_water.setDescription("Stop " + getPojo().getName() + " waterLevel");
        block_water.setReceiver("app.events.sensors.behavior.request.objects");
        block_water.setProperty("object", getPojo().getName());
        block_water.setProperty("behavior", "waterLevel");
        block_water.setProperty("value", "95");
        commandRepository.create(block_water);
        

    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
            
        Trigger soft_water_level = new Trigger();
        soft_water_level.setName("When " + this.getPojo().getName() + " water Level is 85%");
        soft_water_level.setChannel("app.event.sensor.object.behavior.change");
        soft_water_level.getPayload().addStatement("object.name", this.getPojo().getName());
        soft_water_level.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_WATER, "EQUAL", "85");
        soft_water_level.setPersistence(false);
        triggerRepository.create(soft_water_level);
        
                    
        Trigger hard_water_level = new Trigger();
        hard_water_level.setName("When " + this.getPojo().getName() + " water Level is 95%");
        hard_water_level.setChannel("app.event.sensor.object.behavior.change");
        hard_water_level.getPayload().addStatement("object.name", this.getPojo().getName());
        hard_water_level.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_WATER, "GREATER_THAN", "94");
        hard_water_level.setPersistence(false);
        triggerRepository.create(hard_water_level);
        
    }
}
