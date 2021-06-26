package com.example.leo.springwebfluxdemo.server;

import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class EmployeeFunctionalConfig {

	
	/**
	 * RouterFunction serves as an alternative to the @RequestMapping annotation. We can use it to route requests to the handler functions.
	 * The HandlerFunction represents a function that generates responses for requests routed to them.
	 * 
	 * Typically, we can import the helper function RouterFunctions.route()  to create routes, instead of writing a complete router function.
	 * It allows us to route requests by applying a RequestPredicate. When the predicate is matched, then the second argument, the handler function, is returned.
	 * 
	 * Note we are statically importing RouterFunctions.route, passing the predicate and a Handler Function
	 * 
	 * 
	 * USE THE TESTS
	 * */
	
	
	
    @Bean
    public EmployeeRepository employeeRepository() {
        return new EmployeeRepository();
    }

    @Bean
    public RouterFunction<ServerResponse> getAllEmployeesRoute() {
      return route(GET("/employees"), 
        req -> ok().body(
          employeeRepository().findAllEmployees(), Employee.class));
    }

    @Bean
	public RouterFunction<ServerResponse> getEmployeeByIdRoute() {
      return route(GET("/employees/{id}"), 
        req -> ok().body(
          employeeRepository().findEmployeeById(req.pathVariable("id")), Employee.class));
    }

    @Bean
    public RouterFunction<ServerResponse> updateEmployeeRoute() {
      return route(POST("/employees/update"), 
        req -> req.body(toMono(Employee.class))
                  .doOnNext(employeeRepository()::updateEmployee)
                  .then(ok().build()));
    }

    
    /**
     * WE CAN ALSO JOIN ALL ROUTES IN A SINGLE ROUTE OBJECT
     * */
    @Bean
    public RouterFunction<ServerResponse> composedRoutes() {
      return 
          route(GET("/employees"), 
            req -> ok().body(
              employeeRepository().findAllEmployees(), Employee.class))
            
          .and(route(GET("/employees/{id}"), 
            req -> ok().body(
              employeeRepository().findEmployeeById(req.pathVariable("id")), Employee.class)))
            
          .and(route(POST("/employees/update"), 
            req -> req.body(toMono(Employee.class))
                      .doOnNext(employeeRepository()::updateEmployee)
                      .then(ok().build())));
    }

}
