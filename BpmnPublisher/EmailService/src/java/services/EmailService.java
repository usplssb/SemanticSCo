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

import java.util.Properties;
import java.util.UUID;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.*;


@Path("emailservice")
public class EmailService {

    @GET
    @Path("sendEmail/{receiver}/{subject}/{textBody}")
    public String sendEmail(@PathParam("receiver") String receiver,
                            @PathParam("subject") String subject,
                            @PathParam("textBody") String textBody) {
        
        //Initialize variables
        String host = "smtp.gmail.com"; //change to your host
        String sender = "address@email.com"; //place your email address
        String pass = "password"; //place your email password
        
        //Initialize and set private variable props
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable","true"); //change according to selected host
        props.put("mail.smtp.host",host); //change according to selected host
        props.put("mail.smtp.user",sender); //change according to selected host
        props.put("mail.smtp.password",pass); //change according to selected host
        props.put("mail.smtp.port","587"); //change according to selected host
        props.put("mail.smtp.auth","true"); //change according to selected host
        
        //Set receiver address
        String[] to = {receiver};
        
        //Send email to receiver address
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
       
        try {
            message.setFrom(new InternetAddress(sender));
            InternetAddress[] toAddress = new InternetAddress[to.length];

            //Get the array of addresses
            for(int i = 0; i < to.length; i++)
                toAddress[i] = new InternetAddress(to[i]);

            for(int i = 0; i < toAddress.length; i++)
                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
            
            message.setSubject(subject);
            String sentRegistrationCode = UUID.randomUUID().toString().substring(0,8);
            message.setText(textBody + sentRegistrationCode);
            Transport transport = session.getTransport("smtp"); //change according to selected host
            transport.connect(host, sender, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            
            return sentRegistrationCode;
            
        } catch (MessagingException ex) {
            //If message could not be sent
            return " ";
        }
    }

}