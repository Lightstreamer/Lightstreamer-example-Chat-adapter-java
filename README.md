# Lightstreamer - Basic Chat Demo - Java Adapter
<!-- START DESCRIPTION lightstreamer-example-chat-adapter-java -->

The *Lightstreamer Basic Chat Demo* is a very simple chat application based on [Lightstreamer](http://www.lightstreamer.com) for its real-time communication needs.

This project shows the Data Adapter and Metadata Adapters for the *Basic Chat Demo* and how they can be plugged into Lightstreamer Server.

As an example of a client using this adapter, you may refer to the [Basic Chat Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-chat-client-javascript) and view the corresponding [Live Demo](http://demos.lightstreamer.com/ChatDemo/).

## Details

This project includes the implementation of the [SmartDataProvider](https://lightstreamer.com/api/ls-adapter-inprocess/7.4.1/com/lightstreamer/interfaces/data/SmartDataProvider.html) interface and the [MetadataProviderAdapter](https://lightstreamer.com/api/ls-adapter-inprocess/7.4.1/com/lightstreamer/interfaces/metadata/MetadataProviderAdapter.html) interface for the *Lightstreamer Basic Chat Demos*.

### Data Adapter
The `ChatDataAdapter.java` class contains the source code for the Chat Data Adapter. The Data Adapter accepts message submission for the unique chat room. The sender is identified by an IP address and a nickname.

It's possible to flush chat history based on optional parameters provided in the `adapters.xml` file.

### Metadata Adapter
The `ChatMetadataAdapter.java` class contains the source code for a Metadata Adapter to be associated with the Chat Demo Data Adapter.

The Metadata Adapter inherits from the reusable [LiteralBasedProvider](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess/tree/v7.4.1#literalbasedprovider-metadata-adapter) and just adds a simple support for message submission. It should not be used as a reference for a real case of client-originated message handling, as no guaranteed delivery and no clustering support is shown.
<!-- END DESCRIPTION lightstreamer-example-chat-adapter-java -->

### The Adapter Set Configuration

This Adapter Set is configured and will be referenced by the clients as `CHAT`.

The `adapters.xml` file for the Basic Chat Demo, should look like:
```xml
<?xml version="1.0"?>

<adapters_conf id="CHAT">

    <metadata_adapter_initialised_first>Y</metadata_adapter_initialised_first>

    <metadata_provider>

        <adapter_class>com.lightstreamer.examples.chat_demo.adapters.ChatMetadataAdapter</adapter_class>

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
        <param name="item_family_1">chat_room.*</param>
        <param name="modes_for_item_family_1">DISTINCT</param>

    </metadata_provider>


    <data_provider name="CHAT_ROOM">

        <adapter_class>com.lightstreamer.examples.chat_demo.adapters.ChatDataAdapter</adapter_class>

        <!-- Optional for ChatDataAdapter.
             Configuration flag for periodic flush of the snapshot.
             Default: false. -->
        <param name="flush_snapshot">true</param>

        <!-- Optional for ChatDataAdapter.
             Configuration interval in millis for snapshot flush.
             Default: 30 minutes. -->
        <!-- <param name="flush_snapshot_interval">1800000</param> -->
    </data_provider>


</adapters_conf>
```

<i>NOTE: not all configuration options of an Adapter Set are exposed by the file suggested above.
You can easily expand your configurations using the generic template, see the [Java In-Process Adapter Interface Project](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess/tree/v7.4.1#configuration) for details.</i><br>
<br>
Please refer [here](https://lightstreamer.com/docs/ls-server/latest_7_3/General%20Concepts.pdf) for more details about Lightstreamer Adapters.


## Install

If you want to install a version of this demo in your local Lightstreamer Server, follow these steps:
* Download *Lightstreamer Server* (Lightstreamer Server comes with a free non-expiring demo license for 20 connected users) from [Lightstreamer Download page](http://www.lightstreamer.com/download.htm), and install it, as explained in the `GETTING_STARTED.TXT` file in the installation home directory.
* Get the `deploy.zip` file of the ["Release for Lightstreamer 7.3" release](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java/releases), unzip it, and copy the just unzipped `Chat` folder into the `adapters` folder of your Lightstreamer Server installation.
* Launch Lightstreamer Server.
* Test the Adapter, launching one of the clients listed in [Clients Using This Adapter](#clients-using-this-adapter).


## Build

To build your own version of `chat-adapter-java-x.y.z.jar` instead of using the one provided in the `deploy.zip` file from the [Install](#install) section above, you have two options:
either use [Maven](https://maven.apache.org/) (or other build tools) to take care of dependencies and building (recommended) or gather the necessary jars yourself and build it manually.
For the sake of simplicity only the Maven case is detailed here.

### Maven

You can easily build and run this application using Maven through the pom.xml file located in the root folder of this project. As an alternative, you can use an alternative build tool (e.g. Gradle, Ivy, etc.) by converting the provided pom.xml file.

Assuming Maven is installed and available in your path you can build the demo by running
```sh 
 mvn install dependency:copy-dependencies 
```

## See Also

### Clients Using This Adapter
<!-- START RELATED_ENTRIES -->

* [Lightstreamer - Basic Chat Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-chat-client-javascript)

<!-- END RELATED_ENTRIES -->

### Related Projects

* [Lightstreamer - Basic Chat Demo - Node.js Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-node)
* [LiteralBasedProvider Metadata Adapter](https://github.com/Lightstreamer/Lightstreamer-lib-adapter-java-inprocess#literalbasedprovider-metadata-adapter)
* [Lightstreamer - Basic Messenger Demo - Java Adapter](https://github.com/Lightstreamer/Lightstreamer-example-Messenger-adapter-java)
* [Lightstreamer - Basic Messenger Demo - HTML Client](https://github.com/Lightstreamer/Lightstreamer-example-Messenger-client-javascript)


## Lightstreamer Compatibility Notes

- Compatible with Lightstreamer SDK for Java In-Process Adapters version 7.3 to 7.4.
- For a version of this example compatible with Lightstreamer SDK for Java In-Process Adapters version6.0, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java/releases/tag/pre_mvn).
- For a version of this example compatible with Lightstreamer SDK for Java Adapters version 5.1, please refer to [this tag](https://github.com/Lightstreamer/Lightstreamer-example-Chat-adapter-java/releases/tag/for_Lightstreamer_5.1.2).
