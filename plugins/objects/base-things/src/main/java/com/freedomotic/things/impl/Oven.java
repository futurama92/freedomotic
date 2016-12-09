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

import com.freedomotic.behaviors.BooleanBehaviorLogic;
import com.freedomotic.behaviors.ListBehaviorLogic;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.ListBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;
import static com.freedomotic.things.impl.Fridge.BEHAVIOR_SIMULETED_CONSUMPTION;

/**
 * A 'Ovening Machine' thing abstraction. Type is
 * EnvObject.ElectricDevice.OveningMachine
 * <p>
 * Behavior:
 * <ul>
 * <li>Any change to oven machine settings make the machine to power on
 * first</li>
 * <li>Powering off makes the oven machine to pause oven first</li>
 * <li>When selecting custom temperature or spinning rpm the oven program is
 * considered 'Custom'</li>
 * <li>All settings became read only after the oven machine startup</li>
 * </ul>
 * </p>
 * <p>
 * Notes for template configurators:
 * <ul>
 * <li>Do not remove or alter the 'Custom' oven program</li>
 * <li>Do not remove or alter the 'Finished' oven cycle</li>
 * </ul>
 *
 * @author Mauro Cicolella
 */
public class Oven extends ElectricDevice {

    private BooleanBehaviorLogic oven;
    private RangedIntBehaviorLogic ovenTemperature;
    private RangedIntBehaviorLogic ovenTimer;
    private RangedIntBehaviorLogic simuleted_consumption;
    private int simuleted_consumptionValue = 0;

    // The two main parameters of a basic oven machine
    protected final static String BEHAVIOR_OVEN = "oven";
    protected final static String BEHAVIOR_OVEN_TEMPERATURE = "ovenTemperature";
    protected final static String BEHAVIOR_OVEN_TIMER = "ovenTimer";
    protected final static String BEHAVIOR_SIMULETED_CONSUMPTION = "simuleted_consumption";

    @Override
    public void init() {
        
        
        simuleted_consumption = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_SIMULETED_CONSUMPTION));
        simuleted_consumption.setValue(simuleted_consumptionValue);
        simuleted_consumption.addListener(new RangedIntBehaviorLogic.Listener() {

            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                simuleted_consumptionValue = simuleted_consumption.getMin();
                executePowerOff(params);
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                simuleted_consumptionValue = simuleted_consumption.getMax();
                executePowerOn(params);
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                    executeSetPowerConsumption(rangeValue, params);
            }
        });
        
        oven = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_OVEN));
        //add a listener to values changes
        oven.addListener(new BooleanBehaviorLogic.Listener() {
            @Override
            public void onTrue(Config params, boolean fireCommand) {
                startOven(params, fireCommand);
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                pauseOven(params, fireCommand);
            }
        });

        // Set the temparature at which the oven machine should work
        ovenTemperature = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_OVEN_TEMPERATURE));
        ovenTemperature.addListener(new RangedIntBehaviorLogic.Listener() {

            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                setOvenTemperature(ovenTemperature.getMin(), params, fireCommand);
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                setOvenTemperature(ovenTemperature.getMax(), params, fireCommand);
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                setOvenTemperature(rangeValue, params, fireCommand);
            }
        });

        ovenTimer = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_OVEN_TIMER));
        ovenTimer.addListener(new RangedIntBehaviorLogic.Listener() {

            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
                setOvenTimer(ovenTimer.getMin(), params, fireCommand);
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
                setOvenTimer(ovenTimer.getMax(), params, fireCommand);
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                setOvenTimer(rangeValue, params, fireCommand);
            }
        });

       
        registerBehavior(oven);
        registerBehavior(simuleted_consumption);
        registerBehavior(ovenTemperature);
        registerBehavior(ovenTimer);
        super.init();
    }
    
    
    public void executeSetPowerConsumption(int rangeValue, Config params) {
        boolean executed = executeCommand("set waterLevel", params);
        if (executed) {
            simuleted_consumption.setValue(rangeValue);
            simuleted_consumptionValue = simuleted_consumption.getValue();
            setChanged(true);
        }
    }

    @Override
    public void executePowerOff(Config params) {
        // Pause oven before turning off
        pauseOven(params, true);
        // Resume normal poweroff procedure from superclass
        super.executePowerOff(params);
    }

    public void startOven(Config params, boolean fireCommand) {
        executePowerOn(params);
        if (fireCommand) {
            // Action on the hardware is required
            if (executeCommand("start oven", params)) {
                //Executed succesfully, update the value
                oven.setValue(true);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            oven.setValue(true);
            setChanged(true);
        }
    }

    public void pauseOven(Config params, boolean fireCommand) {
        if (fireCommand) {
            // Action on the hardware is required
            if (executeCommand("pause oven", params)) {
                //Executed succesfully, update the value
                oven.setValue(false);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            oven.setValue(false);
            setChanged(true);
        }
    }

    public void setOvenTemperature(int value, Config params, boolean fireCommand) {
        executePowerOn(params);
        if (fireCommand) {
            // Action on the hardware is required
            if (executeCommand("set oven temperature", params)) {
                //Executed succesfully, update the value
                ovenTemperature.setValue(value);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            ovenTemperature.setValue(value);
            setChanged(true);
        }
    }


    public void setOvenTimer(int value, Config params, boolean fireCommand) {

        executePowerOn(params);
        if (fireCommand) {
            // Action on the hardware is required
            if (executeCommand("set spinning rpm", params)) {
                //Executed succesfully, update the value
                ovenTimer.setValue(value);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            ovenTimer.setValue(value);
            setChanged(true);
        }
    }

    

    @Override
    protected void createCommands() {
        super.createCommands();

        Command increareOvenTemp = new Command();
        increareOvenTemp.setName("Increase " + getPojo().getName() + " oven temperature");
        increareOvenTemp.setDescription("increases " + getPojo().getName() + " oven temperature of one step");
        increareOvenTemp.setReceiver("app.events.sensors.behavior.request.objects");
        increareOvenTemp.setProperty("object", getPojo().getName());
        increareOvenTemp.setProperty("behavior", BEHAVIOR_OVEN_TEMPERATURE);
        increareOvenTemp.setProperty("value", Behavior.VALUE_NEXT);

        Command decreaseOvenTemp = new Command();
        decreaseOvenTemp.setName("Decrease " + getPojo().getName() + " oven temperature");
        decreaseOvenTemp.setDescription("decreases " + getPojo().getName() + " oven temperature of one step");
        decreaseOvenTemp.setReceiver("app.events.sensors.behavior.request.objects");
        decreaseOvenTemp.setProperty("object", getPojo().getName());
        decreaseOvenTemp.setProperty("behavior", BEHAVIOR_OVEN_TEMPERATURE);
        decreaseOvenTemp.setProperty("value", Behavior.VALUE_PREVIOUS);
        
        Command timer_0 = new Command();
        timer_0.setName("Turn " + getPojo().getName() + " to 0 power");
        timer_0.setDescription("Stop " + getPojo().getName() + " consumption");
        timer_0.setReceiver("app.events.sensors.behavior.request.objects");
        timer_0.setProperty("object", getPojo().getName());
        timer_0.setProperty("behavior", BEHAVIOR_SIMULETED_CONSUMPTION);
        timer_0.setProperty("value", "0");
        commandRepository.create(timer_0);
        


        //TODO: add missing commands!
        commandRepository.create(increareOvenTemp);
        commandRepository.create(decreaseOvenTemp);

    }

    @Override
    protected void createTriggers() {
        super.createTriggers();
        Trigger trigger_timer_0 = new Trigger();
        trigger_timer_0.setName("When " + this.getPojo().getName() + " timer goes to 0");
        trigger_timer_0.setChannel("app.event.sensor.object.behavior.change");
        trigger_timer_0.getPayload().addStatement("object.name", this.getPojo().getName());
        trigger_timer_0.getPayload().addStatement("AND", "object.behavior." + BEHAVIOR_OVEN_TIMER, "EQUALS", "0");
        trigger_timer_0.setPersistence(false);
        triggerRepository.create(trigger_timer_0);
    }
}
