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
import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Input;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import jaxbClasses.basictypes.SuggestionValue;
import main.Principal;
import management.CompositionManager;
import management.DataSource;


public class MenuBar extends JMenuBar{
    
    private MainFrame jFrame;
    private JMenu menu;
    private JMenuItem itemNewAnalysis, itemExit, itemAddActivity,itemRemoveActivity, itemAddDataSource, itemRemoveDataSource, itemRemoveConnection;
    private JMenu menuValidate, menuResolve;
    private JMenuItem itemExecute,itemSaveResults, itemAbout;
    
    private CompositionManager manager;
    
    private String serviceID;
    private String selectedInputID;
    
    private ServiceInfo execServInfo;
    
    private ToolBar toolBar;
    
    private String valSelectedValue,valServiceID,valSelectedInputID;
    private boolean valIsOutput;
    
    private String edgeID, sourceID, targetID;
    private HashMap<String,String> edgeMap;
    private boolean isDataSource;
    private LinkedList<String> selectedInputIDs;
    
    private String dataSourceID;
    private HashMap<ServiceInfo,LinkedList<String>> mapforDataSourceRemoval;
    
    private ServiceInfo servInfoToBeExcluded;
    private HashMap<ServiceInfo,LinkedList<String>> mapOutgoing;
    
    private ServiceInfo servToSaveResults;
    
    public MenuBar(final MainFrame jFrame, final CompositionManager manager){
        
        //Set local manager
        this.manager = manager;
        
        //Set local MainFrame
        this.jFrame = jFrame;
        
        //Create menu "Analysis"
        menu = add(new JMenu("Analysis"));
        
        //Create item "New analysis"
        itemNewAnalysis = new JMenuItem("New analysis",new ImageIcon("images/new_analysis.gif"));
        
        //Add item "New analysis" to menu "Analysis"
        menu.add(itemNewAnalysis);
        
        //Add separator to menu "Analysis"
        menu.addSeparator();
        
        //Create item "Exit"
        itemExit = new JMenuItem("Exit");
        
        //Add item "Exit" to menu "Analysis"
        menu.add(itemExit);
        
        //Create menu "Activity"
        menu = add(new JMenu("Activity"));
        
        //Create item "Add activity"
        itemAddActivity = new JMenuItem("Add activity",new ImageIcon("images/add_activity.png"));
        itemAddActivity.setEnabled(false); //Disable item "Add activity"
        
        //Add item "Add activity" to menu "Activity"
        menu.add(itemAddActivity);
        
        //Create item "Remove activity"
        itemRemoveActivity = new JMenuItem("Remove activity",new ImageIcon("images/delete_activity.png"));
        itemRemoveActivity.setEnabled(false); //Disable item "Remove activity"
        
        //Add item "Remove activity" to menu "Activity"
        menu.add(itemRemoveActivity);
        
        //Add separator to menu "Activity"
        menu.addSeparator();
        
        //Create menu "Validate Inputs"
        menuValidate = new JMenu("Validate Inputs");
        menuValidate.setIcon(new ImageIcon("images/validate.png"));
        
        //Add menu "Validate Inputs" to menu "Activity"
        menu.add(menuValidate);
        menuValidate.setEnabled(false);
        
        //Create menu "Resolve Inputs"
        menuResolve = new JMenu("Resolve Inputs");
        menuResolve.setIcon(new ImageIcon("images/resolve.png"));
        
        //Add menu "Resolve Inputs" to menu "Activity"
        menu.add(menuResolve);
        menuResolve.setEnabled(false);
        
        //Add separator to menu "Activity"
        menu.addSeparator();
        
        //Create item "Execute"
        itemExecute = new JMenuItem("Execute",new ImageIcon("images/execute.png"));
        itemExecute.setEnabled(false); //Disable item "Execute"
        
        //Add item "Execute" to menu "Activity"
        menu.add(itemExecute);
        
        //Create item "Save results"
        itemSaveResults = new JMenuItem("Save Result(s)",new ImageIcon("images/save.png"));
        itemSaveResults.setEnabled(false); //Disable item "Save results"
        
        //Add item "Save results" to menu "Activity"
        menu.add(itemSaveResults);
        
        //Create menu "Data Source"
        menu = add(new JMenu("Data"));
        
        //Create item "Add Data Source"
        itemAddDataSource = new JMenuItem("Add Data Source",new ImageIcon("images/datasource_add.png"));
        itemAddDataSource.setEnabled(false);
        
        //Add item "Add Data Source" to menu "Data Source"
        menu.add(itemAddDataSource);
        
        //Create item "Remove Data Source"
        itemRemoveDataSource = new JMenuItem("Remove Data Source",new ImageIcon("images/datasource_delete.png"));
        itemRemoveDataSource.setEnabled(false);
        
        //Add item "Remove Data Source" to menu "Data Source"
        menu.add(itemRemoveDataSource);
        
        //Create menu "Connection"
        menu = add(new JMenu("Connection"));
        
        //Create item "Remove connection"
        itemRemoveConnection = new JMenuItem("Remove connection",new ImageIcon("images/edge_delete.png"));
        itemRemoveConnection.setEnabled(false); //Disable item "Remove connection"
                
        //Add item "Remove connection" to menu "Connection"
        menu.add(itemRemoveConnection);
        
        //Create menu "Help"
        menu = add(new JMenu("Help"));
        
        //Create item "About"
        itemAbout = new JMenuItem("About",new ImageIcon("images/about.png"));
        
        //Add item "About" to menu "Help"
        menu.add(itemAbout);
        
    }
    
    public void createListeners(){
        
        //Create listener for item "New analysis"
        itemNewAnalysis.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //If button "Start new analysis" is being pressed for the second or more time
                if(manager.getCountNewAnalysis()!=0){
                    
                    //Disable menus to add activity and to validate/resolve inputs
                    menuValidate.setEnabled(false);
                    menuResolve.setEnabled(false);
                    itemAddActivity.setEnabled(false);
                    itemExecute.setEnabled(false);
                    itemSaveResults.setEnabled(false);
                    itemRemoveConnection.setEnabled(false);
                    itemRemoveDataSource.setEnabled(false);
                    itemRemoveActivity.setEnabled(false);
                    toolBar.disableButtonValidate();
                    toolBar.disableButtonResolve();
                    toolBar.disableButtonAddActivity();
                    toolBar.disableButtonExecute();
                    toolBar.disableButtonSaveResults();
                    toolBar.disableButtonRemoveConnection();
                    toolBar.disableButtonRemoveDataSource();
                    toolBar.disableButtonRemoveActivity();
                                        
                    //Clean all variables of manager
                    manager.cleanAll();
                    
                    //Clean all variables of MainFrame
                    jFrame.cleanAll();
                    
                }  
                
                //Start new session and save session ID
                manager.startSession();
                    
                //Call SwingWorker LoadingLabels to load labels from ontology and all input concepts
                new LoadingLabels().execute();
                
            }
        });
        
        //Create listener for itemExit
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Exclude all created files
                manager.excludeAllFiles();
                
                Principal.main(new String[0]);
                jFrame.dispose();
                System.exit(0);
                
            }
        });
        
        //Create listener for item "Add activity"
        itemAddActivity.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Load function concepts from ontology (SwingWorker)
                new LoadingFunctionConcepts().execute();
                        
            }
        });
        
        //Create listener for item "Remove activity"
        itemRemoveActivity.addActionListener(new java.awt.event.ActionListener() {
            
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
        
        //Create listener for item "Execute"
        itemExecute.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Call SwingWorker "ExecutingServ"
                new ExecutingServ().execute();
                
            }
        });
        
        //Create listener for item "Execute"
        itemSaveResults.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Call SwingWorker "SavingResults"
                new SavingResults().execute();
                
            }
        });
        
        //Create listener for itemAddDataSource
        itemAddDataSource.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Call SwingWorker "LoadingInputConcepts"
                new LoadingInputConcepts().execute();
                
            }
        });
        
        
        //Create listener for itemRemoveDataSource
        itemRemoveDataSource.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Present confirmation question to user
                int result = JOptionPane.showConfirmDialog(jFrame,"<html>Are you sure you want to<br>remove data source?<html>","Remove Data Source",JOptionPane.YES_NO_OPTION);
                                                                    
                //If user select yes. Else, do nothing
                if (result == JOptionPane.YES_OPTION){
                
                    //Call SwingWorker "RemovingDataSource"
                    new RemovingDataSource().execute();
                    
                }
                                
            }
        });
        
        
        //Create listener for itemRemoveConnection
        itemRemoveConnection.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Create vector of mappings to be selected by user
                JPanel jPanel = new JPanel(new BorderLayout());
                JLabel jLabel = new JLabel("<html>Please, select one or more connections to remove:<br>&nbsp;</html>");
                String[] possibilities = new String[edgeMap.size()];
                HashMap<String,String> auxMap = new HashMap<>();
                int count = 0;
                for(Map.Entry<String, String> entry : edgeMap.entrySet()){
                  
                    if(isDataSource){
                        possibilities[count] = "{" + manager.getDataSourceName(manager.getDataSourceValue(sourceID)) + ",Input " + entry.getKey().split("InputID-")[1] + " of " 
                                + manager.getAnalysisNameToServ(targetID) + "}";
                        auxMap.put(entry.getKey(),possibilities[count]);
                    }
                    else{
                        possibilities[count] = "{Output " + entry.getValue().split("OutputID-")[1] +" of " + manager.getAnalysisNameToServ(sourceID)  
                                + ",Input " + entry.getKey().split("InputID-")[1] + " of " + manager.getAnalysisNameToServ(targetID) + "}";
                        auxMap.put(entry.getKey(),possibilities[count]);
                    }
                    count++;
                    /*if(isDataSource){
                        possibilities[count] = "{" + manager.getDataSourceName(manager.getDataSourceValue(sourceID)) + ",Input " + entry.getKey().split("InputID-")[1] + " of " 
                                + manager.getSelectedServiceName(targetID) + "(Instance " + targetID.split("uddi:")[0].split("inst")[1] +"}";
                        auxMap.put(entry.getKey(),possibilities[count]);
                    }
                    else{
                        possibilities[count] = "{Output " + entry.getValue().split("OutputID-")[1] +" of " 
                                + manager.getSelectedServiceName(sourceID) + "(Instance " + sourceID.split("uddi:")[0].split("inst")[1] 
                                + "),Input " + entry.getKey().split("InputID-")[1] + " of " + manager.getSelectedServiceName(targetID) + "(Instance "
                                + targetID.split("uddi:")[0].split("inst")[1] + ")}";
                        auxMap.put(entry.getKey(),possibilities[count]);
                    }
                    count++;
                            */
                }
                JList list = new JList(possibilities);
                list.setBorder(BorderFactory.createLineBorder(Color.gray));
                jPanel.add(jLabel,BorderLayout.NORTH);
                jPanel.add(list,BorderLayout.SOUTH);
            
                //Initialize list of selected inputIDs
                selectedInputIDs = new LinkedList<>();
            
                boolean isValid = false;
                while(!isValid){
                
                    //Present mappings for user to choose
                    int value = JOptionPane.showConfirmDialog(jFrame, jPanel, "Remove Connection",JOptionPane.CLOSED_OPTION);
                
                    //If user select OK
                    if(value == JOptionPane.OK_OPTION){
                    
                        //If no mapping was selected
                        if(list.getSelectedIndices().length==0)
                            JOptionPane.showMessageDialog(jFrame,"Please, select an option to procceed","Remove Connection",JOptionPane.INFORMATION_MESSAGE);
                        else{ //Else, if one or more mappings were selected
                        
                            //Get values selected by user
                            Object[] selectedValues = (Object[]) list.getSelectedValuesList().toArray();
                        
                            //Populate list of selected inputIDs
                            for(int i=0; i<selectedValues.length;i++){
                                for(Map.Entry<String, String> entry : auxMap.entrySet()){
                                    if(entry.getValue().equals(selectedValues[i].toString()))
                                        selectedInputIDs.add(entry.getKey());
                                }
                            }
                
                            //Set isValid to true
                            isValid = true;
                        }
                    } //Else, if user close the ConfirmDialog (cancel the operation), set isValid to true
                    else
                        isValid = true;
                }            
            
                //If some inputID was selected by user
                if(!selectedInputIDs.isEmpty()){
                    
                    //Present confirmation question to user
                    int result = JOptionPane.showConfirmDialog(jFrame,"<html>Are you sure you want to<br>remove connection?<html>","Remove connection",JOptionPane.YES_NO_OPTION);
                                                                    
                    //If user select yes. Else, do nothing
                    if (result == JOptionPane.YES_OPTION){
                
                        //Call SwingWorker to remove selected mappings
                        new RemovingEdges().execute();
                        
                    }
                
                }
                                
            }
        });
        
        //Create listener for itemAbout
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                //Create
                AboutDialog about = new AboutDialog(jFrame);
                about.setModal(true);

                // Centers inside the application frame
                int x = jFrame.getX() + (jFrame.getWidth() - about.getWidth()) / 2;
                int y = jFrame.getY() + (jFrame.getHeight() - about.getHeight()) / 2;
                about.setLocation(x, y);

                //Set AboutDialog visible
                about.setVisible(true);
                
            }
        });
    }
    
    public void setReferenceToToolBar(ToolBar toolBar){
        this.toolBar = toolBar;
    }
    
    //Enable menu "Execute" and set variable "execServInfo"
    public void enableMenuExecute(ServiceInfo serviceInfo){
        
        //Set variable "execServInfo"
        this.execServInfo = serviceInfo;
        
        //Enable item "Execute"
        itemExecute.setEnabled(true); 
        
    }
    
    //Disable menu "Execute"
    public void disableMenuExecute(){
        
        //Disable item "Execute"
        itemExecute.setEnabled(false); 
        
    }
    
    //Enable menu "Save results" and set variable "X" (arrumar)
    public void enableMenuSaveResults(ServiceInfo serviceInfo){
        
        this.servToSaveResults = serviceInfo;
        
        //Enable item "Save results"
        itemSaveResults.setEnabled(true); 
        
    }
    
    //Disable menu "Save results"
    public void disableMenuSaveResults(){
        
        //Disable item "Save results"
        itemSaveResults.setEnabled(false); 
        
    }
    
    //Disable menu "Validate inputs"
    public void disableMenuValidate(){
        
        //Disable menu "Validate inputs"
        menuValidate.setEnabled(false); 
        
    }
    
    //Disable menu "Resolve inputs"
    public void disableMenuResolve(){
        
        //Disable menu "Resolve inputs"
        menuResolve.setEnabled(false); 
        
    }
    
    public void enableMenuAddDataSource(){
        
        itemAddDataSource.setEnabled(true);
        
    }
    
    public void enableMenuAddActivity(){
        
        itemAddActivity.setEnabled(true);
        
    }
    
    public void disableMenuAddActivity(){
        
        itemAddActivity.setEnabled(false);
        
    }
    
    public void disableMenuRemoveDataSource(){
        
        itemRemoveDataSource.setEnabled(false);
        
    }
    
    public void enableMenuRemoveDataSource(String dataSourceID, HashMap<ServiceInfo,LinkedList<String>> mapforDataSourceRemoval){
        
        this.dataSourceID = dataSourceID;
        this.mapforDataSourceRemoval = mapforDataSourceRemoval;
        
        itemRemoveDataSource.setEnabled(true);
    }
    
    
    public void enableMenuRemoveConnection(String edgeID,HashMap<String,String> map,String sourceID,String targetID,boolean isDataSource){
        
        this.edgeID = edgeID;
        this.edgeMap = map;
        this.sourceID = sourceID;
        this.targetID = targetID;
        this.isDataSource = isDataSource;                
        
        itemRemoveConnection.setEnabled(true);
        
    }
    
    public void disableMenuRemoveConnection(){
        
        itemRemoveConnection.setEnabled(false);
        
    }
    
    public void enableMenuRemoveActivity(ServiceInfo servInfoToBeExcluded,HashMap<ServiceInfo,LinkedList<String>> mapOutgoing){
        
        this.servInfoToBeExcluded = servInfoToBeExcluded;
        this.mapOutgoing = mapOutgoing;
        itemRemoveActivity.setEnabled(true);
        
    }
    
    public void disableMenuRemoveActivity(){
        
        itemRemoveActivity.setEnabled(false);
        
    }
    
    //Add options to menu "Validate Inputs"
    public void addOptionsToMenuValidate(ServiceInfo serviceInfo){
        
        //Remove all previously added input items
        menuValidate.removeAll();
        
        //Get id and inputs of selected service
        valServiceID = serviceInfo.getID();
        final List<Input> inputs = serviceInfo.getInput();   
        
        //For each service input
        int countV = 1;
        for(Iterator it = inputs.iterator();it.hasNext();){
            
            Input input = (Input) it.next();
            
            String element = manager.getLabel(input.getSemanticalType());
                
            //Create submenu for input
            final JMenuItem inputItem;
            menuValidate.add(inputItem = new JMenuItem("Input " + countV + ": " + element));
            countV++;
                
            inputItem.setName(input.getID());
                
            //Add listener to submenu
            inputItem.addActionListener(new ActionListener() {
                    
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                
                    valSelectedInputID = inputItem.getName();
                        
                    //Get suggestion values of selected input
                    List<SuggestionValue> suggestions = new LinkedList<>();
                    for(int i=0; i<inputs.size();i++){
                        if(inputs.get(i).getID().equals(valSelectedInputID))
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
        
        //Enable menu "Validate Inputs"
        menuValidate.setEnabled(true);
        
    }
    
    //Clear menu "Validate Inputs"
    public void clearMenuValidate(){
        
        menuValidate.removeAll();
        menuValidate.setEnabled(false);
    }
    
    //Add options to menu "Resolve Inputs"
    public void addOptionsToMenuResolve(ServiceInfo serviceInfo){
        
        //Remove all previously added input items
        menuResolve.removeAll();
        
        //Get id and inputs of selected service
        serviceID = serviceInfo.getID();
        final List<Input> inputs = serviceInfo.getInput();   
        
        //For each service input
        int countR = 1;
        for(Iterator it = inputs.iterator();it.hasNext();){
                
            Input input = (Input) it.next();
            
            String element = manager.getLabel(input.getSemanticalType());
                
            //Create submenu for input
            final JMenuItem inputItem;
            menuResolve.add(inputItem = new JMenuItem("Input " + countR + ": " + element));
            countR++;
                
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
        
        //Enable menu "Resolve Inputs"
        menuResolve.setEnabled(true);
            
    }
    
    //Clear menu "Resolve Inputs"
    public void clearMenuResolve(){
        
        menuResolve.removeAll();
        menuResolve.setEnabled(false);
    }
    
    //Internal class for loading labels from ontology
    class LoadingLabels extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
            
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
            
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"New Analysis","   Starting new analysis   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);

            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
            
            //Set AboutDialog visible
            loadingDialog.setVisible(true);
            
            //If buttom "Start new analysis" is pressed for the first time
            if(manager.getCountNewAnalysis()==0){
            
                //Call SemDiscoveryAllLabels for all gene expression data
                manager.semDiscoveryAllLabels("http://dcm.ffclrp.usp.br/lssb/geas/ontologies/GEXPASO.owl#GEXPASO_0000141");
            
                //Call SemDiscoveryAllLabels for all parameters
                manager.semDiscoveryAllLabels("http://edamontology.org/data_2527");
            
                //Call SemDiscoveryAllLabels for all functions
                manager.semDiscoveryAllLabels("http://www.ebi.ac.uk/efo/swo/SWO_0000393");
            
                //Call semDiscoveryAllInputs
                manager.semDiscoveryAllInputs("http://dcm.ffclrp.usp.br/lssb/geas/ontologies/GEXPASO.owl#GEXPASO_0000141");
            
                //Enable items "Add data source"
                itemAddDataSource.setEnabled(true);
                toolBar.enableButtonAddDataSource();
                
            }
            
            //Set graphComponent color to white
            jFrame.setGraphComponentColor(new Color(255,255,255));
                
            //Insert vertex representing init of process in the graph
            jFrame.insertInitVertex();
            
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
            
            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
            
            return null;
        }

        @Override
        protected void done() {
            
            //Iterate countNewAnalysis
            manager.iterateCountNewAnalysis();
            
        }
    }
    
    //Internal class for loading input concepts from ontology
    class LoadingInputConcepts extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
            
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
            
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Select Data Type","   Loading data types   ",550,400,140,40,3,40);
            loadingDialog.setModal(false);

            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);

            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //Call JDialog "SelectInputConceptDialog"
            SelectInputConceptDialog selectInputConceptDialog = new SelectInputConceptDialog(jFrame,manager);
            
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
            
            //Set selectInputConceptDialog to modal
            selectInputConceptDialog.setModal(true);

            //Center selectInputConceptDialog inside the application frame
            x = jFrame.getX() + (jFrame.getWidth() - selectInputConceptDialog.getWidth()) / 2;
            y = jFrame.getY() + (jFrame.getHeight() - selectInputConceptDialog.getHeight()) / 2;
            selectInputConceptDialog.setLocation(x, y);

            //Set selectInputConceptDialog visible
            selectInputConceptDialog.setVisible(true);
                
            //If some input concept is selected into SelectInputConceptDialog
            if(selectInputConceptDialog.isSelected()){
                    
                //Create a file chooser
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setMultiSelectionEnabled(true);
                
                //Call file chooser
                int returnVal = fileChooser.showOpenDialog(jFrame);
                
                //If user select some file
                if (returnVal == JFileChooser.APPROVE_OPTION){
                    
                    //Get selected files and construct a string representing them
                    File[] selectedFiles = fileChooser.getSelectedFiles();
                    String value = "";
                    if(selectedFiles.length == 1)
                        value = selectedFiles[0].getPath();
                    else{
                        for(int i=0;i<selectedFiles.length-1;i++)
                            value = value + selectedFiles[i].getPath() + ";";
                        value = value + selectedFiles[selectedFiles.length-1].getPath();
                    }
                    
                    //Add data source to manager
                    DataSource ds = manager.addDataSource(value,selectedFiles);
                    
                    //If data source was not already inserted
                    if(ds != null){
                        
                        //Add selected input concept to manager
                        manager.addSelectedInputConcept(ds.getDataSourceID());
                    
                        //Insert vertex for data element
                        jFrame.insertDataElementVertex(ds.getDataSourceID(),ds.getDataSourceName());
                        
                    }
                    else{ //If data source was previously inserted
                        
                        //Present question to user
                        int result = JOptionPane.showConfirmDialog(jFrame,"Data source already exists!\nWould you like to change associated data type?","Add Data Source",JOptionPane.YES_NO_OPTION);
                
                        //If user select yes
                        if (result == JOptionPane.YES_OPTION){
                        
                            //Call JDialog "SelectInputConceptDialog"
                            SelectInputConceptDialog selectInputConceptDialog2 = new SelectInputConceptDialog(jFrame,manager);
                            
                            //Set selectInputConceptDialog to modal
                            selectInputConceptDialog2.setModal(true);

                            //Center selectInputConceptDialog inside the application frame
                            x = jFrame.getX() + (jFrame.getWidth() - selectInputConceptDialog2.getWidth()) / 2;
                            y = jFrame.getY() + (jFrame.getHeight() - selectInputConceptDialog2.getHeight()) / 2;
                            selectInputConceptDialog2.setLocation(x, y);

                            //Set selectInputConceptDialog visible
                            selectInputConceptDialog2.setVisible(true);
                            
                            //If some input concept is selected into SelectInputConceptDialog
                            if(selectInputConceptDialog2.isSelected()){
                                
                                //Add selected input concept to manager
                                manager.addSelectedInputConcept(manager.getDataSourceIDByUserValue(value));
                                
                                //Update input concept associated to data source
                                manager.updateDataSource(value);
                                    
                            }
                        }
                    }
                        
                    //Enable items "Add Activity"
                    itemAddActivity.setEnabled(true);
                    toolBar.enableButtonAddActivity();
                    
                }//Else, if user does not select a file
                else{
                    
                    //If no input concept was already selected and there are no services in the composition
                    if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                        itemAddActivity.setEnabled(false);
                        toolBar.disableButtonAddActivity();
                    }
                
                }
                
            }//Else, if no input concept is selected
            else{
                
                //If no input concept was already selected and there are no services in the composition
                if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                    itemAddActivity.setEnabled(false);
                    toolBar.disableButtonAddActivity();
                }
                
            }
            
            //Dispose selectInputConceptDialog
            selectInputConceptDialog.dispose();
            
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
    
    //Internal class for loading function concepts from ontology
    class LoadingFunctionConcepts extends SwingWorker<String,Object> {

        @Override
        protected String doInBackground() throws JAXBException {
            
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
            
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Select Analysis Activity","   Loading analysis activities   ",550,400,140,40,3,40);
            loadingDialog.setModal(false);

            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
            
            //Set LoadingDialog visible
            loadingDialog.setVisible(true);
            
            //Call JDialog "SelectFunctionConceptDialog"
            SelectFunctionConceptDialog selectFunctionDialog = new SelectFunctionConceptDialog(jFrame,manager);
            
            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
            
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
            
            selectFunctionDialog.setModal(true);

            //Center inside the application frame
            x = jFrame.getX() + (jFrame.getWidth() - selectFunctionDialog.getWidth()) / 2;
            y = jFrame.getY() + (jFrame.getHeight() - selectFunctionDialog.getHeight()) / 2;
            selectFunctionDialog.setLocation(x,y);

            //Set selectFunctionDialog visible
            selectFunctionDialog.setVisible(true);
                
            //If some functional concept is selected into SelectFunctionConceptDialog
            if(selectFunctionDialog.isSelected()){
                
                //Disable jFrame and all of its components
                jFrame.setEnabled(false);
            
                //Call JDialog "LoadingDialog"
                loadingDialog = new LoadingDialog(jFrame,"Select Web Service","   Loading web services   ",550,400,140,40,3,40);
                loadingDialog.setModal(false);

                //Center inside the application frame
                x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
                y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
                loadingDialog.setLocation(x, y);

                //Set LoadingDialog visible
                loadingDialog.setVisible(true);
                
                //Call JDialog "SelectServiceDialog"
                SelectServiceDialog selectServiceDialog = new SelectServiceDialog(jFrame,manager);
                
                //Enable jFrame and all of its components
                jFrame.setEnabled(true);
            
                //Make loadingDialog invisible
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
                
                selectServiceDialog.setModal(true);

                //Center inside the application frame
                x = jFrame.getX() + (jFrame.getWidth() - selectServiceDialog.getWidth()) / 2;
                y = jFrame.getY() + (jFrame.getHeight() - selectServiceDialog.getHeight()) / 2;
                selectServiceDialog.setLocation(x, y);

                //Set AboutDialog visible
                selectServiceDialog.setVisible(true);
                    
                //If some service is selected into selectServiceDialog
                if(selectServiceDialog.isSelected()){
                        
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
                    
                    if(manager.serviceInclusion()){
                        
                        //Call manager to update all services that are ready to be executed
                        manager.execServ();
                            
                        //Call operation to Add analysis activity
                        if(!manager.getSelectedService().getFunction().isEmpty()) //For service
                            jFrame.insertVertexElement(manager.getSelectedService().getID(),manager.getLabel(manager.getSelectedService().getFunction().get(0)),false);
                        else //For connector
                            jFrame.insertVertexElement(manager.getSelectedService().getID(),manager.getSelectedService().getName(),true);
                   
                        //Update connections of selected service to root element
                        jFrame.updateRoot(manager.getSelectedService().getID());
                        
                    }
                    
                    //Enable jFrame and all of its components
                    jFrame.setEnabled(true);
            
                    //Make loadingDialog invisible
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();                    
                }
                
                selectServiceDialog.dispose();

            }            
           
            selectFunctionDialog.dispose();  
            
            
            return null;
                       
        }

        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            jFrame.clearGraphSelection();
            
        }
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
            if(manager.serviceValidation(valServiceID, valSelectedInputID, valSelectedValue, valIsOutput)){
                      
                //Insert edge (only if edge do not already exist)
                jFrame.insertEdgeElement(valSelectedInputID,valSelectedValue,!valIsOutput);
                        
                //Call manager to AddToContext "null" to all outputs of service being validated
                LinkedList<String> serviceIDs = jFrame.findAllChildren(valServiceID);
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
                jFrame.updateVerticesColorToLightGray(valServiceID);

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
            manager.clearServiceDirectory(execServInfo.getID());
            
            //If engine was sucessfully created
            if(activiti.createProcessEngine(manager.getActivitiDbDriver(), 
                                            manager.getActivitiDbURL(),
                                            manager.getActivitiDbUser(),
                                            manager.getActivitiDbPassword())){
                    
                //If process is successfully deployed
                if(activiti.deployProcess(execServInfo.getLocation())){
                
                    //Get execution values
                    HashMap<String,String> executionValues = new HashMap<>();
                    List<Input> inputs = execServInfo.getInput();
                    for(Iterator it=inputs.iterator();it.hasNext();){
                        Input input = (Input) it.next();
                        executionValues.put(input.getName(),input.getExecutionValue());
                    }
                    List<Output> outs = execServInfo.getOutput();
                    for(Iterator it=outs.iterator();it.hasNext();){
                        Output output = (Output) it.next();
                        executionValues.put(output.getName(),manager.getServiceDirectoryPath(execServInfo.getID())+"/");
                    }
                    
                    //Make loadingDialog invisible
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                        
                    //If process is successfully started and finished
                    if(activiti.runProcess(execServInfo.getLocation(),executionValues)){
                        
                        //Create list of ContextValueStructures
                        LinkedList<ContextValueStructure> contextValues = new LinkedList<>();
                        
                        //If process was successfully finished
                        if(activiti.getStatus().equals("finished")){
                            
                            //Get output variables
                            HashMap<String,String> result = activiti.getOutputVariables();
                    
                            //Get outputs of service
                            List<Output> outputs = execServInfo.getOutput();
                            
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
                                                contextValue = new ContextValueStructure(execServInfo.getID(),outputs.get(i).getID(),entry.getValue());
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
                            
                                        //Set variable "servToSaveResults"
                                        servToSaveResults = execServInfo;
                                        
                                        //Call SwingWorker "SavingResults"
                                        new SavingResults().execute();
                                        
                                    }
                            
                                }
                                
                            }//Else, if service has no outputs
                            else{
                                
                                //Call manager to add service ID to context
                                manager.addServiceIdToContext(execServInfo.getID());
                                
                            }
                            
                        
                        }//Else, if process failed
                        else{
                            
                            ContextValueStructure contextValue; 
                            for(Iterator it = execServInfo.getOutput().iterator();it.hasNext();){
                                contextValue = new ContextValueStructure(execServInfo.getID(),((Output)it.next()).getID(),"null");
                                contextValues.add(contextValue);
                            }
                                
                            //Call manager to add output values to context
                            manager.addValuesToContext(contextValues);
                        }
                        
                        //Call manager to update all services that are ready to be executed
                        manager.execServ();
                        
                        //Update vertex color
                        if(activiti.getStatus().equals("finished"))
                            jFrame.changeVertexColor(execServInfo,"lightgreen");
                        else if(activiti.getStatus().equals("failure"))
                            jFrame.changeVertexColor(execServInfo,"salmon");
                        
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
    
    //Internal class for removing mappings of edge
    class RemovingEdges extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
                    
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
                    
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Remove Connection","   Removing connection   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                    
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
                    
            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //Call manager to validate each selected inputID to "null" and remove associated edge
            for(Iterator it = selectedInputIDs.iterator();it.hasNext();){
                String inputID = (String)it.next();
                manager.serviceValidation(targetID,inputID,"null",false);
                jFrame.removeEdgeElement(edgeID,inputID);
            }
            
            //Call manager to AddToContext "null" to all outputs of service being validated
            LinkedList<String> serviceIDs = jFrame.findAllChildren(targetID);
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
            jFrame.updateVerticesColorToLightGray(targetID);

            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
                        
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
                        
            JOptionPane.showMessageDialog(jFrame, "Connection Removed!","Message",JOptionPane.INFORMATION_MESSAGE);
        
            return null;
        }

        @Override
        protected void done() {
        
            //Disable all menus 
            clearMenuValidate();
            clearMenuResolve();
            disableMenuExecute();
            disableMenuSaveResults();
            disableMenuRemoveConnection();
            disableMenuRemoveDataSource();
            disableMenuRemoveActivity();
                        
            toolBar.disableButtonValidate();
            toolBar.disableButtonResolve();
            toolBar.disableButtonExecute();
            toolBar.disableButtonSaveResults();
            toolBar.disableButtonRemoveConnection();
            toolBar.disableButtonRemoveDataSource();
            toolBar.disableButtonRemoveActivity();
        
        }
    }
    
    //Internal class for removing a data source
    class RemovingDataSource extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
                    
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
                    
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Remove Data Source","   Removing data source   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                    
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
                    
            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //For each mapping
            for(Map.Entry<ServiceInfo,LinkedList<String>> entry : mapforDataSourceRemoval.entrySet()){
                
                //Get current service
                ServiceInfo currentServInfo = entry.getKey();
                
                //Get inputIDs of service to be validated to "null"
                LinkedList<String> inputIDs = entry.getValue();
                
                //Call manager to validate each inputID to "null"
                for(Iterator it = inputIDs.iterator();it.hasNext();){
                    String inputID = (String)it.next();
                    manager.serviceValidation(currentServInfo.getID(),inputID,"null",false);
                }
                
                //Call manager to AddToContext "null" to all outputs of current service
                LinkedList<String> serviceIDs = jFrame.findAllChildren(currentServInfo.getID());
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
                
                //Update vertex colors of current service and all of its children
                jFrame.updateVerticesColorToLightGray(currentServInfo.getID());
                
            }
            
            //If map is not empty, call manager to update all services that are ready to be executed
            if(!mapforDataSourceRemoval.isEmpty())
                manager.execServ();
            
            //Call manager to remove all suggestionValues of all services contained in the composition referencing the dataSource being removed
            manager.updateAllSuggestionValuesForDataSourceRemoval(dataSourceID);
            
            //Call manager to update list "listOfSelectedInputs" to remove reference to dataSource being removed
            manager.updateListOfSelectedInputsForDataSourceRemoval(dataSourceID);
            
            //Remove dataSource vertex and all associated edges from graph
            jFrame.removeDataElementVertex(dataSourceID);
            
            //Call manager to remove data source being excluded from list "listOfDataSources" and to put it into "listOfExcludedDataSources"
            manager.removeDataSource(dataSourceID);
            
            //If no input concept was already selected and there are no services in the composition
            if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                itemAddActivity.setEnabled(false);
                toolBar.disableButtonAddActivity();
            }//Else, if numberOfDataSources > 0, enable menus "Add Activity"
            else{
                itemAddActivity.setEnabled(true);
                toolBar.enableButtonAddActivity();
            }
            
            //Enable jFrame and all of its components
            jFrame.setEnabled(true);
                        
            //Make loadingDialog invisible
            loadingDialog.setVisible(false);
            loadingDialog.dispose();
                        
            JOptionPane.showMessageDialog(jFrame, "Data Source Removed!","Message",JOptionPane.INFORMATION_MESSAGE);
        
            return null;
        }

        @Override
        protected void done() {
        
            //Disable all menus 
            clearMenuValidate();
            clearMenuResolve();
            disableMenuExecute();
            disableMenuSaveResults();
            disableMenuRemoveConnection();
            disableMenuRemoveDataSource();
            disableMenuRemoveActivity();
                        
            toolBar.disableButtonValidate();
            toolBar.disableButtonResolve();
            toolBar.disableButtonExecute();
            toolBar.disableButtonSaveResults();
            toolBar.disableButtonRemoveConnection();
            toolBar.disableButtonRemoveDataSource();
            toolBar.disableButtonRemoveActivity();
            
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
            for(Iterator it = servInfoToBeExcluded.getInput().iterator();it.hasNext();){
                Input input = (Input) it.next();
                manager.serviceValidation(servInfoToBeExcluded.getID(),input.getID(),"null",false);
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
            LinkedList<String> serviceIDs = jFrame.findAllChildren(servInfoToBeExcluded.getID());
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
            jFrame.updateVerticesColorToLightGray(servInfoToBeExcluded.getID());
            
            //Call manager to update "listOfSelectedInputs", removing all outputs of service being excluded
            for(Iterator it=servInfoToBeExcluded.getOutput().iterator();it.hasNext();)
                manager.updateListOfSelectedInputsForActivityRemoval(((Output) it.next()).getID());
            
            //Call manager to update "listOfSelectedServices" and "listOfExcludedServices"
            manager.updateListOfIncludedServices(servInfoToBeExcluded.getID());
            
            //Call manager to remove all suggestionValues of all services contained in the composition referencing the service being removed
            manager.updateAllSuggestionValuesForActivityRemoval(servInfoToBeExcluded.getID());
            
            //Remove service vertex and all associated edges from graph
            jFrame.removeServiceVertex(servInfoToBeExcluded.getID());
            
            //If no input concept was already selected and there are no services in the composition
            if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                itemAddActivity.setEnabled(false);
                toolBar.disableButtonAddActivity();
            }//Else, if numberOfDataSources > 0, enable menus "Add Activity"
            else{
                itemAddActivity.setEnabled(true);
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
            clearMenuValidate();
            clearMenuResolve();
            disableMenuExecute();
            disableMenuSaveResults();
            disableMenuRemoveConnection();
            disableMenuRemoveDataSource();
            disableMenuRemoveActivity();
                        
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
                File serviceDir = manager.getServiceDirectory(servToSaveResults.getID());
            
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
