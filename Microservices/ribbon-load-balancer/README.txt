SOURCE:
https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon

DESCRIPTION:
Zuul es un Api gateway, pero usa Ribbon como load balancer, pero una vez redirigido el request a otro servicio, este último pudiera necesitar llamar a otro servicio.
Aquí mostramos cómo configurar Ribbon por separado, integrado con SpribgBoot pero sin usar Zuul.
Permite acoplarse a un RestTemplate o un WebClient (reactive, non-blocking) y decidir qué servidor utilizar basado en una configuración.



In this tutorial, we will introduce client-side load balancing via Spring Cloud Netflix Ribbon.
Ribbon viene integrado en Zuul (API gateway), pero acá lo usamos sin la intengración con Zuul, 
servirá para un nodo que haga consultas a otro nodo y disponga de varias réplicas de este para poder elegir,
estos datos los puede obtener de Eureka (service discovery), pero en este caso configuramos a mano los servidores disponibles.

Ribbon is an Inter Process Communication (IPC) (Remote Procedure Call) cloud library. The primary usage model involves REST calls with various serialization scheme support. Ribbon primarily provides client-side load balancing algorithms.
Apart from the client-side load balancing algorithms, Ribbon provides also other features:
    Service Discovery Integration – Ribbon load balancers provide service discovery in dynamic environments like a cloud. Integration with Eureka and Netflix service discovery component is included in the ribbon library
    Fault Tolerance – the Ribbon API can dynamically determine whether the servers are up and running in a live environment and can detect those servers that are down
    Configurable load-balancing rules – Ribbon supports RoundRobinRule, AvailabilityFilteringRule, WeightedResponseTimeRule out of the box and also supports defining custom rules

Ribbon API works based on the concept called “Named Client”. While configuring Ribbon in our application configuration file we provide a name (ig. 'ping-server') for the list of servers included for the load balancing.
Example:
ping-server:
  ribbon:
    eureka:
      enabled: false
    listOfServers: localhost:9092,localhost:9999
    ServerListRefreshInterval: 15000


STEPS:
boostrap 2 instances of SpringCloudNetflixSimpleApiMicroservice on ports 8081 and 8082
bootstrap 1 instance of SpringCloudNetflixRestClientMicroservice
visit localhost:8080/server-location (Ribbon is forwarding the req to the other services using a load balancing strategy)
