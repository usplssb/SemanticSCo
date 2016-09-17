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

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import jaxbClasses.auxiliarClasses.ContextValueStructure;
import jaxbClasses.basictypes.Output;
import jaxbClasses.basictypes.ServiceInfo;
import management.CompositionManager;

public class PopupMenuDataSource extends JPopupMenu {
    
    private MainFrame jFrame;
    private CompositionManager manager;
    
    private String dataSourceID;
    private HashMap<ServiceInfo,LinkedList<String>> mapforDataSourceRemoval;
    
    private MenuBar menuBar;
    private ToolBar toolBar;
    
    public PopupMenuDataSource(final MainFrame jFrame, MenuBar menuBar, ToolBar toolBar, final CompositionManager manager,String dataSourceID, HashMap<ServiceInfo,LinkedList<String>> mapforDataSourceRemoval) {
        
        super();
        
        this.menuBar = menuBar;
        this.toolBar = toolBar;
        
        //Set local MainFrame
        this.jFrame = jFrame;
        this.manager = manager;
        
        this.dataSourceID = dataSourceID;
        this.mapforDataSourceRemoval = mapforDataSourceRemoval;
        
        JMenuItem itemRemoveConnection = new JMenuItem("Remove",new ImageIcon("images/datasource_delete.png"));
        this.add(itemRemoveConnection);
        
        itemRemoveConnection.addActionListener(new ActionListener() {
            
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
                toolBar.disableButtonAddActivity();
                menuBar.disableMenuAddActivity();
            }//Else, if numberOfDataSources > 0, enable menus "Add Activity"
            else{
                toolBar.enableButtonAddActivity();
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
