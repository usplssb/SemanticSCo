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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import java.awt.Cursor;
import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import juddi.JuddiRegistryAdmin;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.TransportException;

public class CreatePublisher extends javax.swing.JFrame {
    
    /***********************************************************
    * Place your EmailService address here
    ***********************************************************/
    private String emailServiceURL = "http://localhost:8080/EmailService/webresources/emailservice";
    /***********************************************************/

    private String publisherName="", email="", registrationCode="", confirmRegistrationCode="";
    private Semaphore semaphore;
    private WebResource emailResource;
    private Client emailClient;
    
    //Create new form CreatePublisher
    public CreatePublisher() {
        
        initComponents();
        
        //Create semaphore with a single permission
        semaphore = new Semaphore(1);
        
        //Centralizes frame
        setLocationRelativeTo(null);
        
        //Make CreatePublisher frame visible
        setVisible(true);
        
    }
    
    //Enable/Disable registerButton
    private void setRegisterButton(){
        
        //Verifies if all text fields were filled
        if(!publisherName.equals("") && !email.equals(""))
            registerButton.setEnabled(true);
        else
            registerButton.setEnabled(false);   
    }

    //This method is called from within the constructor to initialize the form.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        emailTextField = new javax.swing.JTextField();
        emailLabel = new javax.swing.JLabel();
        registerButton = new javax.swing.JButton();
        createPublisherLabel = new javax.swing.JLabel();
        registrationCodeTextField = new javax.swing.JTextField();
        registrationCodeLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        waitLabel = new javax.swing.JLabel();
        goBackLabel = new javax.swing.JLabel();
        publisherNameLabel = new javax.swing.JLabel();
        publisherNameTextField = new javax.swing.JTextField();
        registeringPublisherProgressBar = new javax.swing.JProgressBar();
        registeringPublisherLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Micro Toolkit");
        setLocationByPlatform(true);

        emailTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                emailTextFieldKeyReleased(evt);
            }
        });

        emailLabel.setText("Email:");

        registerButton.setText("Register");
        registerButton.setEnabled(false);
        registerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerButtonActionPerformed(evt);
            }
        });

        createPublisherLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        createPublisherLabel.setText("<html><b>Create Publisher</b></html>");

        registrationCodeTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                registrationCodeTextFieldKeyReleased(evt);
            }
        });

        registrationCodeLabel.setText("Please, enter Registration Code:");

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        waitLabel.setText("<html><span style=\"color:blue\">This can take a few minutes... Please wait</span></html>");

        goBackLabel.setText("<html><span style=\"color:blue\"><u><b>Go back</b></u></span></html>");
        goBackLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                goBackLabelMouseClicked(evt);
            }
        });
        goBackLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                goBackLabelMouseMoved(evt);
            }
        });

        publisherNameLabel.setText("Publisher name:");

        publisherNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                publisherNameTextFieldKeyReleased(evt);
            }
        });

        registeringPublisherProgressBar.setIndeterminate(true);

        registeringPublisherLabel.setText("Registering publisher...");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(goBackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(emailLabel)
                                    .addComponent(publisherNameLabel))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(registerButton))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(createPublisherLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(publisherNameTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(emailTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(83, 83, 83)
                                            .addComponent(waitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(registrationCodeLabel)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(registrationCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(okButton)))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(registeringPublisherLabel)
                                        .addComponent(registeringPublisherProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 187, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goBackLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(createPublisherLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(emailLabel)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(publisherNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(publisherNameLabel))
                        .addGap(18, 18, 18)
                        .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(registerButton)
                .addGap(29, 29, 29)
                .addComponent(waitLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(registrationCodeLabel)
                    .addComponent(registrationCodeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton))
                .addGap(18, 18, 18)
                .addComponent(registeringPublisherLabel)
                .addGap(1, 1, 1)
                .addComponent(registeringPublisherProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        registrationCodeTextField.setVisible(false);
        registrationCodeLabel.setVisible(false);
        okButton.setVisible(false);
        waitLabel.setVisible(false);
        registeringPublisherProgressBar.setVisible(false);
        registeringPublisherLabel.setVisible(false);

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

    //When user type something in publisherNameTextField
    private void publisherNameTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_publisherNameTextFieldKeyReleased
        
        //Get publisherName typed by user
        publisherName = publisherNameTextField.getText().trim();
        
        //Verify if email field do not exceed 255 words (allowed into juddi database)
        if(publisherName.length()>255){
            publisherName = publisherName.substring(0,255);
            publisherNameTextField.setText(publisherName);
        }

        //Enable/Disable registerButton
        setRegisterButton();
        
    }//GEN-LAST:event_publisherNameTextFieldKeyReleased

    //When user type something in emailTextField
    private void emailTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailTextFieldKeyReleased
        
        //Get email typed by user
        email = emailTextField.getText().trim();
        
        //Verify if email field do not exceed 255 words (allowed into juddi database)
        if(email.length()>255){
            email = email.substring(0,255);
            emailTextField.setText(email);
        }

        //Enable/Disable registerButton
        setRegisterButton();
        
    }//GEN-LAST:event_emailTextFieldKeyReleased

    //When user press registerButton
    private void registerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerButtonActionPerformed
        
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            //Call swing worker to send confirmation email to user
            new SendEmail().execute();
                            
        }
    }//GEN-LAST:event_registerButtonActionPerformed

    //When user type something in registrationCodeTextField
    private void registrationCodeTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_registrationCodeTextFieldKeyReleased
        
        //Get registrationCode typed by user
        registrationCode = registrationCodeTextField.getText().trim();
        
    }//GEN-LAST:event_registrationCodeTextFieldKeyReleased

    //When user press okButton
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    
        //Prevents user to press okButton more than one time
        if(semaphore.tryAcquire()){
        
            //If registrationCode is confirmed
            if(registrationCode.equals(confirmRegistrationCode)){
                
                //Call swing worker to register publisher
                new RegisterPublisher(this).execute();
            
            }
            else{ //Else, if user insert incorrect registration code
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Incorrect Registration Code \nPlease try again", "Error", JOptionPane.ERROR_MESSAGE);
                
                //Release semaphore
                semaphore.release();
            }
        }
    }//GEN-LAST:event_okButtonActionPerformed

    //When user click on goBackLabel
    private void goBackLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_goBackLabelMouseClicked
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            //Destroy CreateAccount frame
            dispose();
            
            //Create new form Login
            new LoginFrame();
        }
    }//GEN-LAST:event_goBackLabelMouseClicked

    //When user pass the mouse over the goBackLabel
    private void goBackLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_goBackLabelMouseMoved
        //Set cursor of goBackLabel to hand cursor
        goBackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_goBackLabelMouseMoved

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel createPublisherLabel;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JLabel goBackLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel publisherNameLabel;
    private javax.swing.JTextField publisherNameTextField;
    private javax.swing.JButton registerButton;
    private javax.swing.JLabel registeringPublisherLabel;
    private javax.swing.JProgressBar registeringPublisherProgressBar;
    private javax.swing.JLabel registrationCodeLabel;
    private javax.swing.JTextField registrationCodeTextField;
    private javax.swing.JLabel waitLabel;
    // End of variables declaration//GEN-END:variables

    //Internal class for sending email to user
    class SendEmail extends SwingWorker<String, Object> {
        protected String doInBackground() {
            
            String subject = "Registration confirmation";
            String textBody = "Thanks for registration. \nYour registration code is: ";
            
            //Disable all fields of frame
            publisherNameTextField.setEnabled(false);
            emailTextField.setEnabled(false);
            registerButton.setEnabled(false);
                
            //Make waitLabel visible
            waitLabel.setVisible(true);
            
            try{
                
                emailClient= Client.create(new com.sun.jersey.api.client.config.DefaultClientConfig());
                
                emailResource = emailClient.resource(emailServiceURL);

                //Send confirmation email via EmailService
                confirmRegistrationCode = emailResource.path(java.text.MessageFormat.format("sendEmail/{0}/{1}/{2}", new Object[]{email, subject, textBody})).get(String.class);
                
                //Close connection to EmailService
                emailClient.destroy();
                
                //If email could be sent
                if (!confirmRegistrationCode.equals(" ")) {
                    
                    //Show success message to user
                    JOptionPane.showMessageDialog(rootPane, "A message was sent to registered email \n Please, insert sent registration code", "Message", JOptionPane.INFORMATION_MESSAGE);
               
                    //Make registration labels, fields and buttons visible
                    registrationCodeLabel.setVisible(true);
                    registrationCodeTextField.setVisible(true);
                    okButton.setVisible(true);
            
                } else { //Else, if email could not be sent
                    
                    //Show error message to user
                    JOptionPane.showMessageDialog(rootPane, "Confirmation message could not be sent to registered email \n Please try again", "Error", JOptionPane.ERROR_MESSAGE);
                
                    //Enable all fields of frame
                    publisherNameTextField.setEnabled(true);
                    emailTextField.setEnabled(true);
                    
                    //Enable registerButton
                    registerButton.setEnabled(true);
                    
                }
                
            }catch(ClientHandlerException|UniformInterfaceException ex){
                            
                //Unable to access EmailService: Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Please verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
             
                //Enable all fields of frame
                publisherNameTextField.setEnabled(true);
                emailTextField.setEnabled(true);
                    
                //Enable registerButton
                registerButton.setEnabled(true);
            }
            
            return null;
        }
        
        protected void done() {
            
            //Make waitLabel invisible
            waitLabel.setVisible(false);
                
            //Release semaphore
            semaphore.release();
        }
    }
    
    //Internal class for sending email to user
    class RegisterPublisher extends SwingWorker<String, Object> {
        
        JFrame frame;

        public RegisterPublisher(JFrame frame) {
            this.frame = frame;
        }
        
        protected String doInBackground() {
            
            //Make progress bar and label visible
            registeringPublisherProgressBar.setVisible(true);
            registeringPublisherLabel.setVisible(true);
            
            try{
            
                //Access UDDI registry
                JuddiRegistryAdmin jra = new JuddiRegistryAdmin();
        
                //Login into UDDI registry as admin
                String authentication = jra.authenticate();
                        
                //Create new publisher into UDDI registry
                jra.createPublisher(email,email,publisherName);
                
                //Create category system TModels
                jra.createCategorySystemTModels();
                        
                jra.logout();
                            
                //Show success message to user
                JOptionPane.showMessageDialog(rootPane, "Publisher \"" +publisherName+ "\" successfully inserted into UDDI registry", "Message", JOptionPane.INFORMATION_MESSAGE);
                    
            //If publisher could not be inserted
            } catch (RemoteException|ConfigurationException|TransportException ex) {
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to insert publisher into UDDI registry", "Error", JOptionPane.ERROR_MESSAGE);
                   
            }
            
            return null;
        }
        
        protected void done() {
            
            //Make progress bar and label invisible
            registeringPublisherProgressBar.setVisible(false);
            registeringPublisherLabel.setVisible(false);
            
            //Release semaphore
            semaphore.release();
            
            new LoginFrame();
            
            this.frame.dispose();
            
        }
    }

}