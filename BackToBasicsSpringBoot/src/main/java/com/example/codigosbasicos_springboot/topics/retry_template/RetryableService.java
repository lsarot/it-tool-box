package com.example.codigosbasicos_springboot.topics.retry_template;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Service
public class RetryableService {

	@Autowired
	private RetryTemplate retryTemplate;
	private int counter = 0;

	
	/** Spring Retry provides RetryOperations interface which supplies a set of execute() methods:
	     public interface RetryOperations {
	         <T> T execute(RetryCallback<T> retryCallback) throws Exception;
	         ...
	   * The RetryCallback which is a parameter of the execute() is an interface that allows insertion of business logic that needs to be retried upon failure:
	    public interface RetryCallback<T> {
	        T doWithRetry(RetryContext context) throws Throwable;
	    }
	 * */
	public boolean retryWithRetryTemplate() {
		return retryTemplate.execute(context -> {
			System.out.println("Execution try from context info: " + context.getRetryCount());
			method();
			return true;// or any Object;
		});
		
		// or without lambda:
		/*return retryTemplate.execute(new RetryCallback<Boolean, RuntimeException>() {
		    @Override public Boolean doWithRetry(RetryContext arg0) {
		    	method();
				return true;// or any Object;
		    }
		});*/
	}

	private void method() {
		counter++;
		System.out.println("Execution try " + counter);
		if	(counter < 3)
			throw new RuntimeException();
		else
			counter = 0; // reset counter for other method
	}
	
	
	//@Retryable(value = { SQLException.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000), recover="methodNameMarkedWith@Recover")
	//@Retryable(value = { SQLException.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000), listeners = "MyRetryLogListener NO ENCUENTRA BEAN CON TAL NOMBRE, y ya creé una clase con ese nombre")
	@Retryable(value = { SQLException.class }, maxAttempts = 2, backoff = @Backoff(delay = 5000))
	public boolean retryWithRetryableAnnotation(String sql) throws SQLException {
		counter++;
		System.out.println("Execution try " + counter);
		if	(counter < 2)
			throw new SQLException();
		else
			counter = 0; // reset counter for other method
		return true;
	}
	
	// If @Retryable is used without any attributes, if the method fails with an exception, then retry will be attempted up to three times, with a delay of one second.
	@Retryable(value = { SQLException.class }, maxAttempts = 4, backoff = @Backoff(delay = 2000))
    public void retryWithRetryableAnnotation_RecoveredByOtherMethod(String sql) throws SQLException {
		counter++;
		System.out.println("Execution try " + counter);
		if	(counter == 4)
			counter = 0; // reset counter for other method

		throw new SQLException();
	}
	
	/** The @Recover annotation is used to define a separate recovery method when a @Retryable method fails with a specified exception:
	 * So if the retryService() method throws an SQLException, the recover() method will be called.
	 * A suitable recovery handler has its first parameter of type Throwable (optional).
	 * Subsequent arguments are populated from the argument list of the failed method in the same order as the failed method, and with the same return type.
	 * SUCEDE UNA VEZ QUE EL OTRO MÉTODO AGOTÓ TODOS SUS INTENTOS, NO ANTES!
	 * */
	@Recover
    public void recover(SQLException e, String sql) {
		System.out.println("Exception recuperada en el @Recover method: \n" + e);
		System.out.println("Parámetro recuperado del método fallido: \n" + sql);
	}

	// podemos tener varios recover, pero usará el que devuelva el mismo tipo, reciba los mismos parámetros y atrape la misma exception
	@Recover
    public void recover2(SQLException e, String sql, String abc) {
		System.out.println("Exception recuperada en el @Recover method: \n" + e);
		System.out.println("Parámetro recuperado del método fallido: \n" + sql);
	}
	
}
