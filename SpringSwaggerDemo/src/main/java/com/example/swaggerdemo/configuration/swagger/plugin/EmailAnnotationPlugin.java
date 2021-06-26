package com.example.swaggerdemo.configuration.swagger.plugin;

import javax.validation.constraints.Email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static springfox.bean.validators.plugins.Validators.annotationFromBean;

import java.util.Optional;

import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;

/**
 * OBSERVAR QUE AHORA
 * el endpoint /users para POST, en Model, tiene pattern y example
 * */

@Component
@Order(-2147483148) //(Validators.BEAN_VALIDATOR_PLUGIN_ORDER)// 	-2147483148
public class EmailAnnotationPlugin implements ModelPropertyBuilderPlugin {
	
	@Autowired
    private ApplicationContext ctx;
	
	
	/**
	 * allow any documentation type like Swagger 1.2 and Swagger 2.
	 * */
    @Override
    public boolean supports(DocumentationType delimiter) {
        return true;
    }
    
    
    /**
     * the API specifications will show the pattern and example values of the property annotated with the @Email annotation.
     * */
    @Override
    public void apply(ModelPropertyContext context) {
        Optional<Email>	email = annotationFromBean(context, Email.class);
        if (email.isPresent()) {
	        context.getSpecificationBuilder().example("email@email.com");
            context.getBuilder().pattern(email.get().regexp());
            context.getBuilder().example("email@email.com");
        }
    }
       
}
