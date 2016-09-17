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

public class StatusManager implements Runnable {

    public String statusFilename;
    public String resultFilename;
    public Process process;

    public StatusManager(String statusFilename,String resultFilename, Process process) {
        this.statusFilename = statusFilename;
        this.resultFilename = resultFilename;
        this.process = process;
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        
        try {
            //Create "Status.txt" file
            File file = new File(statusFilename);
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            
            //Try to use the "Status.txt" file
            FileLock lock = channel.tryLock();
            while (lock == null) {
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
            
            //Create "Result.txt" file
            PrintWriter pw = new PrintWriter(new FileWriter(new File(resultFilename)));
            
            while(process.getErrorStream()==null){
                try {
                    Thread.sleep(7);
                } catch (InterruptedException ex) { }
            }
            
            //Read error output of process (before process finishes) and write it to "Result.txt" file
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            
            String line;
            line = br.readLine();
            if((line = br.readLine()) != null)
                pw.write(line+"\n");
        
            pw.flush();
            pw.close();

            //Wait the end of the process
            process.waitFor();

            //Try to use the "Status.txt" file
            channel = new RandomAccessFile(file, "rw").getChannel();
            lock = channel.tryLock();
            while (lock == null) {
                Thread.sleep(7);
                lock = channel.tryLock();
            }

            //When the process is finished, write "finished"  in the "Status.txt" file
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
            
            buf.clear();
            lock.release();
            channel.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}