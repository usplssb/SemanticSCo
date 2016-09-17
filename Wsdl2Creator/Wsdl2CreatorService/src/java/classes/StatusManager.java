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

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


public class StatusManager implements Runnable{
    
    public String filename; 
    public Process process;
	
	
    public StatusManager(String filename, Process process){
        this.filename = filename;
	this.process = process;
	Thread t = new Thread(this);
	t.start();
    }
	
    public void run(){
        
	try {
            //Create "Status.txt" file
            File file = new File(filename);
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            
            //Try to use the "Status.txt" file
            FileLock lock = channel.tryLock();
            while(lock == null){
                Thread.sleep(7);
                lock = channel.tryLock();
            }   
            
            //Write "running" in the "Status.txt" file
            ByteBuffer buf = ByteBuffer.allocate("running".length());
            byte[] bytes = "running".getBytes();
            buf.put(bytes);
            buf.flip();
  	    try {
                channel.truncate("running".length());
                channel.write(buf);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
            
            buf.clear();
            lock.release();
            channel.close();
                
            //Wait the end of the process
            process.waitFor();
            
            //Try to use the "Status.txt" file
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
            while(lock == null){
                Thread.sleep(7);
                lock = channel.tryLock();
            }
                   
            //When the process is finished
            switch (process.exitValue()) {
                //If the process was successfully finished, write "running" in the "Status.txt" file
                case 0:
                    buf = ByteBuffer.allocate("finished".length());
                    bytes = "finished".getBytes();                    
                    buf.put(bytes);
                    buf.flip();
                    try {
                        channel.truncate("finished".length());
                        channel.write(buf);
                    } catch (IOException e) {
                        e.printStackTrace(System.err);
                    }
                    break;
                
                //If the process was not successfully finished, write "failure" in the "Status.txt" file
                default:
                    buf = ByteBuffer.allocate("failure".length());
                    bytes = "failure".getBytes();
                    buf.put(bytes);
                    buf.flip();
                    try {
                        channel.truncate("failure".length());
                        channel.write(buf);
                    } catch (IOException e) { 
                        e.printStackTrace(System.err);      
                    }
                    break;
            }
            lock.release();
            channel.close();
        
        } catch (IOException e) {
            e.printStackTrace();
	} catch (InterruptedException e) {
            e.printStackTrace();
	}
    }	
}