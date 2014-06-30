Graphene: Search and Graph your data 
======
 Graphene is a high performance Java based web framework you can use to build a searching and graphing application on top of your data.  It is datastore agnostic, but has some built in support for generating a graph database using Neo4J, from your non-graph datastore (RDBMS, Solr, etc).<br>For configurations, building, and deployment instructions, view the [Graphene Wiki](https://github.com/Sotera/graphene/wiki).
 
Using Graphene 
------
 The core of Graphene (this project) is to be used as a WAR overlay for your Java based web application.  We will be providing two examples using the Kiva and Enron (limited) datasets.
 In the future we will be fleshing out a complete Maven archetype to get you going more quickly.
 
 It is our goal that you should not have to modify this project in order to suit your individual needs (although we welcome ideas and suggestions).  The intent is that this project be used as an underlying framework, and that your individual implementations can be wired within your code using IOC.
 
Building Graphene 
------ 
 Graphene is built using [Apache Maven](http://maven.apache.org) version 3.0.4 and a recent version of [Java 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html). 
 
 * A plain 'mvn clean install' will build all the jar files and a single war file (to be overlaid on your project)
 * Test execution is part of the build, but you can add -DskipTests=true to cut down on the build time.
 * A BuildAll.bat is supplied for windows users.  This will perform a few cleans to overcome some windows issues, and then compile and install to your local maven repo.
 
 Graphene overview
 ------
* Graphene expects that you are familiar with some modern Java concepts:
<li>Interfaces and Implementations</li>
<li>Knowledge of Maven, or a knowledge of how to search for answers to your questions</li>
<li>Dependency Injection (aka Inversion of Control or IOC or DI)
<ul><li>Graphene uses [Apache Tapestry](http://tapestry.apache.org/) to provide the IOC framework.  It is very similar to Guice, but also allows distributed configuration and can act as a light weight OSGI alternative.  We may use the term 'wiring' and 'binding' interchangeably.  Essentially a registry is created and lives throughout the life of your program, which defines which implementation services will get when they ask for the interface. The IOC 'wiring' is mostly done at the customer implementation level, although basic shared services are wired in modules within graphene-parent.  For consistency's sake, any class that performs IOC wiring we suffix with the word "Module", i.e. AppModule.java or DAOModule.java.</li>
</ul></li>
<li>Graphene currently requires you to implement an ExtJS UI, although the Kiva and Enron demos should be helpful in setting up the application for your own dataset.</li>


 * Graphene is structured as a multi module maven project.
 
 The modules are
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
          <li>Defines some DAO implementations for a standardized Neo4J property graph, and graph querying abilities using Cypher</li>
        </ul>
     </li>
     <li><h4>graphene-dao-sql</h4>
        <ul> 
          <li>Defines some DAO implementations for standardized SQL tables.  These are not used unless you wire them in IOC</li>
        </ul>
     </li>
      <li><h4>graphene-export</h4>
        <ul> 
          <li>Defines utilities for converting internal lists into CSV and native Excel XLS files</li>
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
      <li><h4>graphene-memorydb</h4>
        <ul> 
          <li>An in house memory database for property graphs, for small datasets and when a graph database is not available.  An implementation of this is preloaded into memory when the application is started (which may take several minutes depending on how much you tell it to load)</li>
        </ul>
      </li>
      <li><h4>graphene-model</h4>
        <ul> 
          <li>This module contains generic model and view classes which are used by graphene's services.  </li>
<li>Your DAO implementations translate your domain specific (and database specific) objects into the more generic objects defined in this module.</li>
<li>Your DAO implementations receive query objects (POJOs) defined in this module.</li>
<li>Many of the objects are generated use [Apache Avro](http://avro.apache.org/).</li>
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
<li>The web module also contains shared html, css and js resources used by ExtJs, Cytoscape and many other libraries.</li>
<li>It also contains Apache Tapestry based UI components and pages (currently limited)</li>
        </ul>
      </li>
    </ul>
  </li>
</ul>

 
 Developing with Graphene
 ------
 We recommend that your application use the Maven module structure, as shown in the Kiva and Enron demos.  For example, if you have a company name or dataset name you are developing for, like IMDB, the structure would look as follows:
 
 * graphene-imdb
 ..*graphene-imdb-ingest (aka the ingest module)
 ..*graphene-imdb-web (aka the web module)
 
 The POM.xml at the IMDB level lists the Ingest and Web modules as children, so you can build both parts together.
 
 We recommend that the ingest module depend on parts from the web module (and not the other way around), so that code relating to ingest doesn't get deployed with your war.
 
 ###The injest module
 The ingest module has to do with ETL (Extract, Transform, Load) of your data into a more generic format.
 
###The web module
#### If you ETL'd into an RDBMS  
   The first thing you might want to do with the web module is to setup and run the DTOGeneration.java main(). Once it connects to your database, it will generate Java model objects that reflect your database tables, and query helping objects which will ensure you write valid SQL (invalid SQL or bad type conversions will be caught during compilation).  This portion of the process uses QueryDSL to do code generation, which is normally tedious and error prone when done by hand.
#### If you didn't ETL into an RDBMS
   You can skip the DTOGeneration, and go straight to implementing your DAOs
 
#### DAO Implementation
   Graphene expects you to create implementations for most of the DAOs, following the interfaces provided in the graphene-dao module.
   This allows the storage mechanism you choose to be independent of the main services and UI.  In the DAO implementations, you will mostly be querying your datastore and then converting the results into one of the model or view objects in the graphene-model module. (try saying that 10 times fast!)
 
 
 
 
#### DAO Implementation
   Graphene expects you to create implementations for most of the DAOs, following the interfaces provided in the graphene-dao module.
 
Running a Graphene application
------

   By default, Graphene expects some other software to be available (Although it is easy to change this or override the defaults) 

* Tomcat 7 or 8, with any JDBC on the class path (i.e. tomcat/lib or packaged with your app)
* You should set the Catalina_Home environment variable
* Graphene supports Chrome and Firefox, and does not require any browser plugins. 
 
 
Licensing
------
   Graphene is an open source project, containing dependencies on other open source projects.  This project was funded by DARPA under part of the XDATA program.
 
