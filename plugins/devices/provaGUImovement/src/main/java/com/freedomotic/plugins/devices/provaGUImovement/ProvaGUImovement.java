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
package com.freedomotic.plugins.devices.provaGUImovement;

import com.freedomotic.api.EventTemplate;
import com.freedomotic.api.Protocol;
import com.freedomotic.environment.Room;
import com.freedomotic.exceptions.PluginStartupException;
import com.freedomotic.exceptions.UnableToExecuteException;
import com.freedomotic.plugins.TrackingReadFile;
import com.freedomotic.reactions.Command;
import com.freedomotic.things.EnvObjectLogic;
import com.freedomotic.things.GenericPerson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Riccardo Trivellato
 */
public class ProvaGUImovement extends Protocol {
    private static final String path = "C:\\Users\\ricca\\Documents\\NetBeansProjects\\freedomotic\\framework\\freedomotic-core\\plugins\\devices\\simulation\\data\\motes\\";
    private static final Logger LOG = LoggerFactory.getLogger(ProvaGUImovement.class.getName());
    final int POLLING_WAIT;
    public static List<Room> rooms = new ArrayList();
    public static List<GenericPerson> users = new ArrayList();
    NewJFrame gui = null;
    boolean check = false;
    /**
     *
     */
    public ProvaGUImovement() {
        //every plugin needs a name and a manifest XML file
        super("provaGUImovement", "/provaGUImovement/provaGUImovement-manifest.xml");
        //read a property from the manifest file below which is in
        //FREEDOMOTIC_FOLDER/plugins/devices/com.freedomotic.hello/hello-world.xml
        POLLING_WAIT = configuration.getIntProperty("time-between-reads", 5000);
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
    protected void onShowGui() {
        bindGuiToPlugin(gui);
    }

    @Override
    protected void onRun() {
        if (check == false) {
            check = true;
            rooms = getApi().environments().findAll().get(0).getRooms();
            for(EnvObjectLogic obj : getApi().things().findAll()){
                if (obj instanceof GenericPerson)
                    users.add((GenericPerson) obj);
            }
            LOG.info("User size: " + users.size());
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

    @Override
    protected void onStart() {
        gui = new NewJFrame();

    }

    @Override
    protected void onStop() {
        deleteFile(path);
        LOG.info("HelloWorld plugin stopped");
    }
    
    public List<Room> getRooms(){
        return rooms;
    }
    public List<GenericPerson> getUsers(){
        return users;
    }

    @Override
    protected void onCommand(Command c) throws IOException, UnableToExecuteException {
        LOG.info("HelloWorld plugin receives a command called {} with parameters {}", c.getName(),
                c.getProperties().toString());
    }

    @Override
    protected boolean canExecute(Command c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void onEvent(EventTemplate event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
