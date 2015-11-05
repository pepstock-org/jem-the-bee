# jem-the-bee

##Version 2.3 Bergamotto has been released!##
 
Download previous versions from [here](http://jemthebee.org/index.php/downloads/)!

**Play with JEM version 2.1!** Download the sandbox from [here](http://www.pepstock.org/download/jem-v2.1-centos-x64-sandbox.ova), for both Virtual Box and VMWare player, and try it!
 
###What's new in release 2.3?###
  
  * **[Multiple java runtimes](../../wiki/ConfiguringJEMNode)** which allows you to use the JRE you need for business logic
  * **[SpringBatch restartability](../../wiki/JCLReferenceUsingSpringBatch#restartability)** out of the box, provided by JEM
  * **[PowerShell](../../wiki/PS_as_JCL)** integration, as JCL
  * **[Generic Shell](../../wiki/Generic_JCL_Script_Factory)** JCL, which allows you to create your JCL based on a whatever shell script language
  * **[REST](../../wiki/REST)** interface improvements 
  * JEM is distributed compiled by Java 7
 
 
###What is it?###
 
**JEM** is java and cloud application which implements a batch execution environment which is able to manage the execution of batches. You could consider it as an application server for batches and long running activities.
 
###Main features###
 
 * **Cloud-aware**: runs on cluster mode
 * **Cross platform**: is a java application, so can run everywhere!
 * **Swarm**: engine to join different JEM groups together, routing the execution of jobs on another group! Consider it like "the cloud of clouds"!
 * **Multi job control languages**: is able to manage [Apache ANT](http://ant.apache.org/),  [SpringBatch](http://docs.spring.io/spring-batch/), [JBoss JBPM](http://docs.jboss.org/jbpm/v6.1/userguide/jBPMOverview.html), [BASH](http://www.gnu.org/software/bash/) and [Windows CMD](http://www.microsoft.com/resources/documentation/windows/xp/all/proddocs/en-us/cmd.mspx?mfr=true)
 * **Multi programming languages**: is able to manage, by [Apache ANT](http://ant.apache.org/), many programming languages to use for business logic
 * Custom **JEM JAVA Annotations** to set data descriptions and data sources on own fields, avoiding to write all JNDI calls
 * **Spring Batch improvements**: reduced the configuration related to JEM and custom tags to use in the better way JEM features, like datadescriptions, datasets, datasources and locks!
 * **Big-Data enable**: first possible integration with [Apache Hadoop](http://hadoop.apache.org/) using [Spring for Hadoop](http://projects.spring.io/spring-hadoop/)
 * **Vertical scalability**: for users who have got big machines with many resources can use a unique node, changing dinamically the number of jobs which can be run.
 * **Parallel Computing**: uses multiple processing elements simultaneously, breaking the business logic into independent parts so that each processing element can execute its part of the algorithm simultaneously with the others. JEM uses [JPPF integration](../../wiki/ParallelComputingJPPF) to perform parallel tasks 
 * **WEB User interface**: complete web interface to work on JEM cluster
 * **Eclipse plugin**: a plugin for Eclipse which provides to developers the capability to connect to many JEM environments and work on them, testing own batch application
 * **Deployer** by a ANT task which can deploy artifacts on JEM by REST
 * **REST API** fully implemented to access to JEM and interact with it
 * **Job Monitoring**: by user interface, you can monitor all job executions
 * **Output management**: is able to collect jcl output and to see by user interfaces
 * **Security engine**: roles engine, both on user interfaces and during job executions
 * **Common resources**: common repository of resources accessing by JNDI or environment variables
 * **Resources template**: capabilities to create custom common resources which can be used inside JEM. With this feature you can connect all middleware, database and application that you want, maintaining a central configuration of them 
 * **GDG**: versioned files like mainframe ones
 * **Multi data paths**: multiple moint points (and then file systems) could be configured to store business data. Using RegEx you can address files on different paths which can use file systems with different backup or performance policies.
 * **GlusterFS and Apache HDFS**: used and configured to be used as global file systems for JEM
 * **Global resources system**: engine to synchronize resources (mainly files) inside the cluster
 * **Full queues persistence** on MySQL, Oracle or DB2
 * Submitting jobs by scripts and by **NodeJS**, to reduce the amount of needed memory if necessary 
 * **License**: JEM, the BEE is under GPL version 3!! JEM, the BEE Eclipse plugin is under EPL version 1
 
###Let's start###
 
Read the [wiki](../../wiki/Home) to see in details all capabilities of JEM.
Start with the [installation instructions](../../wiki/Installation). Enjoy!

