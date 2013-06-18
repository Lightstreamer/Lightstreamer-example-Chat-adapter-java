
Lightstreamer Chat Demo Adapter
===============================

This project include the implementation of the Metadata and Data Adapter in Java managing the functionalities required by Lightstreamer Chat demos.

Java Data Adapter and Metadata Adapter
--------------------------------------

The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.<br>

The Metadata Adapter Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.

Configure Lightstreamer
-----------------------

After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation. You should find a "Demo" folder containing some adapter ready-made for several demo including the Chat ones, please note that the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If this is not your case because you have removed the "Demo" folder or you want to install the Chat Adapter Set alone, please follow this steps to configure the Chat Adapter Set properly:

1. Go to the "adapters" folder of your Lightstreamer Server installation. You have to create a new folder to deploy the chat adapters, let's call it "chat", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "chat" folder and use the following contents (this is an example configuration, you can modify it to your liking):
```xml      
<?xml version="1.0"?>
  <adapters_conf id="DEMO">

    <!-- Mandatory. Define the Metadata Adapter. -->
    <metadata_provider>

        <!-- Mandatory. Java class name of the adapter. -->
        <adapter_class>chat_demo.adapters.ChatMetadataAdapter</adapter_class>

        <!-- Optional for ChatMetadataAdapter.
             Configuration file for the Adapter's own logging.
             Logging is managed through log4j. -->
        <param name="log_config">adapters_log_conf.xml</param>
        <param name="log_config_refresh_seconds">10</param>

        <!-- Optional, managed by the inherited LiteralBasedProvider.
             See LiteralBasedProvider javadoc. -->
        <!--
        <param name="max_bandwidth">40</param>
        <param name="max_frequency">3</param>
        <param name="buffer_size">30</param>
        <param name="prefilter_frequency">5</param>
        <param name="allowed_users">user123,user456</param>
        -->
        <param name="distinct_snapshot_length">30</param>

        <!-- Optional, managed by the inherited LiteralBasedProvider.
             See LiteralBasedProvider javadoc. -->
        <param name="item_family_1">chat_room</param>
        <param name="modes_for_item_family_1">DISTINCT</param>

    </metadata_provider>


    <data_provider name="CHAT_ROOM">

        <!-- Mandatory. Java class name of the adapter. -->
        <adapter_class>chat_demo.adapters.ChatDataAdapter</adapter_class>

        <!-- Optional for ChatDataAdapter.
             Configuration file for the Adapter's own logging.
             Leans on the Metadata Adapter for the configuration refresh.
             Logging is managed through log4j. -->
        <param name="log_config">adapters_log_conf.xml</param>

    </data_provider>


  </adapters_conf>
```
3. Get the ls-adapter-interface.jar, ls-generic-adapters.jar, and log4j-1.2.15.jar files from the [Lightstreamer 5 Colosseo distribution](http://www.lightstreamer.com/download).
4. Copy into "lib" folder the jars LS_chat_metadata_adapter.jar and LS_chat_data_adapter.jar created for something like these commands:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;compile_libs/ls-generic-adapters/ls-generic-adapters.jar -sourcepath src/src_chat -d tmp_classes src/src_chat/chat_demo/adapters/ChatDataAdapter.java
 
 >jar cvf LS_chat_data_adapter.jar -C tmp_classes chat_demo
 
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;compile_libs/ls-generic-adapters/ls-generic-adapters.jar;LS_chat_data_adapter.jar -sourcepath src/src_metadata -d tmp_classes src/src_metadata/chat_demo/adapters/ChatMetadataAdapter.java
 
 >jar cvf LS_chat_metadata_adapter.jar -C tmp_classes chat_demo
```

See Also
--------

* [Lightstreamer Chat Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-chat-client-javascript)
* [Lightstreamer Reusable Metadata Adapter in Java](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)

Lightstreamer Compatibility Notes
---------------------------------

- Compatible with Lightstreamer SDK for Java Adapters since 5.1
