Graphene: Search and Graph your data 
======
 Graphene is a high performance Java based web framework you can use to build a searching and graphing application on top of your data.  It is datastore agnostic, but has some built in support for Elastic Search, SQL Databases, and Titan.

For configurations, building, and deployment instructions, view the [Graphene Wiki](https://github.com/Sotera/graphene/wiki).
Please see the road maps in the wiki for new features and their slated order.
 
Using Graphene 
------
 The core of the Graphene project contains multiple modules, some of which are optional.  The main module that you'll need it the graphene-web module, since it acts as a WAR overlay for your Java based web application.  The current example that will be released soon is based on Instagram data, and will be shown as a separate github project.  Previous demos included Enron, Scott Walker email data, and Kiva Microloan data.

 It's our goal that you use the available modules if they make sense for your needs, but we allow you to wire in your own code in most places.  In addition, Graphene leverages Apache Tapestry's auto discovery of modules, so we will be expanding the number of 'plugin' modules available.  Currently we offer the graphene-augment-mitie as an example of such a module.  It's abilities are made available in your app simply by including the jar file in your POM, no code changes necessary!
 
Building Graphene 
------ 
 Graphene is built using [Apache Maven](http://maven.apache.org) version 3.0.4 or later and a recent version of [currenly Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html). 
 
 * A plain 'mvn clean install' will build all the jar files and a single war file (to be overlaid on your project)
 * Test execution is part of the build, but you can add -DskipTests=true to cut down on the build time.
 * A BuildAll.bat is supplied for windows users.  This will perform a few cleans to overcome some windows issues, and then compile and install to your local maven repo.
 
Graphene overview
------
Graphene expects that you are familiar with some modern Java concepts:

 *  Interfaces and Implementations
 *  Knowledge of Maven, or a knowledge of how to search for answers to your questions
 *  Dependency Injection (aka Inversion of Control or IOC or DI)
  *  Graphene uses [Apache Tapestry](http://tapestry.apache.org/) to provide the IOC framework.  It is very similar to Guice, but also allows distributed configuration and can act as a light weight OSGI alternative.  We may use the term 'wiring' and 'binding' interchangeably.  Essentially a registry is created and lives throughout the life of your program, which defines which implementation services will get when they ask for the interface. The IOC 'wiring' is mostly done at the customer implementation level, although basic shared services are wired in modules within graphene-parent.  For consistency's sake, any class that performs IOC wiring we suffix with the word "Module", i.e. AppModule.java or DAOModule.java.
</ul></li>
<li>Graphene currently requires you to implement an ExtJS UI. The Walker and Kiva demos should be helpful in setting up the application for your own dataset.</li>


Graphene is structured as a multi module maven project. The modules are:
 <ul>
  <li> <h3>graphene-parent</h3>
    <ul>
      <li><h4>graphene-analytics</h4>
        <ul> 
          <li>Still under development.  This is for precomputed or post ingest analytics</li>
        </ul>
      </li>
     <li><h4>graphene-dao</h4>
        <ul> 
          <li>Defines all DAO interfaces used under graphene-parent, as well as any business logic interfaces and implementations</li>
        </ul>
     </li>
     <li><h4>graphene-dao-neo4j</h4>
        <ul> 
          <li>Defines some DAO implementations for a standardized Neo4J property graph, and graph querying abilities using Cypher and the Neo4J Java API</li>
        </ul>
     </li>
     <li><h4>graphene-dao-titan</h4>
        <ul> 
          <li>Defines some helper classes for working with the Titan graph database for property and event graphs, and graph querying abilities using the Tinkerpop/Gremlin APIs</li>
        </ul>
     </li>
     <li><h4>graphene-dao-sql</h4>
        <ul> 
          <li>Defines some DAO implementations for standardized SQL tables.  These are not used unless you wire them in IOC</li>
        </ul>
     </li>
      <li><h4>graphene-dao-es</h4>
        <ul> 
          <li>Defines some DAO implementations for working with Elastic Search with JEST.  Using JEST allows us to be compatible through the REST layer, instead of binding to a particular version of ElasticSearch.  These are not used unless you wire them in IOC</li>
        </ul>
     </li>
      <li><h4>graphene-export</h4>
        <ul> 
          <li>Defines utilities for converting internal lists into CSV and native Excel XLSX files</li>
        </ul>
      </li>
    <li><h4>graphene-hts</h4>
        <ul> 
          <li>Under development. Defines utilities for entity extraction and resolution based on the nature and context of the data. </li>
        </ul>
      </li>
      <li><h4>graphene-ingest</h4>
        <ul> 
          <li>Defines some basic ingest utilities which are used only by other ingest modules.</li>
        </ul>
      </li>
      <li><h4>graphene-introspect</h4>
        <ul> 
          <li>Used during the ingest phase.  Currently its main function is to run a series of queries against every table and every column, so you can get a feel for the bounds of your data and which columns are interesting.</li>
        </ul>
      </li>
      <li><h4>graphene-model</h4>
        <ul> 
          <li>This module contains IDL classes generated by  [Apache Avro](http://avro.apache.org/).  These classes are the lingua franca between modules in Graphene. </li>
        </ul>
      </li>
      <li><h4>graphene-rest</h4>
        <ul> 
          <li>This module defines the REST interfaces which will be exposed to the UI.</li>
<li>Your REST implementations will adhere to the interfaces defined here.</li>
<li>The REST interfaces control the paths of the resource, so the UI will not break because of bad paths.</li>
<li>This module uses Tynamo RESTEasy integration with the Apache Tapestry web framework.</li>
        </ul>
      </li>
      <li><h4>graphene-search</h4>
        <ul> 
          <li>General search utilities</li>
        </ul>
      </li>
      <li><h4>graphene-util</h4>
        <ul> 
          <li>Utilities which have cross cutting concerns for all modules.  For example, query timing, logging, memory and file utilities.</li>
<li>Almost all modules require this module as a dependency.</li>
        </ul>
      </li>
      <li><h4>graphene-web</h4>
        <ul> 
          <li>This module defines some basic wiring and imports many other of the *Module.java classes from other graphene maven modules.</li>
<li>The web module also contains shared html, css and js resources used by ExtJs, [Cytoscape.js](http://js.cytoscape.org/) and many other js libraries.</li>
<li>It also contains Apache Tapestry based UI components and pages (currently limited)</li>
        </ul>
      </li>
    </ul>
  </li>
</ul>

 
 Developing with Graphene
------


 We recommend that your application use the Maven module structure, as shown in the Walker and Kiva demos.  For example, if you have a company name or dataset name you are developing for, like IMDB, the structure would look as follows:
 
 * graphene-imdb
 ..*graphene-imdb-ingest (aka the ingest module)
 ..*graphene-imdb-web (aka the web module)
 
The POM.xml at the IMDB level lists the Ingest and Web modules as children, so you can build both parts together.
 
We recommend that the ingest module depend on parts from the web module (and not the other way around), so that code relating to ingest doesn't get deployed with your war.
 
###The ingest module
 The ingest module has to do with ETL (Extract, Transform, Load) of your data into a more generic format.
 
###The web module
#### If you ETL'd into an RDBMS  
   The first thing you might want to do with the web module is to setup and run the DTOGeneration.java main(). Once it connects to your database, it will generate Java model objects that reflect your database tables, and query helping objects which will ensure you write valid SQL (invalid SQL or bad type conversions will be caught during compilation).  This portion of the process uses QueryDSL to do code generation, which is normally tedious and error prone when done by hand.
#### If you didn't ETL into an RDBMS
   You can skip the DTOGeneration, and go straight to implementing your DAOs
 
#### DAO Implementation
   Graphene expects you to create implementations for most of the DAOs following the interfaces provided in the graphene-dao module.
   This allows the storage mechanism you choose to be independent of the main services and UI.  In the DAO implementations, you will mostly be querying your datastore and then converting the results into one of the model or view objects in the graphene-model module. 
 
  
Running a Graphene application
------

   By default, Graphene expects some other software to be available (Although it is easy to change this or override the defaults) 

* JDK 1.7 (latest will be fine).  Graphene and its dependencies are not built with JDK 8 compatibility.
* Tomcat 7 or 8, with any JDBC on the class path (i.e. tomcat/lib or packaged with your app)
* You should set the Catalina_Home environment variable
* Graphene supports Chrome and Firefox, and does not require any browser plugins. 
 
 
Licensing
------
   Graphene is an open source project, containing dependencies on other open source projects.  This project was funded by DARPA under part of the XDATA program.
 
