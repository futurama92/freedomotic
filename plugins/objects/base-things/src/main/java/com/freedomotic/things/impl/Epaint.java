/**
 *
 * Copyright (c) 2009-2015 Freedomotic team http://freedomotic.com
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
import com.freedomotic.events.ObjectReceiveClick;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.RangedIntBehavior;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.behaviors.RangedIntBehaviorLogic;
import com.freedomotic.model.object.Behavior;
import com.freedomotic.model.object.ListBehavior;
import com.freedomotic.reactions.Command;
import com.freedomotic.reactions.Trigger;
import java.util.logging.Logger;

/**
 *
 * @author Mauro Cicolella
 */
public class Epaint extends EnvObjectLogic {

    private static final Logger LOG = Logger.getLogger(Epaint.class.getName());
    private RangedIntBehaviorLogic brightness;
    private ListBehaviorLogic conditions;
    private int brightnessStoredValue = 0;
    private static final String BEHAVIOR_CONDITIONS = "conditions";
    private static final String BEHAVIOR_BRIGHTNESS = "brightness";

    @Override
    public void init() {
        getPojo().setCurrentRepresentation(0);
        //linking this property with the behavior defined in the XML

        conditions = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_CONDITIONS));
        conditions.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setConditions(params.getProperty("value"), params, fireCommand);
            }
        });

        //register new behaviors to the superclass to make it visible to it
        registerBehavior(conditions);

        super.init();
    }
    

    public void setConditions(String selectedCondition, Config params, boolean fireCommand) {
        if (fireCommand) {
            if (executeCommand("set conditions", params)) {
                //Executed succesfully, update the value
                conditions.setSelected(selectedCondition);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            conditions.setSelected(selectedCondition);
            setChanged(true);
        }
        setIcon();
    }

    private void setIcon() {
        //getPojo().setCurrentRepresentation(1);
        if (conditions.getSelected().equals("epaint_off")) {
            getPojo().setCurrentRepresentation(0);
        }    else if (conditions.getSelected().equals("wall china")) {
            getPojo().setCurrentRepresentation(1);
        } else if (conditions.getSelected().equals("china1")) {
            getPojo().setCurrentRepresentation(2);
        } else if (conditions.getSelected().equals("china2")) {
            getPojo().setCurrentRepresentation(3);
        }else if (conditions.getSelected().equals("china3")) {
            getPojo().setCurrentRepresentation(4);
        } 
    }

    /**
     * Creates user level commands for this class of freedomotic objects
     */
    @Override
    protected void createCommands() {

    }

    @Override
    protected void createTriggers() {
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