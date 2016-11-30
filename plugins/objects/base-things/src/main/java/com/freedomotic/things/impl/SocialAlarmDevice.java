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
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.reactions.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Riccardo Trivellato
 */
public class SocialAlarmDevice extends ElectricDevice {
    
    protected BooleanBehaviorLogic callAlarmSystem;
    protected final static String BEHAVIOR_CALLALLARM = "callAlarmSystem";
    private static final Logger LOG = LoggerFactory.getLogger(ElectricDevice.class.getName());
    
    @Override
    public void init() {
        
        callAlarmSystem = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_CALLALLARM));
        callAlarmSystem.addListener(new BooleanBehaviorLogic.Listener() {
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
                    executePowerOff(params); //executes a turn on command and then sets the object behavior to on
                } else {
                    setOff(); //sets the object behavior to on as a result from a notified value
                }
            }
        });
        
        registerBehavior(callAlarmSystem);
        super.init();
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
    
    private void setOn() {
        LOG.info("Setting behavior ''powered'' of object ''{}'' to true", getPojo().getName());

        //if not already on
        if (callAlarmSystem.getValue() != true) {
            //setting the object as powered
            callAlarmSystem.setValue(true);
            //setting the second view from the XML list (the one with the on light bulb image)
            getPojo().setCurrentRepresentation(1);
            setChanged(true);
        }
    }

    private void setOff() {
        LOG.info("Setting behavior ''" + BEHAVIOR_CALLALLARM + "'' of object ''{}'' to false", getPojo().getName());

        //if not already off
        if (callAlarmSystem.getValue() != false) {
            callAlarmSystem.setValue(false);
            getPojo().setCurrentRepresentation(0);
            setChanged(true);
        }
    }

    @Override
    protected void createCommands() {
        super.createCommands();
        Command call = new Command();
        call.setName("Call 118 with " + getPojo().getName());
        call.setDescription("An allarm call when user has troubled");
        call.setProperty("object", getPojo().getName());
        call.setProperty("behavior", BEHAVIOR_CALLALLARM);
        call.setProperty("value", "true");
        commandRepository.create(call);
        
    }
    
    @Override
    protected void createTriggers() {
        super.createTriggers();
    }
}
