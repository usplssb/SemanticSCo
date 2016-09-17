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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Input;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import management.CompositionManager;

public class ResolveInputDialog extends JDialog {
    
    private HashMap<String,String> map;
    private String selectedValue;
    private JButton okButton;
    private JButton okServiceButton;
    private boolean isSelected = false;
    private boolean isOutput;
    private String serviceID;
    private String inputID;
    
    private MainFrame jFrame;
    private CompositionManager manager;
    
    
    
    public ResolveInputDialog(final MainFrame jFrame,final CompositionManager manager,final String serviceID, final String inputID, Vector<String> suggestions, final HashMap<String,ServiceInfo> mapOfServices){
        
        super(jFrame);
        
        this.jFrame = jFrame;
        this.manager = manager;               
        
        this.serviceID = serviceID;
        this.inputID = inputID;
        
        //Set properties of JDialog "ResolveInputDialog"
        this.setResizable(false); //Make dialog not resizable by user
        this.setSize(745,290); //Set size
        this.setTitle("Resolve Input"); //Set title
        this.setLayout(new BorderLayout()); //Set layout
        
        map = new HashMap<>();
        for(Iterator it=suggestions.iterator();it.hasNext();){
        
            String outputID = (String) it.next();
            
            String name;
            if(outputID.contains("-OutputID-"))
                name = manager.getOutputName(outputID);
            else
                name = manager.getDataSourceName(outputID);
            
            if(name != null)
                map.put(outputID,name);
            
        }
        
        Vector<String> values = new Vector<>();
        for(Map.Entry<String, String> entry : map.entrySet())
            values.add(entry.getValue());
        
        //Create JComboBox
        JComboBox comboBox = new JComboBox(values);
        comboBox.setPreferredSize(new Dimension(490,25));
        
        //Add action listener to JComboBox
        comboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                String selectedName = (String)((JComboBox)e.getSource()).getSelectedItem();
                
                //Set selected outputID
                for(Map.Entry<String, String> entry : map.entrySet())
                    if(entry.getValue().equals(selectedName)){
                        selectedValue = entry.getKey();
                        isOutput = selectedValue.contains("-OutputID-");
                    }
                    
                okButton.setEnabled(true);
                
            }
        });
        
        //Create JPanel and add JComboBox to it
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        
        jPanel.add(new JLabel("Please, select a value: "),BorderLayout.WEST);
        
        JPanel jPanelComboBox = new JPanel();
        jPanelComboBox.add(comboBox);
        
        //Create OK button and add ActionListener to it
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                                
                //Call SwingWorker to validate input with selected value
                new ResolvingInput().execute();
                
            }
        });
        okButton.setEnabled(false);
        
        jPanelComboBox.add(okButton);
        
        jPanel.setBorder(BorderFactory.createEmptyBorder(20,10,3,10)); 
        
        jPanel.add(jPanelComboBox,BorderLayout.EAST);
        
        //Create other jPanel
        JPanel jPanel2 = new JPanel();
        jPanel2.setLayout(new BoxLayout(jPanel2,BoxLayout.PAGE_AXIS));
        jPanel2.setBorder(BorderFactory.createEmptyBorder(20,10,3,10));
        
        JLabel label = new JLabel("Or select a service to add: ");
        label.setAlignmentX(0);
        jPanel2.add(label);
        
        //Create DefaultListModel "listModel"
        DefaultListModel listModel = new DefaultListModel();
        
        //Add elements to "listModel"
        boolean connExists = false;
        boolean serviceExists = false;
        for(Map.Entry<String,ServiceInfo> entry : mapOfServices.entrySet()){
            
            ServiceInfo info = (ServiceInfo) entry.getValue();
            
            if(info.getFunction().isEmpty()){
                listModel.add(0,entry);       
                connExists = true;
            }
            else
                serviceExists = true;
        }
        if(connExists && serviceExists){
            ServiceInfo info = new ServiceInfo();
            info.setID("null");
            info.setName("----------------------------------------------------------------------------------------");
            mapOfServices.put("null",info);
            for(Map.Entry<String,ServiceInfo> entry : mapOfServices.entrySet()){
                if(entry.getKey().equals("null"))
                    listModel.add(0,entry);
            }
        }
        for(Map.Entry<String,ServiceInfo> entry : mapOfServices.entrySet()){
            ServiceInfo info = (ServiceInfo) entry.getValue();
            if(!info.getFunction().isEmpty())
                listModel.add(0,entry);            
        }
        
        //Create JList "list", set its properties and add "listModel" to JList
        final JList list = new JList(listModel){
        
            @Override
            public String getToolTipText(MouseEvent event){  
        
                //Get the mouse location  
                Point point = event.getPoint();  
       
                //Get the item in the list box at the mouse location  
                int index = this.locationToIndex(point);  
                
                if(index >= 0 && !((Map.Entry<String,ServiceInfo>) this.getModel().getElementAt(index)).getKey().equals("null")){
                
                    String info = "<html>";
                    List<String> listOfFunctions = ((Map.Entry<String,ServiceInfo>) this.getModel().getElementAt(index)).getValue().getFunction();
                    List<Input> listOfInputs = ((Map.Entry<String,ServiceInfo>) this.getModel().getElementAt(index)).getValue().getInput();
                    List<Output> listOfOutputs = ((Map.Entry<String,ServiceInfo>) this.getModel().getElementAt(index)).getValue().getOutput();
                                
                    String selOutput = ((Map.Entry<String,ServiceInfo>) this.getModel().getElementAt(index)).getKey();
                
                    //If list of functions is empty, i.e., if it is a software connector
                    if(listOfFunctions.isEmpty()){
                        String description = ((Map.Entry<String,ServiceInfo>) this.getModel().getElementAt(index)).getValue().getDescription();    
                        info = info + "<p width=\"300px\"><b>Description:</b> " + description + "</p>";
                    }
                    
                    int count = 1;
                    for(Iterator it = listOfFunctions.iterator();it.hasNext();){
                        info = info + "<p width=\"300px\"><b>Function " + count + ":</b> " + " " + manager.getLabel((String) it.next()) + "</p>";   
                        count++;
                    }
                
                    count = 1;
                    for(Iterator it = listOfInputs.iterator();it.hasNext();){
                        info = info + "<p width=\"300px\"><b>Input " + count + ":</b> " + " " + manager.getLabel(((Input) it.next()).getSemanticalType()) + "</p>";  
                        count++;
                    }
                
                    count = 1;
                    for(Iterator it = listOfOutputs.iterator();it.hasNext();){
                    
                        Output output = (Output) it.next();
                    
                        if(output.getID().equals(selOutput))
                            info = info + "<p width=\"300px\"><font color=\"blue\"><b>Output " + count + ":</b> " + manager.getLabel(output.getSemanticalType()) + "</font></p>";
                        else
                            info = info + "<p width=\"300px\"><b>Output " + count + ":</b> " + manager.getLabel(output.getSemanticalType()) + "</p>";
                        count++;
                    
                    }
                
                    info = info + "</html>";
                
                    //Return value for item in the list  
                    return info;
                }
                else
                    return null;
                
            }  
        
        };
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(8);
        list.setFixedCellWidth(450);
        
        //If mapOfServices is empty, disable list
        if(mapOfServices.isEmpty()){
            list.setEnabled(false);
        }
        
        //Method to render labels into JList
        ListCellRenderer listCellRenderer = new DefaultListCellRenderer(){
            
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
                
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                Map.Entry<String,ServiceInfo> entry = (Map.Entry<String,ServiceInfo>) value;
                setText(entry.getValue().getName());
                
                return this;
            }
        };
        
        list.setCellRenderer(listCellRenderer);
        
        //Add ListSelectionListener to list
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                
                if(list.getSelectedValue() != null){
                    
                    if(!(((Map.Entry<String,ServiceInfo>)list.getSelectedValue()).getKey().equals("null"))){
                    
                        //Set selected service
                        manager.setSelectedService(((Map.Entry<String,ServiceInfo>) list.getSelectedValue()).getValue());
                    
                        //Set selected output
                        manager.setSelectedOutputID(((Map.Entry<String,ServiceInfo>) list.getSelectedValue()).getKey());
                                        
                        okServiceButton.setEnabled(true);
                    }
                    else
                        okServiceButton.setEnabled(false);
                }
                else
                    okServiceButton.setEnabled(false);
                
            }
            
        });
        
        //Create JScrollPane and add list to it
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setAlignmentX(0);
        
        //Add JScrollPane to JPanel
        jPanel2.add(scrollPane);
        
        //Add JPanel to ResolveInputDialog
        this.getContentPane().add(jPanel,BorderLayout.NORTH);
        
        //Add JPanel2 to ResolveInputDialog
        this.getContentPane().add(jPanel2,BorderLayout.CENTER);
        
        //Create other jPanel
        JPanel jPanel3 = new JPanel();
        
        //Create OK button and add ActionListener to it
        okServiceButton = new JButton("OK");
        okServiceButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                
                isSelected = true;
                
                setVisible(false);
                
            }
        });
        okServiceButton.setEnabled(false);
        
        jPanel3.setLayout(new BorderLayout());
        jPanel3.add(okServiceButton,BorderLayout.EAST);
        
        jPanel3.setBorder(BorderFactory.createEmptyBorder(3,20,20,20));
        
        //Add JPanel3 to ResolveInputDialog
        this.getContentPane().add(jPanel3,BorderLayout.SOUTH);
    }
    
    public boolean isSelected(){
        return this.isSelected;
    }
    
    //Internal class for resolving input
    class ResolvingInput extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws JAXBException {
            
            setVisible(false);
                    
            //Disable jFrame and all of its components
            jFrame.setEnabled(false);
                    
            //Call JDialog "LoadingDialog"
            LoadingDialog loadingDialog = new LoadingDialog(jFrame,"Resolve Input","   Resolving input   ",300,200,60,40,3,40);
            loadingDialog.setModal(false);
                    
            //Center inside the application frame
            int x = jFrame.getX() + (jFrame.getWidth() - loadingDialog.getWidth()) / 2;
            int y = jFrame.getY() + (jFrame.getHeight() - loadingDialog.getHeight()) / 2;
            loadingDialog.setLocation(x, y);
                    
            //Set loadingDialog visible
            loadingDialog.setVisible(true);
                    
            //Call manager to validate input
            if(manager.serviceValidation(serviceID, inputID, selectedValue, isOutput)){
                        
                //Insert edge (only if edge do not already exist)
                jFrame.insertEdgeElement(inputID,selectedValue,!isOutput);
                        
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
            
            return null;
        }

        @Override
        protected void done() {
        
            //Clean graph selection and disable all associated menus
            jFrame.clearGraphSelection();
        
        }
    }
    
}


