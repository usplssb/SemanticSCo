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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AboutDialog extends JDialog {

    public AboutDialog(MainFrame jFrame) {

        super(jFrame);
        
        //Set properties of JDialog "AboutDialog"
        this.setResizable(false); //Make dialog not resizable by user
        this.setSize(430,280); //Set size
        this.setTitle("About GEXPAC Editor"); //Set title
        this.setLayout(new BorderLayout()); //Set layout

        //Create JPanel jPanel to expose general information about GEXPAC Editor
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        //Add information about GEXPAC Editor
        jPanel.add(new JLabel(" "));
        jPanel.add(new JLabel("Gene EXpression Analysis Composition (GEXPAC) Editor"));
        jPanel.add(new JLabel("Version 1.0 is designed to facilitate the creation,"));
        jPanel.add(new JLabel("edition and execution of service compositions for"));
        jPanel.add(new JLabel("gene expression analysis."));
        jPanel.add(new JLabel(" "));
        jPanel.add(new JLabel(" "));
        
        //Add information about operational system and Java version
        jPanel.add(new JLabel("Operational System: " + System.getProperty("os.name") + System.getProperty("os.version")));
        jPanel.add(new JLabel(" "));
        jPanel.add(new JLabel("Java Version: " + System.getProperty("java.version", "undefined")));
                
        //Add JPanel jPanel to AboutDialog
        this.getContentPane().add(jPanel, BorderLayout.NORTH);

        //Create jPanel "buttonPanel" to expose OK button 
        JPanel buttonPanel = new JPanel(new BorderLayout());
        
        //Create OK button and add ActionListener to it
        JButton jButton = new JButton("OK");
        
        jButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        //Add OK button to JPanel "buttonPanel"
        buttonPanel.add(jButton,BorderLayout.CENTER);
        
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(3,180,15,180));

        //Add JPanel "buttonPanel" to AboutDialog
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
    }
}
