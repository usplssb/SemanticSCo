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

package classes;

import java.io.File;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

public class CreatorApplication {

    public static void main(String[] args) throws BuildException{
               
        //Get command line arguments
        String location = args[0];
        String className = args[1];
        String serviceName = args[2];
        String outputLocation = args[3];
        String outpuFilename = args[4];
        String serviceAddress = args[5];
        String targetNamespace = args[6];
        String targetNamespacePrefix = args[7];
        String schemaTargetNamespace = args[8];
        String schemaTargetNamespacePrefix = args[9];
        String attrFormDefault = args[10];
        String elementFormDefault = args[11];
        
        File buildFile;
        Project p;
        ProjectHelper helper;
        
        if(targetNamespacePrefix.equals(" ") && schemaTargetNamespacePrefix.equals(" ")){
                        
            buildFile = new File("build-java2wsdl1.xml");
            p = new Project();
            p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        
            //Set user properties
            p.setProperty("location",location);
            p.setProperty("className",className);
            p.setProperty("serviceName",serviceName);
            p.setProperty("outputLocation",outputLocation);
            p.setProperty("outputFilename",outpuFilename);
            p.setProperty("serviceAddress",serviceAddress);
            p.setProperty("targetNamespace",targetNamespace);
            p.setProperty("schemaTargetNamespace",schemaTargetNamespace);
            p.setProperty("attrFormDefault",attrFormDefault);
            p.setProperty("elementFormDefault",elementFormDefault);
            
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.executeTarget(p.getDefaultTarget());
            p.fireBuildFinished(null);
        }
        else if(!targetNamespacePrefix.equals(" ") && !schemaTargetNamespacePrefix.equals(" ")){
            
            buildFile = new File("build-java2wsdl2.xml");
            p = new Project();
            p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        
            //Set user properties
            p.setProperty("location",location);
            p.setProperty("className",className);
            p.setProperty("serviceName",serviceName);
            p.setProperty("outputLocation",outputLocation);
            p.setProperty("outputFilename",outpuFilename);
            p.setProperty("serviceAddress",serviceAddress);
            p.setProperty("targetNamespace",targetNamespace);
            p.setProperty("targetNamespacePrefix", targetNamespacePrefix);
            p.setProperty("schemaTargetNamespace",schemaTargetNamespace);
            p.setProperty("schemaTargetNamespacePrefix",schemaTargetNamespacePrefix);
            p.setProperty("attrFormDefault",attrFormDefault);
            p.setProperty("elementFormDefault",elementFormDefault);
            
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.executeTarget(p.getDefaultTarget());
            p.fireBuildFinished(null);
        }
        else if(!targetNamespacePrefix.equals(" ") && schemaTargetNamespacePrefix.equals(" ")){
                        
            buildFile = new File("build-java2wsdl3.xml");
            p = new Project();
            p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        
            //Set user properties
            p.setProperty("location",location);
            p.setProperty("className",className);
            p.setProperty("serviceName",serviceName);
            p.setProperty("outputLocation",outputLocation);
            p.setProperty("outputFilename",outpuFilename);
            p.setProperty("serviceAddress",serviceAddress);
            p.setProperty("targetNamespace",targetNamespace);
            p.setProperty("targetNamespacePrefix", targetNamespacePrefix);
            p.setProperty("schemaTargetNamespace",schemaTargetNamespace);
            p.setProperty("attrFormDefault",attrFormDefault);
            p.setProperty("elementFormDefault",elementFormDefault);
            
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.executeTarget(p.getDefaultTarget());
            p.fireBuildFinished(null);
        }
        
        else if(targetNamespacePrefix.equals(" ") && !schemaTargetNamespacePrefix.equals(" ")){
            
            buildFile = new File("build-java2wsdl4.xml");
            p = new Project();
            p.setUserProperty("ant.file", buildFile.getAbsolutePath());
        
            //Set user properties
            p.setProperty("location",location);
            p.setProperty("className",className);
            p.setProperty("serviceName",serviceName);
            p.setProperty("outputLocation",outputLocation);
            p.setProperty("outputFilename",outpuFilename);
            p.setProperty("serviceAddress",serviceAddress);
            p.setProperty("targetNamespace",targetNamespace);
            p.setProperty("schemaTargetNamespace",schemaTargetNamespace);
            p.setProperty("schemaTargetNamespacePrefix", schemaTargetNamespacePrefix);
            p.setProperty("attrFormDefault",attrFormDefault);
            p.setProperty("elementFormDefault",elementFormDefault);
            
            p.fireBuildStarted();
            p.init();
            helper = ProjectHelper.getProjectHelper();
            p.addReference("ant.projectHelper", helper);
            helper.parse(p, buildFile);
            p.executeTarget(p.getDefaultTarget());
            p.fireBuildFinished(null);
        }
    }
}
