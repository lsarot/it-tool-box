package com.example.junitmockitopowermock.customrunners;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.InitializationError;

/**
 * PODEMOS HACER LLAMADAS EN PARALELO EN runChild,
 * SI TENEMOS UN THREAD POOL A NIVEL DE CLASE,
 * pero igual no está mostrando resultados de la ejecución detallado por método.
 * 
 * Puede recuperar Categorías desde getChildren y filtrar qué clases ejecutará.
 * */

public class ParallelRunnerFromParent extends ParentRunner<Object> {

	 	private Class<?> testClass;
	    
	    public ParallelRunnerFromParent(Class<?> testClass) throws InitializationError {
	        super(testClass);
	        this.testClass = testClass;
	    }
	 
	    @Rule
	    public TestName name = new TestName();
	    
	    @Override
	    public Description getDescription() {
	        return Description.createTestDescription(testClass, "Basic Parallel Runner, extends from ParentRunner.");
	    }
	    
	  	@Override
	  	protected List<Object> getChildren() {
	  	    Annotation[] runnerAnnotations = super.getRunnerAnnotations();

	  	    Optional<Object> suitClass = Arrays.asList(runnerAnnotations).stream()
	  	            .filter(a -> ((Annotation) a).annotationType().equals(SuiteClasses.class))
	  	            .findFirst();

	  	    List<Object> list = new ArrayList<>();
	  	    if (suitClass.isPresent()) {
	  	        SuiteClasses s = (SuiteClasses) suitClass.get();
	  	        //s.value();
	  	        System.out.println("Adding items to list");
	  	        for (Class<?> c : s.value()) {
	  	            Class<?> cp = (Class<?>) c;
	  	            try {
	  	                list.add(cp.newInstance());
	  	            } catch (InstantiationException | IllegalAccessException e) {
	  	                e.printStackTrace();
	  	            }
	  	        }
	  	    }
	  	    return list;
	  	}

	  	@Override
	  	protected Description describeChild(Object child) {
	  		System.out.println("describeChild class: " + child.getClass().getSimpleName());
	        Description desc = Description.createTestDescription(name.getMethodName(), name.getMethodName(), getClass().getAnnotations());
	        return desc;
	  	}

	  	@Override
	  	protected void runChild(Object child, RunNotifier notifier) {
	  		System.out.println("runChild " + child.getClass().getSimpleName());
	  		notifier.fireTestStarted(Description.createTestDescription(child.getClass(), child.getClass().getSimpleName()));
	  		
	  		Result result = JUnitCore.runClasses(child.getClass());
	  		
	  		notifier.fireTestFinished(Description.createTestDescription(child.getClass(), child.getClass().getSimpleName()));
	  	}
	
}
