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
package com.freedomotic.plugins.devices.hello;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.things.ThingRepository;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.GenericPerson;
import com.google.inject.Inject;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.Decoration;

/**
 *
 * @author Mauro Cicolella
 */
public class HelloWorld
        extends Protocol {

    private static final Logger LOG = LoggerFactory.getLogger(HelloWorld.class.getName());
    final int POLLING_WAIT;

    @Inject
    private ThingRepository thingsRepository;

    /**
     *
     */
    public HelloWorld() {
        //every plugin needs a name and a manifest XML file
        super("HelloWorld", "/hello-world/hello-world-manifest.xml");
        //read a property from the manifest file below which is in
        //FREEDOMOTIC_FOLDER/plugins/devices/com.freedomotic.hello/hello-world.xml
        POLLING_WAIT = configuration.getIntProperty("time-between-reads", 2000);
        //POLLING_WAIT is the value of the property "time-between-reads" or 2000 millisecs,
        //default value if the property does not exist in the manifest
        setPollingWait(POLLING_WAIT); //millisecs interval between hardware device status reads
    }



    @Override
    protected void onHideGui() {
        //implement here what to do when the this plugin GUI is closed
        //for example you can change the plugin description
        setDescription("My GUI is now hidden");
    }

    @Override
    protected void onRun() {
        
       EnvObjectLogic ciao = new GenericPerson();
       
        
        if(getApi().things().create(ciao))
            LOG.info("PERSONA CREATA");
        GenericPerson prova = (GenericPerson)ciao;
        ciao.init();
        for (EnvObjectLogic thing : thingsRepository.findAll()) {
            LOG.info("HelloWorld sees Thing: {}", thing.getPojo().getName());
        }

        
    }

    @Override
    protected void onStart() {
        LOG.info("HelloWorld plugin started");
    }

    @Override
    protected void onStop() {
        LOG.info("HelloWorld plugin stopped");
    }

    @Override
    protected void onCommand(Command c)
            throws IOException, UnableToExecuteException {
        LOG.info("HelloWorld plugin receives a command called {} with parameters {}", c.getName(),
                c.getProperties().toString());
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
}
