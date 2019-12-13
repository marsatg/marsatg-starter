Foreword
This starter is applicable to spring-boot 1.5.6.RELEASE and above:
Based on netty-all 4.1.43.Final implementation

After Maven executes the install command, the project is installed into the local maven repository
Add the following configuration to the pom file that needs to reference this project:

<dependency>
   <groupId> org.marsatg </ groupId>
   <artifactId> poseidon-spring-boot-starter </ artifactId>
   <version> 1.0 </ version>
</ dependency>



1 server configuration:
When used as a server:
(Note: all non-specified package annotations in this document are under the org.marsatg.annotation package)
Server annotations:
 @EnableNettyServer
Open the web background page annotations:
@EnableWebManage (countClientCall = true, countCallServer = true)
Among them countClientCall = true, countCallServer = true respectively means that when the server is used as a server, the function of recording the number of calls to the client is enabled, and when it is used as a client, the function of the number of calls to the server is enabled.
as the picture shows:





application.properties must indicate the service name and tcp port of the current service
#Server host name (required)
netty-local.applicationName = nettyServer
#Server host binding TCP port (required)
netty-local.applicationPort = 12345
The server uses CachedThreadPool to handle concurrent requests by default
You can use the following configuration to specify FixedThreadPool and set the maximum number of threads to control concurrency
#Using the FixedThreadPool thread pool
netty-local.isFixedThreadPool = true
#Maximum number of threads using the FixedThreadPool thread pool
netty-local.threads = 6



2 service services development:
Define the services needed for the project:
2.1 class definition

The @Component annotation is the spring built-in annotation:
org.springframework.stereotype.Component
This annotation indicates that this service will be instantiated by Spring and stored in the IOC container

The @Service annotation has three parameters:
(value = "hello", name = "hello service", desc = "This is the description of the service")
value = "hello" the service name specified when the client invoked,
name = "hello service" The service name displayed on the web background page,
desc = "This is the description of the service" The description displayed on the web background page

2.2 Define method

Annotation @Method (value = "sayHello", desc = "This is a sayHello method")
Contains two parameters:
value = "sayHello" method name specified when the client invoked
desc = "This is a sayHello method" The description of the method displayed on the web background page

Note: The value attribute and desc attribute can be omitted. When there is no value attribute, the specific method name will be used as the calling name.

When the method name is modified, the client will not find the method call, it is recommended to use value annotation


2.2.1 Details:
There cannot be two identical @Method annotation methods in the same class
(Same parameter value)
Similar to the same Controller, two identical RequestMappings cannot appear




3 WEB background management page:
Visit address http: // {ip}: {port} / {contextPath} /marsatg.html
The contextPath is not configured in this example so the access address is:
http: // localhost: 8081 / marsatg.html
Figure:

3.1 access restrictions
If the page is unreachable: Please ensure that the service has a filter that blocks static pages:
Guaranteed filter is open to requests containing marsatg .js .css keywords
If application.properties has the following configuration items
spring.resources.static-locations configuration item
You need to add the following path
spring.resources.static-locations = classpath: / resources / static
If spring.resources.static-locations configuration is not configured, the above configuration is not needed



4 client development:


The client needs to enable @EnableNettyClient annotation
application.properties must be configured with the following information
#Current client name (required)
netty-local.applicationName = consumer001
#Server host IP (required)
netty-local.server [0] .host = localhost
# Server-side host call name (required, client calls will be based on this server name)
netty-local.server [0] .name = nettyServer
#Server end host call port (required)
netty-local.server [0] .port = 12345
#Server end host disconnection reconnection interval / ms (not required, default 5000 ms)
netty-local.server [0] .reConnectTnterval = 5000
#Maximum number of server host disconnection and reconnection intervals (not required, default 12 times)
netty-local.server [0] .maxReConnectTimes = 12
#Client maximum request timeout time / ms, default 6000 ms, (optional)
netty-local.requestBlockTimeout = 5000


The client can be configured with multiple servers, for example: netty-local.server [subscript] .xxx





In netty mode, the calling server is as follows:
NettyRequest nettyRequest = NettyRequest.invokeBlock ("nettyServer", "hello", "sayHello", "marsatg");

The four parameters are:
1 service name above netty-local.server [0] .name
2 The service class call name provided by the server The above: @Service value attribute
3 The method call name provided by the server The above: @ Method's value attribute
4 The parameter array of the method specified by @Method ([java.lang.String in this example])
The org.marsatg.netty.NettyRequest class provides two static methods
1 invokeBlock blocks the call, blocks until the server returns a response, or blocks until the maximum timeout
2 invokeNonBlock non-blocking call, the server will not return a response, the requesting end is completed, and it ends automatically

The invokeBlock method will return to the instantiated object of the NettyRequest class after execution.
Object result = nettyRequest.getResult ();
Where result is the response from the server