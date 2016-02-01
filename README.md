# Lightstreamer - Basic Chat Demo - Java Adapter 
<!-- START DESCRIPTION lightstreamer-example-chat-adapter-java -->

The *Lightstreamer Basic Chat Demo* is a very simple chat application based on [Lightstreamer](http://www.lightstreamer.com) for its real-time communication needs.

This project shows the Data Adapter and Metadata Adapters for the *Basic Chat Demo* and how they can be plugged into Lightstreamer Server.
 
As an example of a client using this adapter, you may refer to the [Basic Chat Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-chat-client-javascript) and view the corresponding [Live Demo](http://demos.lightstreamer.com/ChatDemo/).
 
## Details

This project includes the implementation of the [SmartDataProvider](http://www.lightstreamer.com/docs/adapter_java_inprocess_api/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](http://www.lightstreamer.com/docs/adapter_java_inprocess_api/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface for the *Lightstreamer Basic Chat Demos*.

### Data Adapter
The `src_chat` folder contains the source code for the Chat Data Adapter. The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.

### Metadata Adapter
The `src_metadata` folder contains the source code for a Metadata Adapter to be associated with the Chat Demo Data Adapter.

The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Lightstreamer/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.
<!-- END DESCRIPTION lightstreamer-example-chat-adapter-java -->

### The Adapter Set Configuration

This Adapter Set is configured and will be referenced by the clients as `CHAT`. 

The `adapters.xml` file for the Basic Chat Demo, should look like:
```xml
<?xml version="1.0"?>

<adapters_conf id="CHAT">

  <!--
    Not all configuration options of an Adapter Set are exposed by this file.
    You can easily expand your configurations using the generic template,
    `DOCS-SDKs/sdk_adapter_java_inprocess/doc/adapter_conf_template/adapters.xml`,
    as a reference.
  -->

    <metadata_adapter_initialised_first>Y</metadata_adapter_initialised_first>

    <metadata_provider>

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

        <adapter_class>chat_demo.adapters.ChatDataAdapter</adapter_class>

        <!-- Optional for ChatDataAdapter.
             Configuration file for the Adapter's own logging.
             Leans on the Metadata Adapter for the configuration refresh.
             Logging is managed through log4j. -->
        <param name="log_config">adapters_log_conf.xml</param>

    </data_provider>


</adapters_conf>
```

<i>NOTE: not all configuration options of an Adapter Set are exposed by the file suggested above. 
You can easily expand your configurations using the generic template, `DOCS-SDKs/sdk_adapter_java_inprocess/doc/adapter_conf_template/adapters.xml`, as a reference.</i><br>
<br>
Please refer [here](http://www.lightstreamer.com/docs/base/General%20Concepts.pdf) for more details about Lightstreamer Adapters.

## Install
If you want to install a version of this demo in your local Lightstreamer Server, follow these steps:
* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* In the `adapters` folder of your Lightstreamer Server installation, you may find a `Demo` folder containing some adapters ready-made for several demos, including the Chat one. If this is the case, you have already a Chat Demo Adapter installed and you may stop here. Please note that, in case of `Demo` folder already installed, the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If the `Demo` folder is not installed, or you have removed it, or you want to install the Chat Adapter Set alone, please continue to follow the next steps.
* Get the `deploy.zip` file of the [latest release](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java/releases), unzip it, and copy the just unzipped `Chat` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching one of the clients listed in [Clients Using This Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java#clients-using-this-adapter).

## Build
To build your own version of `LS_chat_metadata_adapter.jar` and `LS_chat_data_adapter.jar`, instead of using the one provided in the `deploy.zip` file from the [Install](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java#install) section above, follow these steps:
* Clone this project.
* Get the `ls-adapter-interface.jar` file from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download), and copy it into the `lib` folder.
* Get the `log4j-1.2.17.jar` file from [Apache log4j](https://logging.apache.org/log4j/1.2/) and copy it into the `lib` folder.
* Create the jars `LS_chat_metadata_adapter.jar` and `LS_chat_data_adapter.jar` created for something like these commands:
```sh
 > mkdir tmp_classes/adapter tmp_classes/metadata
 > javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.17.jar;lib/ls-adapter-interface.jar -sourcepath src/src_chat -d tmp_classes/adapter src/src_chat/chat_demo/adapters/ChatDataAdapter.java
 > jar cvf LS_chat_data_adapter.jar -C tmp_classes/adapter .
 > javac -source 1.7 -target 1.7 -nowarn -g -classpath lib/log4j-1.2.17.jar;lib/ls-adapter-interface.jar;LS_chat_data_adapter.jar -sourcepath src/src_metadata -d tmp_classes/metadata src/src_metadata/chat_demo/adapters/ChatMetadataAdapter.java
 > jar cvf LS_chat_metadata_adapter.jar -C tmp_classes/metadata .
```
* Stop Lightstreamer Server; copy the just compiled LS_chat_metadata_adapter.jar in the adapters/Chat/lib folder of your Lightstreamer Server installation; restart Lightstreamer Server.
 
## See Also

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Basic Chat Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-chat-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [Lightstreamer - Basic Chat Demo - Node.js Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-node)
* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Basic Messenger Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-Messenger-client-javascript)


## Lightstreamer Compatibility Notes

* Compatible with Lightstreamer SDK for Java In-Process Adapters since 6.0
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 5.1, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java/releases/tag/for_Lightstreamer_5.1.2).
