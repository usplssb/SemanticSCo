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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.xml.bind.JAXBException;
import jaxbClasses.basictypes.SemanticConcept;
import management.CompositionManager;


public class SelectFunctionConceptDialog extends JDialog {
    
    private boolean isSelected = false;
    private JButton okButton;
    
    public SelectFunctionConceptDialog(MainFrame jFrame,final CompositionManager manager) throws JAXBException {

        super(jFrame);
        
        //Set properties of JDialog "AboutDialog"
        this.setResizable(false); //Make dialog not resizable by user
        this.setSize(550,400); //Set size
        this.setTitle("Select Analysis Activity"); //Set title
        this.setLayout(new BorderLayout()); //Set layout
        
        //Call semDiscoveryFunctions
        List<SemanticConcept> listOfFunctionConcepts = manager.semDiscoveryFunctions();
        
        //Create DefaultListModel "listModel"
        DefaultListModel listModel = new DefaultListModel();
        
        //Add elements to "listModel"
        for(Iterator it=listOfFunctionConcepts.iterator();it.hasNext();){
            listModel.add(0,(SemanticConcept) it.next());            
        }
        
        //Create JList "list", set its properties and add "listModel" to JList
        final JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setVisibleRowCount(15);
        list.setFixedCellWidth(450);
        
        //If list is empty, disable list
        if(listOfFunctionConcepts.isEmpty()){
            list.setEnabled(false);
        }
        
        //Method to render labels into JList
        ListCellRenderer listCellRenderer = new DefaultListCellRenderer(){
            
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
                
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                SemanticConcept entry = (SemanticConcept) value;
                setText(entry.getName());
                
                return this;
            }
        };
        
        list.setCellRenderer(listCellRenderer);
        
        //Add ListSelectionListener to list
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                
                if(list.getSelectedValue() != null){
                    
                    //Store selected value
                    isSelected = true;
                    
                    //Set selected function concept
                    manager.setSelectedFunctionConcept((SemanticConcept) list.getSelectedValue());
                    
                    okButton.setEnabled(true);
                }
                else
                    isSelected = false;
            }
            
        });
        
        //Create JScrollPane and add list to it
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setAlignmentX(0f);
        
        //Create JPanel and add scrollPane to it
        JPanel jPanel = new JPanel();
        JPanel jPanelLabel = new JPanel();
        jPanelLabel.setLayout(new BorderLayout());
        jPanelLabel.add(new JLabel("Please, select an analysis activity to perform on data:"),BorderLayout.WEST);
        jPanelLabel.setPreferredSize(new Dimension(450,20));
        jPanel.add(jPanelLabel,BorderLayout.NORTH);
        jPanel.add(scrollPane,BorderLayout.EAST);
        
        jPanel.setBorder(BorderFactory.createEmptyBorder(20,40,3,40)); 
        
        //Add JPanel to SelectFunctionConceptDialog
        this.getContentPane().add(jPanel,BorderLayout.CENTER);
        
        //Create jPanel "buttonPanel" to expose OK button 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(1,40,20,45));
        
        //Create Cancel button and add ActionListener to it
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isSelected = false;
                setVisible(false);
            }
        });
        
        //Add Cancel button to JPanel "buttonPanel"
        buttonPanel.add(cancelButton);
        
        //Create OK button and add ActionListener to it
        okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        okButton.setEnabled(false);
        
        //Add OK button to JPanel "buttonPanel"
        buttonPanel.add(okButton);

        //Add JPanel "buttonPanel" to AboutDialog
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
    }
    
    public boolean isSelected(){
        return this.isSelected;
    }
    
}
