# Lightstreamer - Basic Chat Demo - Java Adapter #
<!-- START DESCRIPTION lightstreamer-example-chat-adapter-java -->

This project includes the resources needed to develop the Metadata and Data Adapters for the Lightstreamer Chat Demos that are pluggable into Lightstreamer Server. Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.
The Lightstreamer Chat Demos are very simple chat applications based on Lightstreamer.<br>
<br>
The project is comprised of source code and a deployment example. The source code is divided into two folders.

## Chat Adapter - src_chat ##
Contains the source code for the Chat Data Adapter. The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.<br>

## Metadata Adapter - src_metadata ##
Contains the source code for a Metadata Adapter to be associated with the Chat Demo Data Adapter.<br>
The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.

<!-- END DESCRIPTION lightstreamer-example-chat-adapter-java -->

# Build #

If you want to skip the build process of this Adapter please note that in the [deploy release](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java/releases) of this project you can find the "deploy.zip" file that contains a ready-made deployment resource for the Lightstreamer server. <br>
Otherwise follow these steps:

*  Get the ls-adapter-interface.jar, ls-generic-adapters.jar, and log4j-1.2.15.jar files from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download).
*  Create the jars LS_chat_metadata_adapter.jar and LS_chat_data_adapter.jar created for something like these commands:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;compile_libs/ls-generic-adapters/ls-generic-adapters.jar -sourcepath src/src_chat -d tmp_classes src/src_chat/chat_demo/adapters/ChatDataAdapter.java
 
 >jar cvf LS_chat_data_adapter.jar -C tmp_classes chat_demo
 
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;compile_libs/ls-generic-adapters/ls-generic-adapters.jar;LS_chat_data_adapter.jar -sourcepath src/src_metadata -d tmp_classes src/src_metadata/chat_demo/adapters/ChatMetadataAdapter.java
 
 >jar cvf LS_chat_metadata_adapter.jar -C tmp_classes chat_demo
```

# Deploy #

Now you are ready to deploy the Chat Demo Adapter into Lighstreamer server.<br>
After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation. You should find a "Demo" folder containing some adapters ready-made for several demo including the Chat one, please note that the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If this is not your case because you have removed the "Demo" folder or you want to install the Chat Adapter Set alone, please follow these steps to configure the Chat Adapter Set properly.

You have to create a specific folder to deploy the Chat Demo Adapter otherwise get the ready-made "Chat" deploy folder from "deploy.zip" of the [latest release](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java/releases) of this project and skips the next three steps.<br>

1. You have to create a new folder to deploy the chat adapters, let's call it "Chat", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "Chat" folder and use the following content (this is an example configuration, you can modify it to your liking):

```xml
<?xml version="1.0"?>

<!-- Mandatory. Define an Adapter Set and sets its unique ID. -->
<adapters_conf id="CHAT">

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
<br>
3. Copy into /chat/lib the jars (LS_chat_metadata_adapter.jar and LS_chat_data_adapter.jar) created in the previous section.

Now your "Chat" folder is ready to be deployed in the Lightstreamer server, please follow these steps:

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the "chat" directory and all of its files to the "adapters" subdirectory in your Lightstreamer Server installation home directory.
4. Copy the "ls-generic-adapters.jar" file from the "lib" directory of the sibling "Reusable_MetadataAdapters" SDK example to the "shared/lib" subdirectory in your Lightstreamer Server installation home directory.
5. Lightstreamer Server is now ready to be launched.

Please test your Adapter with one of the clients in the [list](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java#clients-using-this-adapter) below.

# See Also #

## Clients using this Adapter ##
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Basic Chat Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-chat-client-javascript)

<!-- END RELATED_ENTRIES -->

## Related projects ##

* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Basic Messenger Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-Messenger-client-javascript)

## The same Demo Adapter with other technologies ##
* [Lightstreamer - Basic Chat Demo - Node.js Adapter](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-node)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 6.0

