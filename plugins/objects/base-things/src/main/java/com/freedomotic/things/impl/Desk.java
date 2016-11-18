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
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.EnvObjectLogic;

/**
 *
 * @author Enrico Nicoletti
 */
public class Desk extends EnvObjectLogic {
    
    private RangedIntBehaviorLogic height;
    private int heightStoredValue = 0;
    protected final static String BEHAVIOR_HEIGHT = "height";
    
    @Override
    public void init() {
        //linking this property with the behavior defined in the XML
//linking this property with the behavior defined in the XML
        
        height = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_HEIGHT));
        height.setValue(heightStoredValue);
        height.addListener(new RangedIntBehaviorLogic.Listener() {

            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                    executeSetHeight(rangeValue, params);
            }
        });
        
        //register new behaviors to the superclass to make it visible to it
        registerBehavior(height);
        super.init();
    }
    
    public void executeSetHeight(int rangeValue, Config params) {
        boolean executed = executeCommand("set height", params);
        if (executed) {
            height.setValue(rangeValue);
            heightStoredValue = height.getValue();
            setChanged(true);
        }
    }

    
    @Override
    protected void createCommands() {
        super.createCommands();
        
        
        Command b = new Command();
        // b.setName(I18n.msg("increase_X_height", new Object[]{this.getPojo().getName()}));
        b.setName("Increase " + getPojo().getName() + " height");
        b.setDescription("increases " + getPojo().getName() + " height of one step");
        b.setReceiver("app.events.sensors.behavior.request.objects");
        b.setProperty("object",
                getPojo().getName());
        b.setProperty("behavior", BEHAVIOR_HEIGHT);
        b.setProperty("value", Behavior.VALUE_NEXT);
        
        Command c = new Command();
        // c.setName(I18n.msg("decrease_X_height", new Object[]{this.getPojo().getName()}));
        c.setName("Decrease " + getPojo().getName() + " height");
        c.setDescription("decreases " + getPojo().getName() + " height of one step");
        c.setReceiver("app.events.sensors.behavior.request.objects");
        c.setProperty("object",
                getPojo().getName());
        c.setProperty("behavior", BEHAVIOR_HEIGHT);
        c.setProperty("value", Behavior.VALUE_PREVIOUS);

        commandRepository.create(b);
        commandRepository.create(c);

    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
}
