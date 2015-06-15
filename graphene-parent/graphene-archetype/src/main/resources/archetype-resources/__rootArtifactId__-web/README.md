   __________  ___    ____  __  _________   ________
  / ____/ __ \/   |  / __ \/ / / / ____/ | / / ____/
 / / __/ /_/ / /| | / /_/ / /_/ / __/ /  |/ / __/   
/ /_/ / _, _/ ___ |/ ____/ __  / /___/ /|  / /___   
\____/_/ |_/_/  |_/_/   /_/ /_/_____/_/ |_/_____/   
                                                    

This is the maven module where code for the web-app goes.  It will use the Graphene-Web module from Graphene-Parent as a war overlay, and construct a .war file. Additionally, it will output a .jar file with it's class files, so that your Ingest module can leverage the any classes/implementations you have in the web app.