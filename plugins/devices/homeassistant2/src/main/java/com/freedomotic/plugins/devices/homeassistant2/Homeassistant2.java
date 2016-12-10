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
package com.freedomotic.plugins.devices.homeassistant2;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.events.LocationEvent;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.model.geometry.FreedomPoint;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.GenericPerson;
import com.freedomotic.things.impl.ElectricDevice;
import com.sun.webkit.Timer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.TimerTask;

/**
 *
 * @author Mauro Cicolella
 */
public class Homeassistant2 extends Protocol {

    private static final Logger LOG = LoggerFactory.getLogger(Homeassistant2.class.getName());
    CheckPower pow;
    static int i = 0;
    
    public Homeassistant2() {
        super("Homeassistant2", "/homeassistant2/homeassistant2-manifest.xml");
        setPollingWait(2000);
    }
    
    @Override
    protected void onStart(){ 
        int cont = 0;
        for (EnvObjectLogic object : getApi().things().findAll()){
            if (object.getPojo().getName().equals("Sphygmomanometer") || object.getPojo().getName().equals("Glucometer") 
                || object.getPojo().getName().equals("Oximeter") || object.getPojo().getName().equals("HeartSensor")  ){
                cont++;
            }
        }
        if (cont != 4){
            createObject();
            moveObject(); 
        }
        createUsers();
        
    }

    @Override
    protected void onRun() {
        LOG.info("Starting Assistan Home");
        if (i == 0){
            moveObject();
            moveUsers();
            i++;
        }
       findElectricObject();
        
    }
    
    @Override
    protected void onStop() {
       deleteObject(); 
       LOG.info("Stop Assistant home");
    }

    @Override
    protected void onCommand(Command c)
            throws IOException, UnableToExecuteException {throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected boolean canExecute(Command c) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        //don't mind this method for now
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void createUsers() {
        
        int max = 3;
        int min = 1;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        
        for(int i = 0; i < randomNum; i++){
            
            ProtocolRead event = new ProtocolRead(this, "unknown", "unknown");
            event.getPayload().addStatement("object.class", "User_Troubled");
            event.getPayload().addStatement("object.name", "CFUser" + i);
            event.getPayload().addStatement("object.actAs", "virtual");
            this.notifyEvent(event);
            
            Command c = new Command();
            c.setName("Join Custom User Object");
            c.setReceiver("app.objects.create");
            c.setProperty("object.class", "User_Troubled");
            c.setProperty("object.name", "User_troubled" + i);
            c.setProperty("object.protocol", "unknown");
            c.setProperty("object.address", "unknown");
            c.setProperty("object.actAs", "virtual");
            this.notifyCommand(c);             
        }  
        
        for(int i = 0; i < 3 - randomNum; i++){
            
            ProtocolRead event2 = new ProtocolRead(this, "unknown", "unknown");
            event2.getPayload().addStatement("object.class", "User");
            event2.getPayload().addStatement("object.name", "User" + i);
            event2.getPayload().addStatement("object.actAs", "virtual");
            this.notifyEvent(event2);
            
            Command c2 = new Command();
            c2.setName("Join Custom User Object");
            c2.setReceiver("app.objects.create");
            c2.setProperty("object.class", "User");
            c2.setProperty("object.name", "User" + i);
            c2.setProperty("object.protocol", "unknown");
            c2.setProperty("object.address", "unknown");
            c2.setProperty("object.actAs", "virtual");
            this.notifyCommand(c2);           
        } 
    }
    
    private void findElectricObject(){
        
        List<EnvObjectLogic> list = new ArrayList<>();
        for (EnvObjectLogic object : getApi().things().findAll()) {
            if (object instanceof ElectricDevice) {
                
                list.add(object);
            }
        }
        pow = new CheckPower(list);  
        pow.countConsumption();

    }
    
    public void createObject() {

        //create Sphygmomanometer
        ProtocolRead event = new ProtocolRead(this, "unknown", "unknown");
        event.getPayload().addStatement("object.class", "Sphygmomanometer");
        event.getPayload().addStatement("object.name", "Sphygmomanometer");
        event.getPayload().addStatement("object.actAs", "virtual");
        this.notifyEvent(event);

        Command c = new Command();
        c.setName("Add Sphygmomanometer");
        c.setReceiver("app.objects.create");
        c.setProperty("object.class", "Sphygmomanometer");
        c.setProperty("object.name", "Sphygmomanometer");
        c.setProperty("object.protocol", "unknown");
        c.setProperty("object.address", "unknown");
        c.setProperty("object.actAs", "virtual");
        this.notifyCommand(c);   
        LOG.info("Sphygmomanometer create");

        //create Glucometer
        ProtocolRead event2 = new ProtocolRead(this, "unknown", "unknown");
        event2.getPayload().addStatement("object.class", "Glucometer");
        event2.getPayload().addStatement("object.name", "Glucometer");
        event2.getPayload().addStatement("object.actAs", "virtual");
        this.notifyEvent(event2);
        LOG.info("Glucometer create");

        Command c2 = new Command();
        c2.setName("Add Glucometer");
        c2.setReceiver("app.objects.create");
        c2.setProperty("object.class", "Glucometer");
        c2.setProperty("object.name", "Glucometer");
        c2.setProperty("object.protocol", "unknown");
        c2.setProperty("object.address", "unknown");
        c2.setProperty("object.actAs", "virtual");
        this.notifyCommand(c2);
        
        //create Oximeter
        ProtocolRead event3 = new ProtocolRead(this, "unknown", "unknown");
        event3.getPayload().addStatement("object.class", "Oximeter");
        event3.getPayload().addStatement("object.name", "Oximeter");
        event3.getPayload().addStatement("object.actAs", "virtual");
        this.notifyEvent(event3);
        LOG.info("Oximeter create");

        Command c3 = new Command();
        c3.setName("Add Oximeter");
        c3.setReceiver("app.objects.create");
        c3.setProperty("object.class", "Oximeter");
        c3.setProperty("object.name", "Oximeter");
        c3.setProperty("object.protocol", "unknown");
        c3.setProperty("object.address", "unknown");
        c3.setProperty("object.actAs", "virtual");
        this.notifyCommand(c3);
        
        //create Heart Sensor
        ProtocolRead event4 = new ProtocolRead(this, "unknown", "unknown");
        event4.getPayload().addStatement("object.class", "HeartSensor");
        event4.getPayload().addStatement("object.name", "HeartSensor");
        event4.getPayload().addStatement("object.actAs", "virtual");
        this.notifyEvent(event4);
        LOG.info("HeartSensor create");

        Command c4 = new Command();
        c4.setName("Add HeartSensor");
        c4.setReceiver("app.objects.create");
        c4.setProperty("object.class", "HeartSensor");
        c4.setProperty("object.name", "HeartSensor");
        c4.setProperty("object.protocol", "unknown");
        c4.setProperty("object.address", "unknown");
        c4.setProperty("object.actAs", "virtual");
        this.notifyCommand(c4);
        
        //create Social Alarm
        ProtocolRead event5 = new ProtocolRead(this, "unknown", "unknown");
        event5.getPayload().addStatement("object.class", "SocialAlarmDevice");
        event5.getPayload().addStatement("object.name", "SocialAlarmDevice");
        event5.getPayload().addStatement("object.actAs", "virtual");
        this.notifyEvent(event5);

        Command c5 = new Command();
        c5.setName("Add SocialAlarmDevice");
        c5.setReceiver("app.objects.create");
        c5.setProperty("object.class", "SocialAlarmDevice");
        c5.setProperty("object.name", "SocialAlarmDevice");
        c5.setProperty("object.protocol", "unknown");
        c5.setProperty("object.address", "unknown");
        c5.setProperty("object.actAs", "virtual");
        this.notifyCommand(c5);   
        LOG.info("SocialAlarmDevice create");
        
        //create alert led
        ProtocolRead event6 = new ProtocolRead(this, "unknown", "unknown");
        event6.getPayload().addStatement("object.class", "Alertled");
        event6.getPayload().addStatement("object.name", "Alertled");
        event6.getPayload().addStatement("object.actAs", "virtual");
        this.notifyEvent(event6);

        Command c6 = new Command();
        c6.setName("Add Alertled");
        c6.setReceiver("app.objects.create");
        c6.setProperty("object.class", "Alertled");
        c6.setProperty("object.name", "Alertled");
        c6.setProperty("object.protocol", "unknown");
        c6.setProperty("object.address", "unknown");
        c6.setProperty("object.actAs", "virtual");
        this.notifyCommand(c6);   
        LOG.info("Alertled create");
    }
    
    private void moveObject(){
        for(EnvObjectLogic object : getApi().things().findAll()){
            if (object.getPojo().getName().equals("Glucometer")){
                object.synchLocation(670, 194);
            }
            if (object.getPojo().getName().equals("Sphygmomanometer")){
                object.synchLocation(550, 194);
            }
            
            if (object.getPojo().getName().equals("Oximeter")){
                object.synchLocation(550, 154);
            }
            
            if (object.getPojo().getName().equals("HeartSensor")){
                object.synchLocation(550, 104);
            }
            if (object.getPojo().getName().equals("SocialAlarmDevice")){
                object.synchLocation(703, 787);
            }
            if (object.getPojo().getName().equals("Alertled")){
                object.synchLocation(606, 745);
            }
        }
    }

    private void moveUsers() {
        int i = 0;
        int x;
        int y;
        RandomCoordinate rand = new RandomCoordinate();
        for(EnvObjectLogic object : getApi().things().findAll()){
            if (object instanceof GenericPerson){
                x = rand.getX();
                y = rand.getY();
                LOG.info("Coordinate X: " + x + " Y: " + y);
                FreedomPoint location = new FreedomPoint(x, y);
                LocationEvent event = new LocationEvent(this, object.getPojo().getUUID(), location);
                notifyEvent(event);
            }
        }
    }
    
    private void  deleteObject(){
        for( EnvObjectLogic object : getApi().things().findAll()){
            if (object instanceof GenericPerson){
                getApi().things().delete(object);
            } 
        }
    }
}