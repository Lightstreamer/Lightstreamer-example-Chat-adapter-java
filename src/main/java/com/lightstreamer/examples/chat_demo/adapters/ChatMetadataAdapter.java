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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.adapter.common.InitException;
import com.lightstreamer.adapter.metadata.CreditsException;
import com.lightstreamer.adapter.metadata.LoginInfo;
import com.lightstreamer.adapter.metadata.MessageException;
import com.lightstreamer.adapter.metadata.MetadataAdapter;
import com.lightstreamer.adapter.metadata.SessionListener;
import com.lightstreamer.adapter.metadata.SessionRequestHandler;
import com.lightstreamer.adapter.metadata.SubscriptionHandler;
import com.lightstreamer.adapter.metadata.SubscriptionInfo;


public class ChatMetadataAdapter implements MetadataAdapter {

    /**
     * The associated feed to which messages will be forwarded;
     * it is the Data Adapter itself.
     */
    private volatile ChatDataAdapter chatFeed;

    /**
     * The name of the Data Adapter that provided the current chatFeed.
     */
    private volatile String chatFeedName;

    /**
     * Private logger; a specific "LS_demos_Logger.Chat" category
     * should be supplied by log4j configuration.
     */
    private Logger logger;

    /**
     * Keeps unique identifiers for the currently connected clients.
     * Each client is uniquely identified by the client IP address and the
     * HTTP user agent; in case of conflicts, a custom progressive is appended
     * to the user agent. This set lists the concatenations of the current
     * IP and user agent pairs, to help determining uniqueness.
     */
    private final Set<String> uaIpPairs = ConcurrentHashMap.newKeySet();

    public ChatMetadataAdapter() {
    }

    @Override
    public void init(Map<String, String> params, File configDir) throws InitException {
        logger = LogManager.getLogger("LS_demos_Logger.Chat");

        logger.info("ChatMetadataAdapter ready");
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void onDataAdapterAvailable(String dataAdapterName, Object dataAdapter) {
        if (dataAdapter instanceof ChatDataAdapter) {
            if (this.chatFeed != null) {
                logger.warn("A ChatDataAdapter is already available, ignoring: " + dataAdapterName);
                return;
            }
            this.chatFeed = (ChatDataAdapter) dataAdapter;
            this.chatFeedName = dataAdapterName;
            logger.info("ChatDataAdapter available");
        }
    }

    @Override
    public void onDataAdapterUnavailable(String dataAdapterName) {
        if (dataAdapterName.equals(this.chatFeedName)) {
            this.chatFeed = null;
            this.chatFeedName = null;
            logger.info("ChatDataAdapter unavailable");
        }
    }

    @Override
    public CompletionStage<SessionRequestHandler> loginAsync(LoginInfo loginInfo) {
        //////////////////////////////////////////////////////////////////////////
        // WARNING: No authentication logic is implemented; any login is accepted.
        // This is suitable for demos only and MUST NOT be used in production.
        //////////////////////////////////////////////////////////////////////////

        Map<String, String> sessionInfo = new HashMap<>(loginInfo.getClientContext());

        return CompletableFuture.completedFuture(new SessionRequestHandler() {
            @Override
            public void onStart(SessionListener listener) {
                /* If needed, modify the user agent and store it directly
                 * in the session infos object.
                 * Note: we are free to change and store the received object.
                 */
                uniquelyIdentifyClient(sessionInfo);
            }

            @Override
            public CompletionStage<SubscriptionHandler> newSubscription(SubscriptionInfo info) {
                return CompletableFuture.completedFuture(new SubscriptionHandler(info));
            }

            @Override
            public CompletionStage<String> userMessageAsync(String message)
                    throws CreditsException, MessageException {

                // we won't introduce blocking operations, hence we can proceed inline

                if (message == null) {
                    logger.warn("Null message received");
                    throw new MessageException("Null message received");
                }

                //Split the string on the | character
                //The message must be of the form "CHAT|message"
                String[] pieces = message.split("\\|");

                if (chatFeed == null) {
                    logger.error("ChatDataAdapter not available");
                    throw new CreditsException("No chat feed available", 0, "No chat feed available");
                }
                handleChatMessage(pieces, message, sessionInfo);

                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void onClose() {
                //we have to remove the client identity from the pairs Set

                String ua = sessionInfo.get("USER_AGENT");
                String IP =  sessionInfo.get("REMOTE_IP");

                uaIpPairs.remove(IP+" "+ua);
            }

            /**
             * Modifies the clientContext to provide a unique identification
             * for the client session.
             */
            private void uniquelyIdentifyClient(Map<String, String> clientContext) {

                //extract user agent and ip from session infos
                String ua = clientContext.get("USER_AGENT");
                String ip = clientContext.get("REMOTE_IP");

                /*
                 * we need to ensure that each pair IP-User Agent is unique so that
                 * the sender can be identified on the client, if such pair is not
                 * unique we add a counter on the user agent string
                 */

                String count = "";
                int c = 0;
                //synchronize to ensure that we do not generate two identical pairs
                synchronized(uaIpPairs) {
                    while (uaIpPairs.contains(ip+" "+ua+" "+count)) {
                        c++;
                        count = "["+c+"]";
                    }

                    ua = ua+" "+count;

                    uaIpPairs.add(ip+" "+ua);
                }

                clientContext.put("USER_AGENT", ua);
            }

            private void handleChatMessage(String[] pieces, String message, Map<String,String> sessionInfo) throws MessageException {

                if (pieces.length != 2) {
                    logger.warn("Wrong message received: " + message);
                    throw new MessageException("Wrong message received");
                }

                //read from infos the IP and the user agent of the user
                String ua = sessionInfo.get("USER_AGENT");
                String ip =  sessionInfo.get("REMOTE_IP");

                //Check the message, it must be of the form "CHAT|message"
                if (pieces[0].equals("CHAT")) {
                    //and send it to the feed
                    if (!chatFeed.sendMessage(ip,ua,pieces[1])) {
                         logger.warn("Wrong message received: " + message);
                         throw new MessageException("Wrong message received");
                    }
                } else {
                     logger.warn("Wrong message received: " + message);
                     throw new MessageException("Wrong message received");
                }

            }
        });
    }

}