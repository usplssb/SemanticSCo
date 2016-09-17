/*******************************************************************************
 * Copyright 2016 Gabriela D. A. Guardia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*******************************************************************************/

package main;

import java.awt.BorderLayout;
import swingElements.MainFrame;
import javax.swing.JFrame;
import management.CompositionManager;
import swingElements.MenuBar;
import swingElements.ToolBar;

public class Principal {

    public static void main(String[] args){
        
        /***********************************************************
        * Place your Activiti database properties here
        ***********************************************************/
        String activitiDbDriver = "com.mysql.jdbc.Driver";
        String activitiDbURL = "jdbc:mysql://localhost:3306/activiti";
        String activitiDbUser = "root";
        String activitiDbPassword = "password";
        /***********************************************************/
    
        
        //Create composition manager
        CompositionManager manager = new CompositionManager(activitiDbDriver,activitiDbURL,activitiDbUser,activitiDbPassword);
        
        //Create new frame
        MainFrame frame = new MainFrame(manager);
        
        //Set frame properties
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Frame close operation (exit)
        frame.setSize(870, 590);//Frame size
        frame.setTitle("GEXPAC Editor"); //Frame title
        frame.setLocationByPlatform(true); //Frame location
        
        //Create MenuBar and add it to frame
        MenuBar menuBar = new MenuBar(frame, manager);
        
        //Create ToolBar and add it to frame
        ToolBar toolBar = new ToolBar(frame,manager,menuBar);
                
        //Set reference of menuBar to toolBar
        menuBar.setReferenceToToolBar(toolBar);
        menuBar.createListeners();
        
        //Add menuBar to frame
        frame.setJMenuBar(menuBar);
        
        //Add toolBar to frame
        frame.add(toolBar,BorderLayout.PAGE_START);
        
        //Set reference to toolBar
        frame.setReferenceToToolBar(toolBar);
        
        //Call addMouseListener method of JFrame
        frame.addMouseListener(manager);
        
        //Call addListenterToGraphComponent of JFrame
        frame.addListenerToGraphComponent();
        
        //Set frame visible
        frame.setVisible(true);
        
    }
}
