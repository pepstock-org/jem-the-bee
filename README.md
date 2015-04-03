# jem-the-bee

##Version 2.2 Bergamotto has been released! (January, 2015)##
 
Download from [here](http://www.pepstock.org/download.htm)!

<p><img align="right" src="http://www.pepstock.org/resources/jem-home.png"/>
 
**Play with JEM version 2.1!** Download the sandbox from [here](http://www.pepstock.org/download/jem-v2.1-centos-x64-sandbox.ova), for both Virtual Box and VMWare player, and try it!
 
**New release 2.2, will contain:**
  
  * **[JBoss JBPM](JBossJBPM_as_JCL)** integration, using BPMN2 as JCL
  * **[BASH](BASH_as_JCL) and [Windows CMD](CMD_as_JCL)** integration, as JCLs
  * **[REST API](REST)** fully implemented
  * **[Deployer of JEM](Depolyment)** by a JEM ANT task which can deploy artifacts on JEM by REST
  * **Oracle and IBM DB2** compatibility to be used for the maps persistence
  * **JEM [Annotations](Annotations)** to set data descriptions and data sources on own fields, avoiding to write all JNDI calls
  * [Submitting](Scripts#Submitting_jobs_by_NodeJS_script) jobs by **NodeJS**, to reduce the amount of needed memory 
 
**JEM** is java and cloud application which implements a batch execution environment which is able to manage the execution of batches. You could consider it as an application server for batches and long running activities, with the following main features:
 

 * **Cloud-aware**: runs on cluster mode
 * **Cross platform**: is a java application, so can run everywhere!
 * **Swarm**: engine to join different JEM groups together, routing the execution of jobs on another group! Consider it like "the cloud of clouds"!
 * **Multi job control languages**: is able to manage [Apache ANT](http://ant.apache.org/) and [VMWare SpringBatch](http://docs.spring.io/spring-batch/) application frameworks
 * **Multi programming languages**: is able to manage, by [Apache ANT](http://ant.apache.org/), many programming languages to use for business logic
 * **Spring Batch improvements**: reduced the configuration related to JEM and custom tags to use in the better way JEM features, like datadescriptions, datasets, datasources and locks!
 * **Big-Data enable**: first possible integration with [Apache Hadoop](http://hadoop.apache.org/) using [Spring for Hadoop](http://projects.spring.io/spring-hadoop/)
 * **Vertical scalability**: for users who have got big machines with many resources can use a unique node, changing dinamically the number of jobs which can be run.
 * **Parallel Computing**: uses multiple processing elements simultaneously, breaking the business logic into independent parts so that each processing element can execute its part of the algorithm simultaneously with the others. JEM uses [JPPF integration](ParallelComputingJPPF) to perform parallel tasks 
 * **WEB User interface**: complete web interface to work on JEM cluster
 * **Eclipse plugin**: a plugin for Eclipse which provides to developers the capability to connect to many JEM environments and work on them, testing own batch application
 * **Job Monitoring**: by user interface, you can monitor all job executions
 * **Output management**: is able to collect jcl output and to see by user interfaces
 * **Security engine**: roles engine, both on user interfaces and during job executions
 * **Common resources**: common repository of resources accessing by JNDI or environment variables
 * **Resources template**: capabilities to create custom common resources which can be used inside JEM. With this feature you can connect all middleware, database and application that you want, maintaining a central configuration of them 
 * **GDG**: versioned files like mainframe ones
 * **Multi data paths**: multiple moint points (and then file systems) could be configured to store business data. Using RegEx you can address files on different paths which can use file systems with different backup or performance policies.
 * **GlusterFS and Apache HDFS**: used and configured to be used as global file systems for JEM
 * **Global resources system**: engine to synchronize resources (mainly files) inside the cluster
 * **License**: JEM, the BEE is under GPL version 3!! JEM, the BEE Eclipse plugin is under EPL version 1

</p>
