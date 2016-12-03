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
package com.freedomotic.plugins.devices.movement;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.font.Decoration;

/**
 *
 * @author Mauro Cicolella
 */
public class Movement extends Protocol {

    private static final Logger LOG = LoggerFactory.getLogger(Movement.class.getName());
    private static final String path = "C:\\Users\\ricca\\Documents\\NetBeansProjects\\freedomotic\\framework\\freedomotic-core\\plugins\\devices\\simulation\\data\\motes\\";
    private Boolean powered = true;


    /**
     *
     */
    public Movement() {
        super("Movement", "/movement/movement-manifest.xml");
        setPollingWait(2000);
        
    }





    @Override
    protected void onRun() {
        deleteFile(path);
        LOG.info("MOVEMENT ON RUN");
        //createUsers();
        provaRiccardo();
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

    protected void provaRiccardo() {
        String bedroom_UUID = getApi().environments().findAll().get(0).getRooms().get(0).getPojo().getUuid();
        String env_UUID = getApi().environments().findAll().get(0).getPojo().getUUID();
        List<EnvObject> bedroom_objects = new ArrayList<EnvObject>();
        if(bedroom_UUID != null && !bedroom_UUID.isEmpty()){
            
            bedroom_objects = getApi().environments().findOne(env_UUID).getZoneByUuid(bedroom_UUID).getPojo().getObjects();
            LOG.info("OGGETTI IN CAMERA DA LETTO: " + getApi().environments().findOne(env_UUID).getZoneByUuid(bedroom_UUID).getPojo().getObjects().size());
            int bed_object_size = bedroom_objects.size();
            
            for(int i = 0; i < bed_object_size; i++){
                if (bedroom_objects.get(i).getType().equals("EnvObject.Person.User")){
                    LOG.info("PERSONA");
                    createMovement(bedroom_objects.get(i).getName());
                }
            }
        }
        
        TrackingReadFile mov = new TrackingReadFile();
        mov.start();
        try{
            mov.onStart();
        } catch (PluginStartupException e) {
            LOG.error("no start from readfile");
        }
    }

    private void createMovement(String name) {
        
        try{
            FileWriter w;
            BufferedWriter b;
            w = new FileWriter(path + name + ".mote");
            b = new BufferedWriter (w);
            b.write("Bedroom,3000" + "\n" + "Kitchen,4000" + "\n" + "Garage,20000");
            b.flush();
            LOG.info("File " + name + ".mote create" );
        } catch (IOException e){
            LOG.info("NON HO CREATO IL FILE MOVEMENT");
        }
    }    
    
    private void deleteFile(String path){
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles.length != 0){
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    if (listOfFile.delete()) {
                        LOG.info(".motes gone");
                    }
                }
            } 
        }
    }

    
    //WORK IN PROGRESS
    public void createUsers() {
        
        for(int i = 0; i < 4; i++){
            
            ProtocolRead event = new ProtocolRead(this, "unknown", "unknown");
            event.getPayload().addStatement("object.class", "user");
            event.getPayload().addStatement("object.name", "CFUser" + i);
            event.getPayload().addStatement("object.actAs", "virtual");
            this.notifyEvent(event);
            
            Command c = new Command();
            c.setName("Join Custom User Object");
            c.setReceiver("app.objects.create");
            c.setProperty("object.class", "user");
            c.setProperty("object.name", "CFUser" + i);
            c.setProperty("object.protocol", "unknown");
            c.setProperty("object.address", "unknown");
            c.setProperty("object.actAs", "virtual");
            this.notifyCommand(c);            
            LOG.info("Create CFUser" + i);    
        }  
    }
}