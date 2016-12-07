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

import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.reactions.Command;
import static com.freedomotic.things.impl.Light.BEHAVIOR_BRIGHTNESS;

/**
 *
 * @author Enrico Nicoletti
 */
public class Alertled extends ElectricDevice {
    
    private RangedIntBehaviorLogic simuleted_consumption;
    private int simuleted_consumptionValue = 0;
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

        registerBehavior(simuleted_consumption);
        super.init();
    }
    
    @Override
    public void executePowerOff(Config params) {
        super.executePowerOff(params);
    }
    
    @Override
    public void executePowerOn(Config params) {
        super.executePowerOn(params);
    }
    
    
    @Override
    protected void createCommands() {
        super.createCommands();
        Command g = new Command();
        g.setName("Turn on " + getPojo().getName());
        g.setDescription("set its brightness to the value in the event");
        g.setReceiver("app.events.sensors.behavior.request.objects");
        g.setProperty("object", "@event.object.name");
        g.setProperty("behavior", BEHAVIOR_BRIGHTNESS);
        g.setProperty("value", "@event.value");

    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
}
