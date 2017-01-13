dbms_java.grant_permission( 'IBS', 'SYS:java.lang.reflect.ReflectPermission', 'suppressAccessChecks', '' )
dbms_java.grant_permission( 'IBS', 'SYS:java.net.SocketPermission', '10.95.5.19:8888', 'connect,resolve' )
dbms_java.grant_permission( 'IBS', 'SYS:java.net.SocketPermission', 'iitcloud-demo.iitrust.ru:443', 'connect,resolve' )