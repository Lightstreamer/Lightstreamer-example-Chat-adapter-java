/*
  Copyright 2013 Weswit Srl

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

package chat_demo.adapters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.lightstreamer.interfaces.data.DataProviderException;
import com.lightstreamer.interfaces.data.FailureException;
import com.lightstreamer.interfaces.data.ItemEventListener;
import com.lightstreamer.interfaces.data.SmartDataProvider;
import com.lightstreamer.interfaces.data.SubscriptionException;


public class ChatDataAdapter implements SmartDataProvider {

    private static final String ITEM_NAME = "chat_room";
    
    /**
     * A static map, to be used by the Metadata Adapter to find the data
     * adapter instance; this allows the Metadata Adapter to forward client
     * messages to the adapter.
     * The map allows multiple instances of this Data Adapter to be included
     * in different Adapter Sets. Each instance is identified with the name
     * of the related Adapter Set; defining multiple instances in the same
     * Adapter Set is not allowed.
     */
    public static final ConcurrentHashMap<String, ChatDataAdapter> feedMap =
        new ConcurrentHashMap<String, ChatDataAdapter>();

    /**
     * Private logger; a specific "LS_demos_Logger.Chat" category
     * should be supplied by log4j configuration.
     */
    private Logger logger;

    /**
     * The listener of updates set by Lightstreamer Kernel.
     */
    private ItemEventListener listener;

    /**
     * Used to enqueue the calls to the listener.
     */
    private final ExecutorService executor;

    /**
     * An object representing the subscription.
     */
    private volatile Object subscribed;

    public ChatDataAdapter() {
        executor = Executors.newSingleThreadExecutor();
    }

    public void init(Map params, File configDir) throws DataProviderException {
        String logConfig = (String) params.get("log_config");
        if (logConfig != null) {
            File logConfigFile = new File(configDir, logConfig);
            String logRefresh = (String) params.get("log_config_refresh_seconds");
            if (logRefresh != null) {
                DOMConfigurator.configureAndWatch(logConfigFile.getAbsolutePath(), Integer.parseInt(logRefresh) * 1000);
            } else {
                DOMConfigurator.configure(logConfigFile.getAbsolutePath());
            }
        }
        logger = Logger.getLogger("LS_demos_Logger.Chat");

        // Read the Adapter Set name, which is supplied by the Server as a parameter
        String adapterSetId = (String) params.get("adapters_conf.id");

        // Put a reference to this instance on a static map
        // to be read by the Metadata Adapter
        feedMap.put(adapterSetId, this);

        // Adapter ready
        logger.info("ChatDataAdapter ready");

    }

    public void subscribe(String item, Object handle, boolean arg2)
            throws SubscriptionException, FailureException {

        if (!item.equals(ITEM_NAME)) {
            // only one item for a unique chat room is managed
            throw new SubscriptionException("No such item");
        }
        
        assert(subscribed == null);

        subscribed = handle;

    }


    public void unsubscribe(String arg0) throws SubscriptionException,
        FailureException {

        assert(subscribed != null);

        subscribed = null;
    }

    public boolean isSnapshotAvailable(String arg0)
            throws SubscriptionException {
        //This adapter does not handle the snapshot.
        //If there is someone subscribed the snapshot is kept by the server
        return false;
    }


    public void setListener(ItemEventListener listener) {
        this.listener = listener;

    }


    /**
     * Accepts message submission for the unique chat room.
     * The sender is identified by an IP address and a nickname.
     */
    public boolean sendMessage(String IP, String nick, String message) {
        final Object currSubscribed = subscribed;
        if (currSubscribed == null) {
            return false;
        }

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
                listener.smartUpdate(currSubscribed, update, false);
            }
        };

        //We add the task on the executor to pass to the listener the actual status
        executor.execute(updateTask);

        return true;
    }

    public void subscribe(String arg0, boolean arg1)
        throws SubscriptionException, FailureException {
    //NEVER CALLED

    }

}