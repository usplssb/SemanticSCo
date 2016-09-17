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

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JOptionPane;

public class Help extends javax.swing.JFrame {

    private String element;
    
    //Creates new form Help
    public Help(String type) {
        
        initComponents();
        
        switch(type){
            case "serviceAddress":{
                
                element = "WSDL";
                setTitle("Help: Service Address");
                helpTextArea.append("Service Address is an absolute URI specifying the physical address at which the "
                        + "service can be accessed.\n\n The service address must be in the form \"http://example.com/2013/service\"");
                break;
            }
                
            case "targetNamespace":{
                
                element = "WSDL";
                setTitle("Help: Target Namespace");
                helpTextArea.append("WSDL Target Namespace is an absolute URI analogous to an XML Schema target namespace. "
                        + "Interface, binding and service elements defined in a WSDL document will be associated with its "
                        + "WSDL target namespace, and thus will be distinguishable from similar names in a different "
                        + "WSDL target namespace. (This is important when using WSDL 2.0's import or interface inheritance "
                        + "mechanisms. \n\n"
                        + "The target namespace must be in the form \"http://example.com/2013/wsdl/service.wsdl\"");
                break;
            }
                
            case "targetNamespacePrefix":{
                
                element = "WSDL";
                setTitle("Help: Target Namespace Prefix");
                helpTextArea.append("A Target Namespace Prefix is a string used to uniquely identify WSDL elements, avoiding "
                        + "thus name conflicts. The namespace declaration has the following syntax: xmlns:prefix=\"URI\".");
                break;
            }
                
            case "XMLTargetNamespace":{
                
                element = "XML";
                setTitle("Help: XML Schema Target Namespace");
                helpTextArea.append("XML Schema Target Namespace is an absolute URI. XML elements defined according to a "
                        + "XML Schema will be associated with its target namespace, and thus will be distinguishable "
                        + "from similar names in a different XML target namespace. \n\n"
                        + "The target namespace must be in the form \"http://example.com/2013/wsdl/service.wsdl\"");
                break;
            }
                
            case "XMLTargetNamespacePrefix":{
                
                element = "XML";
                setTitle("Help: XML Schema Target Namespace Prefix");
                helpTextArea.append("A XML Schema Target Namespace Prefix is a string used to uniquely identify XML elements, avoiding "
                        + "thus name conflicts. The namespace declaration has the following syntax: xmlns:prefix=\"URI\".");
                break;
            }
                
            case "attributeFormDefault":{
                
                element = "XML";
                setTitle("Help: Attribute Form Default");
                helpTextArea.append("Attribute Form Default indicates the form of attributes declared in the target "
                        + "namespace of a XML schema. Its value must be \"qualified\" or \"unqualified\": \n\n"
                        + "     >> \"qualified\" indicates that attributes from the target namespace must be qualified with the "
                        + "namespace prefix.\n \n"
                        + "     >> \"unqualified\" indicates that attributes from the target namespace are not required to be "
                        + "qualified with the namespace prefix. \n\n Default is \"qualified\".");
                break;
            }
            
            case "elementFormDefault":{
                
                element = "XML";
                setTitle("Help: Element Form Default");
                helpTextArea.append("Element Form Default indicates the form of elements declared in the target "
                        + "namespace of a XML schema. Its value must be \"qualified\" or \"unqualified\": \n\n"
                        + "     >> \"qualified\" indicates that elements from the target namespace must be qualified with the "
                        + "namespace prefix.\n \n"
                        + "     >> \"unqualified\" indicates that elements from the target namespace are not required to be "
                        + "qualified with the namespace prefix. \n\n Default is \"qualified\".");                
                break;
            }
        }
       
        //Put scroll bar at the top of helpTextArea
        helpTextArea.setCaretPosition(0);
        
        //Centralizes frame
        setLocationRelativeTo(null);
        
        //Make frame visible
        setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        helpTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        hereLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        helpTextArea.setColumns(20);
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setRows(5);
        helpTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(helpTextArea);

        jLabel1.setText("More information can be found");

        hereLabel.setText("<html><a href=\\\"\\\">here</a></html>");
        hereLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hereLabelMouseClicked(evt);
            }
        });
        hereLabel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                hereLabelMouseMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hereLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(hereLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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

    //When user positions the mouse over hereLabel, default cursor is changed to hand cursor
    private void hereLabelMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hereLabelMouseMoved
        //Set cursor of hereLabel to hand cursor
        hereLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }//GEN-LAST:event_hereLabelMouseMoved

    private void hereLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_hereLabelMouseClicked
        try {
            if(element.equals("WSDL"))
                Desktop.getDesktop().browse(new URI("http://www.w3.org/TR/wsdl20"));
            else
                Desktop.getDesktop().browse(new URI("http://www.w3.org/TR/xmlschema11-1/"));
        } catch (IOException|URISyntaxException ex) {
            //Show error message to user
            JOptionPane.showMessageDialog(rootPane, "Please verify your connection to Internet and try again", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_hereLabelMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea helpTextArea;
    private javax.swing.JLabel hereLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
