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

import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Input;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import jaxbClasses.basictypes.SuggestionValue;
import management.CompositionManager;


public class MainFrame extends JFrame {
    
    private mxGraph graph;
    private mxGraphComponent graphComponent;
    
    private double positionX = 80.0, positionY = 80.0;
    
    private CompositionManager manager;
    
    private ToolBar toolBar;
    
    //Variables for inputValidation
    private Input selectedInput;
    private String selectedServiceID;
    private boolean isOutput;
    private String selectedValue;
    
    HashMap<String,String> analysisNames;
        
    public MainFrame(CompositionManager manager) {
        
        this.manager = manager;
        
        //Call initComponents method
        this.initComponents(manager);
        
        analysisNames = new HashMap<>();

    }
    
    public void setReferenceToToolBar(ToolBar toolBar){
        this.toolBar = toolBar;
    }
    
    private void initComponents(final CompositionManager manager){
        
        //Add listener to JFrame
        this.addWindowListener(new WindowAdapter(){
            
            //When user close frame (on X)
            @Override
            public void windowClosing(WindowEvent e){
            
                //Exclude all created files
                manager.excludeAllFiles();
                
                System.exit(0);
                
            }
        });
        
        //Initialize graph
        graph = new mxGraph(){
            
            @Override
            public String getToolTipForCell(Object cell){
                
                //If element is a vertex
                if(model.isVertex(cell)){
                    
                    //If is the root element
                    if(((mxICell) cell).getId().equals("root")){
                        return null;
                    }//Else, if is a service
                    else if(manager.getServiceInfo(((mxICell) cell).getId()) != null){
                        return manager.getServiceInfoForToolTip(((mxICell) cell).getId());
                    }//Else, if is a data element
                    else{
                        String id = ((mxICell) cell).getId();
                        return manager.getDataSourceInfoForToolTip(id);
                    }
                        
                }//Else, if element is an edge
                else{
                    
                    String result = "";
                    
                    mxICell edge = (mxICell) cell;
                    
                    //If is an edge connecting a data source element to a service
                    if(edge.getId().contains("dataEdge")){
                        
                        //Get source element
                        mxICell source = edge.getTerminal(true);
                        String dataSourceName = manager.getDataSourceName(manager.getDataSourceValue(source.getId()));
                        
                        //Get target element
                        mxICell target = edge.getTerminal(false);
                                                
                        //Get mappings of current edge
                        result = "<html>";
                        HashMap<String,String> map = (HashMap<String,String>) edge.getValue();
                        for(Map.Entry<String, String> entry : map.entrySet()){
                            result = result + "<p>{" + dataSourceName + ",Input " + entry.getKey().split("InputID-")[1] + " of " + target.getValue() +"}</p>";
                        }
                        result = result + "</html>";
                        
                        return result;
                    
                    }//Else if is an edge connecting two services
                    else if(edge.getId().contains("serviceEdge")){
                        
                        //Get source element
                        mxICell source = edge.getTerminal(true);
                                            
                        //Get target element
                        mxICell target = edge.getTerminal(false);
                        
                        //Get mappings of current edge
                        result = "<html>";
                        HashMap<String,String> map = (HashMap<String,String>) edge.getValue();
                        for(Map.Entry<String, String> entry : map.entrySet()){
                
                            result = result + "<p>{Output " + entry.getValue().split("OutputID-")[1] +" of " 
                                    + source.getValue() + ",Input " + entry.getKey().split("InputID-")[1] + " of " + target.getValue() + "} </p>";
                        }
                        result = result + "</html>";
                    
                        return result;
                        
                    }//Else, if is an edge connecting the root to a service
                    else{
                        return null;
                    }
                }
                       
            }
        };
        
        //Make graph cells not editable by user
        graph.setCellsEditable(false);
        
        //Make edges not disconnectable on move
        graph.setDisconnectOnMove(false);
        
        //Make edges not disconnectable (from one vertex to another)
        graph.setCellsDisconnectable(false);
        
        //Prevents user to create edges going to nowhere
        graph.setAllowDanglingEdges(false);
        
        //Initialize graphComponent and set properties
        graphComponent = new mxGraphComponent(graph);
        graphComponent.setConnectable(true); //Enable drag and drop edge creation
        graphComponent.setBorder(BorderFactory.createMatteBorder(20, 20, 20, 20, new Color(160, 160, 160)));
        graphComponent.setPageBackgroundColor(Color.lightGray); //Set color of page behind graphComponent
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.lightGray); //Set color of graphComponent
        graphComponent.setToolTips(true); //Enable tool tips in the graph
        
        //Add graphComponent to frame
        this.add(graphComponent, BorderLayout.CENTER);
        
    }
    
    //Add listener to graphComponent for drag-and-drop edge creation (validation)
    public void addListenerToGraphComponent(){
        
        graphComponent.getConnectionHandler().addListener(mxEvent.CONNECT, new mxEventSource.mxIEventListener(){
                
            @Override
            public void invoke(Object sender, mxEventObject evt) {
                
                //Get edge being created
                mxICell edge = (mxICell)evt.getProperty("cell");
                
                //Get source and target vertices
                mxICell sourceVertex = edge.getTerminal(true);
                mxICell targetVertex = edge.getTerminal(false);
                
                //If targetVertex is a service and the sourceVertex is not the root
                if(manager.getServiceInfo(targetVertex.getId()) != null && !sourceVertex.getId().equals("root")){
                    
                    //Get service info of targetVertex
                    ServiceInfo targetServiceInfo = manager.getServiceInfo(targetVertex.getId());
                    
                    //Get inputs of targetVertex
                    List<Input> inputs = targetServiceInfo.getInput();
                    
                    //Initialize variable selectedInput (to be used in validation)
                    selectedInput = null;
            
                    //If only one input is available, set selectedInput with it
                    if(inputs.size()==1)
                        selectedInput = inputs.get(0);
                    else if(inputs.size()>1){ //Else, if there are more inputs, ask user what input to validate
                
                        HashMap<Input,String> map = new HashMap<>();
                        Object[] possibilities = new Object[inputs.size()];
                        int count = 0;
                        for(Iterator it=inputs.iterator();it.hasNext();){
                            Input input = (Input) it.next();
                            String concept = manager.getLabel(input.getSemanticalType());
                            map.put(input,"Input " + (count+1) + ": " + concept);
                            possibilities[count] = "Input " + (count+1) + ": " + concept;
                            count++;
                        }
                        
                        String selected = (String)JOptionPane.showInputDialog(MainFrame.this, "Please, select an input to validate: ","Select input",JOptionPane.PLAIN_MESSAGE,null,possibilities,"");
                
                        //If some value is selected, set selectedInput with it
                        if(selected != null){
                            if(map.containsValue(selected)){
                                for (Map.Entry<Input, String> entry : map.entrySet()){
                                    if(entry.getValue().equals(selected))
                                        selectedInput = entry.getKey();
                                }
                            }                    
                        }
                    }
                    
                    //If some inputID was selected
                    if(selectedInput != null){
                        
                        //Set selected service ID (to be used in validation)
                        selectedServiceID = targetServiceInfo.getID();
                        
                        //Initialize variable selectedValue (to be used in validation)
                        selectedValue = null;
                        
                        //If sourceVertex is a service
                        if(manager.getServiceInfo(sourceVertex.getId()) != null){
                            
                            //Get service info of sourceVertex
                            ServiceInfo sourceServiceInfo = manager.getServiceInfo(sourceVertex.getId());
                            
                            //Populate "map" only with the suggestion values for selectedInput (targetVertex) that are associated to the sourceVertex
                            HashMap<String,String> map = new HashMap<>();
                            for(Iterator it=selectedInput.getSuggestedValue().iterator();it.hasNext();){
                                SuggestionValue sv = (SuggestionValue) it.next();
                                if(sv.getValueID().contains(sourceServiceInfo.getID()))
                                    map.put(sv.getValueID(),manager.getOutputName(sv.getValueID()));
                            }
                            
                            //If no output of sourceVertex can be used to validate the selectedInput (from targetVertex)
                            if(map.isEmpty()){
                                JOptionPane.showMessageDialog(MainFrame.this, "Input cannot be validated for compatibility reasons!","Warning",JOptionPane.WARNING_MESSAGE);
                            }
                            else{ //Else, if one or more outputs of sourceVertex can be used to validate the selectedInput (from targetVertex)
                            
                                //Add each entry of "map" to vector "possibilities"
                                Object[] possibilities = new Object[map.size()];
                                int i=0;
                                for(Map.Entry<String, String> entry : map.entrySet()){
                                    possibilities[i] = entry.getValue();
                                    i++;
                                }
                                
                                //Ask user what output to use for validation
                                String selectedOutput = (String)JOptionPane.showInputDialog(MainFrame.this, "Please, select a value: ","Validate Input",JOptionPane.PLAIN_MESSAGE,null,possibilities,"");
                                
                                //If some output is selected, set variable "selectedValue"
                                if(selectedOutput != null){
                                    if(map.containsValue(selectedOutput)){
                                        for(Map.Entry<String, String> entry : map.entrySet()){
                                            if(entry.getValue().equals(selectedOutput))
                                                selectedValue = entry.getKey();
                                        }
                                    }
                                }
                            }
                            
                            //Set if is output (to be used in validation)
                            isOutput = true;
                            
                        }//Else, if sourceVertex is a dataElement
                        else{
                            
                            selectedValue = manager.getDataSourceValue(sourceVertex.getId());
                            
                            //Set if is output (to be used in validation)
                            isOutput = false;
                        }
                        
                        //If some value was selected for validation
                        if(selectedValue != null){
                            
                            //Call SwingWorker to validate input on framework
                            new ValidatingInput().execute();
                            
                        }
                    }                    
                }
                
                //Remove recently created edge (in any situation)
                graph.getModel().beginUpdate();
                try {
                    graph.removeCells(new Object[]{edge});
                } finally {
                    graph.getModel().endUpdate();
                }
                graphComponent.setGraph(graph);
                graphComponent.repaint();
                
            }
            
        });
        
    }
    
    //Insert vertex representing init of process
    public void insertInitVertex(){
        
        //Get default parent of graph
        Object parent = graph.getDefaultParent();

        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            
            //Insert new vertex into graph
            Object v1 = graph.insertVertex(parent,"root","Init",20,200,25,25);
            
            //Set vertex properties
            graph.setCellStyles(mxConstants.STYLE_STROKECOLOR, "black", new Object[]{v1}); //Vertex border color
            graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "1.5", new Object[]{v1}); //Vertex border color
            graph.setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE, new Object[]{v1}); //Label font size
            graph.setCellStyles(mxConstants.STYLE_FONTSIZE, "11", new Object[]{v1}); //Label font size
            graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, "black", new Object[]{v1}); //Label color
            graph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM, new Object[]{v1}); //Label position
            graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD, new Object[]{v1}); //Bold label
            
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
    }
    
    //Remove a service vertex from graph (ARRUMANDO AQUI)
    public void removeServiceVertex(String serviceID){
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        //Create list to store all target serviceIds (to be used to update root)
        LinkedList<String> serviceIDs = new LinkedList<>();
            
        try {
            
            //Get vertex of service
            Object vertex = ((mxGraphModel)graph.getModel()).getCell(serviceID);
            
            //Get all outgoing edges of vertex
            Object[] outgoingEdges = graph.getOutgoingEdges(vertex);
            
            //For each outgoing edge of vertex
            for(int i=0;i<outgoingEdges.length;i++){
                
                //Add id of target vertex (always a service) of current edge to list "serviceIDs"
                serviceIDs.add(((mxICell) outgoingEdges[i]).getTerminal(false).getId());
                
            }
            
            //Remove vertex of service from graph and all associated edges
            graph.removeCells(new Object[]{vertex},true);
            
        } finally {
            graph.getModel().endUpdate();
        }
        
        //Call method to update root of all vertices contained in the list "serviceIDs"
        for(Iterator it = serviceIDs.iterator();it.hasNext();)
            this.updateRoot((String) it.next());
        
        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Insert new vertex element into graph
    public void insertVertexElement(String serviceID, String analysisName, boolean isConnector){

        //Get default parent of graph
        Object parent = graph.getDefaultParent();

        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            
            //Set position X and Y
            boolean hasElement;
            do{
                double limitX;
            
                //If frame is not maximized
                if(this.getExtendedState() == JFrame.NORMAL)
                    limitX = 600.0;
                else //Else, if frame is maximized
                    limitX = 1200.0;
                
                hasElement = false;
                for(int i = (int)positionX;i<=positionX+130;i++)
                    for(int j = (int)positionY;j<=positionY+80;j++)
                        if(graphComponent.getCellAt(i,j)!=null)
                            hasElement = true;
            
                if(hasElement){
                    if(positionX > limitX){
                        positionX = 80.0;
                        positionY = positionY + 90.0;  
                    }
                    else
                        positionX = positionX + 150.0;
                }
            }while(hasElement);
            
            if(analysisNames.containsKey(analysisName)){
                int value = Integer.parseInt(analysisNames.get(analysisName));
                value++;
                analysisNames.put(analysisName,String.valueOf(value));
            }
            else
                analysisNames.put(analysisName,"1");
            
            manager.addToMapServToAnalysisName(serviceID,analysisName + " (" + analysisNames.get(analysisName) + ")");
            
            //Insert new vertex into graph
            Object v1 = graph.insertVertex(parent, serviceID, analysisName + " (" + analysisNames.get(analysisName) + ")", positionX, positionY, 130, 80);
            
            //Set vertex properties
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "lightgray", new Object[]{v1}); //Vertex color
            graph.setCellStyles(mxConstants.STYLE_FONTSIZE, "11", new Object[]{v1}); //Label font size
            graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, "black", new Object[]{v1}); //Label font color
            graph.setCellStyles(mxConstants.STYLE_WHITE_SPACE, "wrap", new Object[]{v1}); //Make label wrapped
            graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD, new Object[]{v1}); //Bold label
            
            //If is a connector, change vertex shape
            if(isConnector)
                graph.setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_ELLIPSE, new Object[]{v1});            

        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Remove a data element vertex from graph
    public void removeDataElementVertex(String dataSourceID){
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        //Create list to store all target serviceIds (to be used to update root)
        LinkedList<String> serviceIDs = new LinkedList<>();
            
        try {
            
            //Get vertex of data element
            Object vertex = ((mxGraphModel)graph.getModel()).getCell(dataSourceID);
            
            //Get all outgoing edges of vertex
            Object[] outgoingEdges = graph.getOutgoingEdges(vertex);
            
            //For each outgoing edge of vertex
            for(int i=0;i<outgoingEdges.length;i++){
                
                //Add id of target vertex (always a service) of current edge to list "serviceIDs"
                serviceIDs.add(((mxICell) outgoingEdges[i]).getTerminal(false).getId());
                
            }
            
            //Remove vertex of data element from graph and all associated edges
            graph.removeCells(new Object[]{vertex},true);
            
        } finally {
            graph.getModel().endUpdate();
        }
        
        //Call method to update root of all vertices contained in the list "serviceIDs"
        for(Iterator it = serviceIDs.iterator();it.hasNext();)
            this.updateRoot((String) it.next());
        
        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    public void insertDataElementVertex(String id, String name){
        
        //Get default parent of graph
        Object parent = graph.getDefaultParent();
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            
            double limitX;
            if(this.getExtendedState() == JFrame.NORMAL)//If frame is not maximized
                limitX = 750;
            else //Else, if frame is maximized
                limitX = 1500;
                    
            double position = 130;        
            while(graphComponent.getCellAt((int)position,350)!=null && position<limitX)
                position = position + 60;
            
            if(position>=limitX)
                position = 130;        
            
            //Insert new vertex into graph
            Object dataElement = graph.insertVertex(parent, id, name, position, 350, 40, 50);
                    
            //Set vertex properties
            graph.setCellStyles(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CYLINDER, new Object[]{dataElement}); //Label font size
            graph.setCellStyles(mxConstants.STYLE_FONTSIZE, "11", new Object[]{dataElement}); //Label font size
            graph.setCellStyles(mxConstants.STYLE_FONTCOLOR, "black", new Object[]{dataElement}); //Label color
            graph.setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_BOTTOM, new Object[]{dataElement}); //Label position
            graph.toggleCellStyleFlags(mxConstants.STYLE_FONTSTYLE, mxConstants.FONT_BOLD, new Object[]{dataElement}); //Bold label
            
            
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
            
    }
    
    //Update connection of root vertex to service
    public void updateRoot(String serviceID){
    
        //Get default parent of graph
        Object parent = graph.getDefaultParent();
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            
            //Get vertex of service
            Object vertex = ((mxGraphModel)graph.getModel()).getCell(serviceID);
            
            //Get service inputs
            List<Input> inputs = manager.getServiceInfo(serviceID).getInput();
            
            //Create counts
            int countConnectionsWithExternalData = 0;
            int countConnectionsWithServices = 0;
            
            //For each service input
            for(Iterator it = inputs.iterator();it.hasNext();){
                Input input = (Input) it.next();
                Object[] incomingEdges = graph.getIncomingEdges(vertex);
                for(int i=0;i<incomingEdges.length;i++){
                    mxICell currentEdge = (mxICell) incomingEdges[i];
                    HashMap<String,String> map = (HashMap<String,String>) currentEdge.getValue();
                    for(Map.Entry<String, String> entry : map.entrySet()){
                        //If there is a mapping defined for service input
                        if(entry.getKey().equals(input.getID())){
                            if(currentEdge.getId().contains("dataEdge"))
                                countConnectionsWithExternalData++;
                            else if(currentEdge.getId().contains("serviceEdge"))
                                countConnectionsWithServices++;
                        }
                    }        
                }
            }
            
            //Get root vertex
            Object rootVertex = ((mxGraphModel)graph.getModel()).getCell("root");
            
            //If all inputs of service were validated/resolved
            if(countConnectionsWithExternalData+countConnectionsWithServices == inputs.size()){
                //If the service is not connected to any service
                if(countConnectionsWithExternalData == inputs.size()){
                    
                    //If an edge is not already defined between the root and the service vertex, create it
                    Object[] edgesWithRoot = graph.getEdgesBetween(rootVertex,vertex);
                    if(edgesWithRoot.length == 0){
                    
                        //Generate Unique ID for edge
                        String edgeID;        
                        boolean alreadyExists;
                        do{
                            alreadyExists = false;
                            edgeID = UUID.randomUUID().toString();
                            //If element with edgeID already exists in the graph
                            if(((mxGraphModel)graph.getModel()).getCell(edgeID) != null)
                                alreadyExists = true;
                        }while(alreadyExists);
                    
                        //Insert new edge into graph connecting root vertex to service vertex
                        Object edge = graph.insertEdge(parent,edgeID,new HashMap<String,String>(),rootVertex,vertex);
            
                        //Make edge label invisible
                        graph.setCellStyles(mxConstants.STYLE_NOLABEL, "1", new Object[]{edge});
                    
                    }
                    
                }//Else, if the service is connected to any service, remove edge between root and service vertex (if it exists)
                else{
                    Object[] edgesToRemove = graph.getEdgesBetween(rootVertex,vertex);
                    if(edgesToRemove != null)
                        graph.removeCells(edgesToRemove);
                }
            } //Else, if not all inputs of service were validated/resolved, remove edge between root service vertices (if it exists)
            else{
                
                Object[] edgesToRemove = graph.getEdgesBetween(rootVertex,vertex);
                if(edgesToRemove != null)
                    graph.removeCells(edgesToRemove);
            }
            
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
    }
        
    //Remove edge from graph
    public void removeEdgeElement(String edgeID,String inputID){
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        //Find edge with edgeID
        mxICell edge = (mxICell)(((mxGraphModel)graph.getModel()).getCell(edgeID));
        
        //Get target vertex (service) associated to this edge
        mxICell targetVertex = edge.getTerminal(false);
        
        try {
            
            //Find mapping of edge associated to inputID and remove it
            HashMap<String,String> map = (HashMap<String,String>) edge.getValue();
            HashMap<String,String> changedMap = new HashMap<>();
            for(Map.Entry<String, String> entry : map.entrySet()){
                if(!entry.getKey().equals(inputID)){
                    changedMap.put(entry.getKey(),entry.getValue());
                }
            }
            edge.setValue(changedMap);
            
            //If mapping is empty, remove edge
            if(changedMap.isEmpty())
                graph.removeCells(new Object[]{edge});   

        } finally {
            graph.getModel().endUpdate();
        }
        
        //Call method to update root of target vertex
        this.updateRoot(targetVertex.getId());

        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Insert new edge element into graph
    public void insertEdgeElement(String inputID,String value,boolean isDataElement){
        
        //Get default parent of graph
        Object parent = graph.getDefaultParent();
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        //Find target cell (whose input is being validated)
        String targetID = inputID.split("-InputID")[0];
        Object targetCell = ((mxGraphModel)graph.getModel()).getCell(targetID);
            
        try {
        
            //Find source cell (service or data element)
            Object sourceCell;
            if(!isDataElement)
                sourceCell = ((mxGraphModel)graph.getModel()).getCell(value.split("-OutputID")[0]);
            else
                sourceCell = ((mxGraphModel)graph.getModel()).getCell(manager.getDataSourceID(value));
        
            //Get all incoming edges of targetCell (input being validated)
            Object[] incomingEdges = graph.getIncomingEdges(targetCell);
            String key = "";
            HashMap<String,String> mapToChange = new HashMap<>();
            Object edgeToVerify = null;
            //For each incoming edge 
            for(int i=0;i<incomingEdges.length;i++){
            
                //Get current edge
                mxICell currentEdge = (mxICell) incomingEdges[i];
            
                //Get mappings of current edge
                HashMap<String,String> map = (HashMap<String,String>) currentEdge.getValue();
                for(Map.Entry<String, String> entry : map.entrySet()){
                
                    //If there is a mapping between the input being validated and other element, set key
                    if(entry.getKey().equals(inputID)){
                        key = entry.getKey();
                        mapToChange = map;
                        edgeToVerify = incomingEdges[i];
                    }
                }
            }
            
            //If some mapping was found
            if(!key.equals("")){
                
                //Remove mapping
                mapToChange.remove(key);
                
                //If mapping is empty
                if(mapToChange.isEmpty()){
                    
                    //Remove edge
                    graph.removeCells(new Object[]{edgeToVerify});
                    
                } //Else, if mapping is not empty, only update it
                else 
                    ((mxICell) edgeToVerify).setValue(mapToChange);
            }
            
        
            //Get all incoming edges of targetCell (input being validated) (again cause it can be previously updated)
            boolean exists =false;
            incomingEdges = graph.getIncomingEdges(targetCell);
                
            //For each incoming edge
            for(int i=0;i<incomingEdges.length;i++){
                
                //Get current edge
                mxICell currentEdge = (mxICell) incomingEdges[i];
                
                //If there is an edge already defined between the sourceCell and the targetCell
                if(currentEdge.getTerminal(true).equals(sourceCell)){
                    
                    //Set exists to true
                    exists = true;
                    
                    //Only update mapping of this edge
                    HashMap<String,String> currentMap = (HashMap<String,String>) currentEdge.getValue();
                    currentMap.put(inputID,value);
                    currentEdge.setValue(currentMap);                    
                    
                }
            }
            
            //If there was no edge already defined between the sourceCell and the targetCell, create it
            if(!exists){
            
                //Generate Unique ID for edge
                String edgeID;        
                boolean alreadyExists;
                do{
                    alreadyExists = false;
                    
                    if(!isDataElement)
                        edgeID = "serviceEdge" + UUID.randomUUID().toString();
                    else
                        edgeID = "dataEdge" + UUID.randomUUID().toString();
                    
                    //If element with edgeID already exists in the graph
                    if(((mxGraphModel)graph.getModel()).getCell(edgeID) != null)
                        alreadyExists = true;
            
                }while(alreadyExists);
            
                //Create hashmap to store <InputID,value> (in this order because 1 input can be only connected to 1 output)
                //Contrarily, 1 output can be connected to different inputs
                HashMap<String,String> map = new HashMap<>();
                map.put(inputID,value);
            
                //Insert new edge into graph
                Object edge = graph.insertEdge(parent,edgeID,map,sourceCell,targetCell);
            
                //Make edge label invisible
                graph.setCellStyles(mxConstants.STYLE_NOLABEL, "1", new Object[]{edge});
            
            }

        } finally {
            graph.getModel().endUpdate();
        }

        //Call method to update root
        this.updateRoot(targetID);
        
        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Change color of vertex specified by service
    public void changeVertexColor(ServiceInfo service, String color){
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            
            //Change vertex color. Possible colors: lightgreen, lightgray
            Object cell = ((mxGraphModel)graph.getModel()).getCell(service.getID());
            graph.setCellStyles(mxConstants.STYLE_FILLCOLOR,color, new Object[]{cell});
            
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Update color of vertex represented by serviceID and all subsequent vertices (children)
    public void updateVerticesColorToLightGray(String serviceID){
        
        LinkedList<String> list = new LinkedList<>();
        
        //Find all children of the vertex represented by serviceID (includes the vertex itself)
        findChildren(list,serviceID);
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            
            //Change color of each child vertex to gray
            for(int i=0;i<list.size();i++){
                Object cell = ((mxGraphModel)graph.getModel()).getCell(list.get(i));
                graph.setCellStyles(mxConstants.STYLE_FILLCOLOR, "lightgray", new Object[]{cell});
            }
        
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Return all children of vertex represented by serviceID (includes the vertex itself)
    public LinkedList<String> findAllChildren(String serviceID){
        
        LinkedList<String> list = new LinkedList<>();
        
        //Call method findChildren
        findChildren(list,serviceID);
        
        return list;
    }
    
    //Recursively find all children of vertex represented by serviceID
    private void findChildren(LinkedList<String> list,String serviceID){
        
        //If list do not contains serviceID, i.e., vertex was not visited yet
        if(!list.contains(serviceID)){
            
            //Add vertex to list
            list.add(serviceID);
            
            //Get vertex
            Object cell = ((mxGraphModel)graph.getModel()).getCell(serviceID);
            
            //Get all outgoing edges
            Object[] outgoingEdges = graph.getOutgoingEdges(cell);
            
            //For each outgoing edge of vertex
            for(int i=0;i<outgoingEdges.length;i++){
                
                //Get current edge
                mxICell currentEdge = (mxICell) outgoingEdges[i];
                
                //Get target cell of current edge
                mxICell currentChild = currentEdge.getTerminal(false);
                
                //Call findChildren
                findChildren(list,currentChild.getId());
                
            }   
        }
        
    }
    
    public void addMouseListener(final CompositionManager manager){
        
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
        
            @Override
            public void mousePressed(MouseEvent e) {
                
                //If user right-click, call mouseReleased action
                if(e.isPopupTrigger()){
                    
                    //If there is no cell at mouse position, clear selection
                    if(graphComponent.getCellAt(e.getX(), e.getY()) == null){
                        
                        //Begin update of graph model
                        graph.getModel().beginUpdate();
        
                        try {
                            graph.clearSelection();
                        } finally {
                            graph.getModel().endUpdate();
                        }

                        graphComponent.setGraph(graph);
                        
                    }
                    
                    //If some element is selected
                    if(graph.getSelectionCount()>0){
                        
                        //If only one element is selected
                        if(graph.getSelectionCount() == 1){
                            
                            //Get selected graph cell
                            Object obj = graph.getSelectionCell();
                            mxICell cell = (mxICell) obj;
                            
                            //If selected element is a vertex, is not the initial vertex and is a service vertex
                            if(cell.isVertex() && !cell.getId().equals("root") && manager.getServiceInfo(((mxICell) cell).getId()) != null){
                                
                                //Disable menus "Remove Connection" and "Remove DataSource"
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                                toolBar.disableButtonRemoveConnection();
                                toolBar.disableButtonRemoveDataSource();
                                
                                //Get service information of selected graph cell
                                ServiceInfo serviceInfo = manager.getIncludedService(cell.getId());
                                
                                //Get all outgoing edges of service vertex and create map to store all needed values for activity removal
                                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                                HashMap<ServiceInfo,LinkedList<String>> map = new HashMap<>();
                                for(int i=0;i<outgoingEdges.length;i++){
                                    
                                    //Get current edge and associated mappings
                                    mxICell currentEdge = (mxICell) outgoingEdges[i];
                                    HashMap<String,String> edgeMappings = (HashMap<String,String>) currentEdge.getValue();
                                    
                                    //Add each inputID of edgeMappings to listOfInputs
                                    LinkedList<String> listOfInputs = new LinkedList<>();
                                    for(Map.Entry<String, String> entry : edgeMappings.entrySet())
                                        listOfInputs.add(entry.getKey());
                                    
                                    //Get target vertex (always a service) and associated serviceInfo
                                    mxICell targetVertex = currentEdge.getTerminal(false);
                                    ServiceInfo servInfo = manager.getServiceInfo(targetVertex.getId());
                                    
                                    map.put(servInfo,listOfInputs);
                                    
                                }
                                
                                //Add options to menus "Validate/Resolve Inputs" and enable them
                                ((MenuBar) MainFrame.this.getJMenuBar()).addOptionsToMenuValidate(serviceInfo);
                                ((MenuBar) MainFrame.this.getJMenuBar()).addOptionsToMenuResolve(serviceInfo);
                                ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveActivity(serviceInfo,map);
                                
                                //Set serviceInfo of tool bar and enable buttonValidate
                                toolBar.setServiceInfo(serviceInfo);
                                toolBar.enableButtonValidate();
                                toolBar.enableButtonResolve();
                                toolBar.setInfoForActivityRemoval(serviceInfo,map);
                                toolBar.enableButtonRemoveActivity();
                                
                                //If selected service is ready for execution, enable menu "Execute"
                                if(serviceInfo.isReadyForExec()){
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuExecute(serviceInfo);
                                    toolBar.enableButtonExecute();
                                }
                                else{ //Else, disable menu "Execute"
                                    ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                    toolBar.disableButtonExecute();
                                }
                                
                                //If directory associated to selected service is empty, disable menu "Save results"
                                if(manager.serviceDirectoryIsEmpty(serviceInfo.getID())){
                                    ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                    toolBar.disableButtonSaveResults();
                                }//Else, enable menu "Save results"
                                else{
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuSaveResults(serviceInfo);
                                    toolBar.enableButtonSaveResults();
                                }
                                
                            }//Else, if selected element is an edge and is not connecting the root vertex to some element
                            else if(cell.isEdge() && !cell.getTerminal(true).getId().equals("root")){
                                
                                //Clear options of menus "Validate/Resolve Inputs,Execute" and disable them
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                                toolBar.disableButtonValidate();
                                toolBar.disableButtonResolve();
                                toolBar.disableButtonExecute();
                                toolBar.disableButtonSaveResults();
                                toolBar.disableButtonRemoveDataSource();
                                toolBar.disableButtonRemoveActivity();
                                
                                //Set edge info and enable menu "Remove Connection"
                                mxICell source = cell.getTerminal(true);
                                mxICell target = cell.getTerminal(false);
                                if(manager.getServiceInfo(source.getId()) != null){
                                    toolBar.setEdgeInfo(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),false);
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveConnection(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),false);
                                }
                                else{
                                    toolBar.setEdgeInfo(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),true);
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveConnection(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),true);
                                }
                                toolBar.enableButtonRemoveConnection();                                
                                
                            }//Else, if selected element is a data source
                            else if(cell.isVertex() && !cell.getId().equals("root") && manager.getServiceInfo(((mxICell) cell).getId()) == null){
                                
                                //Clear options of menus "Validate/Resolve Inputs,Execute,RemoveConnection" and disable them
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                                toolBar.disableButtonValidate();
                                toolBar.disableButtonResolve();
                                toolBar.disableButtonExecute();
                                toolBar.disableButtonSaveResults();
                                toolBar.disableButtonRemoveConnection();
                                toolBar.disableButtonRemoveActivity();
                                
                                //Get all outgoing edges of dataSource vertex
                                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                                
                                //Create map to store all needed values for dataSource removal
                                HashMap<ServiceInfo,LinkedList<String>> map = new HashMap<>();
                                
                                //For each outgoing edge of dataSource vertex
                                for(int i=0;i<outgoingEdges.length;i++){
                                    
                                    //Get current edge and associated mappings
                                    mxICell currentEdge = (mxICell) outgoingEdges[i];
                                    HashMap<String,String> edgeMappings = (HashMap<String,String>) currentEdge.getValue();
                                    
                                    //Add each inputID of edgeMappings to listOfInputs
                                    LinkedList<String> listOfInputs = new LinkedList<>();
                                    for(Map.Entry<String, String> entry : edgeMappings.entrySet())
                                        listOfInputs.add(entry.getKey());
                                    
                                    //Get target vertex (always a service) and associated serviceInfo
                                    mxICell targetVertex = currentEdge.getTerminal(false);
                                    ServiceInfo servInfo = manager.getServiceInfo(targetVertex.getId());
                                    
                                    map.put(servInfo,listOfInputs);
                                    
                                }
                                
                                //Set parameters and enable menus "Remove data source"
                                toolBar.setDataSourceInfo(cell.getId(),map);
                                toolBar.enableButtonRemoveDataSource();
                                ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveDataSource(cell.getId(),map);
                                
                            }//Else
                            else{
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                                
                                toolBar.disableButtonValidate();
                                toolBar.disableButtonResolve();
                                toolBar.disableButtonExecute();
                                toolBar.disableButtonSaveResults();
                                toolBar.disableButtonRemoveConnection();
                                toolBar.disableButtonRemoveDataSource();
                                toolBar.disableButtonRemoveActivity();
                            }
                        }//Else, if more than one element is selected, clear options of menus "Validate/Resolve Inputs,Execute" and disable them
                        else{
                            ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                            ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                            
                            toolBar.disableButtonValidate();
                            toolBar.disableButtonResolve();
                            toolBar.disableButtonExecute();
                            toolBar.disableButtonSaveResults();
                            toolBar.disableButtonRemoveConnection();
                            toolBar.disableButtonRemoveDataSource();
                            toolBar.disableButtonRemoveActivity();
                        }
                    } //Else, if none element is selected, clear options of menus "Validate/Resolve Inputs,Execute" and disable them
                    else{
                        
                        ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                        ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                        
                        toolBar.disableButtonValidate();
                        toolBar.disableButtonResolve();
                        toolBar.disableButtonExecute();
                        toolBar.disableButtonSaveResults();
                        toolBar.disableButtonRemoveConnection();
                        toolBar.disableButtonRemoveDataSource();
                        toolBar.disableButtonRemoveActivity();
                    }
                    
                    //Call mouseReleased for popup items
                    this.mouseReleased(e);
                }
                else{ //Else, if user left-click
                    
                    //If there is no cell at mouse position, clear selection
                    if(graphComponent.getCellAt(e.getX(), e.getY()) == null){
                        
                        //Begin update of graph model
                        graph.getModel().beginUpdate();
        
                        try {
                            graph.clearSelection();
                        } finally {
                            graph.getModel().endUpdate();
                        }

                        graphComponent.setGraph(graph);
                    }
                    
                    //If some element is selected
                    if(graph.getSelectionCount()>0){
                        
                        //If only one element is selected
                        if(graph.getSelectionCount() == 1){
                            
                            //Get selected graph cell
                            Object obj = graph.getSelectionCell();
                            mxICell cell = (mxICell) obj;
                            
                            //If selected element is a vertex, is not the initial vertex and is a service vertex
                            if(cell.isVertex() && !cell.getId().equals("root") && manager.getServiceInfo(((mxICell) cell).getId()) != null){
                                
                                //Disable menus "Remove Connection" and "Remove Data Source"
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                                toolBar.disableButtonRemoveConnection();
                                toolBar.disableButtonRemoveDataSource();
                                
                                //Get service information of selected graph cell
                                ServiceInfo serviceInfo = manager.getIncludedService(cell.getId());
                                
                                //Get all outgoing edges of service vertex and create map to store all needed values for activity removal
                                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                                HashMap<ServiceInfo,LinkedList<String>> map = new HashMap<>();
                                for(int i=0;i<outgoingEdges.length;i++){
                                    
                                    //Get current edge and associated mappings
                                    mxICell currentEdge = (mxICell) outgoingEdges[i];
                                    HashMap<String,String> edgeMappings = (HashMap<String,String>) currentEdge.getValue();
                                    
                                    //Add each inputID of edgeMappings to listOfInputs
                                    LinkedList<String> listOfInputs = new LinkedList<>();
                                    for(Map.Entry<String, String> entry : edgeMappings.entrySet())
                                        listOfInputs.add(entry.getKey());
                                    
                                    //Get target vertex (always a service) and associated serviceInfo
                                    mxICell targetVertex = currentEdge.getTerminal(false);
                                    ServiceInfo servInfo = manager.getServiceInfo(targetVertex.getId());
                                    
                                    map.put(servInfo,listOfInputs);
                                    
                                }
                                
                                //Add options to menus "Validate/Resolve Inputs" and enable them
                                ((MenuBar) MainFrame.this.getJMenuBar()).addOptionsToMenuValidate(serviceInfo);
                                ((MenuBar) MainFrame.this.getJMenuBar()).addOptionsToMenuResolve(serviceInfo);
                                ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveActivity(serviceInfo,map);
                                
                                //Set serviceInfo of tool bar and enable buttonValidate
                                toolBar.setServiceInfo(serviceInfo);
                                toolBar.enableButtonValidate();
                                toolBar.enableButtonResolve();
                                toolBar.setInfoForActivityRemoval(serviceInfo,map);
                                toolBar.enableButtonRemoveActivity();
                                
                                //If selected service is ready for execution, enable menu "Execute"
                                if(serviceInfo.isReadyForExec()){
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuExecute(serviceInfo);
                                    toolBar.enableButtonExecute();
                                }
                                else{ //Else, disable menu "Execute"
                                    ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                    toolBar.disableButtonExecute();
                                }
                                
                                //If directory associated to selected service is empty, disable menu "Save results"
                                if(manager.serviceDirectoryIsEmpty(serviceInfo.getID())){
                                    ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                    toolBar.disableButtonSaveResults();
                                }//Else, enable menu "Save results"
                                else{
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuSaveResults(serviceInfo);
                                    toolBar.enableButtonSaveResults();
                                }
                                
                            }//Else, if selected element is an edge and is not connecting the root vertex to some element
                            else if(cell.isEdge() && !cell.getTerminal(true).getId().equals("root")){
                                
                                //Clear options of menus "Validate/Resolve Inputs,Execute,RemoveDataSource" and disable them
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                                toolBar.disableButtonValidate();
                                toolBar.disableButtonResolve();
                                toolBar.disableButtonExecute();
                                toolBar.disableButtonSaveResults();
                                toolBar.disableButtonRemoveDataSource();
                                toolBar.disableButtonRemoveActivity();
                                
                                //Set edge info and enable menu "Remove Connection"
                                mxICell source = cell.getTerminal(true);
                                mxICell target = cell.getTerminal(false);
                                if(manager.getServiceInfo(source.getId()) != null){
                                    toolBar.setEdgeInfo(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),false);
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveConnection(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),false);
                                }
                                else{
                                    toolBar.setEdgeInfo(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),true);
                                    ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveConnection(cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),true);
                                }
                                toolBar.enableButtonRemoveConnection();                                
                                
                            }//Else, if selected element is a data source
                            else if(cell.isVertex() && !cell.getId().equals("root") && manager.getServiceInfo(((mxICell) cell).getId()) == null){
                                
                                //Clear options of menus "Validate/Resolve Inputs,Execute,RemoveConnection" and disable them
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                                toolBar.disableButtonValidate();
                                toolBar.disableButtonResolve();
                                toolBar.disableButtonExecute();
                                toolBar.disableButtonSaveResults();
                                toolBar.disableButtonRemoveConnection();
                                toolBar.disableButtonRemoveActivity();
                                
                                //Get all outgoing edges of dataSource vertex
                                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                                
                                //Create map to store all needed values for dataSource removal
                                HashMap<ServiceInfo,LinkedList<String>> map = new HashMap<>();
                                
                                //For each outgoing edge of dataSource vertex
                                for(int i=0;i<outgoingEdges.length;i++){
                                    
                                    //Get current edge and associated mappings
                                    mxICell currentEdge = (mxICell) outgoingEdges[i];
                                    HashMap<String,String> edgeMappings = (HashMap<String,String>) currentEdge.getValue();
                                    
                                    //Add each inputID of edgeMappings to listOfInputs
                                    LinkedList<String> listOfInputs = new LinkedList<>();
                                    for(Map.Entry<String, String> entry : edgeMappings.entrySet())
                                        listOfInputs.add(entry.getKey());
                                    
                                    //Get target vertex (always a service) and associated serviceInfo
                                    mxICell targetVertex = currentEdge.getTerminal(false);
                                    ServiceInfo servInfo = manager.getServiceInfo(targetVertex.getId());
                                    
                                    map.put(servInfo,listOfInputs);
                                    
                                }
                                
                                //Set parameters and enable menus "Remove data source"
                                toolBar.setDataSourceInfo(cell.getId(),map);
                                toolBar.enableButtonRemoveDataSource();
                                ((MenuBar) MainFrame.this.getJMenuBar()).enableMenuRemoveDataSource(cell.getId(),map);
                                
                            }//Else
                            else{
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                                ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                                ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                                
                                toolBar.disableButtonValidate();
                                toolBar.disableButtonResolve();
                                toolBar.disableButtonExecute();
                                toolBar.disableButtonSaveResults();
                                toolBar.disableButtonRemoveConnection();
                                toolBar.disableButtonRemoveDataSource();
                                toolBar.disableButtonRemoveActivity();
                            }
                        }//Else, if more than one element is selected, clear options of menus "Validate/Resolve Inputs,Execute" and disable them
                        else{
                            ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                            ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                            ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                            
                            toolBar.disableButtonValidate();
                            toolBar.disableButtonResolve();
                            toolBar.disableButtonExecute();
                            toolBar.disableButtonSaveResults();
                            toolBar.disableButtonRemoveConnection();
                            toolBar.disableButtonRemoveDataSource();
                            toolBar.disableButtonRemoveActivity();
                        }
                    } //Else, if none element is selected, clear options of menus "Validate/Resolve Inputs,Execute" and disable them
                    else{
                        
                        ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
                        ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
                        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                        
                        toolBar.disableButtonValidate();
                        toolBar.disableButtonResolve();
                        toolBar.disableButtonExecute();
                        toolBar.disableButtonSaveResults();
                        toolBar.disableButtonRemoveConnection();
                        toolBar.disableButtonRemoveDataSource();
                        toolBar.disableButtonRemoveActivity();
                        
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                
                //If user right-click
                if (e.isPopupTrigger()) {
                    
                    //Get cell at mouse position
                    Object obj = graphComponent.getCellAt(e.getX(), e.getY());
                    
                    //If user right-click in a graph element
                    if(obj != null){
                        
                        //Get selected graph cell
                        mxICell cell = (mxICell) obj;
                        
                        //If selected element is a vertex and is not the initial vertex
                        if(cell.isVertex() && !cell.getId().equals("root")){
                            
                            //If selected element is a vertex representing a service
                            if(manager.getServiceInfo(((mxICell) cell).getId()) != null){
                            
                                //Get service information of selected graph cell
                                ServiceInfo serviceInfo = manager.getIncludedService(cell.getId());
                                
                                //Get all outgoing edges of service vertex and create map to store all needed values for activity removal
                                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                                HashMap<ServiceInfo,LinkedList<String>> map = new HashMap<>();
                                for(int i=0;i<outgoingEdges.length;i++){
                                    
                                    //Get current edge and associated mappings
                                    mxICell currentEdge = (mxICell) outgoingEdges[i];
                                    HashMap<String,String> edgeMappings = (HashMap<String,String>) currentEdge.getValue();
                                    
                                    //Add each inputID of edgeMappings to listOfInputs
                                    LinkedList<String> listOfInputs = new LinkedList<>();
                                    for(Map.Entry<String, String> entry : edgeMappings.entrySet())
                                        listOfInputs.add(entry.getKey());
                                    
                                    //Get target vertex (always a service) and associated serviceInfo
                                    mxICell targetVertex = currentEdge.getTerminal(false);
                                    ServiceInfo servInfo = manager.getServiceInfo(targetVertex.getId());
                                    
                                    map.put(servInfo,listOfInputs);
                                    
                                }
                                
                                //Call PopupMenuCell
                                PopupMenuCell popupMenu = new PopupMenuCell(MainFrame.this,manager,((MenuBar) MainFrame.this.getJMenuBar()),toolBar,serviceInfo,map);
                                popupMenu.show(graphComponent, e.getX(), e.getY());

                                e.consume();
                            } //Else, if selected element is a vertex representing a data element
                            else{
                                
                                //Get all outgoing edges of dataSource vertex
                                Object[] outgoingEdges = graph.getOutgoingEdges(cell);
                                
                                //Create map to store all needed values for dataSource removal
                                HashMap<ServiceInfo,LinkedList<String>> map = new HashMap<>();
                                
                                //For each outgoing edge of dataSource vertex
                                for(int i=0;i<outgoingEdges.length;i++){
                                    
                                    //Get current edge and associated mappings
                                    mxICell currentEdge = (mxICell) outgoingEdges[i];
                                    HashMap<String,String> edgeMappings = (HashMap<String,String>) currentEdge.getValue();
                                    
                                    //Add each inputID of edgeMappings to listOfInputs
                                    LinkedList<String> listOfInputs = new LinkedList<>();
                                    for(Map.Entry<String, String> entry : edgeMappings.entrySet())
                                        listOfInputs.add(entry.getKey());
                                    
                                    //Get target vertex (always a service) and associated serviceInfo
                                    mxICell targetVertex = currentEdge.getTerminal(false);
                                    ServiceInfo servInfo = manager.getServiceInfo(targetVertex.getId());
                                    
                                    map.put(servInfo,listOfInputs);
                                    
                                }
                                
                                PopupMenuDataSource popupMenu = new PopupMenuDataSource(MainFrame.this,((MenuBar) MainFrame.this.getJMenuBar()),toolBar,manager,cell.getId(),map);
                                popupMenu.show(graphComponent, e.getX(), e.getY());
                                e.consume();
                                
                            }
                        }//Else, if selected element is an edge and is not connecting the root vertex to some element
                        else if(cell.isEdge() && !cell.getTerminal(true).getId().equals("root")){
                            
                            PopupMenuEdge popupMenu;
                            
                            //Set edge info and call PopupMenuEdge
                            mxICell source = cell.getTerminal(true);
                            mxICell target = cell.getTerminal(false);
                            
                            if(manager.getServiceInfo(source.getId()) != null)
                                popupMenu = new PopupMenuEdge(MainFrame.this,((MenuBar) MainFrame.this.getJMenuBar()),toolBar,manager,cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),false);
                            else
                                popupMenu = new PopupMenuEdge(MainFrame.this,((MenuBar) MainFrame.this.getJMenuBar()),toolBar,manager,cell.getId(),(HashMap<String,String>)cell.getValue(),source.getId(),target.getId(),true);
                                    
                            popupMenu.show(graphComponent, e.getX(), e.getY());
                            e.consume();
                        }
                        
                    }
                }
            }
        });
        
    }
    
    //Clean graph selection and disable all associated menus
    public void clearGraphSelection(){
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
            graph.clearSelection();
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        
        
        //Disable all menus 
        ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuValidate();
        ((MenuBar) MainFrame.this.getJMenuBar()).clearMenuResolve();
        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuExecute();
        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuSaveResults();
        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveConnection();
        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveDataSource();
        ((MenuBar) MainFrame.this.getJMenuBar()).disableMenuRemoveActivity();
                        
        toolBar.disableButtonValidate();
        toolBar.disableButtonResolve();
        toolBar.disableButtonExecute();
        toolBar.disableButtonSaveResults();
        toolBar.disableButtonRemoveConnection();
        toolBar.disableButtonRemoveDataSource();
        toolBar.disableButtonRemoveActivity();
        
    }
    
    //Set background color of graphComponent
    public void setGraphComponentColor(Color color){
        
        graphComponent.getViewport().setBackground(color);
        
    }
    
    //Clean all elements of graph and initialize all variables frame
    public void cleanAll(){
        
        analysisNames = new HashMap<>();
        
        positionX = 80.0;
        positionY = 80.0;
        
        //Begin update of graph model
        graph.getModel().beginUpdate();
        
        try {
        
            graph.selectAll();
            graph.removeCells();
            
        } finally {
            graph.getModel().endUpdate();
        }

        graphComponent.setGraph(graph);
        graphComponent.repaint();
        
    }
    
    //Internal class for loading input concepts from ontology
    class ValidatingInput extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
                    
            //Disable jFrame and all of its components
            MainFrame.this.setEnabled(false);
                    
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(MainFrame.this,"Validate Input","   Validating input   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                    
            //Center inside the application frame
            int x = MainFrame.this.getX() + (MainFrame.this.getWidth() - loadingDialog.getWidth()) / 2;
            int y = MainFrame.this.getY() + (MainFrame.this.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
                    
            //Set loadingDialog visible
            loadingDialog.setVisible(true);
            
            //Call manager to validate input
            if(manager.serviceValidation(selectedServiceID, selectedInput.getID(), selectedValue, isOutput)){
                      
                //Insert edge (only if edge do not already exist)
                MainFrame.this.insertEdgeElement(selectedInput.getID(),selectedValue,!isOutput);
                        
                //Call manager to AddToContext "null" to all outputs of service being validated
                LinkedList<String> serviceIDs = MainFrame.this.findAllChildren(selectedServiceID);
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
                MainFrame.this.updateVerticesColorToLightGray(selectedServiceID);

                //Enable jFrame and all of its components
                MainFrame.this.setEnabled(true);
                        
                //Make loadingDialog invisible
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
                        
                JOptionPane.showMessageDialog(MainFrame.this,"Input Validated!","Message",JOptionPane.INFORMATION_MESSAGE);
                        
            }
            else{
                
                //Enable jFrame and all of its components
                MainFrame.this.setEnabled(true);
                        
                //Make loadingDialog invisible
                loadingDialog.setVisible(false);
                loadingDialog.dispose();
                        
                JOptionPane.showMessageDialog(MainFrame.this, "Error to Validate Input!","Error",JOptionPane.ERROR_MESSAGE);

            }  
        
            return null;
        }

        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            MainFrame.this.clearGraphSelection();
        
        }
    }
    
}





