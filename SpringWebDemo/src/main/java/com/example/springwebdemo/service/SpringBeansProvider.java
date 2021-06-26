package com.example.springwebdemo.service;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/** PARA OBTENER BEANS DE LUGARES RECÓNDITOS COMO UN .JSP
 * 
 * You can create a class that is Spring-managed and application context aware.
 * This class will provide Spring bean via static methods from anywhere in your code.
 * 
 * From anywhere in your code, use SpringBeansProvider.getBean("myBean", MyBean.class). 
 * Yes, this breaks down a concept of beans injection and mixes up static and non-static methods usage, 
 * but such kind of task always cause those unfair things.
 * 
 * No creo que sirva!!!, setApplicationContext sólo lo llama el container si inyectamos, ya que el lo instancia,
 * entonces applicationContext será null.
 * */

@Service
public class SpringBeansProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static <T> T getBean(String beanName, Class<T> type) {
        return applicationContext.getBean(beanName, type);
    }
    
    public static <T> T getBean(Class<T> clazz) {
    	return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

}