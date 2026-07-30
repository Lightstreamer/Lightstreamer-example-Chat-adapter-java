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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lightstreamer.adapter.common.InitException;
import com.lightstreamer.adapter.common.Mode;
import com.lightstreamer.adapter.data.DataAdapter;
import com.lightstreamer.adapter.data.ItemListener;
import com.lightstreamer.adapter.data.ItemManager;
import com.lightstreamer.adapter.data.SubscriptionException;


public class ChatDataAdapter implements DataAdapter {

    private static final String ITEM_NAME = "chat_room";

    /**
     * Private logger; a specific "LS_demos_Logger.Chat" category
     * should be supplied by log4j configuration.
     */
    private Logger logger;

    /**
     * The currently active ItemManager for the chat room item.
     * At most one ItemManager can be active at a time, since only
     * one item ("chat_room") is supported.
     */
    private volatile ChatItemManager currentItemManager;

    /**
     * Used to enqueue the calls to the listener.
     */
    private final ExecutorService executor;
    
    /**
     * A configurable list of messages for a custom initial snapshot.
     */
    private final LinkedList<String> customSnapshot = new LinkedList<>();

    /**
     * Boolean flag for periodic flush of snapshot (call clearSnaphot).
     */
    private boolean flushSnapshot;
    
    /**
     * Interval period (in millis) for snapshot flush.
     */
    private int flushInterval;
    
    /**
     * Timer for snapshot flush.
     */
    private Timer myTimer;
    
    /**
     * Default interval period (in millis) for snapshot flush.
     */
    private static final int DEFAULT_FLUSH_INTERVAL = 30 * 60 * 1000;
    
    /**
     * Boolean flag that indicate messages presence.
     */
    private boolean messagesPresence;
    
    public ChatDataAdapter() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void init(Map<String, String> params, File configDir) throws InitException {
        
        // Logging configuration for the demo is carried out in the init
        // method of Metadata Adapter. In order to be sure that this method 
        // is executed after log configuration was completed, this parameter 
        // must be present in the Adapter Set configuration (adapters.xml):
        // <metadata_adapter_initialised_first>Y</metadata_adapter_initialised_first>
        logger = LogManager.getLogger("LS_demos_Logger.Chat");

        boolean stop = false;
        for (int i = 1; ! stop; i++) {
            if (params.containsKey("custom_snapshot_" + i)) {
                String msg = params.get("custom_snapshot_" + i);
                this.customSnapshot.add(msg);
            } else {
                stop = true;
            }
        }
        
        if (params.containsKey("flush_snapshot")) {
            String tmp = params.get("flush_snapshot");
            this.flushSnapshot = Boolean.parseBoolean(tmp);
        } else {
            this.flushSnapshot = false;
        }
        
        if (params.containsKey("flush_snapshot_interval")) {
            String tmp = params.get("flush_snapshot_interval");
            this.flushInterval = Integer.parseInt(tmp);
        } else {
            this.flushInterval = DEFAULT_FLUSH_INTERVAL;
        }
        
        this.messagesPresence = false;

        // Adapter ready
        logger.info("ChatDataAdapter ready");

    }


    /**
     * Accepts message submission for the unique chat room.
     * The sender is identified by an IP address and a nickname.
     */
    public boolean sendMessage(String IP, String nick, String message) {
        final ChatItemManager mgr = currentItemManager;
        if (mgr == null) {
            return false;
        }
        final ItemListener currListener = mgr.listener;

        //NB no anti-flood control

        if (message == null || message.length() == 0) {
            logger.warn("Received empty or null message");
            return false;
        }
        if (nick == null || nick.length() == 0) {
            logger.warn("Received empty or null nick");
            return false;
        }
        if (IP == null || IP.length() == 0) {
            logger.warn("Received empty or null IP");
            return false;
        }
        
        this.messagesPresence = true;

        Date now = new Date();
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(now);
        long raw_timestamp = now.getTime();
        

        logger.debug(timestamp + "|New message: " + IP + "->" + nick + "->" + message);

        final HashMap<String, String> update = new HashMap<String, String>();
        update.put("nick", nick);
        update.put("message", message);
        update.put("timestamp", timestamp);
        update.put("raw_timestamp", String.valueOf(raw_timestamp));
        update.put("IP", IP);

        //If we have a listener create a new Runnable to be used as a task to pass the
        //new update to the listener
        Runnable updateTask = new Runnable() {
            public void run() {
                // call the update on the listener;
                // in case the listener has just been detached,
                // the listener should detect the case
                currListener.update(update);
            }
        };

        //We add the task on the executor to pass to the listener the actual status
        executor.execute(updateTask);

        return true;
    }

    // used in case of flush_snapshot set to true.
    public void clearHistory() {
        final ChatItemManager mgr = currentItemManager;
        if (mgr == null || this.messagesPresence == false) {
            return;
        }
        final ItemListener currListener = mgr.listener;
        //If we have a listener create a new Runnable to be used as a task to pass the
        //event to the listener
        Runnable updateTask = new Runnable() {
            public void run() {
                // call the update on the listener;
                // in case the listener has just been detached,
                // the listener should detect the case
                currListener.clearSnapshot();
            }
        };

        executor.execute(updateTask);
        
        this.messagesPresence = false;
    }

    @Override
    public CompletionStage<ItemManager> getItemManager(String itemName) throws SubscriptionException {
        if (!itemName.equals(ITEM_NAME)) {
        	// only one item for a unique chat room is managed
            CompletableFuture<ItemManager> f = new CompletableFuture<>();
            f.completeExceptionally(new SubscriptionException("No such item: " + itemName));
            return f;
        }
        return CompletableFuture.completedFuture(new ChatItemManager());
    }

    /**
     * ItemManager for the unique "chat_room" item.
     * At most one instance can be active at a time.
     */
    private class ChatItemManager implements ItemManager {

        private volatile ItemListener listener;

        private void sendCustomSnapshot() {
            int i = 1;
            for (String snapshotMsg : customSnapshot) {
                sendMessage("0.0.0.0", "user" + i, snapshotMsg);
                i++;
            }
        }

        @Override
        public Mode getSupportedMode() {
            return Mode.DISTINCT;
        }

        @Override
        public Integer getDistinctSnapshotLength() {
            return 30;
        }

        @Override
        public CompletionStage<Void> subscribe(ItemListener itemListener) {
            assert(currentItemManager == null);

            this.listener = itemListener;
            currentItemManager = this;

            sendCustomSnapshot();

            if(flushSnapshot) {
                // Start Thread for periodic flush of the snapshot.
                myTimer = new Timer(true);
                
                myTimer.scheduleAtFixedRate(new TimerTask() {
                   @Override
                    public void run() {
                        clearHistory();
                        sendCustomSnapshot();
                    }
                }, new Date(System.currentTimeMillis() + flushInterval), flushInterval);
            }

            return CompletableFuture.completedFuture(null);
        }

        @Override
        public boolean isSnapshotAvailable() {
            //This adapter does not handle the snapshot.
            //If there is someone subscribed the snapshot is kept by the server
            return false;
        }

        @Override
        public CompletionStage<Void> unsubscribe() {
            assert(currentItemManager != null);

            currentItemManager = null;
            this.listener = null;
            
            if (myTimer != null) {
                myTimer.cancel();
                myTimer.purge();
                myTimer = null;
            }

            return CompletableFuture.completedFuture(null);
        }
    }

    @Override
    public void shutdown() {
        executor.shutdown();
        if (myTimer != null) {
            myTimer.cancel();
        }
    }

}