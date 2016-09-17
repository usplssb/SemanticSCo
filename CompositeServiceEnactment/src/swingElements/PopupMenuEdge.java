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
import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import management.CompositionManager;

public class PopupMenuEdge extends JPopupMenu {
    
    private MainFrame jFrame;
    private CompositionManager manager;
    
    private String edgeID;
    private String targetID;
    private LinkedList<String> selectedInputIDs;
    
    private MenuBar menuBar;
    private ToolBar toolBar;
    
    public PopupMenuEdge(final MainFrame jFrame, MenuBar menuBar, ToolBar toolBar, final CompositionManager manager,String edgeID,final HashMap<String,String> edgeMap,final String sourceID,final String targetID,final boolean isDataSource) {
        
        super();
        
        this.menuBar = menuBar;
        this.toolBar = toolBar;
        
        //Set local MainFrame
        this.jFrame = jFrame;
        this.manager = manager;
        
        this.edgeID = edgeID;
        this.targetID = targetID;
        
        JMenuItem itemRemoveConnection = new JMenuItem("Remove",new ImageIcon("images/edge_delete.png"));
        this.add(itemRemoveConnection);
        
        itemRemoveConnection.addActionListener(new ActionListener() {
            
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
