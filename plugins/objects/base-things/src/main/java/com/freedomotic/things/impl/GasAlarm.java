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

/**
 *
 * @author Enrico Nicoletti
 */
public class GasAlarm extends ElectricDevice {
    
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
        getPojo().setCurrentRepresentation(0);
        super.executePowerOff(params);
    }
    
    @Override
    public void executePowerOn(Config params) {
        getPojo().setCurrentRepresentation(1);
        super.executePowerOn(params);
    }
    
    
    @Override
    protected void createCommands() {
        super.createCommands();
    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
}
