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
import java.util.*;
import javax.ws.rs.*;

@Path("wsdl2editorservice")
public class Wsdl2EditorService {
    
    /***********************************************************
    * Place your own directories here
    ***********************************************************/
    public String localDirectory = "/usr/path/Analysis_";
    String jarPath = "usr/path/EditorApplication/dist/EditorApplication.jar";
    /**********************************************************/        
    
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
        
        //Create edition sub-directories
        new File(newDirectory.concat("/InputWsdlFile")).mkdir();
        new File(newDirectory.concat("/OutputWsdlFile")).mkdir();

        return identifier;
    }
    
    @POST
    @Path("sendWsdlFile/{identifier}/{filename}")
    public void sendWsdlFile(@PathParam("identifier") String identifier, @PathParam("filename") String filename, File file) throws Exception {

        String defaultDirectory = localDirectory + identifier + "/InputWsdlFile";
        File dir = new File(defaultDirectory);

        //If the directory with "Analysis_identifier" name exists     
        if (dir.isDirectory()) {

            //Delete existing file
            for (File old : dir.listFiles())
                old.delete();

            //Stores new file locally
            File newFile = new File(defaultDirectory + "/" + filename);
            try {
                newFile.createNewFile();
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                FileWriter fw = new FileWriter(newFile);
                PrintWriter pw = new PrintWriter(fw);
                String line;
                while ((line = br.readLine()) != null) {
                    pw.write(line + "\n");
                }
                pw.flush();
                fw.close();
                pw.close();
                fr.close();
                br.close();
            } catch (IOException ex) { }
        }
        else //Else, generate an "Exception"
            throw new Exception();
    }
    
    @POST
    @Path("editWsdlFile/{identifier}")
    public void editWsdlFile(@PathParam("identifier") String identifier) throws Exception {
        
        String analysisDirectory = localDirectory.concat(identifier);
        File dir = new File(analysisDirectory);
         
        //If the directory with "Analysis_identifier" name exists  
        if (dir.isDirectory()) {
            //Get input file path
            File inputDir = new File(analysisDirectory + "/InputWsdlFile");
            String[] input = inputDir.list();
            String inputFile = null;
            for(int i=0;i<input.length;i++)
                inputFile = analysisDirectory.concat("/InputWsdlFile/").concat(input[i]);
            
            //Set output file path
            String outputFile = analysisDirectory.concat("/OutputWsdlFile/Result.wsdl");
            
            //Delete previous created Result.wsdl file
            File f = new File(outputFile);
            if(f.isFile())
                f.delete();
               
            //Create a process builder for Wsdl2SoapBindingsRemoval Application
            ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath, inputFile, outputFile);
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

        File f = new File(localDirectory + identifier + "/OutputWsdlFile/Result.wsdl");
            
        //If the analysis identifier is valid and the file "Result.wsdl" exists, return the file
        if (f.isFile()) 
            return f;
        else //Else, generate an "Exception"
            throw new Exception();
    }
}
