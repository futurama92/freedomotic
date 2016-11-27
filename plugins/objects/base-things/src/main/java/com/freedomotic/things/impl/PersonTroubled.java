/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.freedomotic.things.impl;

import com.freedomotic.behaviors.BooleanBehaviorLogic;
import com.freedomotic.behaviors.ListBehaviorLogic;
import com.freedomotic.behaviors.PropertiesBehaviorLogic;
import com.freedomotic.events.ObjectReceiveClick;
import com.freedomotic.model.ds.Config;
import com.freedomotic.model.object.BooleanBehavior;
import com.freedomotic.model.object.ListBehavior;
import com.freedomotic.model.object.PropertiesBehavior;
import com.freedomotic.reactions.Trigger;
import com.freedomotic.things.GenericPerson;
import java.util.logging.Logger;

/**
 *
 * @author ricca
 */
public class PersonTroubled extends GenericPerson {
    
    protected BooleanBehaviorLogic present;
    protected BooleanBehaviorLogic troubled;
    protected ListBehaviorLogic activity;
    protected PropertiesBehaviorLogic properties;
    private static final String BEHAVIOR_PRESENT = "present";
    private static final String BEHAVIOR_TROUBLED = "troubled";
    private static final String BEHAVIOR_PROPERTIES = "properties";
    private static final String BEHAVIOR_ACTIVITY = "activity";

    @Override
    public void init() {
        present = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_PRESENT));
        //add a listener to values changes
        present.addListener(new BooleanBehaviorLogic.Listener() {
            @Override
            public void onTrue(Config params, boolean fireCommand) {
                setPresent();
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                setNotPresent();
            }
        });
        
        
        troubled = new BooleanBehaviorLogic((BooleanBehavior) getPojo().getBehavior(BEHAVIOR_TROUBLED));
        //add a listener to values changes
        troubled.addListener(new BooleanBehaviorLogic.Listener() {
            @Override
            public void onTrue(Config params, boolean fireCommand) {
                setTroubled();
            }

            @Override
            public void onFalse(Config params, boolean fireCommand) {
                setTroubled();
            }
        });

        activity = new ListBehaviorLogic((ListBehavior) getPojo().getBehavior(BEHAVIOR_ACTIVITY));
        activity.addListener(new ListBehaviorLogic.Listener() {
            @Override
            public void selectedChanged(Config params, boolean fireCommand) {
                String oldActivity = activity.getSelected();
                //in "value" property is stored the name of the new selection. It is a value from the list for sure and it is not the current one, already checked.
                activity.setSelected(params.getProperty("value"));
                LOG.severe("Person '" + getPojo().getName()
                        + "' has changed its activity from " + oldActivity + " to "
                        + activity.getSelected());
                setChanged(true);
            }
        });

        properties = new PropertiesBehaviorLogic((PropertiesBehavior) getPojo().getBehavior(BEHAVIOR_PROPERTIES));
        properties.addListener(new PropertiesBehaviorLogic.Listener() {
            @Override
            public void propertyChanged(String key, String newValue, Config params, boolean fireCommand) {
                //in "value" property is stored the name of the new selection. It is a value from the list for sure and it is not the current one, already checked.
                // properties.setProperty(key, newValue);
                LOG.severe("Person '" + getPojo().getName()
                        + "' has changed its property from " + params.getProperty(key) + " to "
                        + newValue);
                setChanged(true);
            }
        });

        //register this behavior to the superclass to make it visible to it
        registerBehavior(present);
        registerBehavior(troubled);
        registerBehavior(activity);
        registerBehavior(properties);
        //caches hardware level commands and builds user command
        super.init();
    }

    private void setPresent() {
        present.setValue(true);
        getPojo().setCurrentRepresentation(1);
        setChanged(true);
    }
    
    private void setTroubled() {
        troubled.setValue(true);
        getPojo().setCurrentRepresentation(0);
        setChanged(true);
    }

    private void setNotPresent() {
        present.setValue(false);
        getPojo().setCurrentRepresentation(0);
        setChanged(true);
    }

    /**
     *
     */
    @Override
    protected void createCommands() {
    }

    /**
     *
     */
    @Override
    protected void createTriggers() {
        Trigger clicked = new Trigger();
        clicked.setName("When " + this.getPojo().getName() + " is clicked");
        clicked.setChannel("app.event.sensor.object.behavior.clicked");
        clicked.getPayload().addStatement("object.name", this.getPojo().getName());
        clicked.getPayload().addStatement("click", ObjectReceiveClick.SINGLE_CLICK);
        clicked.setPersistence(false);

        Trigger login = new Trigger();
        login.setName("When account " + this.getPojo().getName() + " logs in");
        login.setChannel("app.event.sensor.account.change");
        login.getPayload().addStatement("object.action", "LOGIN");
        login.setPersistence(false);

        Trigger logout = new Trigger();
        logout.setName("When account " + this.getPojo().getName() + " logs out");
        logout.setChannel("app.event.sensor.account.change");
        logout.getPayload().addStatement("object.action", "LOGOUT");
        logout.setPersistence(false);

        triggerRepository.create(clicked);
        triggerRepository.create(login);
        triggerRepository.create(logout);
    }
    private static final Logger LOG = Logger.getLogger(Person.class.getName());
    
}
