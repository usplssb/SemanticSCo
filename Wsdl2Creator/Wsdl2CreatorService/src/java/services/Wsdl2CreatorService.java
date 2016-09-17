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

package services;

import classes.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.UUID;
import javax.ws.rs.*;
import net.lingala.zip4j.core.ZipFile;

@Path("wsdl2creatorservice")
public class Wsdl2CreatorService {
    
    /***********************************************************
    * Place your own directories here
    ***********************************************************/
    public String localDirectory = "/usr/path/Analysis_";
    String jarPath = "usr/path/CreatorApplication/dist/CreatorApplication.jar";
    /**********************************************************/ 

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
    
    @GET
    @Produces("text/plain")
    @Path("generateID")
    public String generateID() {

        //Create a directory with a unique name
        String identifier = UUID.randomUUID().toString();
        String defaultDirectory = localDirectory;
        String newDirectory = defaultDirectory.concat(identifier);
        while (new File(newDirectory).isDirectory()) {
            identifier = UUID.randomUUID().toString();
            newDirectory = defaultDirectory.concat(identifier);
        }
        new File(newDirectory).mkdir();
        
        //Create generation sub-directories
        new File(newDirectory.concat("/InputWarFile")).mkdir();
        new File(newDirectory.concat("/OutputWsdlFile")).mkdir();
        
        return identifier;
    }
    
    @POST
    @Path("sendWarFile/{identifier}/{filename}")
    public void sendWarFile(@PathParam("identifier") String identifier, @PathParam("filename") String filename, File file) throws Exception {
        
        String defaultDirectory = localDirectory + identifier + "/InputWarFile";
        
        File dir = new File(defaultDirectory);
        
        //If the directory with "Analysis_identifier" name exists, save the file locally
        if (dir.isDirectory()){
            
            //Delete existing WAR file and unzipped directories and files
            for (File old : dir.listFiles())
                old.delete();
            
            delete(new File(defaultDirectory.concat("/WEB-INF")));
            delete(new File(defaultDirectory.concat("/META-INF")));
            
            //Stores new file locally
            File newFile = new File(defaultDirectory.concat("/").concat(filename));           
            
            byte[] b = new byte[1024];
            int lenght;
            InputStream input = new FileInputStream(file);
            OutputStream output = new FileOutputStream(newFile);
            
            while(input.available()>0){
                lenght = input.read(b);
                output.write(b, 0, lenght);
            }
            
            input.close();
            output.flush();
            output.close();
            
            //Unzip WAR file into default directory
            ZipFile zip = new ZipFile(newFile);
            zip.extractAll(defaultDirectory);
            
        }
        else //Else, generate an "Exception"
            throw new Exception();
    }
    
    @POST
    @Consumes("application/xml")
    @Path("generateWsdlFile/{identifier}")
    public void generateWsdlFile(@PathParam("identifier") String identifier, Wsdl2Parameters parameters) throws Exception {
        
        String analysisDirectory = localDirectory.concat(identifier);
        File dir = new File(analysisDirectory);
         
         //If the directory with "Analysis_identifier" name exists  
         if (dir.isDirectory()) {
             
             //Set location of classes from which the WSDL file should be generated
             String location = analysisDirectory.concat("/InputWarFile/WEB-INF/classes");
             
             //Set className (name of class from which the WSDL file should be generated)
             String classPathName = parameters.getClassPath();
             String[] parts = classPathName.split("WEB-INF/classes/");
             parts[1] = parts[1].replace(".class", "");
             String className = parts[1].replace("/", ".");
             
             //Set serviceName
             parts = classPathName.split("/");
             String serviceName = parts[parts.length-1].replace(".class", "");
             
             //Set outputLocation and outputFilename
             String outputLocation = analysisDirectory.concat("/OutputWsdlFile"); //OK
             String outputFilename = serviceName.concat(".wsdl");
             
             //Delete existing WSDL files from outputLocation
             for (File old : new File(outputLocation).listFiles())
                old.delete();
             
             //Create a process builder for Wsdl2Generation Application
             ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath,location,className,serviceName,outputLocation,outputFilename,parameters.getServiceAddress(),parameters.getTargetNamespace(),parameters.getTargetNamespacePrefix(),parameters.getSchemaTargetNamespace(),parameters.getSchemaTargetNamespacePrefix(),parameters.getAttributeFormDefault(),parameters.getElementFormDefault());
             Process process;
             
             //Try to execute the process
             process = pb.start();
            
             //Instantiate an StatusManager object, responsible for monitoring the execution of the process
             StatusManager sm = new StatusManager(analysisDirectory.concat("/Status.txt"), process);
            
        } else //Else, generate an "Exception"
            throw new Exception();
    }
    
    @GET
    @Produces("text/plain")
    @Path("getStatus/{identifier}")
    public String getStatus(@PathParam("identifier") String identifier) throws Exception {
        String status = "running";
        File file = new File(localDirectory.concat(identifier).concat("/Status.txt"));
        FileChannel channel;
        FileLock lock;

        //If the identifier is not valid or the file "Status.txt" does not exist, generate an "Exception"
        if (!file.exists()) {
            throw new Exception();
        }

        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
            
            //Try to access the file "Status.txt"
            lock = channel.tryLock();
            
            //If the file "Status.txt" can be accessed
            if (lock != null) {
                
                //Read the file and set the value of the variable "status"
                ByteBuffer buf = ByteBuffer.allocate(1024);
                int bytes = channel.read(buf);
                buf.flip();
                char[] aux = new char[bytes];
                int i = 0;
                while (buf.hasRemaining()) {
                    aux[i] = (char) buf.get();
                    i++;
                }
                
                buf.clear();
                status = new String(aux);
                lock.release();
                channel.close();
            }
        } catch (Exception e) {
            //If some problem occurs during the access to the file "Status.txt"
            return status;
        }
        return status;
    }
    
    @GET
    @Produces("text/plain")
    @Path("getResult/{identifier}")
    public File getResult(@PathParam("identifier") String identifier) throws Exception {

        File f = new File(localDirectory + identifier + "/OutputWsdlFile");
            
        //If the analysis identifier is valid
        if (f.isDirectory()){
            File[] files = f.listFiles();
            //If the result file exists
            if(files[0].isFile())
                return files[0];
            else //Else, generate an "Exception"
                throw new Exception();
        }
        else //Else, generate an "Exception"
            throw new Exception();
    }
    
}
