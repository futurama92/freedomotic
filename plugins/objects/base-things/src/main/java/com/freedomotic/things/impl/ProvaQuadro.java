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
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.Random;

/**
 *
 * @author Mauro Cicolella
 */
public class ProvaQuadro extends EnvObjectLogic {

    private static final Logger LOG = Logger.getLogger(ProvaQuadro.class.getName());
    private RangedIntBehaviorLogic brightness;
    private ListBehaviorLogic conditions;
    private int brightnessStoredValue = 0;
    private static final String BEHAVIOR_CONDITIONS = "conditions";
    private static final String BEHAVIOR_BRIGHTNESS = "brightness";
    public static  String BEHAVIOR_NATIONS = "no_nation";

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
        
        //register new behaviors to the superclass to make it visible to it
        registerBehavior(brightness);
        super.init();
        
        
     
        

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
       //setIcon();
        
       setNation();
    }
    

    public String switchNation(String check){
        
        if (check.equals("china"))
            return "china";
        if (check.equals("japan"))
            return "japan";
        
        return "default";
    }
    
    public void setNation(){
        int icon_number;
        
        if (conditions.getSelected().equals("china")){
           icon_number = random(1);
            getPojo().setCurrentRepresentation(icon_number);
        } else if (conditions.getSelected().equals("japan")){
           icon_number = random(2);
            getPojo().setCurrentRepresentation(icon_number);
        }
       

        else 
            getPojo().setCurrentRepresentation(0);
    }
    
    public int random(int nations)
    {
        int min = 1;
        int max = 5;
        int range;
        int randomNum;
        range = nations;
        min = min + max*(range-1);
        max = max + max*(range-1);
        Random rand = new Random();
        
        switch (range){
            case 1:
                randomNum = rand.nextInt(max - min + 1) + min;
                break;
            case 2:
                randomNum = rand.nextInt(max - min + 1) + min;
                break;
                
            default:
                randomNum = 0;
        }
                
        return randomNum;          
    }

    private void setIcon() {
        //getPojo().setCurrentRepresentation(1);
        if (conditions.getSelected().equals("china")) {
            getPojo().setCurrentRepresentation(1);
        }    else if (conditions.getSelected().equals("china1")) {
            getPojo().setCurrentRepresentation(2);
        } else if (conditions.getSelected().equals("china2")) {
            getPojo().setCurrentRepresentation(3);
        } else if (conditions.getSelected().equals("china3")) {
            getPojo().setCurrentRepresentation(4);
        } else if (conditions.getSelected().equals("china4")) {
            getPojo().setCurrentRepresentation(5);
        } 
        
    }
    

    /**
     * Creates user level commands for this class of freedomotic objects
     */
    @Override
    protected void createCommands() {
        
        ArrayList prova;
        prova = conditions.getValuesList();
        Command changeNext[] = new Command[prova.size()];
        for(int i = 0; i < prova.size(); i++){
            BEHAVIOR_NATIONS = (String) prova.get(i);
            
            changeNext[i].setName("Set " + getPojo().getName() + " next Image " + BEHAVIOR_NATIONS);
            changeNext[i].setDescription("Change the paint image " + getPojo().getName() + " to next");
            changeNext[i].setReceiver("app.events.sensors.behavior.request.objects");
            changeNext[i].setProperty("object", getPojo().getName());
            changeNext[i].setProperty("behavior", "conditions" );        
            changeNext[i].setProperty("value", BEHAVIOR_NATIONS );
            commandRepository.create(changeNext[i]);
        }
        
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