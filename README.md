GOR (Generic Online Reporting) 
==============================

This web application was done for a final year project
by Bob Marks in 2002.

![Screenshot of GOR](https://github.com/bobmarks/gor/raw/master/images/screenshot_report_format.gif "Screenshot of GOR")

GOR is a web based reporting application with a 
query-by-example query generator and WYSIWYG report 
formatter.  Also reports can be run in a variety of 
languages.

**NOTE:** GOR has been updated slightly from original final 
   year project source.  Updates are mostly refactors 
   and packaging as a WAR file for easier deployment.

INSTALLATION GUIDE
------------------

Use the following steps to install the system.

1.  Extract `gor.zip` file somewhere on the filesystem / or git clone.

2.  Ensure a Java webserver is installed. E.g. tomcat 6.0

3.  Copy `gor.war` file to the deploy directory of the 
    webserver (usually webapps). e.g. c:\tomcat\webapps.

4.  Copy the \gor\gor data folder somewhere.  E.g.
    
        c:\tomcat\gor
    
    This folder should contain 3 data sub directorys. E.g.
    
        c:\tomcat\gor\database
        c:\tomcat\gor\reports
        c:\tomcat\gor\system

5.  Set the GOR environment variable to point to this 
    directory. E.g.

        GOR=c:\tomcat\gor

    This can be done several ways e.g. update the 
    starup batch file of the webserver.  E.g. in tomcat 
    update the `c:\tomcat\bin\catalina.bat` file.  
    Look for following line.
    
        set JAVA_OPTS=%JAVA_OPTS%  ....
    
    and add
    
        -DGOR="c:\tomcat\gor"
    
    to the end of the line.
    
    Alternatively you can see an `GOR` environment variable. 

6.  Start the web server.

7.  Access to the GOR webapp is now possible using the 
    following web address ...
    
    [http://localhost/gor/Logon](http://localhost/gor/Logon)
    
    or
    
    [http://localhost:8080/gor/Logon](http://localhost:8080/gor/Logon)

    ... and log on using the following username and password.
     
    * Username: admin        
    * Password: admin

8.  When creating database connections you may need to put 
    a driver jar into the lib directory of the web server.
    E.g. to connect to a HSQLDB database ensure that the 
    `hsqldb.jar` file is in the lib folder of tomcat for 
    example.

9.  To create new usernames and passwords run the 
    `UserNameAndPassword.class` file and copy the generated
    String into the `\servlets\files\system\users.dat` file.

        java UserNameAndPassword
    
10. **Note:** if you fancy doing some development to GOR you 
    should download ANT and Eclipse.   In Eclipse simply
    import an existing project as a `.classpath` / `.project`
    file exist.
    
Contact
-------

If any problems with the above steps then contact Bob
Marks at the below email addresss.

marksie531@gmail.com
