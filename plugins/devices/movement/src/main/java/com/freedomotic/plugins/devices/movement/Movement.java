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
import com.freedomotic.model.geometry.FreedomPoint;
import com.freedomotic.model.object.EnvObject;
import com.freedomotic.model.object.Representation;
import com.freedomotic.plugins.TrackingReadFile;
import com.freedomotic.plugins.fromfile.WorkerThread;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.things.GenericPerson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mauro Cicolella
 */
public class Movement extends Protocol {
    List<GenericPerson> person = new ArrayList();
    private static final Logger LOG = LoggerFactory.getLogger(Movement.class.getName());
    private static final String path = "C:\\Users\\ricca\\Documents\\NetBeansProjects\\freedomotic\\framework\\freedomotic-core\\plugins\\devices\\simulation\\data\\motes\\";
    private Boolean powered = true;
    TrackingReadFile mov;
    String UUID_sick;
    String UUID_nurse;
    boolean sick_condition = false;
    int sick_x;
    int sick_y;
    FreedomPoint coordinate;
    List<Representation> rep = new ArrayList<Representation>();
    GenericPerson sick;
    GenericPerson nurse;
    /**
     *
     */
    public Movement() {
        super("Movement", "/movement/movement-manifest.xml");
        setPollingWait(5000);
    }
    
    @Override
    protected void onRun() {
        for (int i = 0; i < person.size(); i++) {
            if(person.get(i).getBehavior("activity").getValueAsString().equals("Sick") || person.get(i).getBehavior("activity").getValueAsString().equals("Dead")) {
                sick = person.get(i);
                UUID_sick = person.get(i).getPojo().getUUID();
                sick_condition = true;
                rep = person.get(i).getPojo().getRepresentations();
                coordinate = rep.get(0).getOffset();
                sick_x = coordinate.getX();
                sick_y = coordinate.getY();
                LOG.info("X: " + sick_x + " Y: " + sick_y);
            }
            if ((sick_condition == true) && !(person.get(i).getPojo().getUUID().equals(UUID_sick))) {
                nurse = person.get(i);
                LOG.info("Qualcun altro non malato ed e': " + person.get(i).getPojo().getName());
                person.get(i).getPojo().setCurrentRepresentation(2);
                person.get(i).synchLocation(sick_x + 50, sick_y);
                UUID_nurse = person.get(i).getPojo().getUUID();
                try {
                     Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    java.util.logging.Logger.getLogger(Movement.class.getName()).log(Level.SEVERE, null, ex);
                } 
                sick_condition = false;
                if(sick_condition == false){
                    nurse.getPojo().setCurrentRepresentation(0);
                    sick.getPojo().setCurrentRepresentation(0);
                    Command c = new Command();
                    c.setName("Change activity behavior");
                    c.setReceiver("app.events.sensors.behavior.request.objects");
                    c.setProperty("object", sick.getPojo().getName());
                    c.setProperty("behavior", "activity");
                    c.setProperty("value", "Standing");
                    this.notifyCommand(c);
                }
            }
            
        }
    }

    @Override
    protected void onStart()   {
        for (EnvObjectLogic obj : getApi().things().findAll()) {
            if (obj instanceof GenericPerson){
                GenericPerson temp = (GenericPerson)obj;
                person.add(temp);
                LOG.info("Create list of person");
            }
        }/*
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
 */
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