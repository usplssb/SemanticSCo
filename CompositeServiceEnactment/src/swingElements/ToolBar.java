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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
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
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Input;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import jaxbClasses.basictypes.SuggestionValue;
import management.CompositionManager;
import management.DataSource;

public class ToolBar extends JPanel implements ActionListener {
    
    static final private String NEWANALYSIS = "newanalysis";
    static final private String ADDACTIVITY = "addactivity";
    static final private String REMOVEACTIVITY = "removeactivity";
    static final private String VALIDATEINPUTS = "validateinputs";
    static final private String RESOLVEINPUTS = "resolveinputs";
    static final private String EXECUTEACTIVITY = "executeactivity";
    static final private String SAVERESULTS = "saveresults";
    static final private String ADDDATASOURCE = "adddatasource";
    static final private String REMOVEDATASOURCE = "removedatasource";
    static final private String REMOVECONNECTION = "removeconnection";
    
    //Create buttons
    private JButton buttonNewAnalysis,buttonAddActivity,buttonRemoveActivity,buttonValidate,
            buttonResolve,buttonExecute,buttonSaveResults,buttonAddDataSource,buttonRemoveDataSource,buttonRemoveConnection;
    
    private CompositionManager manager;
    private MainFrame jFrame;
    private MenuBar menuBar;
    
    private ServiceInfo serviceInfo;
    private String selectedInputID;
    
    private String valSelectedValue;
    private boolean valIsOutput;
    
    private String edgeID;
    private HashMap<String,String> edgeMap;
    private String sourceID;
    private String targetID;
    private boolean isDataSource;
    private LinkedList<String> selectedInputIDs;
    
    private String dataSourceID;
    private HashMap<ServiceInfo,LinkedList<String>> mapforDataSourceRemoval;
    
    private ServiceInfo servInfoToBeExcluded;
    private HashMap<ServiceInfo,LinkedList<String>> mapOutgoing;
    
    public ToolBar(MainFrame jFrame , CompositionManager manager, MenuBar menuBar) {
        
        super(new BorderLayout());
        
        this.setName("myToolBar");
        
        this.manager = manager;
        this.jFrame = jFrame;
        this.menuBar = menuBar;

        //Create JToolBar toolBar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false); //Make tool bar not floatable
        toolBar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); //Set color o toolbar
        
        //Add separator to toolBar
        toolBar.addSeparator(new Dimension(4,20));
        
        //Create button "New analysis"
        buttonNewAnalysis = new JButton();
        buttonNewAnalysis.setActionCommand(NEWANALYSIS);
        buttonNewAnalysis.setToolTipText("New analysis");
        buttonNewAnalysis.addActionListener(this);
        buttonNewAnalysis.setIcon(new ImageIcon("images/new_analysis.gif"));
        toolBar.add(buttonNewAnalysis);
        
        //Add separators to toolBar
        toolBar.addSeparator();
        toolBar.addSeparator();
        toolBar.addSeparator();
        
        //Create button "Add activity"
        buttonAddActivity = new JButton();
        buttonAddActivity.setActionCommand(ADDACTIVITY);
        buttonAddActivity.setToolTipText("Add activity");
        buttonAddActivity.addActionListener(this);
        buttonAddActivity.setIcon(new ImageIcon("images/add_activity.png"));
        buttonAddActivity.setEnabled(false);
        toolBar.add(buttonAddActivity);

        //Add separator to toolBar
        toolBar.addSeparator(new Dimension(3,20));
        
        //Create button "Remove activity"
        buttonRemoveActivity = new JButton();
        buttonRemoveActivity.setActionCommand(REMOVEACTIVITY);
        buttonRemoveActivity.setToolTipText("Remove activity");
        buttonRemoveActivity.addActionListener(this);
        buttonRemoveActivity.setIcon(new ImageIcon("images/delete_activity.png"));
        buttonRemoveActivity.setEnabled(false);
        toolBar.add(buttonRemoveActivity);

        //Add separator to toolBar
        toolBar.addSeparator();
        
        //Create button "Validate inputs"
        buttonValidate = new JButton();
        buttonValidate.setActionCommand(VALIDATEINPUTS);
        buttonValidate.setToolTipText("Validate inputs");
        buttonValidate.addActionListener(this);
        buttonValidate.setIcon(new ImageIcon("images/validate.png"));
        buttonValidate.setEnabled(false);
        toolBar.add(buttonValidate);

        //Add separator to toolBar
        toolBar.addSeparator(new Dimension(3,20));
        
        //Create button "Resolve inputs"
        buttonResolve = new JButton();
        buttonResolve.setActionCommand(RESOLVEINPUTS);
        buttonResolve.setToolTipText("Resolve inputs");
        buttonResolve.addActionListener(this);
        buttonResolve.setIcon(new ImageIcon("images/resolve.png"));
        buttonResolve.setEnabled(false);
        toolBar.add(buttonResolve);
        
        //Add separator to toolBar
        toolBar.addSeparator();
        
        //Create button "Execute activity"
        buttonExecute = new JButton();
        buttonExecute.setActionCommand(EXECUTEACTIVITY);
        buttonExecute.setToolTipText("Execute activity");
        buttonExecute.addActionListener(this);
        buttonExecute.setIcon(new ImageIcon("images/execute.png"));
        buttonExecute.setEnabled(false);
        toolBar.add(buttonExecute);
        
        //Create button "Save results"
        buttonSaveResults = new JButton();
        buttonSaveResults.setActionCommand(SAVERESULTS);
        buttonSaveResults.setToolTipText("Save result(s)");
        buttonSaveResults.addActionListener(this);
        buttonSaveResults.setIcon(new ImageIcon("images/save.png"));
        buttonSaveResults.setEnabled(false);
        toolBar.add(buttonSaveResults);
        
        //Add separators to toolBar
        toolBar.addSeparator();
        toolBar.addSeparator();
        toolBar.addSeparator();
        
        //Create button "Add data source"
        buttonAddDataSource = new JButton();
        buttonAddDataSource.setActionCommand(ADDDATASOURCE);
        buttonAddDataSource.setToolTipText("Add data source");
        buttonAddDataSource.addActionListener(this);
        buttonAddDataSource.setIcon(new ImageIcon("images/datasource_add.png"));
        buttonAddDataSource.setEnabled(false);
        toolBar.add(buttonAddDataSource);
        
        //Add separator to toolBar
        toolBar.addSeparator(new Dimension(3,20));
        
        //Create button "Remove data source"
        buttonRemoveDataSource = new JButton();
        buttonRemoveDataSource.setActionCommand(REMOVEDATASOURCE);
        buttonRemoveDataSource.setToolTipText("Remove data source");
        buttonRemoveDataSource.addActionListener(this);
        buttonRemoveDataSource.setIcon(new ImageIcon("images/datasource_delete.png"));
        buttonRemoveDataSource.setEnabled(false);
        toolBar.add(buttonRemoveDataSource);
        
        //Add separators to toolBar
        toolBar.addSeparator();
        toolBar.addSeparator();
        toolBar.addSeparator();
        
        //Create button "Remove connection"
        buttonRemoveConnection = new JButton();
        buttonRemoveConnection.setActionCommand(REMOVECONNECTION);
        buttonRemoveConnection.setToolTipText("Remove connection");
        buttonRemoveConnection.addActionListener(this);
        buttonRemoveConnection.setIcon(new ImageIcon("images/edge_delete.png"));
        buttonRemoveConnection.setEnabled(false);
        toolBar.add(buttonRemoveConnection);
        
                
        //Set layout properties of panel
        this.setPreferredSize(new Dimension(450,30));
        this.add(toolBar,BorderLayout.PAGE_START);
  
    }
    
    //Enable button "Add data source"
    public void enableButtonAddDataSource(){
        
        buttonAddDataSource.setEnabled(true);
        
    }
    
    //Enable button "Add activity"
    public void enableButtonAddActivity(){
        
        buttonAddActivity.setEnabled(true);
        
    }
    
    //Disable button "Add activity"
    public void disableButtonAddActivity(){
        
        buttonAddActivity.setEnabled(false);
        
    }
    
    //Enable button "Validate inputs"
    public void enableButtonValidate(){
        buttonValidate.setEnabled(true);
    }
    
    //Disable button "Validate inputs"
    public void disableButtonValidate(){
        
        buttonValidate.setEnabled(false);
        
    }
    
    //Enable button "Resolve inputs"
    public void enableButtonResolve(){
        
        buttonResolve.setEnabled(true);
        
    }
    
    //Disable button "Resolve inputs"
    public void disableButtonResolve(){
        
        buttonResolve.setEnabled(false);
        
    }
    
    //Enable button "Execute activity"
    public void enableButtonExecute(){
        
        buttonExecute.setEnabled(true);
        
    }
    
    //Disable button "Execute activity"
    public void disableButtonExecute(){
        
        buttonExecute.setEnabled(false);
        
    }
    
    //Enable button "Save results"
    public void enableButtonSaveResults(){
        
        buttonSaveResults.setEnabled(true);
        
    }
    
    //Disable button "Save results"
    public void disableButtonSaveResults(){
        
        buttonSaveResults.setEnabled(false);
        
    }
    
    //Enable button "Remove connection"
    public void enableButtonRemoveConnection(){
        
        buttonRemoveConnection.setEnabled(true);
        
    }
    
    //Disable button "Remove connection"
    public void disableButtonRemoveConnection(){
        
        buttonRemoveConnection.setEnabled(false);
        
    }
    
    //Disable button "Remove data source"
    public void disableButtonRemoveDataSource(){
        
        buttonRemoveDataSource.setEnabled(false);
        
    }
    
    //Enable button "Remove data source"
    public void enableButtonRemoveDataSource(){
        
        buttonRemoveDataSource.setEnabled(true);
        
    }
    
    //Disable button "Remove activity"
    public void disableButtonRemoveActivity(){
        buttonRemoveActivity.setEnabled(false);
    }
    
    //Enable button "Remove activity"
    public void enableButtonRemoveActivity(){
        buttonRemoveActivity.setEnabled(true);
    }
    
    //Set serviceInfo
    public void setServiceInfo(ServiceInfo serviceInfo){
        this.serviceInfo = serviceInfo;
    }
    
    //Set edge info
    public void setEdgeInfo(String edgeID,HashMap<String,String> map,String sourceID,String targetID,boolean isDataSource){
        this.edgeID = edgeID;
        this.edgeMap = map;
        this.sourceID = sourceID;
        this.targetID = targetID;
        this.isDataSource = isDataSource;
    }
    
    public void setDataSourceInfo(String dataSourceID, HashMap<ServiceInfo,LinkedList<String>> mapforDataSourceRemoval){
        
        this.dataSourceID = dataSourceID;
        this.mapforDataSourceRemoval = mapforDataSourceRemoval;
        
    }
    
    public void setInfoForActivityRemoval(ServiceInfo servInfoToBeExcluded,HashMap<ServiceInfo,LinkedList<String>> mapOutgoing){
        
        this.servInfoToBeExcluded = servInfoToBeExcluded;
        this.mapOutgoing = mapOutgoing;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        String cmd = e.getActionCommand();
        
        //If user select "New analysis"
        if (NEWANALYSIS.equals(cmd)){
            
            //If button "New analysis" is being pressed for the second or more time
            if(manager.getCountNewAnalysis()!=0){
                    
                //Disable menus to add activity and to validate/resolve inputs
                buttonValidate.setEnabled(false);
                buttonResolve.setEnabled(false);
                buttonAddActivity.setEnabled(false);
                buttonExecute.setEnabled(false);
                buttonSaveResults.setEnabled(false);
                buttonRemoveConnection.setEnabled(false);
                buttonRemoveDataSource.setEnabled(false);
                buttonRemoveActivity.setEnabled(false);
                menuBar.disableMenuValidate();
                menuBar.disableMenuResolve();
                menuBar.disableMenuAddActivity();
                menuBar.disableMenuExecute();
                menuBar.disableMenuSaveResults();
                menuBar.disableMenuRemoveConnection();
                menuBar.disableMenuRemoveDataSource();
                menuBar.disableMenuRemoveActivity();
                                
                //Clean all variables of manager
                manager.cleanAll();
                    
                //Clean all variables of MainFrame
                jFrame.cleanAll();
                    
            }  
                
            //Start new session and save session ID
            manager.startSession();
                    
            //Call SwingWorker LoadingLabels to load labels from ontology and all input concepts
            new LoadingLabels().execute();
            
        }//Else, if user select "Add activity"
        else if (ADDACTIVITY.equals(cmd)){
            
            //Load function concepts from ontology (SwingWorker)
            new LoadingFunctionConcepts().execute();
            
        } //Else, if user select "Remove activity"
        else if (REMOVEACTIVITY.equals(cmd)){
        
            //Present confirmation question to user
            int result = JOptionPane.showConfirmDialog(jFrame,"<html>Are you sure you want to<br>remove analysis activity?<html>","Remove activity",JOptionPane.YES_NO_OPTION);
                                                                    
            //If user select yes. Else, do nothing
            if (result == JOptionPane.YES_OPTION){
                        
                //Call SwingWorker "RemovingActivity"
                new RemovingActivity().execute();
                
            }
        
        }//Else, if user select "Validate inputs"
        else if (VALIDATEINPUTS.equals(cmd)){
            
            List<Input> inputs = serviceInfo.getInput();
            
            selectedInputID = null;
            
            //If only one input is available
            if(inputs.size()==1)
                selectedInputID = inputs.get(0).getID();
            else if(inputs.size()>1){ //Else, if there are more inputs, ask user what input to validate
                
                HashMap<String,String> map = new HashMap<>();
                Object[] possibilities = new Object[inputs.size()];
                int count = 0;
                for(Iterator it=inputs.iterator();it.hasNext();){
                    Input input = (Input) it.next();
                    String concept = manager.getLabel(input.getSemanticalType());
                    map.put(input.getID(),"Input " + (count+1) + ": " + concept);
                    possibilities[count] = "Input " + (count+1) + ": " + concept;
                    count++;
                }
                
                String selectedInput = (String)JOptionPane.showInputDialog(jFrame, "Please, select an input to validate: ","Select input",JOptionPane.PLAIN_MESSAGE,null,possibilities,"");
                
                //If some value is selected
                if(selectedInput != null){
                    if(map.containsValue(selectedInput)){
                        for (Map.Entry<String, String> entry : map.entrySet()){
                            if(entry.getValue().equals(selectedInput))
                                selectedInputID = entry.getKey();
                        }
                    }
                }
            }
            
            //If some inputID was selected
            if(selectedInputID != null){
                
                //Get suggestion values of selected input
                List<SuggestionValue> suggestions = new LinkedList<>();
                for(int i=0; i<inputs.size();i++){
                    if(inputs.get(i).getID().equals(selectedInputID))
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
            
        }//Else, if user select "Resolve inputs"
        else if (RESOLVEINPUTS.equals(cmd)){
            
            List<Input> inputs = serviceInfo.getInput();
            
            selectedInputID = null;
            
            //If only one input is available
            if(inputs.size()==1)
                selectedInputID = inputs.get(0).getID();
            else if(inputs.size()>1){
                
                HashMap<String,String> map = new HashMap<>();
                Object[] possibilities = new Object[inputs.size()];
                int count = 0;
                for(Iterator it=inputs.iterator();it.hasNext();){
                    Input input = (Input) it.next();
                    String concept = manager.getLabel(input.getSemanticalType());
                    map.put(input.getID(),"Input " + (count+1) + ": " + concept);
                    possibilities[count] = "Input " + (count+1) + ": " + concept;
                    count++;
                }
                
                String selectedInput = (String)JOptionPane.showInputDialog(jFrame, "Please, select an input to resolve: ","Select input",JOptionPane.PLAIN_MESSAGE,null,possibilities,"");
                
                //If some value is selected
                if(selectedInput != null){
                    if(map.containsValue(selectedInput)){
                        for (Map.Entry<String, String> entry : map.entrySet()){
                            if(entry.getValue().equals(selectedInput))
                                selectedInputID = entry.getKey();
                        }
                    }
                }
            }
            
            //If some inputID was selected
            if(selectedInputID != null){
                
                //Call SwingWorker "LoadingResolveServ"
                new LoadingResolveServ().execute();
                
            }
            
            
        }//Else, if user select "Execute activity"
        else if(EXECUTEACTIVITY.equals(cmd)){
            
            //Call SwingWorker "ExecutingServ"
            new ExecutingServ().execute();
            
        }//Else, if user select "Save results"
        else if(SAVERESULTS.equals(cmd)){
            
            //Call SwingWorker "SavingResults"
            new SavingResults().execute();
            
            
        }//Else, if user select "Add data source"
        else if(ADDDATASOURCE.equals(cmd)){
            
            //Call SwingWorker "LoadingInputConcepts"
            new LoadingInputConcepts().execute();
            
        }//Else, if user select "Remove data source"
        else if(REMOVEDATASOURCE.equals(cmd)){
            
            //Present confirmation question to user
            int result = JOptionPane.showConfirmDialog(jFrame,"<html>Are you sure you want to<br>remove data source?<html>","Remove Data Source",JOptionPane.YES_NO_OPTION);
                                                                    
            //If user select yes. Else, do nothing
            if (result == JOptionPane.YES_OPTION){
            
                //Call SwingWorker "RemovingDataSource"
                new RemovingDataSource().execute();
                
            }
            
        }
        else if(REMOVECONNECTION.equals(cmd)){
            
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
            
                //Enable item "Add data source"
                menuBar.enableMenuAddDataSource();
                buttonAddDataSource.setEnabled(true);
                
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
                        
                    //Enable item "Add Activity"
                    menuBar.enableMenuAddActivity();
                    buttonAddActivity.setEnabled(true);
                    
                }//Else, if user does not select a file
                else{
                    
                    //If no input concept was already selected and there are no services in the composition
                    if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                        menuBar.disableMenuAddActivity();
                        buttonAddActivity.setEnabled(false);
                    }
                
                }
                
            }//Else, if no input concept is selected
            else{
                
                //If no input concept was already selected and there are no services in the composition
                if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                    menuBar.disableMenuAddActivity();
                    buttonAddActivity.setEnabled(false);
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
            if(manager.serviceValidation(serviceInfo.getID(), selectedInputID, valSelectedValue, valIsOutput)){
                      
                //Insert edge (only if edge do not already exist)
                jFrame.insertEdgeElement(selectedInputID,valSelectedValue,!valIsOutput);
                        
                //Call manager to AddToContext "null" to all outputs of service being validated
                LinkedList<String> serviceIDs = jFrame.findAllChildren(serviceInfo.getID());
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
                jFrame.updateVerticesColorToLightGray(serviceInfo.getID());

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
            HashMap<String,ServiceInfo> mapOfServices = manager.resolveServiceInput(serviceInfo.getID(), selectedInputID);
                            
            //Get updated suggestion values of selected input
            List<SuggestionValue> suggestions = new LinkedList<>();
            List<Input> auxInputs = manager.getIncludedService(serviceInfo.getID()).getInput();
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
            ResolveInputDialog resolveInputDialog = new ResolveInputDialog(jFrame,manager,serviceInfo.getID(),selectedInputID,vc,mapOfServices);
                            
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
                    jFrame.updateVerticesColorToLightGray(serviceInfo.getID());
                    
                    //Call operation to Add analysis activity
                    if(!manager.getSelectedService().getFunction().isEmpty()) //For service
                        jFrame.insertVertexElement(manager.getSelectedService().getID(),manager.getLabel(manager.getSelectedService().getFunction().get(0)),false);
                    else //For connector
                        jFrame.insertVertexElement(manager.getSelectedService().getID(),manager.getSelectedService().getName(),true);
                
                    //Update connections of selected service to root element
                    jFrame.updateRoot(manager.getSelectedService().getID());
                    
                    //Call manager to validate input
                    if(manager.serviceValidation(serviceInfo.getID(), selectedInputID, manager.getSelectedOutputID(),true)){
                        
                        //Insert edge (only if edge do not already exist)
                        jFrame.insertEdgeElement(selectedInputID,manager.getSelectedOutputID(),false);
                        
                        //Set suggestion values of service being resolved to include the selected output
                        manager.addSuggestionValue(serviceInfo.getID(), selectedInputID, manager.getSelectedOutputID());
                        
                        //Call manager to AddToContext "null" to all outputs of service being validated
                        LinkedList<String> serviceIDs = jFrame.findAllChildren(serviceInfo.getID());
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
                        jFrame.updateVerticesColorToLightGray(serviceInfo.getID());

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
            manager.clearServiceDirectory(serviceInfo.getID());
            
            //If engine was sucessfully created
            if(activiti.createProcessEngine(manager.getActivitiDbDriver(), 
                                            manager.getActivitiDbURL(),
                                            manager.getActivitiDbUser(),
                                            manager.getActivitiDbPassword())){
                    
                //If process is successfully deployed
                if(activiti.deployProcess(serviceInfo.getLocation())){
                
                    //Get execution values
                    HashMap<String,String> executionValues = new HashMap<>();
                    List<Input> inputs = serviceInfo.getInput();
                    for(Iterator it=inputs.iterator();it.hasNext();){
                        Input input = (Input) it.next();
                        executionValues.put(input.getName(),input.getExecutionValue());
                    }
                    List<Output> outs = serviceInfo.getOutput();
                    for(Iterator it=outs.iterator();it.hasNext();){
                        Output output = (Output) it.next();
                        executionValues.put(output.getName(),manager.getServiceDirectoryPath(serviceInfo.getID())+"/");
                    }
                    
                    //Make loadingDialog invisible
                    loadingDialog.setVisible(false);
                    loadingDialog.dispose();
                        
                    //If process is successfully started and finished
                    if(activiti.runProcess(serviceInfo.getLocation(),executionValues)){
                        
                        //Create list of ContextValueStructures
                        LinkedList<ContextValueStructure> contextValues = new LinkedList<>();
                        
                        //If process was successfully finished
                        if(activiti.getStatus().equals("finished")){
                            
                            //Get output variables
                            HashMap<String,String> result = activiti.getOutputVariables();
                    
                            //Get outputs of service
                            List<Output> outputs = serviceInfo.getOutput();
                            
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
                                                contextValue = new ContextValueStructure(serviceInfo.getID(),outputs.get(i).getID(),entry.getValue());
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
                                manager.addServiceIdToContext(serviceInfo.getID());
                                
                            }
                            
                        
                        }//Else, if process failed
                        else{
                            
                            ContextValueStructure contextValue; 
                            for(Iterator it = serviceInfo.getOutput().iterator();it.hasNext();){
                                contextValue = new ContextValueStructure(serviceInfo.getID(),((Output)it.next()).getID(),"null");
                                contextValues.add(contextValue);
                            }
                                
                            //Call manager to add output values to context
                            manager.addValuesToContext(contextValues);
                        }
                        
                        //Call manager to update all services that are ready to be executed
                        manager.execServ();
                        
                        //Update vertex color
                        if(activiti.getStatus().equals("finished"))
                            jFrame.changeVertexColor(serviceInfo,"lightgreen");
                        else if(activiti.getStatus().equals("failure"))
                            jFrame.changeVertexColor(serviceInfo,"salmon");
                        
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
            menuBar.clearMenuValidate();
            menuBar.clearMenuResolve();
            menuBar.disableMenuExecute();
            menuBar.disableMenuSaveResults();
            menuBar.disableMenuRemoveConnection();
            menuBar.disableMenuRemoveDataSource();
            menuBar.disableMenuRemoveActivity();
                        
            disableButtonValidate();
            disableButtonResolve();
            disableButtonExecute();
            disableButtonSaveResults();
            disableButtonRemoveConnection();
            disableButtonRemoveDataSource();
            disableButtonRemoveActivity();
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
            
            //Call manager to remove data source being excluded from list "listOfDataSources"
            manager.removeDataSource(dataSourceID);
            
            //If no input concept was already selected and there are no services in the composition
            if((manager.getNumberOfDataSources()+manager.getNumberOfIncludedServices())==0){
                buttonAddActivity.setEnabled(false);
                menuBar.disableMenuAddActivity();
            }//Else, if numberOfDataSources > 0, enable menus "Add Activity"
            else{
                buttonAddActivity.setEnabled(true);
                menuBar.enableMenuAddActivity();
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
            menuBar.clearMenuValidate();
            menuBar.clearMenuResolve();
            menuBar.disableMenuExecute();
            menuBar.disableMenuSaveResults();
            menuBar.disableMenuRemoveConnection();
            menuBar.disableMenuRemoveDataSource();
            menuBar.disableMenuRemoveActivity();
                        
            disableButtonValidate();
            disableButtonResolve();
            disableButtonExecute();
            disableButtonSaveResults();
            disableButtonRemoveConnection();
            disableButtonRemoveDataSource();
            disableButtonRemoveActivity();
            
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
                buttonAddActivity.setEnabled(false);
                menuBar.disableMenuAddActivity();
            }//Else, if numberOfDataSources > 0, enable menus "Add Activity"
            else{
                buttonAddActivity.setEnabled(true);
                menuBar.enableMenuAddActivity();
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
                        
            disableButtonValidate();
            disableButtonResolve();
            disableButtonExecute();
            disableButtonSaveResults();
            disableButtonRemoveConnection();
            disableButtonRemoveDataSource();
            disableButtonRemoveActivity();
            
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
                File serviceDir = manager.getServiceDirectory(serviceInfo.getID());
            
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