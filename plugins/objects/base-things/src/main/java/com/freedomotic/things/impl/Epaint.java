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
import java.util.Random;

/**
 *
 * @author Mauro Cicolella
 */
public class Epaint extends EnvObjectLogic {

    private static final Logger LOG = Logger.getLogger(Epaint.class.getName());
    private RangedIntBehaviorLogic brightness;
    private ListBehaviorLogic conditions;
    private ListBehaviorLogic conditions_2;
    private ListBehaviorLogic conditions_3;
    private ListBehaviorLogic conditions_4;    
    private ListBehaviorLogic conditions_5;        
    private int brightnessStoredValue = 0;
    private static final String BEHAVIOR_CONDITIONS = "conditions";
    private static final String BEHAVIOR_CONDITIONS_2 = "conditions_2";
    private static final String BEHAVIOR_CONDITIONS_3 = "conditions_3";
    private static final String BEHAVIOR_CONDITIONS_4 = "conditions_4";
    private static final String BEHAVIOR_CONDITIONS_5 = "conditions_5";
    private static final String BEHAVIOR_BRIGHTNESS = "brightness";

    @Override
    public void init() {
        
        getPojo().setCurrentRepresentation(0);
      
        
        //linking this property with the behavior defined in the XML
        
        brightness = new RangedIntBehaviorLogic((RangedIntBehavior) getPojo().getBehavior(BEHAVIOR_BRIGHTNESS));
        brightness.setValue(brightnessStoredValue);
        brightness.addListener(new RangedIntBehaviorLogic.Listener() {

            @Override
            public void onLowerBoundValue(Config params, boolean fireCommand) {
            }

            @Override
            public void onUpperBoundValue(Config params, boolean fireCommand) {
            }

            @Override
            public void onRangeValue(int rangeValue, Config params, boolean fireCommand) {
                    executeSetBrightness(rangeValue, params);
            }
        });

        registerBehavior(brightness);
        super.init();
        
        conditions = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_CONDITIONS));
        conditions.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setConditions(params.getProperty("value"), params, fireCommand);
            }
        });
        registerBehavior(conditions);
        super.init();
        
        conditions_2 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_CONDITIONS_2));
        conditions_2.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setConditions_2(params.getProperty("value"), params, fireCommand);
            }
        });
        registerBehavior(conditions_2);
        super.init();
        
        conditions_3 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_CONDITIONS_3));
        conditions_3.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setConditions_3(params.getProperty("value"), params, fireCommand);
            }
        });
        registerBehavior(conditions_3);
        super.init();
        
        conditions_4 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_CONDITIONS_4));
        conditions_4.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setConditions_4(params.getProperty("value"), params, fireCommand);
            }
        });
        registerBehavior(conditions_4);
        super.init();
        
        conditions_5 = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_CONDITIONS_5));
        conditions_5.addListener(new ListBehaviorLogic.Listener() {

            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                setConditions_5(params.getProperty("value"), params, fireCommand);
            }
        });
        registerBehavior(conditions_5);
        super.init();
                
                
        
    }
    
    
    public void executeSetBrightness(int rangeValue, Config params) {
        boolean executed = executeCommand("set brightness", params);
        if (executed) {
            brightness.setValue(rangeValue);
            brightnessStoredValue = brightness.getValue();
            setChanged(true);
        }
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
    public void setConditions_2(String selectedCondition, Config params, boolean fireCommand) {
        if (fireCommand) {
            if (executeCommand("set conditions_2", params)) {
                //Executed succesfully, update the value
                conditions_2.setSelected(selectedCondition);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            conditions_2.setSelected(selectedCondition);
            setChanged(true);
        }
        setIcon();
    }
    public void setConditions_3(String selectedCondition, Config params, boolean fireCommand) {
        if (fireCommand) {
            if (executeCommand("set conditions_3", params)) {
                //Executed succesfully, update the value
                conditions_3.setSelected(selectedCondition);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            conditions_3.setSelected(selectedCondition);
            setChanged(true);
        }
       setIcon();
       
    }
    public void setConditions_4(String selectedCondition, Config params, boolean fireCommand) {
        if (fireCommand) {
            if (executeCommand("set conditions_4", params)) {
                //Executed succesfully, update the value
                conditions_4.setSelected(selectedCondition);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            conditions_4.setSelected(selectedCondition);
            setChanged(true);
        }
       setIcon();
       
    }
    public void setConditions_5(String selectedCondition, Config params, boolean fireCommand) {
        if (fireCommand) {
            if (executeCommand("set conditions_5", params)) {
                //Executed succesfully, update the value
                conditions_5.setSelected(selectedCondition);
                setChanged(true);
            }
        } else {
            // Just a change in the virtual thing status
            conditions_5.setSelected(selectedCondition);
            setChanged(true);
        }
       setIcon();
       
    }



    private void setIcon() {
        //getPojo().setCurrentRepresentation(1);
        if (conditions.getSelected().equals("generic")) {
            getPojo().setCurrentRepresentation(0);
        }    else if (conditions.getSelected().equals("china")) {
            getPojo().setCurrentRepresentation(1);
        } else if (conditions.getSelected().equals("china1")) {
            getPojo().setCurrentRepresentation(2);
        } else if (conditions.getSelected().equals("china2")) {
            getPojo().setCurrentRepresentation(3);
        } else if (conditions.getSelected().equals("china3")) {
            getPojo().setCurrentRepresentation(4);
        } else if (conditions.getSelected().equals("china4")) {
            getPojo().setCurrentRepresentation(5);
        } 
        if (conditions_2.getSelected().equals("japan")) {
            getPojo().setCurrentRepresentation(6);
        } else if (conditions_2.getSelected().equals("japan1")) {
            getPojo().setCurrentRepresentation(7);
        } else if (conditions_2.getSelected().equals("japan2")) {
            getPojo().setCurrentRepresentation(8);
        } else if (conditions_2.getSelected().equals("japan3")) {
            getPojo().setCurrentRepresentation(9);
        } else if (conditions_2.getSelected().equals("japan4")) {
            getPojo().setCurrentRepresentation(10);
        } 
        
        if (conditions_3.getSelected().equals("brazil")) {
            getPojo().setCurrentRepresentation(11);
        } else if (conditions_3.getSelected().equals("brazil1")) {
            getPojo().setCurrentRepresentation(12);
        } else if (conditions_3.getSelected().equals("brazil2")) {
            getPojo().setCurrentRepresentation(13);
        } else if (conditions_3.getSelected().equals("brazil3")) {
            getPojo().setCurrentRepresentation(14);
        } else if (conditions_3.getSelected().equals("brazil4")) {
            getPojo().setCurrentRepresentation(15);
        } 
        
        if (conditions_4.getSelected().equals("usa")) {
            getPojo().setCurrentRepresentation(16);
        } else if (conditions_4.getSelected().equals("usa1")) {
            getPojo().setCurrentRepresentation(17);
        } else if (conditions_4.getSelected().equals("usa2")) {
            getPojo().setCurrentRepresentation(18);
        } else if (conditions_4.getSelected().equals("usa3")) {
            getPojo().setCurrentRepresentation(19);
        } else if (conditions_4.getSelected().equals("usa4")) {
            getPojo().setCurrentRepresentation(20);
        } 
    
        if (conditions_5.getSelected().equals("italy")) {
            getPojo().setCurrentRepresentation(21);
        } else if (conditions_5.getSelected().equals("italy1")) {
            getPojo().setCurrentRepresentation(22);
        } else if (conditions_5.getSelected().equals("italy2")) {
            getPojo().setCurrentRepresentation(23);
        } else if (conditions_5.getSelected().equals("italy3")) {
            getPojo().setCurrentRepresentation(24);
        } else if (conditions_5.getSelected().equals("italy4")) {
            getPojo().setCurrentRepresentation(25);
        } 
        
    }

    /**
     * Creates user level commands for this class of freedomotic objects
     */
    @Override
    protected void createCommands() {

        
        Command changeNext = new Command();
        changeNext.setName("Set " + getPojo().getName() + " next Image(china)");
        changeNext.setDescription("Change the paint image " + getPojo().getName() + " to next");
        changeNext.setReceiver("app.events.sensors.behavior.request.objects");
        changeNext.setProperty("object", getPojo().getName());
        changeNext.setProperty("behavior", BEHAVIOR_CONDITIONS );
        changeNext.setProperty("value", Behavior.VALUE_NEXT);
        commandRepository.create(changeNext);
        
        Command changeNext_2 = new Command();
        changeNext_2.setName("Set " + getPojo().getName() + " next Image(japan)");
        changeNext_2.setDescription("Change the paint image " + getPojo().getName() + " to next");
        changeNext_2.setReceiver("app.events.sensors.behavior.request.objects");
        changeNext_2.setProperty("object", getPojo().getName());
        changeNext_2.setProperty("behavior", BEHAVIOR_CONDITIONS_2);
        changeNext_2.setProperty("value", Behavior.VALUE_NEXT);
        commandRepository.create(changeNext_2);
        
        Command changeNext_3 = new Command();
        changeNext_3.setName("Set " + getPojo().getName() + " next Image(brazil)");
        changeNext_3.setDescription("Change the paint image " + getPojo().getName() + " to next");
        changeNext_3.setReceiver("app.events.sensors.behavior.request.objects");
        changeNext_3.setProperty("object", getPojo().getName());
        changeNext_3.setProperty("behavior", BEHAVIOR_CONDITIONS_3);
        changeNext_3.setProperty("value", Behavior.VALUE_NEXT);
        commandRepository.create(changeNext_3);
        
        Command changeNext_4 = new Command();
        changeNext_4.setName("Set " + getPojo().getName() + " next Image(usa)");
        changeNext_4.setDescription("Change the paint image " + getPojo().getName() + " to next");
        changeNext_4.setReceiver("app.events.sensors.behavior.request.objects");
        changeNext_4.setProperty("object", getPojo().getName());
        changeNext_4.setProperty("behavior", BEHAVIOR_CONDITIONS_4);
        changeNext_4.setProperty("value", Behavior.VALUE_NEXT);
        commandRepository.create(changeNext_4);
        
        Command changeNext_5 = new Command();
        changeNext_5.setName("Set " + getPojo().getName() + " next Image(italy)");
        changeNext_5.setDescription("Change the paint image " + getPojo().getName() + " to next");
        changeNext_5.setReceiver("app.events.sensors.behavior.request.objects");
        changeNext_5.setProperty("object", getPojo().getName());
        changeNext_5.setProperty("behavior", BEHAVIOR_CONDITIONS_5);
        changeNext_5.setProperty("value", Behavior.VALUE_NEXT);
        commandRepository.create(changeNext_5);
        
        
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
        
        
        
        Trigger paint = new Trigger();
        paint.setName("If " + this.getPojo().getName()+" is japan ");
        paint.setChannel("app.event.sensor.calendar.event.schedule");
        paint.getPayload().addStatement("object.name",
                this.getPojo().getName());
        paint.getPayload().addStatement("change", null);
        paint.setPersistence(false);
        triggerRepository.create(paint);
    }
}