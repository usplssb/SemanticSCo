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
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBException;
import jaxbClasses.basictypes.SemanticConcept;
import management.CompositionManager;


public class SelectInputConceptDialog extends JDialog {
    
    private boolean isSelected = false;
    private JButton okButton;
    
    private JTree tree;
    
    public SelectInputConceptDialog(MainFrame jFrame,final CompositionManager manager) throws JAXBException {
        
        super(jFrame);
        
        //Set properties of JDialog "AboutDialog"
        this.setResizable(false); //Make dialog not resizable by user
        this.setSize(550,400); //Set size
        this.setTitle("Select Data Type"); //Set title
        this.setLayout(new BorderLayout()); //Set layout
        
        tree = manager.getTreeOfInputs();
        
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setVisibleRowCount(15);
        tree.clearSelection();
        
        //Method to render labels into JTree
        TreeCellRenderer treeCellRenderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                    boolean expanded, boolean leaf, int row, boolean hasFocus) {
                
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

                //Each node represents a set of classes
                HashMap<String,String> set = (HashMap<String, String>) node.getUserObject();
                String label = "";

                //A set may contain one or more elements
                if (set.size() > 1)
                    label += "[";

                for (String key : set.keySet())
                    label += set.get(key);

                if (set.size() > 1)
                    label += "]";

                setText(label);
                setIcon(getDefaultClosedIcon());

                return this;
            }
        };
        
        //Set renderer or JTree
        tree.setCellRenderer(treeCellRenderer);
        
        //Add TreeSelectionListener to tree
        tree.addTreeSelectionListener(new TreeSelectionListener() {
    
            @Override
            public void valueChanged(TreeSelectionEvent e) {
        
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

                //If something was selected
                if(node != null){
                    
                    //Store selected value
                    isSelected = true;
                    HashMap<String,String> selectedValue = (HashMap<String, String>) node.getUserObject();
                    for(Map.Entry<String, String> entry : selectedValue.entrySet()){
                        
                        SemanticConcept selectedConcept = new SemanticConcept();
                        selectedConcept.setName(entry.getValue());
                        selectedConcept.setUrl(entry.getKey());
                        selectedConcept.setSemanticSimilarity("5.0");
                        
                        //Set selected input concept
                        manager.setSelectedInputConcept(selectedConcept);
                        
                    }
                    
                    //Enable okButton
                    okButton.setEnabled(true);
                    
                }
                else
                    isSelected = false;
            }
        });
        
        //Create JScrollPane and add tree to it
        JScrollPane scrollPane = new JScrollPane(tree);
        scrollPane.setAlignmentX(0f);
        
        //Create JPanel and add scrollPane to it
        JPanel jPanel = new JPanel();
        JPanel jPanelLabel = new JPanel();
        jPanelLabel.setLayout(new BorderLayout());
        jPanelLabel.add(new JLabel("Please, select a data type:"),BorderLayout.WEST);
        jPanelLabel.setPreferredSize(new Dimension(450,20));
        jPanel.add(jPanelLabel,BorderLayout.NORTH);
        jPanel.add(scrollPane,BorderLayout.EAST);
        
        jPanel.setBorder(BorderFactory.createEmptyBorder(20,40,3,40)); 
        
        //Add JPanel to SelectFunctionConceptDialog
        this.getContentPane().add(jPanel,BorderLayout.CENTER);
        
        //Create jPanel "buttonPanel" to expose OK button 
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3,40,10,40)); 
        
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
