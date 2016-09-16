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

import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.util.concurrent.Semaphore;
import javax.swing.JOptionPane;
import juddi.JuddiRegistry;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.juddi.v3.client.transport.TransportException;

public class LoginFrame extends javax.swing.JFrame {
    
    private String email="";
    private Semaphore semaphore;

    //Creates new form LoginFrame
    public LoginFrame() {
        
        initComponents();
        
        //Enable calling the clients
        System.setSecurityManager(null);
        
        //Create semaphore with a single permission
        semaphore = new Semaphore(1);
        
        //Centralizes frame
        setLocationRelativeTo(null);
        
        //Make Login frame visible
        setVisible(true);
    }

    //This method is called from within the constructor to initialize the form.
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signInLabel = new javax.swing.JLabel();
        emailLabel = new javax.swing.JLabel();
        emailTextField = new javax.swing.JTextField();
        incorrectLoginLabel = new javax.swing.JLabel();
        loginButton = new javax.swing.JButton();
        createAccountButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        signInLabel.setText("<html><b>Sign in</b></html>");

        emailLabel.setText("e-mail:");

        emailTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                emailTextFieldKeyReleased(evt);
            }
        });

        incorrectLoginLabel.setText("<html><span style=\"color:red\">E-mail not registered</span></html>");

        loginButton.setText("Login");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        createAccountButton.setText("Create New Publisher");
        createAccountButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createAccountButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(createAccountButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(signInLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(43, 43, 43)
                                .addComponent(emailLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(loginButton)
                                        .addGap(4, 4, 4)
                                        .addComponent(incorrectLoginLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 103, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(signInLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginButton)
                    .addComponent(incorrectLoginLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(createAccountButton)
                .addContainerGap())
        );

        incorrectLoginLabel.setVisible(false);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //When user press createAccountButton
    private void createAccountButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createAccountButtonActionPerformed
        
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            //Create new CreatePublisher form
            new CreatePublisher();
            
            //Destroy Login frame
            dispose();
        }
    }//GEN-LAST:event_createAccountButtonActionPerformed

    private void emailTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailTextFieldKeyReleased
        
        //Get email typed by user
        email = emailTextField.getText().trim();
        
        //Verify if email field do not exceed 255 words (allowed into database)
        if(email.length()>255){
            email = email.substring(0,255);
            emailTextField.setText(email);
        }
        
        //Enable/Disable loginButton
        if(!email.equals(""))
            loginButton.setEnabled(true);
        else
            loginButton.setEnabled(false);
        
        //If user press enter, click loginButton
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loginButton.doClick();
        }
    }//GEN-LAST:event_emailTextFieldKeyReleased

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        //Try to acquire permission
        if(semaphore.tryAcquire()){
            
            try {
                
                JuddiRegistry jr = new JuddiRegistry();
                
                String authentication = jr.authenticate(email);
                
                //If user is authenticated
                if(authentication != null){
                
                    //Call MainFrame
                    new MainFrame(jr);
                    
                    //Destroy LoginFrame
                    dispose();
                }//Else, if user is not authenticated
                else{
                    
                    //Show error message to user
                    JOptionPane.showMessageDialog(rootPane, "Error to authenticate", "Error", JOptionPane.ERROR_MESSAGE);
                    
                    //Release semaphore
                    semaphore.release();
                    
                }
                    
            } catch (ConfigurationException|TransportException|RemoteException ex) {
                
                //Show error message to user
                JOptionPane.showMessageDialog(rootPane, "Error to authenticate", "Error", JOptionPane.ERROR_MESSAGE);
                    
                //Release semaphore
                semaphore.release();
            }
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    //@param args the command line arguments
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton createAccountButton;
    private javax.swing.JLabel emailLabel;
    private javax.swing.JTextField emailTextField;
    private javax.swing.JLabel incorrectLoginLabel;
    private javax.swing.JButton loginButton;
    private javax.swing.JLabel signInLabel;
    // End of variables declaration//GEN-END:variables
}
