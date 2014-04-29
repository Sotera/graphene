README

If you notice too much logging going on and you can't disable the logging, it possible that you have more than one 
implementation of slf4j on the classpath.  I recommend removing any references to the logback logger, especially in
 the Tomcat Devloader settings.
 
 
 
 Tapestry templates from archetypes:
 You may notice that eclipse will give you 'undeclared entity' errors or warnings on the TML templates, if you've told Eclipse to treat TML documents as HTML.
 To solve this, add this to the top of the documents that have the error:
 
 <!DOCTYPE html [
    <!ENTITY nbsp "&#160;">  
    <!ENTITY copy "&#169;"> 
    <!ENTITY bull "&#8226;">
]>

Note: you may need to add additional entity declarations as you add them.  You can find the full list here:
http://www.w3.org/TR/html4/sgml/entities.html
And later on, here:
http://www.w3.org/TR/html5/


Running in jetty:
mvn jetty:run

Running in jetty from command line, without "Terminate batch job"
mvn jetty:run -Djetty.port=8000 < Nul


Packaging this module as a jar vs as a war:


With war packaging, we could test this app without having to make a new baseline application.
With jar packaging, it would only be really usable as an overlay into a war project (we'd have to specify that certain folders, containing the js/html, etc, get overlaid into a particular directory)

With jar packaging, it's easy for other modules to use the classes as dependencies.  With war packaging, we have to use the "attachClasses" flag during the war plugin setup, which creates a separate jar file with just the classes.  This ends up causing the class files to exist twice in the customer war file--once as a dependency in WEB-INF/lib, and once in the classes directory.  Might be able to fix this using scope on the jar.

With jar packaging, the file is always smaller.  

Jar packaging is how it's done when you are treating the module as a library.