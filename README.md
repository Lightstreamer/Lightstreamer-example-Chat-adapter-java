# Lightstreamer - Basic Chat Demo - Java Adapter 
<!-- START DESCRIPTION lightstreamer-example-chat-adapter-java -->

The *Lightstreamer Basic Chat Demo* is a very simple chat application based on [Lightstreamer](http://www.lightstreamer.com) for its real-time communication needs.

This project shows the Data Adapter and Metadata Adapters for the *Basic Chat Demo* and how they can be plugged into Lightstreamer Server.
 
As an example of a client using this adapter, you may refer to the [Basic Chat Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-chat-client-javascript) and view the corresponding [Live Demo](http://demos.lightstreamer.com/ChatDemo/).
 
## Details

This project includes the implementation of the [SmartDataProvider](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](http://www.lightstreamer.com/docs/adapter_java_api/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface for the *Lightstreamer Basic Chat Demos*. Please refer to [General Concepts](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.

### Data Adapter ##
The `src_chat` folder contains the source code for the Chat Data Adapter. The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.

### Metadata Adapter ##
The `src_metadata` folder contains the source code for a Metadata Adapter to be associated with the Chat Demo Data Adapter.

The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.

**NOTE: At this stage, the demo is based on a version of LS Java Adapter SDK that is currently available only as a pre-release. Skip the notes below and refer to the "for_Lightstreamer_5.1.2" tag for a demo version suitable for building and deploying.**
<!-- END DESCRIPTION lightstreamer-example-chat-adapter-java -->

## Install
If you want to install a version of this demo in your local Lightstreamer Server, follow these steps:
* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* In the `adapters` folder of your Lightstreamer Server installation, you may find a `Demo` folder containing some adapters ready-made for several demos, including the Chat one. If this is the case, you have already a Chat Demo Adapter installed and you may stop here. Please note that, in case of `Demo` folder already installed, the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If the `Demo` folder is not installed, or you have removed it, or you want to install the Chat Adapter Set alone, please continue to follow the next steps.
* Get the `deploy.zip` file of the [latest release](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java/releases), unzip it, and copy the just unzipped `Chat` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching one of the clients listed in [Clients Using This Adapter](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java#clients-using-this-adapter).

## Build
To build your own version of `LS_chat_metadata_adapter.jar` and `LS_chat_data_adapter.jar`, instead of using the one provided in the `deploy.zip` file from the [Install](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java#install) section above, follow these steps:
* Clone this project.
* Get the `ls-adapter-interface.jar`, `ls-generic-adapters.jar`, and `log4j-1.2.15.jar` files from the [latest Lightstreamer distribution](http://www.lightstreamer.com/download), and copy them into the `lib` directory.
* Create the jars `LS_chat_metadata_adapter.jar` and `LS_chat_data_adapter.jar` created for something like these commands:
```sh
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;compile_libs/ls-generic-adapters/ls-generic-adapters.jar -sourcepath src/src_chat -d tmp_classes src/src_chat/chat_demo/adapters/ChatDataAdapter.java
 >jar cvf LS_chat_data_adapter.jar -C tmp_classes chat_demo
 
 >javac -source 1.7 -target 1.7 -nowarn -g -classpath compile_libs/log4j-1.2.15.jar;compile_libs/ls-adapter-interface/ls-adapter-interface.jar;compile_libs/ls-generic-adapters/ls-generic-adapters.jar;LS_chat_data_adapter.jar -sourcepath src/src_metadata -d tmp_classes src/src_metadata/chat_demo/adapters/ChatMetadataAdapter.java
 >jar cvf LS_chat_metadata_adapter.jar -C tmp_classes chat_demo
```
* Stop Lightstreamer Server; copy the just compiled LS_chat_metadata_adapter.jar in the adapters/Chat/lib folder of your Lightstreamer Server installation; restart Lightstreamer Server.
 
## See Also

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Basic Chat Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-chat-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [Lightstreamer - Basic Chat Demo - Node.js Adapter](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-node)
* [Lightstreamer - Reusable Metadata Adapters - Java Adapter](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Weswit/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Basic Messenger Demo - HTML Client](https://github.com/Weswit/Lightstreamer-example-Messenger-client-javascript)


## Lightstreamer Compatibility Notes

- Compatible with Lightstreamer SDK for Java Adapters since 6.0
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 5.1.2, please refer to [this tag](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java/releases/tag/for_Lightstreamer_5.1.2).
