 = Graphene: Search and Graph your data = 
 
 Graphene is a high performance Java based web framework you can use to build a searching and graphing application on top of your data.  It is datastore agnostic, but has some built in support for generating a graph database using Neo4J, from your non-graph datastore (RDBMS, Solr, etc).
 
 == Using Graphene ==
 
 The core of Graphene (this project) is to be used as a WAR overlay for your Java based web application.  We will be providing two examples using the Kiva and Enron (limited) datasets.
 In the future we will be fleshing out a complete Maven archetype to get you going more quickly.
 
 It is our goal that you should not have to modify this project in order to suit your individual needs (although we welcome ideas and suggestions).  The intent is that this project be used as an underlying framework, and that your individual implementations can be wired within your code using IOC.
 
 == Building Graphene ==
 
 Graphene is built using http://maven.apache.org/[Apache Maven] version 3.0.4 and a recent version of http://www.oracle.com/technetwork/java/javase/downloads/index.html[Java 7]. 
 
 * A plain 'mvn clean install' will build all the jar files and a single war file (to be overlaid on your project)
 * Test execution is part of the build, but you can add -DskipTests=true to cut down on the build time.
 * A BuildAll.bat is supplied for windows users.  This will perform a few cleans to overcome some windows issues, and then compile and install to your local maven repo.
 
  == Licensing ==
  
  Graphene is an open source project, containing dependencies on other open source projects.  This project was funded by DARPA under part of the XData program.
 
 
#