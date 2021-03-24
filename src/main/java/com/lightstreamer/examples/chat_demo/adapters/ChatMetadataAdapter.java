/*
  Copyright (c) Lightstreamer Srl

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/

package com.lightstreamer.examples.chat_demo.adapters;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.adapters.metadata.LiteralBasedProvider;
import com.lightstreamer.interfaces.metadata.CreditsException;
import com.lightstreamer.interfaces.metadata.MetadataProviderException;
import com.lightstreamer.interfaces.metadata.NotificationException;


public class ChatMetadataAdapter extends LiteralBasedProvider {

    /**
     * The associated feed to which messages will be forwarded;
     * it is the Data Adapter itself.
     */
    private volatile ChatDataAdapter chatFeed;

    /**
     * Unique identification of the Adapter Set. It is used to uniquely
     * identify the related Data Adapter instance;
     * see feedMap on ChatDataAdapter.
     */
    private String adapterSetId;

    /**
     * Private logger; a specific "LS_demos_Logger.Chat" category
     * should be supplied by log4j configuration.
     */
    private Logger logger;

    /**
     * Keeps the client context information supplied by Lightstreamer on the
     * new session notifications.
     * Session information is needed to uniquely identify each client.
     */
    private ConcurrentHashMap<String,Map<String,String>> sessions = new ConcurrentHashMap<String,Map<String,String>>();

    /**
     * Used as a Set, to keep unique identifiers for the currently connected
     * clients.
     * Each client is uniquely identified by the client IP address and the
     * HTTP user agent; in case of conflicts, a custom progressive is appended
     * to the user agent. This set lists the concatenations of the current
     * IP and user agent pairs, to help determining uniqueness.
     */
    private ConcurrentHashMap<String,Object> uaIpPairs = new ConcurrentHashMap<String,Object>();

    public ChatMetadataAdapter() {
    }

    public void init(Map params, File configDir) throws MetadataProviderException {
        //Call super's init method to handle basic Metadata Adapter features
        super.init(params,configDir);

        logger = LogManager.getLogger("LS_demos_Logger.Chat");

        // Read the Adapter Set name, which is supplied by the Server as a parameter
        this.adapterSetId = (String) params.get("adapters_conf.id");

        /*
         * Note: the ChatDataAdapter instance cannot be looked for
         * here to initialize the "chatFeed" variable, because the Chat
         * Data Adapter may not be loaded and initialized at this moment.
         * We need to wait until the first "sendMessage" occurrence;
         * then we can store the reference for later use.
         */

        logger.info("ChatMetadataAdapter ready");
    }

    /**
     * Triggered by a client "sendMessage" call.
     * The message encodes a chat message from the client.
     */
    public void notifyUserMessage(String user, String session, String message)
        throws NotificationException, CreditsException {

        if (message == null) {
            logger.warn("Null message received");
            throw new NotificationException("Null message received");
        }

        //Split the string on the | character
        //The message must be of the form "CHAT|message"
        String[] pieces = message.split("\\|");

        this.loadChatFeed();
        this.handleChatMessage(pieces,message,session);
    }

    public void notifyNewSession(String user, String session, Map sessionInfo)
            throws CreditsException, NotificationException {

        //we can't have duplicate sessions
        assert(!sessions.containsKey(session));

        /* If needed, modify the user agent and store it directly
         * in the session infos object.
         * Note: we are free to change and store the received object.
         */
        uniquelyIdentifyClient(sessionInfo);

        // Register the session details on the sessions HashMap.
        sessions.put(session, sessionInfo);

    }

    public void notifySessionClose(String session) throws NotificationException {
        //the session must exist to be closed
        assert(sessions.containsKey(session));

        //we have to remove session information from the session HashMap
        //and from the pairs "Set"

        Map<String,String> sessionInfo = sessions.get(session);
        String ua = sessionInfo.get("USER_AGENT");
        String IP =  sessionInfo.get("REMOTE_IP");

        uaIpPairs.remove(IP+" "+ua);
        sessions.remove(session);
    }

    /**
     * Modifies the clientContext to provide a unique identification
     * for the client session.
     */
    private void uniquelyIdentifyClient(Map clientContext) {

        //extract user agent and ip from session infos
        String ua = (String) clientContext.get("USER_AGENT");
        String ip = (String) clientContext.get("REMOTE_IP");

        /*
         * we need to ensure that each pair IP-User Agent is unique so that
         * the sender can be identified on the client, if such pair is not
         * unique we add a counter on the user agent string
         */

        String count = "";
        int c = 0;
        //synchronize to ensure that we do not generate two identical pairs
        synchronized(uaIpPairs) {
            while (uaIpPairs.containsKey(ip+" "+ua+" "+count)) {
                c++;
                count = "["+c+"]";
            }

            ua = ua+" "+count;

            uaIpPairs.put(ip+" "+ua,new Object());
        }

        clientContext.put("USER_AGENT", ua);
    }

    private void loadChatFeed() throws CreditsException {
        if (this.chatFeed == null) {
             try {
                 // Get the ChatDataAdapter instance to bind it with this
                 // Metadata Adapter and send chat messages through it
                 this.chatFeed = ChatDataAdapter.feedMap.get(this.adapterSetId);
             } catch(Throwable t) {
                 // It can happen if the Chat Data Adapter jar was not even
                 // included in the Adapter Set lib directory (the Chat
                 // Data Adapter could not be included in the Adapter Set as well)
                 logger.error("ChatDataAdapter class was not loaded: " + t);
                 throw new CreditsException(0, "No chat feed available", "No chat feed available");
             }

             if (this.chatFeed == null) {
                 // The feed is not yet available on the static map, maybe the
                 // Chat Data Adapter was not included in the Adapter Set
                 logger.error("ChatDataAdapter not found");
                 throw new CreditsException(0, "No chat feed available", "No chat feed available");
             }
        }
    }

    private void handleChatMessage(String[] pieces, String message, String session) throws NotificationException {
        //extract session infos

        if (pieces.length != 2) {
            logger.warn("Wrong message received: " + message);
            throw new NotificationException("Wrong message received");
        }

        Map<String,String> sessionInfo = sessions.get(session);
        if (sessionInfo == null) {
             logger.warn("Message received from non-existent session: " + message);
             throw new NotificationException("Wrong message received");
        }
        //read from infos the IP and the user agent of the user
        String ua = sessionInfo.get("USER_AGENT");
        String ip =  sessionInfo.get("REMOTE_IP");

        //Check the message, it must be of the form "CHAT|message"
        if (pieces[0].equals("CHAT")) {
            //and send it to the feed
            if (!this.chatFeed.sendMessage(ip,ua,pieces[1])) {
                 logger.warn("Wrong message received: " + message);
                 throw new NotificationException("Wrong message received");
            }
        } else {
             logger.warn("Wrong message received: " + message);
             throw new NotificationException("Wrong message received");
        }

    }

}