# Lightstreamer Chat Demo Adapter #

This project includes the resources needed to develop the Metadata and Data Adapters for the Lightstreamer Chat Demos that are pluggable into Lightstreamer Server. Please refer [here](http://www.lightstreamer.com/latest/Lightstreamer_Allegro-Presto-Vivace_5_1_Colosseo/Lightstreamer/DOCS-SDKs/General%20Concepts.pdf) for more details about Lightstreamer Adapters.
The Lightstreamer Chat Demos are very simple chat applications based on Lightstreamer.<br>
<br>
The project is comprised of source code and a deployment example. The source code is divided into two folders.

## Chat Adapter - src_chat ##
Contains the source code for the Chat Data Adapter. The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.<br>

## Metadata Adapter - src_metadata ##
Contains the source code for a Metadata Adapter to be associated with the Chat Demo Data Adapter.<br>
The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.

# Build #

If you want to skip the build process of this Adapter please note that the "Deployment_LS" folder of this project contains a ready-made deployment resource for the Lightstreamer server.<br>
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
After you have Downloaded and installed Lightstreamer, please go to the "adapters" folder of your Lightstreamer Server installation. You should find a "Demo" folder containing some adapters ready-made for several demo including the Chat one, please note that the MetaData Adapter jar installed is a mixed one that combines the functionality of several demos. If this is not your case because you have removed the "Demo" folder or you want to install the Chat Adapter Set alone, please follow these steps to configure the Chat Adapter Set properly:

1. You have to create a new folder to deploy the chat adapters, let's call it "chat", and a "lib" folder inside it.
2. Create an "adapters.xml" file inside the "chat" folder and use a content similar to that of the file in the directory /Deplolyment_LS/chat (this is an example configuration, you can modify it to your liking).
3. Copy into /chat/lib the jars (LS_chat_metadata_adapter.jar and LS_chat_data_adapter.jar) created in the previous section.

Now with the "chat" folder obtained on your behalf or with the one in the "Deployment_LS" folder of this project, you must follow these steps:

1. Make sure you have installed Lightstreamer Server, as explained in the GETTING_STARTED.TXT file in the installation home directory.
2. Make sure that Lightstreamer Server is not running.
3. Copy the "chat" directory and all of its files to the "adapters" subdirectory in your Lightstreamer Server installation home directory.
4. Copy the "ls-generic-adapters.jar" file from the "lib" directory of the sibling "Reusable_MetadataAdapters" SDK example to the "shared/lib" subdirectory in your Lightstreamer Server installation home directory.
5. Lightstreamer Server is now ready to be launched.

Please test your Adapter with one of the clients in the [list](https://github.com/Weswit/Lightstreamer-example-Chat-adapter-java#clients-using-this-adapter) below.

# See Also #

## Clients using this Adapter ##

* [Lightstreamer Chat Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-chat-client-javascript)

## Related projects ##

* [Lightstreamer Reusable Metadata Adapter in Java](https://github.com/Weswit/Lightstreamer-example-ReusableMetadata-adapter-java)
* [Lightstreamer Instant Messenger Demo Adapter](https://github.com/Weswit/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer Messenger Demo Client for JavaScript](https://github.com/Weswit/Lightstreamer-example-Messenger-client-javascript)

# Lightstreamer Compatibility Notes #

- Compatible with Lightstreamer SDK for Java Adapters since 5.1

