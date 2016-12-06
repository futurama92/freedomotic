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
import com.freedomotic.environment.EnvironmentLogic;
import com.freedomotic.events.ProtocolRead;
import com.freedomotic.exceptions.PluginStartupException;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.plugins.TrackingReadFile;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.EnvObjectLogic;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mauro Cicolella
 */
public class Movement extends Protocol {

    private static final Logger LOG = LoggerFactory.getLogger(Movement.class.getName());
    private static final String path = "C:\\Users\\ricca\\Documents\\NetBeansProjects\\freedomotic\\framework\\freedomotic-core\\plugins\\devices\\simulation\\data\\motes\\";
    private Boolean powered = true;
    TrackingReadFile mov;

    /**
     *
     */
    public Movement() {
        super("Movement", "/movement/movement-manifest.xml");
        setPollingWait(2000);
    }
    
    @Override
    protected void onRun() {
        //do nothing
    }

    @Override
    protected void onStart()   {
        deleteFile(path);
        LOG.info(Movement.class.getName() + " START");
        provaRiccardo();
        mov = new TrackingReadFile();
        try{
            mov.start();
        mov.onStart();}
        catch(PluginStartupException e){
            LOG.info("errore");
        }
 
    }
    
    @Override
    protected void onStop(){
        LOG.info(Movement.class.getName() + " STOP");
        mov.stop();
        mov.destroy();
        deleteFile(path);
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
        String waiting_room_UUID = null;
        for (EnvironmentLogic object : getApi().environments().findAll()){
            if (object.getRooms().get(0).getPojo().getName().equals("waiting_room")){
                LOG.info(object.getRooms().get(0).getPojo().getName());
                waiting_room_UUID = object.getRooms().get(0).getPojo().getUuid();
                LOG.info(waiting_room_UUID);
            }
        }
        String env_UUID = getApi().environments().findAll().get(0).getPojo().getUUID();
        List<EnvObject> waiting_room_objects = new ArrayList<EnvObject>();
        if(waiting_room_UUID != null && !waiting_room_UUID.isEmpty()){
            LOG.info(env_UUID);
            waiting_room_objects = getApi().environments().findOne(env_UUID).getZoneByUuid(waiting_room_UUID).getPojo().getObjects();
            LOG.info("OGGETTI IN waiting_room: " + getApi().environments().findOne(env_UUID).getZoneByUuid(waiting_room_UUID).getPojo().getObjects().size());
            int waiting_room_object_size = waiting_room_objects.size();
            
            for(int i = 0; i < waiting_room_object_size; i++){
                if (waiting_room_objects.get(i).getType().equals("EnvObject.Person.User")){
                    LOG.info("PERSONA");
                    createMovement(waiting_room_objects.get(i).getName());
                }
            }
        }
    }

    private void createMovement(String name) {
        
        try{
            FileWriter w;
            BufferedWriter b;
            w = new FileWriter(path + name + ".mote");
            b = new BufferedWriter (w);
            b.write("waiting_room,20000" + "\n" + "conference_room,15000" + "\n" + "hall_room,3000");
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

    private int randomNumber(int max){
        int min = 1;
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
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