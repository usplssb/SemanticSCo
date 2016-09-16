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

package frames;

import filters.BpmnFilter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.Semaphore;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.namespace.QName;
import juddi.BindingInfo;
import juddi.JuddiRegistry;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.DataSpec;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.uddi.api_v3.BusinessInfo;
import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.bpmn.model.IOSpecification;
import org.activiti.bpmn.model.ServiceTask;
import org.apache.woden.WSDLException;
import org.apache.woden.WSDLFactory;
import org.apache.woden.WSDLReader;
import org.apache.woden.schema.Schema;
import org.apache.woden.wsdl20.Description;
import org.apache.woden.wsdl20.xml.BindingElement;
import org.apache.woden.wsdl20.xml.DescriptionElement;
import org.apache.woden.wsdl20.xml.EndpointElement;
import org.apache.woden.wsdl20.xml.InterfaceElement;
import org.apache.woden.wsdl20.xml.InterfaceOperationElement;
import org.apache.woden.wsdl20.xml.ServiceElement;
import org.apache.woden.wsdl20.xml.TypesElement;
import org.apache.woden.xml.XMLAttr;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;

public class MainFrame extends javax.swing.JFrame {
    
    /***********************************************************
    * Place your Activiti database properties here
    ***********************************************************/
    private final String dbDriver = "com.mysql.jdbc.Driver";
    private final String dbUrl = "jdbc:mysql://localhost:3306/activiti";
    private final String dbUsername = "root";
    private final String dbPassword = "password";
    /***********************************************************/
    
    private final Semaphore semaphore;
    private final JuddiRegistry jr;

    private String businessEntity = "Business Entity Name";
    private String selectedBusinessKey;
    private URL selectedBpmnUrl;
    
    private List<BusinessInfo> listOfBusinessInfos;
    
    private String processDescription = "My description";
    
    //Creates new form MainFrame
    public MainFrame(JuddiRegistry jr) {
        
        //set JuddiRegistry reference
        this.jr = jr;
        
        initComponents();
        
        //Create semaphore with a single permission
        semaphore = new Semaphore(1);
        
        //Centralizes frame
        setLocationRelativeTo(null);
            
        //Make Bpmn2Generator frame visible
        setVisible(true);
        
        //Set options of businessEntityComboBox
        setbusinessEntityComboBox();
        
    }
    
    //Set options of businessEntityComboBox
    private void setbusinessEntityComboBox() {
       
        try{
            //Get all available business entities
            listOfBusinessInfos = jr.findAllBusinessEntities();
        
            Vector<String> vector = new Vector<>();
        
            vector.add("--Select one option--");
        
            //If there is some business entity
            if(listOfBusinessInfos != null){
            
                //Iterate over all business entities and add them to vector
                Iterator<BusinessInfo> it= listOfBusinessInfos.iterator();
                while(it.hasNext())
                    vector.add(it.next().getName().get(0).getValue());
            }
        
            //Set businessEntityComboBox model
            businessEntityComboBox.setModel(new javax.swing.DefaultComboBoxModel(vector));
        
        }catch(RemoteException ex){
            //Show error message to user
            JOptionPane.showMessageDialog(rootPane, "Error to load application", "Error", JOptionPane.ERROR_MESSAGE);
            
            //Destroy frame
            dispose();
        }
            
    }

    //WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bpmnFileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        uploadPanel = new javax.swing.JPanel();
        selectedBpmnFileTextField = new javax.swing.JTextField();
        selectBpmnFileButton = new javax.swing.JButton();
        selectBpmnFileLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        selectedBpmnFileTextArea = new javax.swing.JTextArea();
        publishProcessButton = new javax.swing.JButton();
        publishingProcessProgressBar = new javax.swing.JProgressBar();
        publishingProcessLabel = new javax.swing.JLabel();
        processDescriptionLabel = new javax.swing.JLabel();
        processDescriptionTextField = new javax.swing.JTextField();
        businessEntityPanel = new javax.swing.JPanel();
        businessEntityComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        selectBusinessLabel = new javax.swing.JLabel();
        businessEntityTextField = new javax.swing.JTextField();
        insertBusinessNameLabel = new javax.swing.JLabel();
        createBusinessEntityButton = new javax.swing.JButton();

        bpmnFileChooser.setAcceptAllFileFilterUsed(false);
        bpmnFileChooser.setDialogTitle("Open");
        bpmnFileChooser.setFileFilter(new BpmnFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BPMN Publisher");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        uploadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Upload BPMN File"));
        uploadPanel.setEnabled(false);

        selectedBpmnFileTextField.setText("No file selected");
        selectedBpmnFileTextField.setEnabled(false);
        selectedBpmnFileTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                selectedBpmnFileTextFieldKeyReleased(evt);
            }
        });

        selectBpmnFileButton.setText("Select File");
        selectBpmnFileButton.setEnabled(false);
        selectBpmnFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectBpmnFileButtonActionPerformed(evt);
            }
        });

        selectBpmnFileLabel.setText("Please, select a BPMN file or enter a URL:");
        selectBpmnFileLabel.setEnabled(false);

        selectedBpmnFileTextArea.setEditable(false);
        selectedBpmnFileTextArea.setColumns(20);
        selectedBpmnFileTextArea.setRows(5);
        selectedBpmnFileTextArea.setEnabled(false);
        jScrollPane1.setViewportView(selectedBpmnFileTextArea);

        publishProcessButton.setText("Publish Process");
        publishProcessButton.setEnabled(false);
        publishProcessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publishProcessButtonActionPerformed(evt);
            }
        });

        publishingProcessProgressBar.setIndeterminate(true);

        publishingProcessLabel.setText("Publishing process ...");

        processDescriptionLabel.setText("Description:");
        processDescriptionLabel.setEnabled(false);

        processDescriptionTextField.setText("My description");
        processDescriptionTextField.setEnabled(false);
        processDescriptionTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                processDescriptionTextFieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout uploadPanelLayout = new javax.swing.GroupLayout(uploadPanel);
        uploadPanel.setLayout(uploadPanelLayout);
        uploadPanelLayout.setHorizontalGroup(
            uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(uploadPanelLayout.createSequentialGroup()
                        .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectBpmnFileLabel)
                            .addGroup(uploadPanelLayout.createSequentialGroup()
                                .addGap(182, 182, 182)
                                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(publishingProcessLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(publishProcessButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(publishingProcessProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(uploadPanelLayout.createSequentialGroup()
                                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(selectBpmnFileButton)
                                    .addComponent(processDescriptionLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(selectedBpmnFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE)
                                    .addComponent(processDescriptionTextField))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        uploadPanelLayout.setVerticalGroup(
            uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(uploadPanelLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(selectBpmnFileLabel)
                .addGap(18, 18, 18)
                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(selectedBpmnFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBpmnFileButton))
                .addGap(18, 18, 18)
                .addGroup(uploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(processDescriptionLabel)
                    .addComponent(processDescriptionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(publishProcessButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(publishingProcessLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(publishingProcessProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(79, 79, 79))
        );

        publishingProcessProgressBar.setVisible(false);
        publishingProcessLabel.setVisible(false);

        businessEntityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Create/Select Business Entity"));

        businessEntityComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        businessEntityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                businessEntityComboBoxActionPerformed(evt);
            }
        });

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        selectBusinessLabel.setText("Please, select a Business Entity:");

        businessEntityTextField.setText("Business Entity Name");
        businessEntityTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                businessEntityTextFieldKeyReleased(evt);
            }
        });

        insertBusinessNameLabel.setText("Please, insert Business Entity name:");

        createBusinessEntityButton.setText("Create");
        createBusinessEntityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createBusinessEntityButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout businessEntityPanelLayout = new javax.swing.GroupLayout(businessEntityPanel);
        businessEntityPanel.setLayout(businessEntityPanelLayout);
        businessEntityPanelLayout.setHorizontalGroup(
            businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(businessEntityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(insertBusinessNameLabel)
                    .addComponent(createBusinessEntityButton)
                    .addComponent(businessEntityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(businessEntityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectBusinessLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        businessEntityPanelLayout.setVerticalGroup(
            businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(businessEntityPanelLayout.createSequentialGroup()
                .addGroup(businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(businessEntityPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(insertBusinessNameLabel)
                            .addComponent(selectBusinessLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(businessEntityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(businessEntityPanelLayout.createSequentialGroup()
                                .addComponent(businessEntityTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(createBusinessEntityButton))
                            .addComponent(businessEntityComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 9, Short.MAX_VALUE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(uploadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(businessEntityPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(businessEntityPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(uploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 505, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //When user type something in businessEntityTextField
    private void businessEntityTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_businessEntityTextFieldKeyReleased
        
        businessEntity = businessEntityTextField.getText().trim();
        
        //Enable/disable createBusinessEntityButton
        if(!businessEntity.equals(""))
            createBusinessEntityButton.setEnabled(true);
        else
            createBusinessEntityButton.setEnabled(false);
    }//GEN-LAST:event_businessEntityTextFieldKeyReleased

    //When user press createBusinessEntityButton
    private void createBusinessEntityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createBusinessEntityButtonActionPerformed
        //Try to obtain permission
        if(semaphore.tryAcquire()){
            
            //Disable and clear components of uploadPanel
            uploadPanel.setEnabled(false);
            selectBpmnFileLabel.setEnabled(false);
            selectBpmnFileButton.setEnabled(false);
            selectedBpmnFileTextField.setEnabled(false);
            selectedBpmnFileTextArea.setEnabled(false);
            selectedBpmnFileTextField.setText("No file selected");
            processDescriptionTextField.setEnabled(false);
            processDescriptionLabel.setEnabled(false);
            processDescriptionTextField.setText("My description");
            selectedBpmnFileTextArea.setText("");
            publishProcessButton.setEnabled(false);
                        
            try {
                
                //Try to create business entity, if not possible, generate exception
                jr.createBusinessEntity(businessEntity);
                
                //Set options of businessEntityComboBox
                setbusinessEntityComboBox();
                
                //Show success message to user
                JOptionPane.showMessageDialog(rootPane, "Business Entity \"" + businessEntity + "\" successfully created", "Message", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (RemoteException ex) {
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to create Business Entity \"" + businessEntity + "\"", "Error", JOptionPane.ERROR_MESSAGE);
                
            }
            
            //Release semaphore
            semaphore.release();
            
        }
    }//GEN-LAST:event_createBusinessEntityButtonActionPerformed

    //When user select some option of businessEntityComboBox
    private void businessEntityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_businessEntityComboBoxActionPerformed
        
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            //Clear components of upload panel
            selectedBpmnFileTextField.setText("No file selected");
            selectedBpmnFileTextArea.setText("");
            publishProcessButton.setEnabled(false);
            processDescriptionTextField.setEnabled(false);
            processDescriptionLabel.setEnabled(false);
            processDescriptionTextField.setText("My description");
                        
            String selectedBusinessEntity = (String) businessEntityComboBox.getSelectedItem();
            
            //Enable/Disable components of uploadPanel and clear fields
            if(businessEntityComboBox.getSelectedIndex() > 0){
                
                //Iterate over all Business Entities to find key associated to selected BusinessEntity
                Iterator<BusinessInfo> it = listOfBusinessInfos.iterator();
                while(it.hasNext()){
                    BusinessInfo businessInfo = it.next();
                    //Set selected business key
                    if(businessInfo.getName().get(0).getValue().equals(selectedBusinessEntity))
                        selectedBusinessKey = businessInfo.getBusinessKey();
                }
                
                uploadPanel.setEnabled(true);
                selectBpmnFileLabel.setEnabled(true);
                selectBpmnFileButton.setEnabled(true);
                selectedBpmnFileTextField.setEnabled(true);
                selectedBpmnFileTextArea.setEnabled(true);
                
            }
            else{
                uploadPanel.setEnabled(false);
                selectBpmnFileLabel.setEnabled(false);
                selectBpmnFileButton.setEnabled(false);
                selectedBpmnFileTextField.setEnabled(false);
                processDescriptionTextField.setEnabled(false);
                processDescriptionLabel.setEnabled(false);
                selectedBpmnFileTextArea.setEnabled(false);
            }

            //Release semaphore
            semaphore.release();
        }
        
    }//GEN-LAST:event_businessEntityComboBoxActionPerformed

    private void selectBpmnFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectBpmnFileButtonActionPerformed
        
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            //Open bpmn file chooser
            int returnVal = bpmnFileChooser.showOpenDialog(this);

            //If user selects a file
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                
                //Set text field with selected filename
                selectedBpmnFileTextField.setText(bpmnFileChooser.getSelectedFile().getPath());
                
                //Enable and Clear processDescriptionTextField and processDescriptionLabel
                processDescriptionTextField.setEnabled(true);
                processDescriptionTextField.setText("My description");
                processDescriptionLabel.setEnabled(true);
                
                //Clear selectedBpmnFileTextArea
                selectedBpmnFileTextArea.setText("");
                
                //Disable publishProcessButton
                publishProcessButton.setEnabled(false);
                    
                try{
                    //Convert selected file to URI
                    selectedBpmnUrl = bpmnFileChooser.getSelectedFile().toURI().toURL();
                
                    //Try to open and read BPMN file. If ok, set selectedBpmnFileTextArea. If not, generate exception
                    BufferedReader reader = new BufferedReader(new InputStreamReader(selectedBpmnUrl.openStream()));
                    StringBuilder out = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        out.append(line).append("\n");
                    selectedBpmnFileTextArea.setText(out.toString());
                    reader.close();
                    
                    //Enable publishProcessButton
                    publishProcessButton.setEnabled(true);                    
                    
                //In case BPMN file can not be opened
                } catch (IOException ex) {              
                    
                    //Make valFileLoadingFailedLabel enabled and visible
                    JOptionPane.showMessageDialog(rootPane, "Error to load BPMN file", "Error", JOptionPane.ERROR_MESSAGE);
                
                }
            }
            
            //Release semaphore
            semaphore.release();
        }
    }//GEN-LAST:event_selectBpmnFileButtonActionPerformed

    //When user press enter in selectedBpmnFileTextField
    private void selectedBpmnFileTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_selectedBpmnFileTextFieldKeyReleased
        
        //Try to acquire permition
        if(semaphore.tryAcquire()){
            
            //Get key typed by user
            int key = evt.getKeyCode();
        
            //If user pressed Enter
            if(key == KeyEvent.VK_ENTER){
            
                //If text field is not empty
                if(!selectedBpmnFileTextField.getText().equals("")){
                    
                    //Clear selectedBpmnFileTextArea
                    selectedBpmnFileTextArea.setText("");
                    
                    //Disable and clear processDescriptionTextField and processDescriptionLabel
                    processDescriptionTextField.setEnabled(false);
                    processDescriptionTextField.setText("My description");
                    processDescriptionLabel.setEnabled(false);
                
                    //Disable publishProcessButton
                    publishProcessButton.setEnabled(false);
                
                    try {
                    
                        //Verify if BPMN file is a file or URL
                        if(new File(selectedBpmnFileTextField.getText()).isFile())
                            selectedBpmnUrl = new File(selectedBpmnFileTextField.getText()).toURI().toURL();
                        else
                            selectedBpmnUrl = new URL(selectedBpmnFileTextField.getText());
                
                        //Try to open and read BPMN file. If ok, set selectedBpmnFileTextArea. If not, generate exception
                        BufferedReader reader = new BufferedReader(new InputStreamReader(selectedBpmnUrl.openStream()));
                        StringBuilder out = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null)
                            out.append(line).append("\n");
                        selectedBpmnFileTextArea.setText(out.toString());
                        reader.close();
                    
                        //Enable processDescriptionTextField and processDescriptionLabel
                        processDescriptionTextField.setEnabled(true);
                        processDescriptionLabel.setEnabled(true);
                        
                        //Enable publishProcessButton
                        publishProcessButton.setEnabled(true);                    
                       
                    //In case BPMN file can not be opened
                    } catch (IOException ex) {
                        
                        //Make valFileLoadingFailedLabel enabled and visible
                        JOptionPane.showMessageDialog(rootPane, "Error to load BPMN file", "Error", JOptionPane.ERROR_MESSAGE);
                        
                    }
                }
            }
             
            //Release semaphore
            semaphore.release();
            
        }
    }//GEN-LAST:event_selectedBpmnFileTextFieldKeyReleased

    //When user press publishProcessButton
    private void publishProcessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishProcessButtonActionPerformed
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            //Call swing worker to publish BPMN process into jUDDI database
            new RegisterProcess().execute();
            
        }
    }//GEN-LAST:event_publishProcessButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //Logout registry
        jr.logout();
    }//GEN-LAST:event_formWindowClosing

    //When user type something in processDescriptionTextField
    private void processDescriptionTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_processDescriptionTextFieldKeyReleased
        processDescription = processDescriptionTextField.getText().trim();
    }//GEN-LAST:event_processDescriptionTextFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFileChooser bpmnFileChooser;
    private javax.swing.JComboBox businessEntityComboBox;
    private javax.swing.JPanel businessEntityPanel;
    private javax.swing.JTextField businessEntityTextField;
    private javax.swing.JButton createBusinessEntityButton;
    private javax.swing.JLabel insertBusinessNameLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel processDescriptionLabel;
    private javax.swing.JTextField processDescriptionTextField;
    private javax.swing.JButton publishProcessButton;
    private javax.swing.JLabel publishingProcessLabel;
    private javax.swing.JProgressBar publishingProcessProgressBar;
    private javax.swing.JButton selectBpmnFileButton;
    private javax.swing.JLabel selectBpmnFileLabel;
    private javax.swing.JLabel selectBusinessLabel;
    private javax.swing.JTextArea selectedBpmnFileTextArea;
    private javax.swing.JTextField selectedBpmnFileTextField;
    private javax.swing.JPanel uploadPanel;
    // End of variables declaration//GEN-END:variables

    //Internal class for sending email to user
    class RegisterProcess extends SwingWorker<String, Object> {
        
        protected String doInBackground() {
            
            //Make progress bar and label visible
            publishingProcessLabel.setVisible(true);
            publishingProcessProgressBar.setVisible(true);
            
            try {
                
                //Create process engine
                ProcessEngine processEngine = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration()
                        .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_FALSE)
                        .setJdbcDriver(dbDriver)
                        .setJdbcUrl(dbUrl)
                        .setJdbcUsername(dbUsername)
                        .setJdbcPassword(dbPassword)
                        .buildProcessEngine();
                
                //Get repository service
                RepositoryService repositoryService = processEngine.getRepositoryService();
        
                String[] partsAux = selectedBpmnUrl.getPath().split("/");
                        
                //Add BPMN process to repository and return deploymentId
                String deploymentId = repositoryService.createDeployment().addInputStream(partsAux[partsAux.length-1],selectedBpmnUrl.openStream()).deploy().getId();
            
                //Get id of deployed process
                List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).list();
                String processDefinitionId = list.get(0).getId();
                
                //Get BPMN model of deployed process
                BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
                
                //Set BPMN location
                String bpmnLocation = selectedBpmnUrl.toString();
                
                //Set BPMN Target Namespace
                String bpmnTargetNamespace = bpmnModel.getTargetNamespace();
                                
                //Store BPMN registered namespaces
                Map<String,String> registeredNamespaces = bpmnModel.getNamespaces();
                
                //Get BPMN Process
                List<Process> bpmnProcesses = bpmnModel.getProcesses();
                
                //If there is some defined BPMN Process
                if(!bpmnProcesses.isEmpty()){
                    
                    //For each BPMN Process
                    for(Iterator itProc=bpmnProcesses.iterator();itProc.hasNext();){
                        
                        Process bpmnProcess = (Process) itProc.next();
                        
                        //Initialize LinkedList "wsdlInterfaceKeys"
                        LinkedList<String> wsdlInterfaceKeys = new LinkedList<>();
                
                        //Initialize LinkedLists to store semantic informations
                        LinkedList<String> functionalities = new LinkedList<>();
                        LinkedList<String> inputs = new LinkedList<>();
                        LinkedList<String> outputs = new LinkedList<>();
                        
                        //Create LinkedList "bpmnServiceTasks"
                        LinkedList<ServiceTask> bpmnServiceTasks = new LinkedList<>();
                        
                        //Get BPMN service tasks defined within the process and add them to LinkedList "bpmnServiceTasks"
                        Collection<FlowElement> flowElements = bpmnProcess.getFlowElements();
                        for(Iterator it=flowElements.iterator();it.hasNext();){
                            
                            FlowElement flowElement = (FlowElement) it.next();
                            
                            if(flowElement instanceof ServiceTask)
                                bpmnServiceTasks.add(((ServiceTask) flowElement));
                            
                        }
                        
                        //If there are service tasks defined in the process
                        if(!bpmnServiceTasks.isEmpty()){
                            
                            //Set BPMN process name
                            String bpmnProcessName = bpmnProcess.getName();
                            
                            //Initialize LinkedList "listOfBindingInfos"
                            LinkedList<BindingInfo> listOfBindingInfos = new LinkedList<>();
                            
                            //For each BPMN Service task
                            for(Iterator it=bpmnServiceTasks.iterator();it.hasNext();){
                                
                                ServiceTask bpmnServiceTask = (ServiceTask) it.next();
                                
                                //Set WSDL location associated with BPMN Service task
                                String wsdlLocation = registeredNamespaces.get(bpmnServiceTask.getName().split(":")[0]);
                                
                                //Transform wsdl location into URL to avoid errors
                                URL wsdlUrl;
                                if(new File(wsdlLocation).isFile())
                                    wsdlUrl = new File(wsdlLocation).toURI().toURL();
                                else
                                    wsdlUrl = new URL(wsdlLocation);
                                
                                //Read WSDL document, try to validate it and if it is a valid document then return it as a WSDL Description
                                WSDLFactory factoryReader = WSDLFactory.newInstance();
                                WSDLReader reader = factoryReader.newWSDLReader();
                                reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
                                Description descriptionComponent = reader.readWSDL(wsdlUrl.toString());
                                DescriptionElement descriptionElement = descriptionComponent.toElement();
                                
                                //Extract interface name associated with BPMN Service task
                                String bpmnInterfaceName = bpmnServiceTask.getName().split(":")[1].split("\\.")[0];
                                
                                //Obtain WSDL Interfaces
                                InterfaceElement[] wsdlInterfaces = descriptionElement.getInterfaceElements();
                                
                                String wsdlInterfaceName = null;
                                String wsdlInterfaceNamespace = null;
                                
                                //For each WSDL Interface
                                for(int i=0;i<wsdlInterfaces.length;i++){
                                    
                                    //If WSDL interface is the same associated with BPMN Service task
                                    if(wsdlInterfaces[i].getName().getLocalPart().equals(bpmnInterfaceName)){
                                        
                                        //Set WSDL Interface name
                                        wsdlInterfaceName = bpmnInterfaceName;
                                        
                                        //Set WSDL Interface namespace
                                        wsdlInterfaceNamespace = wsdlInterfaces[i].getName().getNamespaceURI();
                                        
                                    }
                                            
                                }
                                
                                //Create/find WSDL Interface TModel
                                String wsdlInterfaceKey = jr.publishWsdlInterfaceTModel(wsdlInterfaceName,wsdlUrl.toString(),wsdlInterfaceNamespace);       
                        
                                //If created interfaceKey was not inserted into LinkedList
                                if(!wsdlInterfaceKeys.contains(wsdlInterfaceKey))
                                    wsdlInterfaceKeys.add(wsdlInterfaceKey);
                                
                                //Obtain WSDL Bindings
                                BindingElement[] wsdlBindings = descriptionElement.getBindingElements();
                                
                                //For each WSDL Binding
                                for (int i=0; i<wsdlBindings.length; i++) {
                                    
                                    //If Binding is associated to current Interface
                                    if(wsdlBindings[i].getInterfaceName().getLocalPart().equals(wsdlInterfaceName)){   
                                        
                                        String protocol;
                                
                                        if(wsdlBindings[i].getType().toString().equals("http://www.w3.org/ns/wsdl/http"))
                                            protocol = "rest";
                                        else{
                                            //Get version of SOAP protocol
                                            XMLAttr versionAttribute = wsdlBindings[i].getExtensionAttribute(new QName("http://www.w3.org/ns/wsdl/soap","version"));
                                    
                                            if(versionAttribute.getContent().toString().equals("1.1"))
                                                protocol = "soap11";
                                            else
                                                protocol = "soap12";   
                                        }
                                        
                                        //Create/Find WSDL Binding TModel
                                        String bindingTModelKey = jr.publishWsdlBindingTModel(wsdlInterfaceKey, wsdlBindings[i].getName().getLocalPart(),wsdlUrl.toString(),wsdlBindings[i].getName().getNamespaceURI(),protocol);
                                
                                        //Obtain WSDL Services
                                        ServiceElement[] wsdlServices = descriptionElement.getServiceElements();
                                        
                                        //For each WSDL Service
                                        for(int j=0;j<wsdlServices.length;j++){
                                            
                                            BindingInfo info;  
                                    
                                            //If WSDL Service refers to current interface
                                            if(wsdlServices[j].getInterfaceName().getLocalPart().equals(wsdlInterfaceName)){
                                        
                                                //Obtain WSDL Endpoints
                                                EndpointElement[] wsdlEndpoints = wsdlServices[j].getEndpointElements();
                                        
                                                //For each WSDL Endpoint
                                                for(int k=0;k<wsdlEndpoints.length;k++){
                                                
                                                    //If WSDL Endpoint refers to current binding
                                                    if(wsdlEndpoints[k].getBindingName().getLocalPart().equals(wsdlBindings[i].getName().getLocalPart())){
                                                    
                                                        //Store binding informations into LinkedList
                                                        if(protocol.equals("rest"))
                                                            info = new BindingInfo(wsdlInterfaceKey,bindingTModelKey,wsdlEndpoints[k].getAddress().toString(),wsdlEndpoints[k].getName().toString(),false);
                                                        else
                                                            info = new BindingInfo(wsdlInterfaceKey,bindingTModelKey,wsdlEndpoints[k].getAddress().toString(),wsdlEndpoints[k].getName().toString(),true);
                                                
                                                        //If BindingInfo was not inserted into LinkedList, insert it
                                                        boolean alreadyDefined = false;
                                                        Iterator itInfo = listOfBindingInfos.iterator();
                                                        while(itInfo.hasNext()){
                                                            BindingInfo bInfo = (BindingInfo) itInfo.next();
                                                            if(bInfo.equals(info))
                                                                alreadyDefined = true;
                                                        }
                                                        if(!alreadyDefined)
                                                            listOfBindingInfos.add(info);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                //Set service task name
                                String serviceTaskName = bpmnServiceTask.getId();
                                
                                //Find functionalities (semantic concepts) associated with service task
                                for(int i=0;i<wsdlInterfaces.length;i++){
                            
                                    //If WSDL interface is the same associated with BPMN Service task
                                    if(wsdlInterfaces[i].getName().getLocalPart().equals(wsdlInterfaceName)){
                                
                                        //Obtain WSDL Operations
                                        InterfaceOperationElement[] wsdlOperations = wsdlInterfaces[i].getInterfaceOperationElements();
                                
                                        //For each WSDL Operation
                                        for (int j=0; j<wsdlOperations.length; j++) {
                                    
                                            //If WSDL Operation is the same referenced by Service Task
                                            if(wsdlOperations[j].getName().getLocalPart().equals(serviceTaskName)){
                                        
                                                //If WSDL operation is semantically annotated, store concept
                                                XMLAttr attr = wsdlOperations[j].getExtensionAttribute(new QName("http://www.w3.org/ns/sawsdl","modelReference"));
                                                if(attr != null)
                                                    functionalities.add(attr.getContent().toString());
                                        
                                            }
                                        }
                                    }
                                }                                
                            }
                            
                            //Find inputs and outputs (semantic concepts) associated with BPMN process
                    
                            //Get IO Specification and data inputs
                            IOSpecification ioSpecification = bpmnProcess.getIoSpecification();
                            List<DataSpec> dataInputs = ioSpecification.getDataInputs();
                            List<DataSpec> dataOutputs = ioSpecification.getDataOutputs();
                                
                            //For each data input
                            for(Iterator itIn=dataInputs.iterator();itIn.hasNext();){
                                    
                                DataSpec dataInput = (DataSpec) itIn.next();
                                
                                //Set message name and element name
                                String inMessage = dataInput.getName().split(":")[1].split("\\.")[0];
                                String inData = dataInput.getName().split("\\.")[1];
                                
                                //Set WSDL location associated with BPMN data input
                                String wsdlLocation = registeredNamespaces.get(dataInput.getName().split(":")[0]);
                                
                                //Transform wsdl location into URL to avoid errors
                                URL wsdlUrl;
                                if(new File(wsdlLocation).isFile())
                                    wsdlUrl = new File(wsdlLocation).toURI().toURL();
                                else
                                    wsdlUrl = new URL(wsdlLocation);
                                    
                                //Read WSDL document, try to validate it and if it is a valid document then return it as a WSDL Description
                                WSDLFactory factoryReader = WSDLFactory.newInstance();
                                WSDLReader reader = factoryReader.newWSDLReader();
                                reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
                                Description descriptionComponent = reader.readWSDL(wsdlUrl.toString());
                                DescriptionElement descriptionElement = descriptionComponent.toElement();
                                
                                //Obtain WSDL Types
                                TypesElement typesElement= descriptionElement.getTypesElement();
        
                                //Obtain XML schemas
                                Schema[] schemas = typesElement.getSchemas();

                                //For each XML Schema
                                for(int i=0;i<schemas.length;i++){
                                    
                                    //Get top-level XML elements
                                    XmlSchemaObjectTable elements = schemas[i].getSchemaDefinition().getElements();
            
                                    Iterator elementIterator = elements.getValues();
            
                                    //For each top-level XML element
                                    while(elementIterator.hasNext()) {
                                    
                                        XmlSchemaElement topLevelElement = (XmlSchemaElement) elementIterator.next();
                                        XmlSchemaType schemaType = topLevelElement.getSchemaType();
                
                                        //If is a XML complex element
                                        if(schemaType instanceof XmlSchemaComplexType) {
                                            
                                            //If finds complex element
                                            if(topLevelElement.getName().equals(inMessage)){
                        
                                                //Get XML simple elements contained within the complex element
                                                if(((XmlSchemaComplexType)topLevelElement.getSchemaType()).getParticle() instanceof XmlSchemaSequence) {

                                                    XmlSchemaSequence schemaSequence = (XmlSchemaSequence) ((XmlSchemaComplexType)topLevelElement.getSchemaType()).getParticle();
                                                    Iterator iterator = schemaSequence.getItems().getIterator();                           

                                                    //For each XML simple element
                                                    while (iterator.hasNext()) {
                                                    
                                                        XmlSchemaElement simpleElement = (XmlSchemaElement) iterator.next();
                                                
                                                        //If finds simple element
                                                        if(simpleElement.getName().equals(inData)){
                                
                                                            //Get associated SAWSDL annotation (if available) and store it
                                                            if(simpleElement.getMetaInfoMap()!=null){
                                                                Map ty = simpleElement.getMetaInfoMap();
                                                                String[] parts = ty.get("EXTERNAL_ATTRIBUTES").toString().split("\"");
                                                                //Store {semantic_concept|variable_name|variable_type}
                                                                inputs.add(parts[1] + "|" + dataInput.getName().split(":")[1] + "|" + simpleElement.getSchemaTypeName().getLocalPart());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                        //Else, if is a XML simple element
                                        }else if (schemaType instanceof XmlSchemaSimpleType){
                                            
                                            //If finds simple element
                                            if(topLevelElement.getName().equals(inMessage)){
                                                
                                                //Get associated SAWSDL annotation (if available) and store it
                                                if(topLevelElement.getMetaInfoMap()!=null){
                                                    Map ty = topLevelElement.getMetaInfoMap();            
                                                    String[] parts = ty.get("EXTERNAL_ATTRIBUTES").toString().split("\"");
                                                    //Store {semantic_concept|variable_name|variable_type}
                                                    inputs.add(parts[1] + "|" + dataInput.getName().split(":")[1] + "|" + topLevelElement.getSchemaTypeName().getLocalPart());      
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //For each data output
                            for(Iterator itIn=dataOutputs.iterator();itIn.hasNext();){
                                    
                                DataSpec dataOutput = (DataSpec) itIn.next();
                                
                                //Set message name and element name
                                String outMessage = dataOutput.getName().split(":")[1].split("\\.")[0];
                                String outData = dataOutput.getName().split("\\.")[1];
                                
                                //Set WSDL location associated with BPMN data output
                                String wsdlLocation = registeredNamespaces.get(dataOutput.getName().split(":")[0]);
                                
                                //Transform wsdl location into URL to avoid errors
                                URL wsdlUrl;
                                if(new File(wsdlLocation).isFile())
                                    wsdlUrl = new File(wsdlLocation).toURI().toURL();
                                else
                                    wsdlUrl = new URL(wsdlLocation);
                                    
                                //Read WSDL document, try to validate it and if it is a valid document then return it as a WSDL Description
                                WSDLFactory factoryReader = WSDLFactory.newInstance();
                                WSDLReader reader = factoryReader.newWSDLReader();
                                reader.setFeature(WSDLReader.FEATURE_VALIDATION, true);
                                Description descriptionComponent = reader.readWSDL(wsdlUrl.toString());
                                DescriptionElement descriptionElement = descriptionComponent.toElement();
                                
                                //Obtain WSDL Types
                                TypesElement typesElement= descriptionElement.getTypesElement();
        
                                //Obtain XML schemas
                                Schema[] schemas = typesElement.getSchemas();

                                //For each XML Schema
                                for(int i=0;i<schemas.length;i++){
                                    
                                    //Get top-level XML elements
                                    XmlSchemaObjectTable elements = schemas[i].getSchemaDefinition().getElements();
            
                                    Iterator elementIterator = elements.getValues();
            
                                    //For each top-level XML element
                                    while(elementIterator.hasNext()) {
                                    
                                        XmlSchemaElement topLevelElement = (XmlSchemaElement) elementIterator.next();
                                        XmlSchemaType schemaType = topLevelElement.getSchemaType();
                
                                        //If is a XML complex element
                                        if(schemaType instanceof XmlSchemaComplexType) {
                                            
                                            //If finds complex element
                                            if(topLevelElement.getName().equals(outMessage)){
                        
                                                //Get XML simple elements contained within the complex element
                                                if(((XmlSchemaComplexType)topLevelElement.getSchemaType()).getParticle() instanceof XmlSchemaSequence) {

                                                    XmlSchemaSequence schemaSequence = (XmlSchemaSequence) ((XmlSchemaComplexType)topLevelElement.getSchemaType()).getParticle();
                                                    Iterator iterator = schemaSequence.getItems().getIterator();                           

                                                    //For each XML simple element
                                                    while (iterator.hasNext()) {
                                                    
                                                        XmlSchemaElement simpleElement = (XmlSchemaElement) iterator.next();
                                                
                                                        //If finds simple element
                                                        if(simpleElement.getName().equals(outData)){
                                
                                                            //Get associated SAWSDL annotation (if available) and store it
                                                            if(simpleElement.getMetaInfoMap()!=null){
                                                                Map ty = simpleElement.getMetaInfoMap();
                                                                String[] parts = ty.get("EXTERNAL_ATTRIBUTES").toString().split("\"");
                                                                //Store {semantic_concept|variable_name|variable_type}
                                                                outputs.add(parts[1] + "|" + dataOutput.getName().split(":")[1] + "|" + simpleElement.getSchemaTypeName().getLocalPart());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            
                                        //Else, if is a XML simple element
                                        }else if (schemaType instanceof XmlSchemaSimpleType){
                                            
                                            //If finds simple element
                                            if(topLevelElement.getName().equals(outMessage)){
                                                
                                                //Get associated SAWSDL annotation (if available) and store it
                                                if(topLevelElement.getMetaInfoMap()!=null){
                                                    Map ty = topLevelElement.getMetaInfoMap();            
                                                    String[] parts = ty.get("EXTERNAL_ATTRIBUTES").toString().split("\"");
                                                    //Store {semantic_concept|variable_name|variable_type}
                                                    outputs.add(parts[1] + "|" + dataOutput.getName().split(":")[1] + "|" + topLevelElement.getSchemaTypeName().getLocalPart());
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            //Create/find BPMN Process TModel
                            String bpmnProcessKey = jr.publishBpmnProcessTModel(wsdlInterfaceKeys,bpmnProcessName, bpmnLocation, bpmnTargetNamespace);
                    
                            //Create/find BusinessService
                            String businessServiceKey = jr.publishBusinessService(functionalities,inputs,outputs,selectedBusinessKey,bpmnProcessName,processDescription,bpmnTargetNamespace,bpmnProcessKey,listOfBindingInfos);
                            
                        }
                    }
                }
                            
                //Show success message to user
                JOptionPane.showMessageDialog(rootPane, "Bpmn Process successfully inserted into UDDI registry", "Message", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException|WSDLException ex) {
                System.err.println("Error: " + ex);
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to insert BPMN process into UDDI registry", "Error: " + ex, JOptionPane.ERROR_MESSAGE);
            
            }
            
            return null;
        }
        
        protected void done() {
            
            //Make progress bar and label invisible
            publishingProcessLabel.setVisible(false);
            publishingProcessProgressBar.setVisible(false);
            
            //Release semaphore
            semaphore.release();
        }
    }
    
}
