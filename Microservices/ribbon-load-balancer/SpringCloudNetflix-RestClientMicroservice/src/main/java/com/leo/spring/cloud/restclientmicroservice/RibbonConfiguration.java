package com.leo.spring.cloud.restclientmicroservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.IPing;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PingUrl;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.WeightedResponseTimeRule;

/**
We'll use one of Ribbon's load-balancing strategies, WeightedResponseTimeRule, to enable the client side load balancing between 2 servers, which are defined under a named client in the configuration file (ping-server)

Ribbon API enables us to configure the following components of the load balancer:

    Rule – Logic component which specifies the load balancing rule we are using in our application
    Ping – A Component which specifies the mechanism we use to determine the server's availability in real-time
    ServerList – can be dynamic or static. In our case, we are using a static list of servers and hence we are defining them in the application configuration file directly

 * */
//@Configuration
public class RibbonConfiguration {

    @Autowired IClientConfig ribbonClientConfig;

    /* Available impl:
     * PingConstant
     * PingUrl
     * NoOpPing
     * DummyPing
     * NIWSDiscoveryPing
     */
    @Bean
    public IPing ribbonPing(IClientConfig config) {
        return new PingUrl(); //PingUrl mechanism to determine the server's availability in real-time.
    }

    /* Available impl:
     * IRule
     *     AbstractLoadBalancerRule (abstract)
     *         ClientConfigEnabledRoundRobinRule
     *             BestAvailableRule
     *             PredicateBasedRule (abstract)
     *                 AvailabilityFilteringRule
     *                 ZoneAvoidanceRule
     *         RandomRule
     *         RetryRule
     *         RoundRobinRule   (default strategy)
     *             WeightedResponseTimeRule
     */
    @Bean
    public IRule ribbonRule(IClientConfig config) {
        return new WeightedResponseTimeRule(); //WeightedResponseTimeRule rule to determine the server
    }
}


/** NOTE TO CONSIDER WHEN CREATING YOUR OWN RULE
 * https://stackoverflow.com/questions/50141896/predefining-own-load-balancing-strategy-with-zuul-in-spring-application

Zuul's round robin load balancing only works as advertised within the scope of a refresh of the discovery client which happens every few seconds by default.
When Zuul refreshes its discovery client the list of server instances is shuffled randomly by the code in com.netflix.discovery.shared.Applications and cannot be configured out. This shuffle breaks the stateful logic in com.netflix.loadbalancer.RoundRobinRule that maintains a 'last position' in the list of servers so it can do its round-robin logic.
Bear this in mind when you're implementing your own rule that selects a server. You may need to sort by instance id or something like that to undo the shuffle that the discovery client did.
To implement your own rule you just need to provide a bean that implements com.netflix.loadbalancer.IRule. What we did is extend the abstract class com.netflix.loadbalancer.AbstractLoadBalancerRule. The choose() method is where you do your work.
 * */
/* INTERPRETACIÓN:
 * Zuul Round Robin strategy funciona cuando recuperamos de Eureka la lista de instancias disponibles, lo cual sucede cada tantos segundos.
 * Cuando se recupera la lista de instancias, estas se ordenan aleatoriamente (no se puede configurar), esto rompe el estado de la estrategia RoundRobinRule, que mantiene un last-position del último servidor usado.
 * Cuando hagamos nuestra propia regla, tengamos esto en cuenta, ordenando nuevamente la lista por algún criterio para romper con ese barajeo aleatorio que introdujo el recuperar la lista.
 * Sólo tenemos que crear una clase que implemente IRule.
 * */
class MyOwnRule extends AbstractLoadBalancerRule {

	 /* From IRule doc:
     * choose one alive server from lb.allServers or lb.upServers according to key
     * @return choosen Server object. NULL is returned if none server is available */
	@Override
	public Server choose(Object key) {
		//List<Server> upServers =  this.getLoadBalancer().getReachableServers();
		//now we should order based on some criteria and use the mantained cursor of the last used server
		return null;
	}

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
		//
	}
}
