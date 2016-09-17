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

package swingElements;

import activitiExecution.Activiti;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Input;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import jaxbClasses.basictypes.SuggestionValue;
import management.CompositionManager;
import management.DataSource;

public class PopupMenuCell extends JPopupMenu {
    
    private MainFrame jFrame;
    private CompositionManager manager;
    
    private List<Input> inputs;
    private String serviceID;
    
    private String selectedInputID;
    
    private ServiceInfo servInfo;
    
    private String valInputID,valSelectedValue;
    private boolean valIsOutput;
    
    private MenuBar menuBar;
    private ToolBar toolBar;
    private HashMap<ServiceInfo,LinkedList<String>> mapOutgoing;
    
    public PopupMenuCell(final MainFrame jFrame, final CompositionManager manager, MenuBar menuBar, ToolBar toolBar, final ServiceInfo serviceInfo, HashMap<ServiceInfo,LinkedList<String>> mapOutgoing) {
        
        super();
        
        //Set variables
        this.jFrame = jFrame;
        this.manager = manager;
        this.menuBar = menuBar;
        this.toolBar = toolBar;
        this.servInfo = serviceInfo;
        this.mapOutgoing = mapOutgoing;
        
        //Get id and inputs of selected service
        serviceID = serviceInfo.getID();
        inputs = serviceInfo.getInput();    
        
        //If list of inputs is not empty
        if(!inputs.isEmpty()){
            
            //Create "Validate Inputs" menu
            JMenu validateMenu = new JMenu("Validate Inputs");
            validateMenu.setIcon(new ImageIcon("images/validate.png"));
            JMenu menu = (JMenu) add(validateMenu);
            this.add(menu);
                    
            //For each service input
            int count = 1;
            for(Iterator it = inputs.iterator();it.hasNext();){
                
                Input input = (Input) it.next();
            
                String element = manager.getLabel(input.getSemanticalType());
                
                //Create submenu for input
                final JMenuItem inputItem;
                menu.add(inputItem = new JMenuItem("Input " + count + ": " + element));
                count++;
                
                inputItem.setName(input.getID());
                
                //Add listener to submenu
                inputItem.addActionListener(new ActionListener() {
                    
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                        valInputID = inputItem.getName();
                        
                        //Get suggestion values of selected input
                        List<SuggestionValue> suggestions = new LinkedList<>();
                        for(int i=0; i<inputs.size();i++){
                            if(inputs.get(i).getID().equals(valInputID))
                                suggestions = inputs.get(i).getSuggestedValue();                            
                        }
                        
                        //Add suggestion values to vector and map
                        HashMap<String,String> map = new HashMap<>();
                        for(Iterator it=suggestions.iterator();it.hasNext();){
                            SuggestionValue sv = (SuggestionValue) it.next();
                            if(sv.getValueID().contains("-OutputID-"))
                                map.put(sv.getValueID(),manager.getOutputName(sv.getValueID()));
                            else if(sv.getValue() != null && !sv.getValue().equals("null"))
                                map.put(sv.getValue(),manager.getDataSourceName(sv.getValue()));                        
                        }
                        LinkedList<DataSource> listOfDataSources = manager.getListOfDataSources();
                        for(Iterator it=listOfDataSources.iterator();it.hasNext();){
                            DataSource dataSource = (DataSource) it.next();
                            if(!map.containsKey(dataSource.getDataSourceValue()))
                                map.put(dataSource.getDataSourceValue(),manager.getDataSourceName(dataSource.getDataSourceValue()));
                        }  
                        Object[] possibilities = new Object[map.size()];
                        int count = 0;
                        for (Map.Entry<String, String> entry : map.entrySet()){
                            possibilities[count] = entry.getValue();
                            count++;
                        }
                        
                        String selectedValue = (String)JOptionPane.showInputDialog(jFrame, "Please, select a value: ","Validate Input",JOptionPane.PLAIN_MESSAGE,null,possibilities,"");
                    
                        valSelectedValue = null;
                    
                        //If some value is selected, set valSelectedValue with it
                        if(selectedValue != null){
                            if(map.containsValue(selectedValue)){
                                for(Map.Entry<String, String> entry : map.entrySet()){
                                    if(entry.getValue().equals(selectedValue)){
                                        valSelectedValue = entry.getKey();
                                        valIsOutput = valSelectedValue.contains("-OutputID-");
                                    }
                                }
                            }
                    
                            //If some value was selected
                            if(valSelectedValue != null){
                        
                                //Validate input with selected value (SwingWorker)
                                new ValidatingInput().execute();
                            }
                        }
                    }
                });
            }
        
        }
        
        //If list of inputs is not empty
        if(!inputs.isEmpty()){
            
            //Create "Resolve Inputs" menu
            JMenu resolveMenu = new JMenu("Resolve Inputs");
            resolveMenu.setIcon(new ImageIcon("images/resolve.png"));
            JMenu menu = (JMenu) add(resolveMenu);
            this.add(menu);
        
            //For each service input
            int count = 1;
            for(Iterator it = inputs.iterator();it.hasNext();){
                
                Input input = (Input) it.next();
            
                String element = manager.getLabel(input.getSemanticalType());
                
                //Create submenu for input
                final JMenuItem inputItem;
                menu.add(inputItem = new JMenuItem("Input " + count + ": " + element));
                count++;
                
                inputItem.setName(input.getID());
                
                //Add listener to submenu
                inputItem.addActionListener(new ActionListener() {
                    
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                        selectedInputID = inputItem.getName();
                            
                        //Call SwingWorker "Loading"
                        new LoadingResolveServ().execute();
                        
                    }
                });
            }
        
            this.addSeparator();
        }
        
        JMenuItem removeItem;
        this.add(removeItem = new JMenuItem("Remove",new ImageIcon("images/delete_activity.png")));
        
        removeItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Present confirmation question to user
                int result = JOptionPane.showConfirmDialog(jFrame,"<html>Are you sure you want to<br>remove analysis activity?<html>","Remove activity",JOptionPane.YES_NO_OPTION);
                                                                    
                //If user select yes. Else, do nothing
                if (result == JOptionPane.YES_OPTION){
                    
                    //Call SwingWorker "RemovingActivity"
                    new RemovingActivity().execute();
                    
                }
                
            }
        });
        
        this.addSeparator();
        
                
        JMenuItem executeItem = new JMenuItem("Execute",new ImageIcon("images/execute.png"));
        this.add(executeItem);
        
        executeItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Call SwingWorker "ExecutingServ"
                new ExecutingServ().execute();
                
            }
        });
        
        //Enable/Disable item "executeItem" according to serviceInfo ready for execution parameter
        if(serviceInfo.isReadyForExec() != null && serviceInfo.isReadyForExec())
            executeItem.setEnabled(true);
        else 
            executeItem.setEnabled(false);
        
        JMenuItem saveResultsItem = new JMenuItem("Save result(s)",new ImageIcon("images/save.png"));
        this.add(saveResultsItem);
        
        saveResultsItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Call SwingWorker "SavingResults"
                new SavingResults().execute();
                
            }
        });
        
        //Enable/Disable item "saveResultsItem" according to service directory
        if(manager.serviceDirectoryIsEmpty(serviceID))
            saveResultsItem.setEnabled(false);
        else
            saveResultsItem.setEnabled(true);
        
    }
    
    //Internal class for validating input
    class ValidatingInput extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
                    
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
                    
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Validate Input","   Validating input   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                    
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
                    
            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //Call manager to validate input
            if(manager.serviceValidation(serviceID, valInputID, valSelectedValue, valIsOutput)){
                      
                //Insert edge (only if edge do not already exist)
                jFrame.insertEdgeElement(valInputID,valSelectedValue,!valIsOutput);
                        
                //Call manager to AddToContext "null" to all outputs of service being validated
                LinkedList<String> serviceIDs = jFrame.findAllChildren(serviceID);
                for(Iterator itS = serviceIDs.iterator();itS.hasNext();){
                    String currentID = (String) itS.next();
                    LinkedList<ContextValueStructure> contextValues = new LinkedList<>();
                    ServiceInfo servInfo = manager.getServiceInfo(currentID);
                    ContextValueStructure contextValue; 
                    for(Iterator it=servInfo.getOutput().iterator();it.hasNext();){
                        contextValue = new ContextValueStructure(servInfo.getID(),((Output) it.next()).getID(),"null");
                        contextValues.add(contextValue);
                    }
                    manager.addValuesToContext(contextValues);
                }
                        
                //Call manager to update all services that are ready to be executed
                manager.execServ();
                            
                //Update all vertex colors
                jFrame.updateVerticesColorToLightGray(serviceID);

                //Enable jFrame and all of its components
                jFrame.setEnabled(true);
                        
                //Make loadingDialog invisible
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
                        
                JOptionPane.showMessageDialog(jFrame, "Input Validated!","Message",JOptionPane.INFORMATION_MESSAGE);
                        
            }
            else{
                
                //Enable jFrame and all of its components
                jFrame.setEnabled(true);
                        
                //Make loadingDialog invisible
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
                        
                JOptionPane.showMessageDialog(jFrame, "Error to Validate Input!","Error",JOptionPane.ERROR_MESSAGE);

            }     
        
            return null;
        }

        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            jFrame.clearGraphSelection();
        
        }
    }
    
    //Internal class for loading input concepts from ontology
    class LoadingResolveServ extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
           
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
            
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Resolve Input","   Loading options   ",745,290,98,47,2,47);
            loadingDialog.setModal(false);
                            
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);

            //Set loadingDialog visible
            loadingDialog.setVisible(true);
                        
            //Call manager to resolve service
            HashMap<String,ServiceInfo> mapOfServices = manager.resolveServiceInput(serviceID, selectedInputID);
                            
            //Get updated suggestion values of selected input
            List<SuggestionValue> suggestions = new LinkedList<>();
            List<Input> auxInputs = manager.getIncludedService(serviceID).getInput();
            for(int i=0; i<auxInputs.size();i++){
                if(auxInputs.get(i).getID().equals(selectedInputID))
                    suggestions = auxInputs.get(i).getSuggestedValue();
            }
            
            //Add suggestion values to vector
            Vector<String> vc=new Vector<>();
            for(Iterator it=suggestions.iterator();it.hasNext();){
                SuggestionValue sv = (SuggestionValue) it.next();
                if(sv.getValueID().contains("-OutputID-"))
                    vc.add(sv.getValueID());
                else if(sv.getValue() != null && !sv.getValue().equals("null"))
                    vc.add(sv.getValue());                        
            }
            LinkedList<DataSource> listOfDataSources = manager.getListOfDataSources();
            for(Iterator it=listOfDataSources.iterator();it.hasNext();){
                DataSource dataSource = (DataSource) it.next();
                if(!vc.contains(dataSource.getDataSourceValue()))
                    vc.add(dataSource.getDataSourceValue());
            }  
            
            //Call JDialog "ResolveInputDialog"
            ResolveInputDialog resolveInputDialog = new ResolveInputDialog(jFrame,manager,serviceID,selectedInputID,vc,mapOfServices);
                            
            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
            
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
                            
            resolveInputDialog.setModal(true);

            //Center inside the application frame
            x = jFrame.getX() + (jFrame.getWidth() - resolveInputDialog.getWidth()) / 2;
            y = jFrame.getY() + (jFrame.getHeight() - resolveInputDialog.getHeight()) / 2;
            resolveInputDialog.setLocation(x, y);

            //Set resolveInputDialog visible
            resolveInputDialog.setVisible(true);
            
            //If some new service is selected to be included into composition
            if(resolveInputDialog.isSelected()){
                
                //Disable jFrame and all of its components
                jFrame.setEnabled(false);
            
                //Call JDialog LoadingDialog
                loadingDialog = new LoadingDialog(jFrame,"Adding Web Service","   Adding web service   ",300,200,60,40,3,40);
                loadingDialog.setModal(false);

                //Center inside the application frame
                x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
                y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
                loadingDialog.setLocation(x, y);

                //Set LoadingDialog visible
                loadingDialog.setVisible(true);
                    
                //Call manager to make service selection
                if(manager.serviceInclusion()){
                            
                    //Call manager to update all services that are ready to be executed
                    manager.execServ();
                      
                    //Update all vertex colors
                    jFrame.updateVerticesColorToLightGray(serviceID);
                    
                    //Call operation to Add analysis activity
                    if(!manager.getSelectedService().getFunction().isEmpty()) //For service
                        jFrame.insertVertexElement(manager.getSelectedService().getID(),manager.getLabel(manager.getSelectedService().getFunction().get(0)),false);
                    else //For connector
                        jFrame.insertVertexElement(manager.getSelectedService().getID(),manager.getSelectedService().getName(),true);
                
                    //Update connections of selected service to root element
                    jFrame.updateRoot(manager.getSelectedService().getID());
                    
                    //Call manager to validate input
                    if(manager.serviceValidation(serviceID, selectedInputID, manager.getSelectedOutputID(), true)){
                        
                        //Insert edge (only if edge do not already exist)
                        jFrame.insertEdgeElement(selectedInputID,manager.getSelectedOutputID(),false);
                        
                        //Set suggestion values of service being resolved to include the selected output
                        manager.addSuggestionValue(serviceID, selectedInputID, manager.getSelectedOutputID());
                        
                        //Call manager to AddToContext "null" to all outputs of service being validated
                        LinkedList<String> serviceIDs = jFrame.findAllChildren(serviceID);
                        for(Iterator itS = serviceIDs.iterator();itS.hasNext();){
                            String currentID = (String) itS.next();
                            LinkedList<ContextValueStructure> contextValues = new LinkedList<>();
                            ServiceInfo servInfo = manager.getServiceInfo(currentID);
                            ContextValueStructure contextValue; 
                            for(Iterator it=servInfo.getOutput().iterator();it.hasNext();){
                                contextValue = new ContextValueStructure(servInfo.getID(),((Output) it.next()).getID(),"null");
                                contextValues.add(contextValue);
                            }
                            manager.addValuesToContext(contextValues);
                        }
                        
                        //Call manager to update all services that are ready to be executed
                        manager.execServ();
                          
                        //Update all vertex colors
                        jFrame.updateVerticesColorToLightGray(serviceID);

                        //Enable jFrame and all of its components
                        jFrame.setEnabled(true);
            
                        //Make loadingDialog invisible
                        loadingDialog.setVisible(false);
                        loadingDialog.dispose();
                        
                        JOptionPane.showMessageDialog(jFrame, "Input Resolved!","Message",JOptionPane.INFORMATION_MESSAGE);
                        
                    }
                    else{
                        
                        //Enable jFrame and all of its components
                        jFrame.setEnabled(true);
                        
                        //Make loadingDialog invisible
                        loadingDialog.setVisible(false);
                        loadingDialog.dispose();
                        
                        JOptionPane.showMessageDialog(jFrame, "Error to Resolve Input!","Error",JOptionPane.ERROR_MESSAGE);
                        
                    }
                }
            }
            
            resolveInputDialog.dispose();
            
            return null;
        }

        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            jFrame.clearGraphSelection();
        
        }
    }
    
    //Internal class for service execution
    class ExecutingServ extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
            
            jFrame.setEnabled(false);
           
            Activiti activiti = new Activiti();
                
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Service Execution","   Deploying service   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                            
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);

            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //Remove all files contained into service directory
            manager.clearServiceDirectory(servInfo.getID());
            
            //If engine was sucessfully created
            if(activiti.createProcessEngine(manager.getActivitiDbDriver(), 
                                            manager.getActivitiDbURL(),
                                            manager.getActivitiDbUser(),
                                            manager.getActivitiDbPassword())){
                    
                //If process is successfully deployed
                if(activiti.deployProcess(servInfo.getLocation())){
                
                    //Get execution values
                    HashMap<String,String> executionValues = new HashMap<>();
                    List<Input> inputs = servInfo.getInput();
                    for(Iterator it=inputs.iterator();it.hasNext();){
                        Input input = (Input) it.next();
                        executionValues.put(input.getName(),input.getExecutionValue());
                    }
                    List<Output> outs = servInfo.getOutput();
                    for(Iterator it=outs.iterator();it.hasNext();){
                        Output output = (Output) it.next();
                        executionValues.put(output.getName(),manager.getServiceDirectoryPath(servInfo.getID())+"/");
                    }
                    
                    //Make loadingDialog invisible
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                        
                    //If process is successfully started and finished
                    if(activiti.runProcess(servInfo.getLocation(),executionValues)){
                        
                        //Create list of ContextValueStructures
                        LinkedList<ContextValueStructure> contextValues = new LinkedList<>();
                        
                        //If process was successfully finished
                        if(activiti.getStatus().equals("finished")){
                            
                            //Get output variables
                            HashMap<String,String> result = activiti.getOutputVariables();
                    
                            //Get outputs of service
                            List<Output> outputs = servInfo.getOutput();
                            
                            //If service has outputs
                            if(!outputs.isEmpty()){
                                
                                //If there are output variables, call AddToContext to add them
                                if(!result.isEmpty()){
                            
                                    //For each output variable
                                    for(Map.Entry<String, String> entry : result.entrySet()){
                                    
                                        ContextValueStructure contextValue; 

                                        //If output variable name is the same of service output
                                        for(int i=0;i<outputs.size();i++){
                                            if(outputs.get(i).getName().equals(entry.getKey())){
                                                contextValue = new ContextValueStructure(servInfo.getID(),outputs.get(i).getID(),entry.getValue());
                                                contextValues.add(contextValue);
                                            }
                                        }
                                    }
                            
                                    //Call manager to add output values to context
                                    manager.addValuesToContext(contextValues);
                                    
                                    //Ask if user wants to locally save results
                                    int val = JOptionPane.showConfirmDialog(jFrame,"<html>Do you want to locally save the result(s)?<html>","Save Result(s)",JOptionPane.YES_NO_OPTION);
                                                                    
                                    //If user select yes
                                    if (val == JOptionPane.YES_OPTION){
                            
                                        //Call SwingWorker "SavingResults"
                                        new SavingResults().execute();
                                        
                                    }
                            
                                }
                                
                            }//Else, if service has no outputs
                            else{
                                
                                //Call manager to add service ID to context
                                manager.addServiceIdToContext(servInfo.getID());
                                
                            }
                            
                        
                        }//Else, if process failed
                        else{
                            
                            ContextValueStructure contextValue; 
                            for(Iterator it = servInfo.getOutput().iterator();it.hasNext();){
                                contextValue = new ContextValueStructure(servInfo.getID(),((Output)it.next()).getID(),"null");
                                contextValues.add(contextValue);
                            }
                                
                            //Call manager to add output values to context
                            manager.addValuesToContext(contextValues);
                        }
                        
                        //Call manager to update all services that are ready to be executed
                        manager.execServ();
                        
                        //Update vertex color
                        if(activiti.getStatus().equals("finished"))
                            jFrame.changeVertexColor(servInfo,"lightgreen");
                        else if(activiti.getStatus().equals("failure"))
                            jFrame.changeVertexColor(servInfo,"salmon");
                        
                    }
                    else{
                        //Preciso fazer algo aqui
                        JOptionPane.showMessageDialog(jFrame, "Service could not be executed!","Error",JOptionPane.ERROR_MESSAGE);
                    }
                }//Else, if process is not successfully deployed
                else{
                    
                    //Make loadingDialog invisible
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                    
                    JOptionPane.showMessageDialog(jFrame, "Service could not be deployed!","Error",JOptionPane.ERROR_MESSAGE);
                }
            
            }//Else, if engine could not be created
            else{
                
                //Make loadingDialog invisible
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
                    
                JOptionPane.showMessageDialog(jFrame, "Service could not be deployed!","Error",JOptionPane.ERROR_MESSAGE);
            }
            
            jFrame.setEnabled(true);
            
            return null;
        }
        
        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            jFrame.clearGraphSelection();
        
        }
    }
    
    //Internal class for removing an activity (service)
    class RemovingActivity extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
                    
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
                    
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Remove Activity","   Removing activity   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                    
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
                    
            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //Validate all inputs of service being removed
            for(Iterator it = servInfo.getInput().iterator();it.hasNext();){
                Input input = (Input) it.next();
                manager.serviceValidation(servInfo.getID(),input.getID(),"null",false);
            }
            
            //Validate inputs of services directly after the service being removed (only inputs validated with outputs of service being removed)
            for(Map.Entry<ServiceInfo,LinkedList<String>> entry : mapOutgoing.entrySet()){
                
                //Get current service
                ServiceInfo currentServInfo = entry.getKey();
                
                //Get inputIDs of service to be validated to "null"
                LinkedList<String> inputIDs = entry.getValue();
                
                //Call manager to validate each inputID to "null"
                for(Iterator it = inputIDs.iterator();it.hasNext();){
                    String inputID = (String)it.next();
                    manager.serviceValidation(currentServInfo.getID(),inputID,"null",false);
                }
            }
            
            //Call manager to AddToContext "null" to all outputs of current service and all subsequent services
            LinkedList<String> serviceIDs = jFrame.findAllChildren(servInfo.getID());
            for(Iterator itS = serviceIDs.iterator();itS.hasNext();){
                String currentID = (String) itS.next();
                LinkedList<ContextValueStructure> contextValues = new LinkedList<>();
                ServiceInfo servInfo = manager.getServiceInfo(currentID);
                ContextValueStructure contextValue; 
                for(Iterator it=servInfo.getOutput().iterator();it.hasNext();){
                    contextValue = new ContextValueStructure(servInfo.getID(),((Output) it.next()).getID(),"null");
                    contextValues.add(contextValue);
                }
                manager.addValuesToContext(contextValues);
                    
            }
            
            //Call manager to update all services that are ready to be executed
            manager.execServ();
            
            //Update vertex colors of current service and all of its children
            jFrame.updateVerticesColorToLightGray(servInfo.getID());
            
            //Call manager to update "listOfSelectedInputs", removing all outputs of service being excluded
            for(Iterator it=servInfo.getOutput().iterator();it.hasNext();)
                manager.updateListOfSelectedInputsForActivityRemoval(((Output) it.next()).getID());
            
            //Call manager to update "listOfSelectedServices" and "listOfExcludedServices"
            manager.updateListOfIncludedServices(servInfo.getID());
            
            //Call manager to remove all suggestionValues of all services contained in the composition referencing the service being removed
            manager.updateAllSuggestionValuesForActivityRemoval(servInfo.getID());
            
            //Remove service vertex and all associated edges from graph
            jFrame.removeServiceVertex(servInfo.getID());
            
            //If no input concept was already selected and there are no services in the composition
            if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                menuBar.disableMenuAddActivity();
                toolBar.disableButtonAddActivity();
            }//Else, if numberOfDataSources > 0, enable menus "Add Activity"
            else{
                menuBar.enableMenuAddActivity();
                toolBar.enableButtonAddActivity();
            }
            
            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
                        
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
                        
            JOptionPane.showMessageDialog(jFrame, "Analysis Activity Removed!","Message",JOptionPane.INFORMATION_MESSAGE);
        
            return null;
        }

        @Override
        protected void done() {
        
            //Disable all menus 
            menuBar.clearMenuValidate();
            menuBar.clearMenuResolve();
            menuBar.disableMenuExecute();
            menuBar.disableMenuSaveResults();
            menuBar.disableMenuRemoveConnection();
            menuBar.disableMenuRemoveDataSource();
            menuBar.disableMenuRemoveActivity();
                        
            toolBar.disableButtonValidate();
            toolBar.disableButtonResolve();
            toolBar.disableButtonExecute();
            toolBar.disableButtonSaveResults();
            toolBar.disableButtonRemoveConnection();
            toolBar.disableButtonRemoveDataSource();
            toolBar.disableButtonRemoveActivity();
            
        }
    }
    
    //Internal class for saving results of a service
    class SavingResults extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
            
            try{
                
                //Disable jFrame and all of its components
                jFrame.setEnabled(false);
           
                //Get service directory path
                File serviceDir = manager.getServiceDirectory(servInfo.getID());
            
                //Get all result files of service
                File[] listOfFiles = serviceDir.listFiles();
            
                //Create a file chooser and set its selection mode
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setMultiSelectionEnabled(false);
                
                //Call file chooser
                int returnVal = fileChooser.showSaveDialog(jFrame);
                
                //If user select some directory
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    
                    //Get selected directory
                    File selectedDir = fileChooser.getSelectedFile();
                
                    //Save each file to selected directory
                    for(File file : listOfFiles){
                        
                        //Create local file on selected directory
                        File localFile = new File(selectedDir.getPath() + "/" + file.getName());
                        
                        int count = 1;
                        while(localFile.exists()){
                            String[] parts = file.getName().split("\\.");
                            localFile = new File(selectedDir.getPath() + "/" + parts[0] + " (" + count + ")." + parts[1]);
                            count++;
                        }
                        localFile.createNewFile();
                        
                        //If result is an image
                        if(file.getName().contains(".png")){
                            
                            //Copy image to local image
                            ImageInputStream in = new FileImageInputStream(file);
                            ImageOutputStream out = new FileImageOutputStream(localFile);
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len=in.read(buf)) > 0)
                                out.write(buf,0,len);
                            in.close();
                            out.close();
                        }
                        else{
                        
                            //Copy file to local file
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            PrintWriter pw = new PrintWriter(new FileWriter(localFile));
                            String line;
                            while((line = br.readLine()) != null)
                                pw.write(line+"\n");
                            pw.flush();
                            pw.close();
                            br.close();
                        }
                        
                    }
                    
                }   
                
            }catch(IOException ex){
            
                //Show error message to user
                JOptionPane.showMessageDialog(jFrame, "Error to save file(s)!","Error",JOptionPane.ERROR_MESSAGE);
            
            }
            
            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
        
            return null;
        }

        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            jFrame.clearGraphSelection();
            
        }
    }
    
}
