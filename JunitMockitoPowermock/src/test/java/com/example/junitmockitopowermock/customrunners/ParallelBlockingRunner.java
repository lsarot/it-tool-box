package com.example.junitmockitopowermock.customrunners;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;


//NO SÉ POR QUÉ EL BLOCK EN EL PARENT CLASS
public class ParallelBlockingRunner extends BlockJUnit4ClassRunner {

	public ParallelBlockingRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		System.out.println("running the tests from ParallelBlockingRunner: " + testClass);
	}
	
	@Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
		//super.
		
        return super.methodInvoker(method, test);
    }
	
}
