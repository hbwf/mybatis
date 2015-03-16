## Introduction to MyBatis Generator ##

MyBatis Generator is a code generator for MyBatis. MyBatis Generator will introspect a database table (or many tables) and will generate MyBatis artifacts that can be used to access the table(s). This abates some of the initial nuisance of setting up objects and configuration files to interact with database tables. MyBatis Generator seeks to make a major impact on the large percentage of database operations that are simple CRUD (Create, Retrieve, Update, Delete). You will still need to hand code SQL and objects for custom queries, or stored procedures.

MyBatis Generator will generate:

  * SqlMap XML Files
  * Java Classes to match the primary key and fields of the table(s)
  * Java Client Classes that use the above objects (optional)

MyBatis Generator can run as a standalone JAR file, as an Ant task, a Maven plugin, or
an Eclipse plugin.

## Get MyBatis Generator ##

### Google Code ###
MyBatis generator is available from the MyBatis site at http://code.google.com/p/mybatis/downloads/list?can=3&q=Product%3DGenerator  The bundle includes documentation for the generator.

### Maven ###
MyBatis generator is available in the Maven central repository at:
|**Group**|org.mybatis.generator|
|:--------|:--------------------|
|**Artifact**|mybatis-generator-maven-plugin|
|**Version**|1.3.2|

### Eclipse ###
MyBatis generator is available as an Eclipse plugin and includes the extra
capability of merging generated Java files as databases evolve.  Documentation for
MyBatis generator is integrated into the Eclipse help system.  To install the
Eclipse plugin, configure a new Eclipse install repository and point to this location:

http://mybatis.googlecode.com/svn/sub-projects/generator/trunk/eclipse/UpdateSite/

The plugin is currently developed and compiled with Eclipse Indigo SR2.  It is also tested on Eclipse Juno and known to work there.