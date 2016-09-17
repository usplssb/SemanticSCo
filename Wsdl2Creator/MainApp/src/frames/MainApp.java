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

import classes.*;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.config.ClientConfig;
import filters.*;
import java.awt.Color;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Semaphore;
import javax.swing.*;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class MainApp extends javax.swing.JFrame {
    
    /***********************************************************
    * Place your services' URLs here
    ***********************************************************/
    private final String creatorServiceURL = "http://address/Wsdl2CreatorService/webresources/wsdl2creatorservice";
    private final String editorServiceURL = "http://address/Wsdl2EditorService/webresources/wsdl2editorservice";
    private final String validatorServiceURL = "http://address/Wsdl2ValidatorService/webresources/wsdl2validatorservice";
    /***********************************************************/    
    
    //General variables
    private String genAnalysisIdentifier;
    private String genEdAnalysisIdentifier;
    private String edAnalysisIdentifier;
    private String valAnalysisIdentifier;
    
    //Generation variables
    private File genSelectedWarFile;
    private String genSelectedService;
    private Semaphore genSemaphore;
    private Path temporaryDirectory;
    private String genServiceAddress = "";
    private String genTargetNamespace = "";
    private String genTargetNamespacePrefix = "";
    private String genXMLTargetNamespace = "";
    private String genXMLTargetNamespacePrefix = "";
    private String genAttributeFormDefault = "qualified";
    private String genElementFormDefault = "qualified";
    private String genResultFilename = "";
    private String genEdResultFilename = "";
    private File genResultFile;
    private File genEdResultFile;
    
    //Edition variables
    private File edSelectedWsdlFile;
    private Semaphore edSemaphore;
    private String edResultFilename = "";
    private File edResultFile;
    
    //Validation variables
    private File valSelectedFile;
    private Semaphore valSemaphore;
    private boolean wasValidated = true;

    //Creates new form MainApp
    public MainApp() {
        
        try {
            
            //Initiate semaphores
            genSemaphore = new Semaphore(1);
            edSemaphore = new Semaphore(1);
            valSemaphore = new Semaphore(1);
            
            //Create temporary directory on client to store unzipped WAR files (generation)
            temporaryDirectory = Files.createTempDirectory("tempWsdl2Creator");
            
            //Connect to Wsdl2CreatorService
            ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            Client genClient = Client.create(config);
            WebResource genResource = genClient.resource(creatorServiceURL);

            //Generate identifier for the analysis
            genAnalysisIdentifier = genResource.path("generateID").accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        
            //Close connection to Wsdl2CreatorService
            genClient.destroy();
        
            System.out.println("Generated gen ID: " + genAnalysisIdentifier);
        
            //Connect to Wsdl2EditorService
            ClientConfig config2 = new com.sun.jersey.api.client.config.DefaultClientConfig();
            Client edClient = Client.create(config2);
            WebResource edResource = edClient.resource(editorServiceURL);
            
            //Generate IDs
            genEdAnalysisIdentifier = edResource.path("generateID").accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
            edAnalysisIdentifier = edResource.path("generateID").accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        
            //Close connection to Wsdl2EditorService
            edClient.destroy();
        
            System.out.println("Generated genEd ID: " + genEdAnalysisIdentifier);
        
            System.out.println("Generated ed ID: " + edAnalysisIdentifier);
        
            //Connect to Wsdl2ValidatorService
            ClientConfig config3 = new com.sun.jersey.api.client.config.DefaultClientConfig();
            Client valClient = Client.create(config3);
            WebResource valResource = valClient.resource(validatorServiceURL);
        
            valAnalysisIdentifier = valResource.path("generateID").accept(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
        
            //Close connection to Wsdl2ValidatorService
            valClient.destroy();
        
            System.out.println("Generated val ID: " + valAnalysisIdentifier);
            
            initComponents();
        
            //Centralizes frame
            setLocationRelativeTo(null);
            
            //Make MainApp frame visible
            setVisible(true);
            
        } catch (ClientHandlerException | UniformInterfaceException|IOException ex) {
            System.out.println("Unable to access Wsdl2CreatorService/Wsdl2EditorService/Wsdl2ValidatorService or Create temporary directory");
            
            //Show error message to user
            JOptionPane.showMessageDialog(rootPane, "Error to load application \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
        
            //Destroy MainApp frame
            dispose();
        }   
    }
    
    public static void delete(File file){
        if(file.isDirectory()){
                // Get all files in the folder
                File[] files=file.listFiles();

                for(int i=0;i<files.length;i++){
                    // Delete each file in the folder
                    delete(files[i]);
                }

                // Delete the folder
                file.delete();
        }
        else { 
            // Delete the file if it is not a folder 
            file.delete(); 
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        genFileChooser = new javax.swing.JFileChooser();
        valFileChooser = new javax.swing.JFileChooser();
        genResultFileChooser = new javax.swing.JFileChooser();
        edFileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        genPanel = new javax.swing.JPanel();
        genUploadPanel = new javax.swing.JPanel();
        genPleaseSelectFileLabel = new javax.swing.JLabel();
        genSelectFileButton = new javax.swing.JButton();
        genSelectedFileTextField = new javax.swing.JTextField();
        genPleaseSelectServiceLabel = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        genFileTree = new javax.swing.JTree();
        genPerformPanel = new javax.swing.JPanel();
        genTargetNamespaceTextField = new javax.swing.JTextField();
        genTargetNamespaceLabel = new javax.swing.JLabel();
        generateWSDLFileButton = new javax.swing.JButton();
        genHelpTargetNamespaceLabel = new javax.swing.JLabel();
        genServiceAddressLabel = new javax.swing.JLabel();
        genHelpServiceAddressLabel = new javax.swing.JLabel();
        genServiceAddressTextField = new javax.swing.JTextField();
        genProgressBar = new javax.swing.JProgressBar();
        generatingWsdlLabel = new javax.swing.JLabel();
        genAttributeFormDefaultLabel = new javax.swing.JLabel();
        genAttributeFormDefaultCheckBox = new javax.swing.JCheckBox();
        genElementFormDefaultLabel = new javax.swing.JLabel();
        genElementFormDefaultCheckBox = new javax.swing.JCheckBox();
        genHelpAttributeFormDefaultLabel = new javax.swing.JLabel();
        genHelpElementFormDefaultLabel = new javax.swing.JLabel();
        genXMLTargetNamespaceLabel = new javax.swing.JLabel();
        genHelpXMLTargetNamespaceLabel = new javax.swing.JLabel();
        genHelpTargetNamespacePrefixLabel = new javax.swing.JLabel();
        genTargetNamespacePrefixLabel = new javax.swing.JLabel();
        genTargetNamespacePrefixTextField = new javax.swing.JTextField();
        genHelpXMLTargetNamespacePrefixLabel = new javax.swing.JLabel();
        genXMLTargetNamespacePrefixLabel = new javax.swing.JLabel();
        genXMLTargetNamespaceTextField = new javax.swing.JTextField();
        genXMLTargetNamespacePrefixTextField = new javax.swing.JTextField();
        invalidServiceAddressLabel = new javax.swing.JLabel();
        genAnalysisFailedLabel = new javax.swing.JLabel();
        genResultPanel = new javax.swing.JPanel();
        genResultFileNameTextField = new javax.swing.JTextField();
        genResultFileLabel = new javax.swing.JLabel();
        genSaveFileButton = new javax.swing.JButton();
        genRemoveSoapBindingsButton = new javax.swing.JButton();
        genEditedResultTextField = new javax.swing.JTextField();
        genEditedResultFileButton = new javax.swing.JButton();
        genEditingProgressBar = new javax.swing.JProgressBar();
        genEditingLabel = new javax.swing.JLabel();
        genEdAnalysisFailedLabel = new javax.swing.JLabel();
        edPanel = new javax.swing.JPanel();
        edUploadPanel = new javax.swing.JPanel();
        edSelecFileLabel = new javax.swing.JLabel();
        edSelectFileButton = new javax.swing.JButton();
        edSelectedFileTextField = new javax.swing.JTextField();
        edRemoveSoapBindingsButton = new javax.swing.JButton();
        editingLabel = new javax.swing.JLabel();
        edProgressBar = new javax.swing.JProgressBar();
        edAnalysisFailedLabel = new javax.swing.JLabel();
        edResultPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        edResultTextArea = new javax.swing.JTextArea();
        edSaveFileButton = new javax.swing.JButton();
        edResultLabel = new javax.swing.JLabel();
        valPanel = new javax.swing.JPanel();
        valUploadPanel = new javax.swing.JPanel();
        valPleaseSelectFileLabel = new javax.swing.JLabel();
        valSelectFileButton = new javax.swing.JButton();
        valSelectedFileTextField = new javax.swing.JTextField();
        valPerformPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        valResultTextArea = new javax.swing.JTextArea();
        validateButton = new javax.swing.JButton();
        valProgressBar = new javax.swing.JProgressBar();
        validatingDocumentLabel = new javax.swing.JLabel();

        genFileChooser.setAcceptAllFileFilterUsed(false);
        genFileChooser.setDialogTitle("Open");
        genFileChooser.setFileFilter(new WarFilter());

        valFileChooser.setAcceptAllFileFilterUsed(false);
        valFileChooser.setDialogTitle("Open");
        valFileChooser.setFileFilter(new WsdlFilter());

        genResultFileChooser.setAcceptAllFileFilterUsed(false);
        genResultFileChooser.setDialogTitle("Save as");
        genResultFileChooser.setFileFilter(new WsdlFilter());

        edFileChooser.setAcceptAllFileFilterUsed(false);
        edFileChooser.setDialogTitle("Open");
        edFileChooser.setFileFilter(new WsdlFilter());

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Wsdl2Creator");

        genUploadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Upload WAR File"));

        genPleaseSelectFileLabel.setText("Please, select a .war file:");

        genSelectFileButton.setText("Select File");
        genSelectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSelectFileButtonActionPerformed(evt);
            }
        });

        genSelectedFileTextField.setEditable(false);
        genSelectedFileTextField.setText("No file selected");

        genPleaseSelectServiceLabel.setText("Please, select service file:");

        genFileTree.setModel(null);
        genFileTree.setEnabled(false);
        genFileTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                genFileTreeValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(genFileTree);

        javax.swing.GroupLayout genUploadPanelLayout = new javax.swing.GroupLayout(genUploadPanel);
        genUploadPanel.setLayout(genUploadPanelLayout);
        genUploadPanelLayout.setHorizontalGroup(
            genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genUploadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genPleaseSelectFileLabel)
                    .addGroup(genUploadPanelLayout.createSequentialGroup()
                        .addComponent(genSelectFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genSelectedFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(genPleaseSelectServiceLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        genUploadPanelLayout.setVerticalGroup(
            genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genUploadPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genPleaseSelectFileLabel)
                    .addComponent(genPleaseSelectServiceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(genUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(genSelectedFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(genSelectFileButton))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        genPerformPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Generate WSDL File"));

        genTargetNamespaceTextField.setEnabled(false);
        genTargetNamespaceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                genTargetNamespaceTextFieldKeyReleased(evt);
            }
        });

        genTargetNamespaceLabel.setText("Target Namespace (required):");
        genTargetNamespaceLabel.setEnabled(false);

        generateWSDLFileButton.setText("Generate WSDL");
        generateWSDLFileButton.setEnabled(false);
        generateWSDLFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateWSDLFileButtonActionPerformed(evt);
            }
        });

        genHelpTargetNamespaceLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpTargetNamespaceLabel.setEnabled(false);
        genHelpTargetNamespaceLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpTargetNamespaceLabelMouseClicked(evt);
            }
        });
        genHelpTargetNamespaceLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpTargetNamespaceLabelMouseMoved(evt);
            }
        });

        genServiceAddressLabel.setText("Service Address (required):");
        genServiceAddressLabel.setEnabled(false);

        genHelpServiceAddressLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpServiceAddressLabel.setEnabled(false);
        genHelpServiceAddressLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpServiceAddressLabelMouseClicked(evt);
            }
        });
        genHelpServiceAddressLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpServiceAddressLabelMouseMoved(evt);
            }
        });

        genServiceAddressTextField.setEnabled(false);
        genServiceAddressTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                genServiceAddressTextFieldKeyReleased(evt);
            }
        });

        genProgressBar.setIndeterminate(true);
        genProgressBar.setVisible(false);

        generatingWsdlLabel.setText("Generating WSDL ...");
        generatingWsdlLabel.setVisible(false);

        genAttributeFormDefaultLabel.setText("Attribute Form Default (optional):");
        genAttributeFormDefaultLabel.setEnabled(false);

        genAttributeFormDefaultCheckBox.setText("unqualified");
        genAttributeFormDefaultCheckBox.setEnabled(false);
        genAttributeFormDefaultCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genAttributeFormDefaultCheckBoxActionPerformed(evt);
            }
        });

        genElementFormDefaultLabel.setText("Element Form Default (optional):");
        genElementFormDefaultLabel.setEnabled(false);

        genElementFormDefaultCheckBox.setText("unqualified");
        genElementFormDefaultCheckBox.setEnabled(false);
        genElementFormDefaultCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genElementFormDefaultCheckBoxActionPerformed(evt);
            }
        });

        genHelpAttributeFormDefaultLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpAttributeFormDefaultLabel.setEnabled(false);
        genHelpAttributeFormDefaultLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpAttributeFormDefaultLabelMouseClicked(evt);
            }
        });
        genHelpAttributeFormDefaultLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpAttributeFormDefaultLabelMouseMoved(evt);
            }
        });

        genHelpElementFormDefaultLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpElementFormDefaultLabel.setEnabled(false);
        genHelpElementFormDefaultLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpElementFormDefaultLabelMouseClicked(evt);
            }
        });
        genHelpElementFormDefaultLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpElementFormDefaultLabelMouseMoved(evt);
            }
        });

        genXMLTargetNamespaceLabel.setText("XML Schema Target Namespace (optional):");
        genXMLTargetNamespaceLabel.setEnabled(false);

        genHelpXMLTargetNamespaceLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpXMLTargetNamespaceLabel.setEnabled(false);
        genHelpXMLTargetNamespaceLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpXMLTargetNamespaceLabelMouseClicked(evt);
            }
        });
        genHelpXMLTargetNamespaceLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpXMLTargetNamespaceLabelMouseMoved(evt);
            }
        });

        genHelpTargetNamespacePrefixLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpTargetNamespacePrefixLabel.setEnabled(false);
        genHelpTargetNamespacePrefixLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpTargetNamespacePrefixLabelMouseClicked(evt);
            }
        });
        genHelpTargetNamespacePrefixLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpTargetNamespacePrefixLabelMouseMoved(evt);
            }
        });

        genTargetNamespacePrefixLabel.setText("Target Namespace Prefix (optional):");
        genTargetNamespacePrefixLabel.setEnabled(false);

        genTargetNamespacePrefixTextField.setEnabled(false);
        genTargetNamespacePrefixTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                genTargetNamespacePrefixTextFieldKeyReleased(evt);
            }
        });

        genHelpXMLTargetNamespacePrefixLabel.setText("<html>[<span style=\"color:blue\"><u>help</u></span>]</html>");
        genHelpXMLTargetNamespacePrefixLabel.setEnabled(false);
        genHelpXMLTargetNamespacePrefixLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                genHelpXMLTargetNamespacePrefixLabelMouseClicked(evt);
            }
        });
        genHelpXMLTargetNamespacePrefixLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                genHelpXMLTargetNamespacePrefixLabelMouseMoved(evt);
            }
        });

        genXMLTargetNamespacePrefixLabel.setText("XML Schema Target Namespace Prefix (optional):");
        genXMLTargetNamespacePrefixLabel.setEnabled(false);

        genXMLTargetNamespaceTextField.setEnabled(false);
        genXMLTargetNamespaceTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                genXMLTargetNamespaceTextFieldKeyReleased(evt);
            }
        });

        genXMLTargetNamespacePrefixTextField.setEnabled(false);
        genXMLTargetNamespacePrefixTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                genXMLTargetNamespacePrefixTextFieldKeyReleased(evt);
            }
        });

        invalidServiceAddressLabel.setText("<html><span style=\"color:red\">* invalid URI</span></html>");
        invalidServiceAddressLabel.setVisible(false);

        genAnalysisFailedLabel.setText("<html><span style=\"color:red\">Generation Failed!</span></html>");
        genAnalysisFailedLabel.setVisible(false);

        javax.swing.GroupLayout genPerformPanelLayout = new javax.swing.GroupLayout(genPerformPanel);
        genPerformPanel.setLayout(genPerformPanelLayout);
        genPerformPanelLayout.setHorizontalGroup(
            genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genPerformPanelLayout.createSequentialGroup()
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(genPerformPanelLayout.createSequentialGroup()
                        .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(genPerformPanelLayout.createSequentialGroup()
                                    .addGap(73, 73, 73)
                                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(genHelpAttributeFormDefaultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(genHelpElementFormDefaultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(genAttributeFormDefaultLabel)
                                        .addComponent(genElementFormDefaultLabel))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(genAttributeFormDefaultCheckBox)
                                        .addComponent(genElementFormDefaultCheckBox)))
                                .addGroup(genPerformPanelLayout.createSequentialGroup()
                                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, genPerformPanelLayout.createSequentialGroup()
                                            .addComponent(genHelpXMLTargetNamespaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(genXMLTargetNamespaceLabel))
                                        .addGroup(genPerformPanelLayout.createSequentialGroup()
                                            .addComponent(genHelpXMLTargetNamespacePrefixLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(genXMLTargetNamespacePrefixLabel)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(genXMLTargetNamespaceTextField)
                                        .addComponent(genXMLTargetNamespacePrefixTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(genPerformPanelLayout.createSequentialGroup()
                                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(genPerformPanelLayout.createSequentialGroup()
                                        .addComponent(genHelpTargetNamespacePrefixLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(genTargetNamespacePrefixLabel))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(genPerformPanelLayout.createSequentialGroup()
                                            .addComponent(genHelpServiceAddressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(genServiceAddressLabel))
                                        .addGroup(genPerformPanelLayout.createSequentialGroup()
                                            .addComponent(genHelpTargetNamespaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(genTargetNamespaceLabel))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(genTargetNamespaceTextField)
                                    .addComponent(genTargetNamespacePrefixTextField)
                                    .addComponent(genServiceAddressTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 308, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(invalidServiceAddressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(genPerformPanelLayout.createSequentialGroup()
                        .addGap(74, 74, 74)
                        .addComponent(genAnalysisFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(generateWSDLFileButton)
                        .addGap(18, 18, 18)
                        .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(generatingWsdlLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(genProgressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        genPerformPanelLayout.setVerticalGroup(
            genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genPerformPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(genServiceAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(invalidServiceAddressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(genHelpServiceAddressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(genServiceAddressLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genTargetNamespaceLabel)
                    .addComponent(genTargetNamespaceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genHelpTargetNamespaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genHelpTargetNamespacePrefixLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genTargetNamespacePrefixLabel)
                    .addComponent(genTargetNamespacePrefixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genHelpXMLTargetNamespaceLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genXMLTargetNamespaceLabel)
                    .addComponent(genXMLTargetNamespaceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genHelpXMLTargetNamespacePrefixLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genXMLTargetNamespacePrefixLabel)
                    .addComponent(genXMLTargetNamespacePrefixTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genHelpAttributeFormDefaultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genAttributeFormDefaultLabel)
                    .addComponent(genAttributeFormDefaultCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genHelpElementFormDefaultLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genElementFormDefaultCheckBox)
                    .addComponent(genElementFormDefaultLabel))
                .addGap(18, 18, 18)
                .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(genPerformPanelLayout.createSequentialGroup()
                        .addComponent(generatingWsdlLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(genPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(generateWSDLFileButton)
                        .addComponent(genAnalysisFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        genResultPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Result"));

        genResultFileNameTextField.setEditable(false);
        genResultFileNameTextField.setEnabled(false);

        genResultFileLabel.setText("WSDL file:");
        genResultFileLabel.setEnabled(false);

        genSaveFileButton.setText("Save As");
        genSaveFileButton.setEnabled(false);
        genSaveFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSaveFileButtonActionPerformed(evt);
            }
        });

        genRemoveSoapBindingsButton.setText("Remove SOAP Bindings >>");
        genRemoveSoapBindingsButton.setEnabled(false);
        genRemoveSoapBindingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genRemoveSoapBindingsButtonActionPerformed(evt);
            }
        });

        genEditedResultTextField.setEditable(false);
        genEditedResultTextField.setEnabled(false);

        genEditedResultFileButton.setText("Save As");
        genEditedResultFileButton.setEnabled(false);
        genEditedResultFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genEditedResultFileButtonActionPerformed(evt);
            }
        });

        genEditingProgressBar.setIndeterminate(true);
        genEditingProgressBar.setVisible(false);

        genEditingLabel.setText("Editing WSDL ...");
        genEditingLabel.setVisible(false);

        genEdAnalysisFailedLabel.setText("<html><span style=\"color:red\">Edition Failed!</span></html>");
        genEdAnalysisFailedLabel.setVisible(false);

        javax.swing.GroupLayout genResultPanelLayout = new javax.swing.GroupLayout(genResultPanel);
        genResultPanel.setLayout(genResultPanelLayout);
        genResultPanelLayout.setHorizontalGroup(
            genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genResultPanelLayout.createSequentialGroup()
                .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(genResultPanelLayout.createSequentialGroup()
                        .addComponent(genResultFileLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genResultFileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(genResultPanelLayout.createSequentialGroup()
                        .addGap(133, 133, 133)
                        .addComponent(genSaveFileButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(genResultPanelLayout.createSequentialGroup()
                        .addComponent(genRemoveSoapBindingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, genResultPanelLayout.createSequentialGroup()
                        .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(genEditingLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(genEditingProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)))
                .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(genEditedResultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(genResultPanelLayout.createSequentialGroup()
                        .addComponent(genEdAnalysisFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(genEditedResultFileButton)
                        .addGap(79, 79, 79)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        genResultPanelLayout.setVerticalGroup(
            genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genResultFileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(genResultFileLabel)
                    .addComponent(genRemoveSoapBindingsButton)
                    .addComponent(genEditedResultTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(genResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(genSaveFileButton)
                        .addComponent(genEditedResultFileButton)
                        .addComponent(genEdAnalysisFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(genResultPanelLayout.createSequentialGroup()
                        .addComponent(genEditingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(genEditingProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout genPanelLayout = new javax.swing.GroupLayout(genPanel);
        genPanel.setLayout(genPanelLayout);
        genPanelLayout.setHorizontalGroup(
            genPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(genPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(genUploadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(genPerformPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(genResultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        genPanelLayout.setVerticalGroup(
            genPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(genPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(genUploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genPerformPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genResultPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("WSDL Generation", genPanel);

        edUploadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Upload WSDL File"));

        edSelecFileLabel.setText("Please, select a WSDL file:");

        edSelectFileButton.setText("Select File");
        edSelectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edSelectFileButtonActionPerformed(evt);
            }
        });

        edSelectedFileTextField.setEditable(false);
        edSelectedFileTextField.setText("No file selected");

        edRemoveSoapBindingsButton.setText("Remove SOAP Bindings");
        edRemoveSoapBindingsButton.setEnabled(false);
        edRemoveSoapBindingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edRemoveSoapBindingsButtonActionPerformed(evt);
            }
        });

        editingLabel.setText("Editing WSDL ...");
        editingLabel.setVisible(false);

        edProgressBar.setIndeterminate(true);
        edProgressBar.setVisible(false);

        edAnalysisFailedLabel.setText("<html><span style=\"color:red\">Edition Failed!</span></html>");
        edAnalysisFailedLabel.setVisible(false);

        javax.swing.GroupLayout edUploadPanelLayout = new javax.swing.GroupLayout(edUploadPanel);
        edUploadPanel.setLayout(edUploadPanelLayout);
        edUploadPanelLayout.setHorizontalGroup(
            edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edUploadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edSelecFileLabel)
                    .addGroup(edUploadPanelLayout.createSequentialGroup()
                        .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(edSelectFileButton)
                            .addGroup(edUploadPanelLayout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(edAnalysisFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(edUploadPanelLayout.createSequentialGroup()
                                .addComponent(edRemoveSoapBindingsButton)
                                .addGap(18, 18, 18)
                                .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(editingLabel)
                                    .addComponent(edProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(edSelectedFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(273, Short.MAX_VALUE))
        );
        edUploadPanelLayout.setVerticalGroup(
            edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edUploadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(edSelecFileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edSelectFileButton)
                    .addComponent(edSelectedFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(edUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(edRemoveSoapBindingsButton)
                        .addComponent(edAnalysisFailedLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(edUploadPanelLayout.createSequentialGroup()
                        .addComponent(editingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(edProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        edResultPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("WSDL Edition"));

        edResultTextArea.setColumns(20);
        edResultTextArea.setEditable(false);
        edResultTextArea.setRows(5);
        edResultTextArea.setEnabled(false);
        jScrollPane3.setViewportView(edResultTextArea);

        edSaveFileButton.setText("Save as");
        edSaveFileButton.setEnabled(false);
        edSaveFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edSaveFileButtonActionPerformed(evt);
            }
        });

        edResultLabel.setText("Edited WSDL File:");
        edResultLabel.setEnabled(false);

        javax.swing.GroupLayout edResultPanelLayout = new javax.swing.GroupLayout(edResultPanel);
        edResultPanel.setLayout(edResultPanelLayout);
        edResultPanelLayout.setHorizontalGroup(
            edResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edResultPanelLayout.createSequentialGroup()
                .addGroup(edResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(edResultPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE))
                    .addGroup(edResultPanelLayout.createSequentialGroup()
                        .addGroup(edResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(edResultPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(edResultLabel))
                            .addGroup(edResultPanelLayout.createSequentialGroup()
                                .addGap(317, 317, 317)
                                .addComponent(edSaveFileButton)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        edResultPanelLayout.setVerticalGroup(
            edResultPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edResultPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(edResultLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(edSaveFileButton)
                .addContainerGap())
        );

        javax.swing.GroupLayout edPanelLayout = new javax.swing.GroupLayout(edPanel);
        edPanel.setLayout(edPanelLayout);
        edPanelLayout.setHorizontalGroup(
            edPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(edPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(edUploadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(edResultPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        edPanelLayout.setVerticalGroup(
            edPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(edPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(edUploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edResultPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("WSDL Edition", edPanel);

        valUploadPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Upload WSDL File"));

        valPleaseSelectFileLabel.setText("Please, select a WSDL file:");

        valSelectFileButton.setText("Select File");
        valSelectFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valSelectFileButtonActionPerformed(evt);
            }
        });

        valSelectedFileTextField.setEditable(false);
        valSelectedFileTextField.setText("No file selected");

        javax.swing.GroupLayout valUploadPanelLayout = new javax.swing.GroupLayout(valUploadPanel);
        valUploadPanel.setLayout(valUploadPanelLayout);
        valUploadPanelLayout.setHorizontalGroup(
            valUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(valUploadPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(valUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valPleaseSelectFileLabel)
                    .addGroup(valUploadPanelLayout.createSequentialGroup()
                        .addComponent(valSelectFileButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(valSelectedFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(317, Short.MAX_VALUE))
        );
        valUploadPanelLayout.setVerticalGroup(
            valUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(valUploadPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(valPleaseSelectFileLabel)
                .addGap(18, 18, 18)
                .addGroup(valUploadPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(valSelectFileButton)
                    .addComponent(valSelectedFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        valPerformPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("WSDL Validation"));

        valResultTextArea.setColumns(20);
        valResultTextArea.setEditable(false);
        valResultTextArea.setLineWrap(true);
        valResultTextArea.setRows(5);
        valResultTextArea.setWrapStyleWord(true);
        valResultTextArea.setEnabled(false);
        jScrollPane2.setViewportView(valResultTextArea);

        validateButton.setText("Validate ");
        validateButton.setEnabled(false);
        validateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateButtonActionPerformed(evt);
            }
        });

        valProgressBar.setIndeterminate(true);
        valProgressBar.setVisible(false);

        validatingDocumentLabel.setText("Validating document...");
        validatingDocumentLabel.setVisible(false);

        javax.swing.GroupLayout valPerformPanelLayout = new javax.swing.GroupLayout(valPerformPanel);
        valPerformPanel.setLayout(valPerformPanelLayout);
        valPerformPanelLayout.setHorizontalGroup(
            valPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(valPerformPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, valPerformPanelLayout.createSequentialGroup()
                .addGap(0, 299, Short.MAX_VALUE)
                .addGroup(valPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(validatingDocumentLabel)
                    .addComponent(valProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(297, 297, 297))
            .addGroup(valPerformPanelLayout.createSequentialGroup()
                .addGap(314, 314, 314)
                .addComponent(validateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        valPerformPanelLayout.setVerticalGroup(
            valPerformPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, valPerformPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validateButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(validatingDocumentLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(valProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout valPanelLayout = new javax.swing.GroupLayout(valPanel);
        valPanel.setLayout(valPanelLayout);
        valPanelLayout.setHorizontalGroup(
            valPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(valPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(valPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valPerformPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(valUploadPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        valPanelLayout.setVerticalGroup(
            valPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(valPanelLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(valUploadPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(valPerformPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(54, 54, 54))
        );

        jTabbedPane1.addTab("WSDL Validation", valPanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //When user press genSelectFileButton
    private void genSelectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSelectFileButtonActionPerformed

        //Open file chooser
        int returnVal = genFileChooser.showOpenDialog(this);
        
        //If user selects a file
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            //Get file selected by user
            genSelectedWarFile = genFileChooser.getSelectedFile();

            //Set text field with selected filename
            genSelectedFileTextField.setText(genSelectedWarFile.getName());
            
            //Delete previous stored files in temporary directory
            for (File old : temporaryDirectory.toFile().listFiles()){
                old.delete();
            }
            delete(new File(temporaryDirectory.toFile().getAbsolutePath().concat("/WEB-INF")));
            delete(new File(temporaryDirectory.toFile().getAbsolutePath().concat("/META-INF")));
            
            //Unzip WAR file into temporary directory
            try {
                ZipFile zip = new ZipFile(genSelectedWarFile);
                zip.extractAll(temporaryDirectory.toFile().getAbsolutePath());  
                
                //Enable genFileTree
                genFileTree.setEnabled(true);
            
                //Set genFileTree
                genFileTree.setModel(new FileSystemModel(new File(temporaryDirectory.toFile().getAbsolutePath().concat("/WEB-INF/classes"))));   
            
                //Disable all components of genPerformPanel, except generatingWsdlLabel and genProgressBar
                Component[] genPerformComponents = genPerformPanel.getComponents();
                for (int i = 0; i < genPerformComponents.length; i++)
                    genPerformComponents[i].setEnabled(false);
                generatingWsdlLabel.setEnabled(true);
                genProgressBar.setEnabled(true);
                
                //Make invalidServiceAddressLabel invisible
                invalidServiceAddressLabel.setVisible(false);
                
                //Make genAnalysisFailedLabel invisible
                genAnalysisFailedLabel.setVisible(false);
                
                //Clear all components of genPerformPanel
                genServiceAddressTextField.setText("");
                genTargetNamespaceTextField.setText("");
                genTargetNamespacePrefixTextField.setText("");
                genXMLTargetNamespaceTextField.setText("");
                genXMLTargetNamespacePrefixTextField.setText("");
                genAttributeFormDefaultCheckBox.setSelected(false);
                genElementFormDefaultCheckBox.setSelected(false);
                
                //Clear all variables
                genServiceAddress = "";
                genTargetNamespace = "";
                genTargetNamespacePrefix = "";
                genXMLTargetNamespace = "";
                genXMLTargetNamespacePrefix = "";
                genAttributeFormDefault = "qualified";
                genElementFormDefault = "qualified"; 
                
                //Disable all components of genResultPanel, except genEditingLabel and genEditingProgressBar
                Component[] genResultComponents = genResultPanel.getComponents();
                for (int i = 0; i < genResultComponents.length; i++)
                    genResultComponents[i].setEnabled(false);
                genEditingLabel.setEnabled(true);
                genEditingProgressBar.setEnabled(true);
                
                //Clear genResultFileNameTextField and genEditedResultTextField
                genResultFileNameTextField.setText("");                
                genEditedResultTextField.setText("");
                
            } catch (ZipException ex) {
            
                System.out.println("Error to unzip file");

                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to load file", "Error", JOptionPane.ERROR_MESSAGE);
            } 
        }
    }//GEN-LAST:event_genSelectFileButtonActionPerformed

    //When user select some file/directory from genFileTree
    private void genFileTreeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_genFileTreeValueChanged

        if(genSemaphore.tryAcquire()){
            //If some item from warFileTree was selected by user
            if(genFileTree.getSelectionPath() != null){
            
                //Read selected service filename
                genSelectedService = genFileTree.getSelectionPath().toString().replaceAll("[\\[\\]]", "").replace(", ", "/");
                genSelectedService = genSelectedService.replace("\\" , "/");
                
                //Create new file
                File serviceFile = new File(genSelectedService);
        
                //If selected file is directory
                if(serviceFile.isDirectory()){
                
                    //Disable all components of genPerformPanel, except generatingWsdlLabel and genProgressBar
                    Component[] genPerformComponents = genPerformPanel.getComponents();
                    for (int i = 0; i < genPerformComponents.length; i++)
                        genPerformComponents[i].setEnabled(false);
                    generatingWsdlLabel.setEnabled(true);
                    genProgressBar.setEnabled(true);

                }//Else, if selected file is a file
                else if(serviceFile.isFile()){
                
                    //Enable all components of genPerformPanel
                    Component[] genPerformComponents = genPerformPanel.getComponents();
                    for (int i = 0; i < genPerformComponents.length; i++)
                        genPerformComponents[i].setEnabled(true);
                    
                    //Enable/Disable generateWSDLFileButton
                    if(!genServiceAddress.equals("") && !genTargetNamespace.equals(""))
                        generateWSDLFileButton.setEnabled(true);
                    else
                        generateWSDLFileButton.setEnabled(false);                
                }
                
                //Modify genSelectedService to send to service
                String[] parts = genSelectedService.split("WEB-INF");
                genSelectedService = "WEB-INF".concat(parts[1]);
            
                System.out.println("Selected service filename (verificar no linux): " + genSelectedService);
            }
            
            //Release semaphore
            genSemaphore.release();
        }
    }//GEN-LAST:event_genFileTreeValueChanged

    //When user type something in genServiceAddressTextField
    private void genServiceAddressTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_genServiceAddressTextFieldKeyReleased

        //Make invalidServiceAddressLabel invisible
        invalidServiceAddressLabel.setVisible(false);
        
        //Get service address typed by user
        genServiceAddress = genServiceAddressTextField.getText().trim();
        
        //Enable/Disable generateWSDLFileButton
        if(!genServiceAddress.equals("") && !genTargetNamespace.equals(""))
            generateWSDLFileButton.setEnabled(true);
        else
            generateWSDLFileButton.setEnabled(false);
    }//GEN-LAST:event_genServiceAddressTextFieldKeyReleased

    //When user type something in genTargetNamespaceTextField
    private void genTargetNamespaceTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_genTargetNamespaceTextFieldKeyReleased
        //Get target namespace typed by user
        genTargetNamespace = genTargetNamespaceTextField.getText().trim();
        
        //Enable/Disable generateWSDLFileButton
        if(!genServiceAddress.equals("") && !genTargetNamespace.equals(""))
            generateWSDLFileButton.setEnabled(true);
        else
            generateWSDLFileButton.setEnabled(false);
    }//GEN-LAST:event_genTargetNamespaceTextFieldKeyReleased

    //When user type something in genTargetNamespacePrefixTextField
    private void genTargetNamespacePrefixTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_genTargetNamespacePrefixTextFieldKeyReleased
        //Get target namespace prefix typed by user
        genTargetNamespacePrefix = genTargetNamespacePrefixTextField.getText().trim();
    }//GEN-LAST:event_genTargetNamespacePrefixTextFieldKeyReleased

    //When user type something in genXMLTargetNamespaceTextField
    private void genXMLTargetNamespaceTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_genXMLTargetNamespaceTextFieldKeyReleased
        //Get XML target namespace typed by user
        genXMLTargetNamespace = genXMLTargetNamespaceTextField.getText().trim();
    }//GEN-LAST:event_genXMLTargetNamespaceTextFieldKeyReleased

    //When user type something in genXMLTargetNamespacePrefixTextField
    private void genXMLTargetNamespacePrefixTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_genXMLTargetNamespacePrefixTextFieldKeyReleased
        //Get XML target namespace prefix typed by user
        genXMLTargetNamespacePrefix = genXMLTargetNamespacePrefixTextField.getText().trim();
    }//GEN-LAST:event_genXMLTargetNamespacePrefixTextFieldKeyReleased

    //When user click genAttributeFormDefaultCheckBox
    private void genAttributeFormDefaultCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genAttributeFormDefaultCheckBoxActionPerformed
        if(genAttributeFormDefaultCheckBox.isSelected())
            genAttributeFormDefault = "unqualified";
        else
            genAttributeFormDefault = "qualified";
    }//GEN-LAST:event_genAttributeFormDefaultCheckBoxActionPerformed

    //When user click genElementFormDefaultCheckBox
    private void genElementFormDefaultCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genElementFormDefaultCheckBoxActionPerformed
        if(genElementFormDefaultCheckBox.isSelected())
            genElementFormDefault = "unqualified";
        else
            genElementFormDefault = "qualified";
    }//GEN-LAST:event_genElementFormDefaultCheckBoxActionPerformed

    //When user positions the mouse over genHelpServiceAddressLabel, default cursor is changed to hand cursor
    private void genHelpServiceAddressLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpServiceAddressLabelMouseMoved
        //Set cursor of genHelpServiceAddressLabel to hand cursor
        genHelpServiceAddressLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpServiceAddressLabelMouseMoved

    //When user positions the mouse over genHelpTargetNamespaceLabel, default cursor is changed to hand cursor
    private void genHelpTargetNamespaceLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpTargetNamespaceLabelMouseMoved
        //Set cursor of genHelpTargetNamespaceLabel to hand cursor
        genHelpTargetNamespaceLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpTargetNamespaceLabelMouseMoved

    //When user positions the mouse over genHelpTargetNamespacePrefixLabel, default cursor is changed to hand cursor
    private void genHelpTargetNamespacePrefixLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpTargetNamespacePrefixLabelMouseMoved
        //Set cursor of genHelpTargetNamespacePrefixLabel to hand cursor
        genHelpTargetNamespacePrefixLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpTargetNamespacePrefixLabelMouseMoved

    //When user positions the mouse over genHelpXMLTargetNamespaceLabel, default cursor is changed to hand cursor
    private void genHelpXMLTargetNamespaceLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpXMLTargetNamespaceLabelMouseMoved
        //Set cursor of genHelpXMLTargetNamespaceLabel to hand cursor
        genHelpXMLTargetNamespaceLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpXMLTargetNamespaceLabelMouseMoved

    //When user positions the mouse over genHelpXMLTargetNamespacePrefixLabel, default cursor is changed to hand cursor
    private void genHelpXMLTargetNamespacePrefixLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpXMLTargetNamespacePrefixLabelMouseMoved
        //Set cursor of genHelpXMLTargetNamespacePrefixLabel to hand cursor
        genHelpXMLTargetNamespacePrefixLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpXMLTargetNamespacePrefixLabelMouseMoved

    //When user positions the mouse over genHelpAttributeFormDefaultLabel, default cursor is changed to hand cursor
    private void genHelpAttributeFormDefaultLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpAttributeFormDefaultLabelMouseMoved
        //Set cursor of genHelpAttributeFormDefaultLabel to hand cursor
        genHelpAttributeFormDefaultLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpAttributeFormDefaultLabelMouseMoved

    //When user positions the mouse over genHelpElementFormDefaultLabel, default cursor is changed to hand cursor
    private void genHelpElementFormDefaultLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpElementFormDefaultLabelMouseMoved
        //Set cursor of genHelpElementFormDefaultLabel to hand cursor
        genHelpElementFormDefaultLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_genHelpElementFormDefaultLabelMouseMoved

    //When user press genHelpServiceAddressLabel
    private void genHelpServiceAddressLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpServiceAddressLabelMouseClicked
        Help help = new Help("serviceAddress");
    }//GEN-LAST:event_genHelpServiceAddressLabelMouseClicked

    //When user press genHelpTargetNamespaceLabel
    private void genHelpTargetNamespaceLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpTargetNamespaceLabelMouseClicked
        Help help = new Help("targetNamespace");
    }//GEN-LAST:event_genHelpTargetNamespaceLabelMouseClicked

    //When user press genHelpTargetNamespacePrefixLabel
    private void genHelpTargetNamespacePrefixLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpTargetNamespacePrefixLabelMouseClicked
        Help help = new Help("targetNamespacePrefix");
    }//GEN-LAST:event_genHelpTargetNamespacePrefixLabelMouseClicked

    //When user press genHelpXMLTargetNamespaceLabel
    private void genHelpXMLTargetNamespaceLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpXMLTargetNamespaceLabelMouseClicked
        Help help = new Help("XMLTargetNamespace");
    }//GEN-LAST:event_genHelpXMLTargetNamespaceLabelMouseClicked

    //When user press genHelpXMLTargetNamespacePrefixLabel
    private void genHelpXMLTargetNamespacePrefixLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpXMLTargetNamespacePrefixLabelMouseClicked
        Help help = new Help("XMLTargetNamespacePrefix");
    }//GEN-LAST:event_genHelpXMLTargetNamespacePrefixLabelMouseClicked

    //When user press genHelpAttributeFormDefaultLabel
    private void genHelpAttributeFormDefaultLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpAttributeFormDefaultLabelMouseClicked
        Help help = new Help("attributeFormDefault");
    }//GEN-LAST:event_genHelpAttributeFormDefaultLabelMouseClicked

    //When user press genHelpElementFormDefaultLabel
    private void genHelpElementFormDefaultLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_genHelpElementFormDefaultLabelMouseClicked
        Help help = new Help("elementFormDefault");
    }//GEN-LAST:event_genHelpElementFormDefaultLabelMouseClicked

    //When user press generateWSDLFileButton
    private void generateWSDLFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateWSDLFileButtonActionPerformed
        //Prevents user to start more than one analysis
        if(genSemaphore.tryAcquire()){
            try{
                
                //Connect to Wsdl2CreatorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client genClient = Client.create(config);
                WebResource genResource = genClient.resource(creatorServiceURL);

                //Set user-selected parameters
                Wsdl2Parameters parameters = new Wsdl2Parameters();
                parameters.setClassPath(genSelectedService);
                
                //Set result filename
                String[] parts = genSelectedService.split("/");
                genResultFilename = parts[parts.length-1].replace(".class", ".wsdl");
                
                //Verify if the provided service address is valid
                try {
                    URL url = new URL(genServiceAddress);
                    URI uri = new URI(genServiceAddress);
                    
                    parameters.setServiceAddress(genServiceAddress);
                    parameters.setTargetNamespace(genTargetNamespace);
                
                    if(!genTargetNamespacePrefix.equals(""))
                        parameters.setTargetNamespacePrefix(genTargetNamespacePrefix);
                
                    if(!genXMLTargetNamespace.equals(""))
                        parameters.setSchemaTargetNamespace(genXMLTargetNamespace);
                
                    if(!genXMLTargetNamespacePrefix.equals(""))
                        parameters.setSchemaTargetNamespacePrefix(genXMLTargetNamespacePrefix);
                
                    if(genAttributeFormDefault.equals("unqualified"))
                        parameters.setAttributeFormDefault(genAttributeFormDefault);
                
                    if(genElementFormDefault.equals("unqualified"))
                        parameters.setElementFormDefault(genElementFormDefault);
                
                    //Send WAR file
                    genResource.path(java.text.MessageFormat.format("sendWarFile/{0}/{1}", new Object[]{genAnalysisIdentifier, genSelectedWarFile.getName()})).post(genSelectedWarFile);
                
                    //Generate WSDL file
                    genResource.path(java.text.MessageFormat.format("generateWsdlFile/{0}", new Object[]{genAnalysisIdentifier})).post(parameters);
                
                    //Close connection to Wsdl2CreatorService
                    genClient.destroy();
                
                    //Call swingWorker
                    new GeneratingWSDL().execute();
                    
                } catch (MalformedURLException | URISyntaxException ex) {
                    System.out.println("URI is not valid");
                    
                    //Make invalidServiceAddressLabel visible
                    invalidServiceAddressLabel.setVisible(true);
                    
                    //Make genAnalysisFailedLabel invisible
                    genAnalysisFailedLabel.setVisible(false);
                    
                    //Close connection to Wsdl2CreatorService
                    genClient.destroy();
                    
                    //Release semaphore
                    genSemaphore.release();
                }
                
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2CreatorService");

                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to generate WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);

                //Make genAnalysisFailedLabel invisible
                genAnalysisFailedLabel.setVisible(false);
                    
                //Disable all components of genResultPanel, except genEditingLabel and genEditingProgressBar
                Component[] genResultComponents = genResultPanel.getComponents();
                for (int i = 0; i < genResultComponents.length; i++)
                    genResultComponents[i].setEnabled(false);
                genEditingLabel.setEnabled(true);
                genEditingProgressBar.setEnabled(true);
                
                //Clear genResultFileNameTextField and genEditedResultTextField
                genResultFileNameTextField.setText("");
                genEditedResultTextField.setText("");
                
                //Release semaphore
                genSemaphore.release();
            }
        }
    }//GEN-LAST:event_generateWSDLFileButtonActionPerformed

    //When user press genSaveFileButton
    private void genSaveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSaveFileButtonActionPerformed
        try {
            //Connect to Wsdl2CreatorService
            ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            Client genClient = Client.create(config);
            WebResource genResource = genClient.resource(creatorServiceURL);
            
            //Get result file from Wsdl2CreatorService
            genResultFile = genResource.path(java.text.MessageFormat.format("getResult/{0}", new Object[]{genAnalysisIdentifier})).get(File.class);

            //Close connection to Wsdl2CreatorService
            genClient.destroy();

            //Set a default file for saving the results
            genResultFileChooser.setSelectedFile(new File(genResultFilename));
            int returnVal = genResultFileChooser.showSaveDialog(this);

            //If user selects a file and press save button
            if (returnVal == JFileChooser.APPROVE_OPTION){
                
                File localFile = genResultFileChooser.getSelectedFile();
                    
                PrintWriter pw = new PrintWriter(new FileWriter(localFile));
                BufferedReader br = new BufferedReader(new FileReader(genResultFile));
                String s;
                while ((s = br.readLine()) != null)
                pw.println(s);

                pw.close();
                br.close();
            }
            
        } catch (IOException | ClientHandlerException | UniformInterfaceException ex) {
            System.out.println("Unable to access Wsdl2CreatorService or Save file");

            //Show error message to user
            JOptionPane.showMessageDialog(rootPane, "Error to save file \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_genSaveFileButtonActionPerformed

    //When user press genRemoveSoapBindingsButton
    private void genRemoveSoapBindingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genRemoveSoapBindingsButtonActionPerformed
       
        if(genSemaphore.tryAcquire()){
            try {
                //Connect to Wsdl2CreatorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client genClient = Client.create(config);
                WebResource genResource = genClient.resource(creatorServiceURL);
            
                //Set result filename
                genEdResultFilename = "Edited" + genResultFilename;
                System.out.println("genEdResultFilename: " + genEdResultFilename);
                
                //Get result file from Wsdl2CreatorService
                genResultFile = genResource.path(java.text.MessageFormat.format("getResult/{0}", new Object[]{genAnalysisIdentifier})).get(File.class);

                //Connect to Wsdl2EditorService
                ClientConfig config2 = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client edClient = Client.create(config2);
                WebResource edResource = edClient.resource(editorServiceURL);
            
                //Send file to Wsdl2EditorService
                edResource.path(java.text.MessageFormat.format("sendWsdlFile/{0}/{1}", new Object[]{genEdAnalysisIdentifier, genResultFile.getName()})).post(genResultFile);
            
                //Edit Wsdl
                edResource.path(java.text.MessageFormat.format("editWsdlFile/{0}", new Object[]{genEdAnalysisIdentifier})).post();
            
                //Close connection to Wsdl2EditorService
                edClient.destroy();
            
                //Close connection to Wsdl2CreatorService
                genClient.destroy();
            
                //Call swingWorker
                new EditingGeneratedWSDL().execute();
   
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2CreatorService/Wsdl2EditorService");
                
                //Disable genEditedResultTextField, genEditedResultFileButton
                genEditedResultTextField.setEnabled(false);
                genEditedResultFileButton.setEnabled(false);
                
                //Clear genEditedResultTextField
                genEditedResultTextField.setText("");
                
                //Make genEdAnalysisFailedLabel invisible
                genEdAnalysisFailedLabel.setVisible(false);
                        
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to edit WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
                
                //Release semaphore
                genSemaphore.release();
            }
        }
    }//GEN-LAST:event_genRemoveSoapBindingsButtonActionPerformed

    //When user press genEditedResultFileButton
    private void genEditedResultFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genEditedResultFileButtonActionPerformed
        try {
            //Connect to Wsdl2EditorService
            ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            Client edClient = Client.create(config);
            WebResource edResource = edClient.resource(editorServiceURL);
            
            //Get result file from Wsdl2EditorService
            genEdResultFile = edResource.path(java.text.MessageFormat.format("getResult/{0}", new Object[]{genEdAnalysisIdentifier})).get(File.class);

            //Close connection to Wsdl2EditorService
            edClient.destroy();

            //Set a default file for saving the results
            genResultFileChooser.setSelectedFile(new File(genEdResultFilename));
            int returnVal = genResultFileChooser.showSaveDialog(this);

            //If user selects a file and press save button
            if (returnVal == JFileChooser.APPROVE_OPTION){
                
                File localFile = genResultFileChooser.getSelectedFile();
                    
                PrintWriter pw = new PrintWriter(new FileWriter(localFile));
                BufferedReader br = new BufferedReader(new FileReader(genEdResultFile));
                String s;
                while ((s = br.readLine()) != null)
                pw.println(s);

                pw.close();
                br.close();
            }
            
        } catch (IOException | ClientHandlerException | UniformInterfaceException ex) {
            System.out.println("Unable to access Wsdl2EditorService or Save file");

            //Show error message to user
            JOptionPane.showMessageDialog(rootPane, "Error to save file \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_genEditedResultFileButtonActionPerformed

    //When user press edSelectFileButton
    private void edSelectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edSelectFileButtonActionPerformed
        //Open file chooser
        int returnVal = edFileChooser.showOpenDialog(this);
        
        //If user selects a file
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            //Get file selected by user
            edSelectedWsdlFile = edFileChooser.getSelectedFile();

            //Set text field with selected filename
            edSelectedFileTextField.setText(edSelectedWsdlFile.getName());
            
            //Enable edRemoveSoapBindingsButton
            edRemoveSoapBindingsButton.setEnabled(true);
            
            //Disable all components of edResultPanel
            edResultTextArea.setEnabled(false);
            Component[] edResultComponents = edResultPanel.getComponents();
            for (int i = 0; i < edResultComponents.length; i++)
                edResultComponents[i].setEnabled(false);
                
            //Make edAnalysisFailedLabel invisible
            edAnalysisFailedLabel.setVisible(false);
                
            //Clear edResultTextArea
            edResultTextArea.setText("");
        }
    }//GEN-LAST:event_edSelectFileButtonActionPerformed

    //When user press edRemoveSoapBindingsButton
    private void edRemoveSoapBindingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edRemoveSoapBindingsButtonActionPerformed
        if(edSemaphore.tryAcquire()){
            try {
                //Connect to Wsdl2EditorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client edClient = Client.create(config);
                WebResource edResource = edClient.resource(editorServiceURL);
            
                //Set result filename
                edResultFilename = "Edited" + edSelectedWsdlFile.getName();
                
                //Send file to Wsdl2EditorService
                edResource.path(java.text.MessageFormat.format("sendWsdlFile/{0}/{1}", new Object[]{edAnalysisIdentifier, edSelectedWsdlFile.getName()})).post(edSelectedWsdlFile);
            
                //Edit Wsdl
                edResource.path(java.text.MessageFormat.format("editWsdlFile/{0}", new Object[]{edAnalysisIdentifier})).post();
            
                //Close connection to Wsdl2EditorService
                edClient.destroy();
            
                //Call swingWorker
                new EditingWSDL().execute();
   
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2EditorService");
                
                //Disable all components of edResultPanel
                edResultTextArea.setEnabled(false);
                Component[] edResultComponents = edResultPanel.getComponents();
                for (int i = 0; i < edResultComponents.length; i++)
                    edResultComponents[i].setEnabled(false);
                
                //Make edAnalysisFailedLabel invisible
                edAnalysisFailedLabel.setVisible(false);
                
                //Clear edResultTextArea
                edResultTextArea.setText("");

                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to edit WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
                
                //Release semaphore
                edSemaphore.release();
            }
        }
    }//GEN-LAST:event_edRemoveSoapBindingsButtonActionPerformed

    //When user press edSaveFileButton
    private void edSaveFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edSaveFileButtonActionPerformed
        try {
            //Connect to Wsdl2EditorService
            ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
            Client edClient = Client.create(config);
            WebResource edResource = edClient.resource(editorServiceURL);
            
            //Get result file from Wsdl2EditorService
            edResultFile = edResource.path(java.text.MessageFormat.format("getResult/{0}", new Object[]{edAnalysisIdentifier})).get(File.class);

            //Close connection to Wsdl2EditorService
            edClient.destroy();

            //Set a default file for saving the results
            edFileChooser.setSelectedFile(new File(edResultFilename));
            int returnVal = edFileChooser.showSaveDialog(this);

            //If user selects a file and press save button
            if (returnVal == JFileChooser.APPROVE_OPTION){
                
                File localFile = edFileChooser.getSelectedFile();
                    
                PrintWriter pw = new PrintWriter(new FileWriter(localFile));
                BufferedReader br = new BufferedReader(new FileReader(edResultFile));
                String s;
                while ((s = br.readLine()) != null)
                pw.println(s);

                pw.close();
                br.close();
            }
            
        } catch (IOException | ClientHandlerException | UniformInterfaceException ex) {
            System.out.println("Unable to access Wsdl2EditorService or Save file");

            //Show error message to user
            JOptionPane.showMessageDialog(rootPane, "Error to save file \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_edSaveFileButtonActionPerformed

    //When user press valSelectFileButton
    private void valSelectFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valSelectFileButtonActionPerformed
        int returnVal = valFileChooser.showOpenDialog(this);
        
        //If user selects a file
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            //Get file selected by user
            valSelectedFile = valFileChooser.getSelectedFile();
            
            //Set text field with selected filename
            valSelectedFileTextField.setText(valSelectedFile.getName());
            
            //Enable validateButton
            validateButton.setEnabled(true);
            
            //Disable valResultTextArea
            valResultTextArea.setEnabled(false);
            
            //Clear valResultTextArea
            valResultTextArea.setText("");
        } 
    }//GEN-LAST:event_valSelectFileButtonActionPerformed

    //When user press validateButton
    private void validateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateButtonActionPerformed
        if(valSemaphore.tryAcquire()){
            
            try{
            
                //Connect to Wsdl2ValidatorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client valClient = Client.create(config);
                WebResource valResource = valClient.resource(validatorServiceURL);
            
                //Send file to Wsdl2ValidatorService
                valResource.path(java.text.MessageFormat.format("sendWsdlFile/{0}/{1}", new Object[]{valAnalysisIdentifier, valSelectedFile.getName()})).post(valSelectedFile);
            
                //Validate file
                valResource.path(java.text.MessageFormat.format("validateWsdlFile/{0}", new Object[]{valAnalysisIdentifier})).post();
                
                //Close connection to Wsdl2ValidatorService
                valClient.destroy();
                
                new ValidatingWSDLFile().execute();
            
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2ValidatorService");

                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to validate WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
                
                //Clear valResultTextArea
                valResultTextArea.setText("");
                
                //Disable valResultTextArea
                valResultTextArea.setEnabled(false);
                
                //Release semaphore
                valSemaphore.release();
            }
        }
    }//GEN-LAST:event_validateButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainApp.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainApp();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel edAnalysisFailedLabel;
    private javax.swing.JFileChooser edFileChooser;
    private javax.swing.JPanel edPanel;
    private javax.swing.JProgressBar edProgressBar;
    private javax.swing.JButton edRemoveSoapBindingsButton;
    private javax.swing.JLabel edResultLabel;
    private javax.swing.JPanel edResultPanel;
    private javax.swing.JTextArea edResultTextArea;
    private javax.swing.JButton edSaveFileButton;
    private javax.swing.JLabel edSelecFileLabel;
    private javax.swing.JButton edSelectFileButton;
    private javax.swing.JTextField edSelectedFileTextField;
    private javax.swing.JPanel edUploadPanel;
    private javax.swing.JLabel editingLabel;
    private javax.swing.JLabel genAnalysisFailedLabel;
    private javax.swing.JCheckBox genAttributeFormDefaultCheckBox;
    private javax.swing.JLabel genAttributeFormDefaultLabel;
    private javax.swing.JLabel genEdAnalysisFailedLabel;
    private javax.swing.JButton genEditedResultFileButton;
    private javax.swing.JTextField genEditedResultTextField;
    private javax.swing.JLabel genEditingLabel;
    private javax.swing.JProgressBar genEditingProgressBar;
    private javax.swing.JCheckBox genElementFormDefaultCheckBox;
    private javax.swing.JLabel genElementFormDefaultLabel;
    private javax.swing.JFileChooser genFileChooser;
    private javax.swing.JTree genFileTree;
    private javax.swing.JLabel genHelpAttributeFormDefaultLabel;
    private javax.swing.JLabel genHelpElementFormDefaultLabel;
    private javax.swing.JLabel genHelpServiceAddressLabel;
    private javax.swing.JLabel genHelpTargetNamespaceLabel;
    private javax.swing.JLabel genHelpTargetNamespacePrefixLabel;
    private javax.swing.JLabel genHelpXMLTargetNamespaceLabel;
    private javax.swing.JLabel genHelpXMLTargetNamespacePrefixLabel;
    private javax.swing.JPanel genPanel;
    private javax.swing.JPanel genPerformPanel;
    private javax.swing.JLabel genPleaseSelectFileLabel;
    private javax.swing.JLabel genPleaseSelectServiceLabel;
    private javax.swing.JProgressBar genProgressBar;
    private javax.swing.JButton genRemoveSoapBindingsButton;
    private javax.swing.JFileChooser genResultFileChooser;
    private javax.swing.JLabel genResultFileLabel;
    private javax.swing.JTextField genResultFileNameTextField;
    private javax.swing.JPanel genResultPanel;
    private javax.swing.JButton genSaveFileButton;
    private javax.swing.JButton genSelectFileButton;
    private javax.swing.JTextField genSelectedFileTextField;
    private javax.swing.JLabel genServiceAddressLabel;
    private javax.swing.JTextField genServiceAddressTextField;
    private javax.swing.JLabel genTargetNamespaceLabel;
    private javax.swing.JLabel genTargetNamespacePrefixLabel;
    private javax.swing.JTextField genTargetNamespacePrefixTextField;
    private javax.swing.JTextField genTargetNamespaceTextField;
    private javax.swing.JPanel genUploadPanel;
    private javax.swing.JLabel genXMLTargetNamespaceLabel;
    private javax.swing.JLabel genXMLTargetNamespacePrefixLabel;
    private javax.swing.JTextField genXMLTargetNamespacePrefixTextField;
    private javax.swing.JTextField genXMLTargetNamespaceTextField;
    private javax.swing.JButton generateWSDLFileButton;
    private javax.swing.JLabel generatingWsdlLabel;
    private javax.swing.JLabel invalidServiceAddressLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JFileChooser valFileChooser;
    private javax.swing.JPanel valPanel;
    private javax.swing.JPanel valPerformPanel;
    private javax.swing.JLabel valPleaseSelectFileLabel;
    private javax.swing.JProgressBar valProgressBar;
    private javax.swing.JTextArea valResultTextArea;
    private javax.swing.JButton valSelectFileButton;
    private javax.swing.JTextField valSelectedFileTextField;
    private javax.swing.JPanel valUploadPanel;
    private javax.swing.JButton validateButton;
    private javax.swing.JLabel validatingDocumentLabel;
    // End of variables declaration//GEN-END:variables

    class GeneratingWSDL extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws Exception {
            
            try{
                
                //Disable all components of genUploadPanel
                genFileTree.setEnabled(false);
                Component[] genUploadComponents = genUploadPanel.getComponents();
                for (int i = 0; i < genUploadComponents.length; i++)
                    genUploadComponents[i].setEnabled(false);
                
                //Disable all components of genPerformPanel, except generatingWsdlLabel and genProgressBar
                Component[] genPerformComponents = genPerformPanel.getComponents();
                for (int i = 0; i < genPerformComponents.length; i++)
                    genPerformComponents[i].setEnabled(false);
                generatingWsdlLabel.setEnabled(true);
                genProgressBar.setEnabled(true);
                
                //Make genAnalysisFailedLabel invisible
                genAnalysisFailedLabel.setVisible(false);
                
                //Disable all components of genResultPanel, except genEditingLabel and genEditingProgressBar
                Component[] genResultComponents = genResultPanel.getComponents();
                for (int i = 0; i < genResultComponents.length; i++)
                    genResultComponents[i].setEnabled(false);
                genEditingLabel.setEnabled(true);
                genEditingProgressBar.setEnabled(true);
                
                //Clear genResultFileNameTextField and genEditedResultTextField
                genResultFileNameTextField.setText("");
                genEditedResultTextField.setText("");
                
                //Make progress bar and label visible
                generatingWsdlLabel.setVisible(true);
                genProgressBar.setVisible(true);
                
                //Connect to Wsdl2CreatorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client genClient = Client.create(config);
                WebResource genResource = genClient.resource(creatorServiceURL);
                
                //While analysis status is equal to "running", sleep
                while (genResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{genAnalysisIdentifier})).get(String.class).equals("running")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) { }
                }
                
                if(genResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{genAnalysisIdentifier})).get(String.class).equals("finished")){
                    //Close connection to Wsdl2CreatorService
                    genClient.destroy();
                    
                    System.out.println("WSDL file generated!!!");
                    
                    //Enable all components of genResultPanel, except genEditedResultTextField and genEditedResultFileButton
                    genResultComponents = genResultPanel.getComponents();
                    for (int i = 0; i < genResultComponents.length; i++)
                        genResultComponents[i].setEnabled(true);
                    genEditedResultTextField.setEnabled(false);
                    genEditedResultFileButton.setEnabled(false);
            
                    //Set genResultFileNameTextField
                    genResultFileNameTextField.setText(genResultFilename);
            
                }
                else{
                    //Close connection to Wsdl2CreatorService
                    genClient.destroy();
                    
                    System.out.println("WSDL file generation failed!!!");
                    
                    //Make genAnalysisFailedLabel visible
                    genAnalysisFailedLabel.setVisible(true);
                }
                            
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2CreatorService");
                
                //Make progress bar and label invisible
                generatingWsdlLabel.setVisible(false);
                genProgressBar.setVisible(false);
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to generate WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            return null;
        }
        
        protected void done() {
            //Make progress bar and label invisible
            generatingWsdlLabel.setVisible(false);
            genProgressBar.setVisible(false);
            
            //Enable all components of genUploadPanel
            genFileTree.setEnabled(true);
            Component[] genUploadComponents = genUploadPanel.getComponents();
            for (int i = 0; i < genUploadComponents.length; i++)
                genUploadComponents[i].setEnabled(true);
                
            //Enable all components of genPerformPanel
            Component[] genPerformComponents = genPerformPanel.getComponents();
            for (int i = 0; i < genPerformComponents.length; i++)
                genPerformComponents[i].setEnabled(true);  
           
            //Release semaphore
            genSemaphore.release();
        }
    }
    
    class EditingGeneratedWSDL extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws Exception {
            
            try{
                
                //Disable all components of genUploadPanel
                genFileTree.setEnabled(false);
                Component[] genUploadComponents = genUploadPanel.getComponents();
                for (int i = 0; i < genUploadComponents.length; i++)
                    genUploadComponents[i].setEnabled(false);
                
                //Disable all components of genPerformPanel, except generatingWsdlLabel and genProgressBar
                Component[] genPerformComponents = genPerformPanel.getComponents();
                for (int i = 0; i < genPerformComponents.length; i++)
                    genPerformComponents[i].setEnabled(false);
                generatingWsdlLabel.setEnabled(true);
                genProgressBar.setEnabled(true);
                
                //Make genEdAnalysisFailedLabel invisible
                genEdAnalysisFailedLabel.setVisible(false);
                
                //Disable all components of genResultPanel, except genEditingLabel and genEditingProgressBar
                Component[] genResultComponents = genResultPanel.getComponents();
                for (int i = 0; i < genResultComponents.length; i++)
                    genResultComponents[i].setEnabled(false);
                genEditingLabel.setEnabled(true);
                genEditingProgressBar.setEnabled(true);
                
                //Clear genEditedResultTextField
                genEditedResultTextField.setText("");
                
                //Make progress bar and label visible
                genEditingLabel.setVisible(true);
                genEditingProgressBar.setVisible(true);
                
                //Connect to Wsdl2EditorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client edClient = Client.create(config);
                WebResource edResource = edClient.resource(editorServiceURL);
                
                //While analysis status is equal to "running", sleep
                while (edResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{genEdAnalysisIdentifier})).get(String.class).equals("running")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) { }
                }
                
                if(edResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{genEdAnalysisIdentifier})).get(String.class).equals("finished")){
                    //Close connection to Wsdl2EditorService
                    edClient.destroy();
                    
                    System.out.println("WSDL file edited!!!");
                    
                    //Enable all components of genResultPanel
                    genResultComponents = genResultPanel.getComponents();
                    for (int i = 0; i < genResultComponents.length; i++)
                        genResultComponents[i].setEnabled(true);
            
                    //Set genEditedResultTextField
                    genEditedResultTextField.setText(genEdResultFilename);
            
                }
                else{
                    //Close connection to Wsdl2EditorService
                    edClient.destroy();
                    
                    System.out.println("WSDL file edition failed!!!");
                    
                    //Make genEdAnalysisFailedLabel visible
                    genEdAnalysisFailedLabel.setVisible(true);
                }
                            
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2EditorService");
                
                //Make progress bar and label invisible
                genEditingLabel.setVisible(true);
                genEditingProgressBar.setVisible(true);
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to edit WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            return null;
        }
        
        protected void done() {
            //Make progress bar and label invisible
            genEditingLabel.setVisible(false);
            genEditingProgressBar.setVisible(false);
            
            //Enable all components of genUploadPanel
            genFileTree.setEnabled(true);
            Component[] genUploadComponents = genUploadPanel.getComponents();
            for (int i = 0; i < genUploadComponents.length; i++)
                genUploadComponents[i].setEnabled(true);
                
            //Enable all components of genPerformPanel
            Component[] genPerformComponents = genPerformPanel.getComponents();
            for (int i = 0; i < genPerformComponents.length; i++)
                genPerformComponents[i].setEnabled(true);  
           
            //Release semaphore
            genSemaphore.release();
        }
    }
    
    class EditingWSDL extends SwingWorker<String, Object> {

        @Override
        protected String doInBackground() throws Exception {
            
            try{
                
                //Disable all components of edUploadPanel, except editingLabel and edProgressBar
                Component[] edUploadComponents = edUploadPanel.getComponents();
                for (int i = 0; i < edUploadComponents.length; i++)
                    edUploadComponents[i].setEnabled(false);
                editingLabel.setEnabled(true);
                edProgressBar.setEnabled(true);
                
                //Make edAnalysisFailedLabel invisible
                edAnalysisFailedLabel.setVisible(false);
                
                //Disable all components of edResultPanel
                edResultTextArea.setEnabled(false);
                Component[] edResultComponents = edResultPanel.getComponents();
                for (int i = 0; i < edResultComponents.length; i++)
                    edResultComponents[i].setEnabled(false);
                
                //Clear edResultTextArea
                edResultTextArea.setText("");
                
                //Make progress bar and label visible
                editingLabel.setVisible(true);
                edProgressBar.setVisible(true);
                
                //Connect to Wsdl2EditorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client edClient = Client.create(config);
                WebResource edResource = edClient.resource(editorServiceURL);
                
                //While analysis status is equal to "running", sleep
                while (edResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{edAnalysisIdentifier})).get(String.class).equals("running")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) { }
                }
                
                if(edResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{edAnalysisIdentifier})).get(String.class).equals("finished")){
                    
                    //Get result file from Wsdl2EditorService
                    edResultFile = edResource.path(java.text.MessageFormat.format("getResult/{0}", new Object[]{edAnalysisIdentifier})).get(File.class);

                    //Close connection to Wsdl2EditorService
                    edClient.destroy();
                    
                    System.out.println("WSDL file edited!!!");
                    
                    //Enable all components of edResultPanel
                    edResultTextArea.setEnabled(true);
                    edResultComponents = edResultPanel.getComponents();
                    for (int i = 0; i < edResultComponents.length; i++)
                        edResultComponents[i].setEnabled(true);
            
                    //Clear edResultTextArea; read result file an put it into edResultTextArea
                    edResultTextArea.setText("");
                    BufferedReader br = new BufferedReader(new FileReader(edResultFile));
                    String s;
                    while ((s = br.readLine()) != null)
                        edResultTextArea.append(s + "\n");
                }
                else{
                    //Close connection to Wsdl2EditorService
                    edClient.destroy();
                    
                    System.out.println("WSDL file edition failed!!!");
                    
                    //Make edAnalysisFailedLabel visible
                    edAnalysisFailedLabel.setVisible(true);
                    
                    //Clear edResultTextArea
                    edResultTextArea.setText("");
                }
                            
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2EditorService");
                
                //Make progress bar and label invisible
                editingLabel.setVisible(false);
                edProgressBar.setVisible(false);
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to edit WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            return null;
        }
        
        protected void done() {
            //Make progress bar and label invisible
            editingLabel.setVisible(false);
            edProgressBar.setVisible(false);
            
            //Enable all components of edUploadPanel
            Component[] edUploadComponents = edUploadPanel.getComponents();
            for (int i = 0; i < edUploadComponents.length; i++)
                edUploadComponents[i].setEnabled(true);
                
            //Release semaphore
            edSemaphore.release();
        }
    }
    
    class ValidatingWSDLFile extends SwingWorker<String, Object> {
        
        @Override
        protected String doInBackground() throws Exception {
            
            try{
                
                wasValidated = true;
                
                //Disable all components of valUploadPanel
                Component[] valUploadComponents = valUploadPanel.getComponents();
                for (int i = 0; i < valUploadComponents.length; i++)
                    valUploadComponents[i].setEnabled(false);
                
                //Disable validateButton
                validateButton.setEnabled(false);
                
                //Clear and disable valResultTextArea
                valResultTextArea.setText("");
                valResultTextArea.setEnabled(false);
                
                //Make progress bar and label visible
                validatingDocumentLabel.setVisible(true);
                valProgressBar.setVisible(true);
                
                //Connect to Wsdl2ValidatorService
                ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
                Client valClient = Client.create(config);
                WebResource valResource = valClient.resource(validatorServiceURL);
                
                //While analysis status is equal to "running", sleep
                while (valResource.path(java.text.MessageFormat.format("getStatus/{0}", new Object[]{valAnalysisIdentifier})).get(String.class).equals("running")) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) { }
                }
                
                String result = valResource.path(java.text.MessageFormat.format("getResult/{0}", new Object[]{valAnalysisIdentifier})).get(String.class);
           
                if(!result.equals("")){
                    valResultTextArea.setText(result);
                    valResultTextArea.setForeground(Color.red);
                }
                else{
                    valResultTextArea.append("Valid Document");
                    valResultTextArea.setForeground(Color.blue);
                }
            
            } catch (ClientHandlerException | UniformInterfaceException ex) {
                System.out.println("Unable to access Wsdl2ValidatorService");
                
                //Make progress bar and label invisible
                validatingDocumentLabel.setVisible(false);
                valProgressBar.setVisible(false);
                
                wasValidated = false;
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to validate WSDL \nPlease verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            return null;
        }
        
        protected void done() {
            //Make progress bar and label invisible
            validatingDocumentLabel.setVisible(false);
            valProgressBar.setVisible(false);
            
            //Enable all components of valUploadPanel
            Component[] valUploadComponents = valUploadPanel.getComponents();
            for (int i = 0; i < valUploadComponents.length; i++)
                valUploadComponents[i].setEnabled(true);
            
            //Enable valResultTextArea
            if(wasValidated)
                valResultTextArea.setEnabled(true);
            else
                valResultTextArea.setEnabled(false);
            
            //Enable validateButton
            validateButton.setEnabled(true);
            
            //Release semaphore
            valSemaphore.release();
        }
    }
}