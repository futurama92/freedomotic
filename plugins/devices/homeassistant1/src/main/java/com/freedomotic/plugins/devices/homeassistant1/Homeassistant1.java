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
package com.freedomotic.plugins.devices.homeassistant1;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.exceptions.PluginStartupException;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.plugins.TrackingReadFile;
import com.freedomotic.plugins.fromfile.WorkerThread;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.things.ThingRepository;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.GenericPerson;
import com.google.inject.Inject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.Decoration;

/**
 *
 * @author Mauro Cicolella
 */
public class Homeassistant1 extends Protocol {

    private static final Logger LOG = LoggerFactory.getLogger(Homeassistant1.class.getName());

    public Homeassistant1() {
        super("Homeassistant1", "/homeassistant1/homeassistant1-manifest.xml");
        
    }

    @Override
    protected void onRun() {
        LOG.info("Starting Assistan Home");
        createUsers();
        LOG.info("USER CREATE");
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
        
        int max = 4;
        int min = 1;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        
        for(int i = 0; i < randomNum; i++){
            
            ProtocolRead event = new ProtocolRead(this, "unknown", "unknown");
            event.getPayload().addStatement("object.class", "user_troubled");
            event.getPayload().addStatement("object.name", "CFUser" + i);
            event.getPayload().addStatement("object.actAs", "virtual");
            this.notifyEvent(event);
            
            Command c = new Command();
            c.setName("Join Custom User Object");
            c.setReceiver("app.objects.create");
            c.setProperty("object.class", "user_troubled");
            c.setProperty("object.name", "User_troubled" + i);
            c.setProperty("object.protocol", "unknown");
            c.setProperty("object.address", "unknown");
            c.setProperty("object.actAs", "virtual");
            this.notifyCommand(c);             
        }  
        /*
        for(int i = 0; i < 5 - randomNum; i++){
            
            ProtocolRead event2 = new ProtocolRead(this, "unknown", "unknown");
            event2.getPayload().addStatement("object.class", "user");
            event2.getPayload().addStatement("object.name", "User" + i);
            event2.getPayload().addStatement("object.actAs", "virtual");
            this.notifyEvent(event2);
            
            Command c2 = new Command();
            c2.setName("Join Custom User Object");
            c2.setReceiver("app.objects.create");
            c2.setProperty("object.class", "user");
            c2.setProperty("object.name", "User" + i);
            c2.setProperty("object.protocol", "unknown");
            c2.setProperty("object.address", "unknown");
            c2.setProperty("object.actAs", "virtual");
            this.notifyCommand(c2);             
        } */
    }
}