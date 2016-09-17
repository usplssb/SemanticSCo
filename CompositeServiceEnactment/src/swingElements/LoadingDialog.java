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
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class LoadingDialog extends JDialog {
    
    //Constructor
    public LoadingDialog(MainFrame jFrame, String title, String label, int width, int height, int topBorder, int leftBorder, int bottomBorder, int rightBorder) {

        super(jFrame);
        
        //Set properties of JDialog "AboutDialog"
        this.setResizable(false); //Make dialog not resizable by user
        this.setSize(width,height); //Set size
        this.setTitle(title); //Set title
        this.setLayout(new BorderLayout()); //Set layout
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //Do nothing when user try to close dialog
        
        //Create JProgressBar and set its properties
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(true);
        
        //Create JPanel and add progressBar and label to it
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setSize(150,50);
        jPanel.add(new JLabel(label),BorderLayout.NORTH);
        jPanel.add(progressBar,BorderLayout.SOUTH);
        
        JPanel jPanel2 = new JPanel();
        jPanel2.add(jPanel,BorderLayout.CENTER);
        jPanel2.setBorder(BorderFactory.createEmptyBorder(topBorder,leftBorder,bottomBorder,rightBorder)); 
        
        //Add JPanel to SelectFunctionConceptDialog
        this.getContentPane().add(jPanel2,BorderLayout.CENTER);
      
    }
    
}
