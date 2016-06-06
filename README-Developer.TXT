********************************************************************
* Updating Important Files
********************************************************************
# REALLY OLD METHOD:
#scp validationSchemas.xml jdeck88,bioValidator@web.sourceforge.net:htdocs
# OLD METHOD:
#scp validationSchemas-0.8.xml biocode@darwin.berkeley.edu:///usr/local/web/biocode/web/validationSchemas-0.8.xml


********************************************************************
* Development Installation
* The installation relies on the one-jar build path, integrated
* as an Ant Task and deployed using izpack (to bundle associated
* XML configuration files).  This means the bundled library
* dependencies need to be installed a certain way, as described below
********************************************************************

1. Required Libraries for Compilation can be found ./lib

2. Required Libraries for Ant: installed in ./lib
    one-jar-ant-task-0.96.jar

3. To use Ant Build file of choice:
    Click "Ant Build" on right hand side
    Click "+" icon
    Add biovalidator.xml file
    Deployable Jar file will install as ../bioValidator.jar


***********************
TODO DEVELOPMENT LIST
***********************
This has been moved to the google code site
http://code.google.com/p/biovalidator



