package com.example.codigosbasicos_springboot.topics.retry_template;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Retry Template to handle the failure operations.
 * */
@RestController
@RequestMapping(value="/test-retry")
public class RetryTemplateController {

	@Autowired
    private RetryableService retriableService;
	
	@Autowired
	private ApplicationContext applicationContext;

    @GetMapping("/with-retry-template")
    public Boolean callRetryService() {
        return retriableService.retryWithRetryTemplate();
    }
    
    @GetMapping("/with-retryable-annotation")
    public Boolean callRetryService2() throws SQLException {
    	return retriableService.retryWithRetryableAnnotation("select from ...");
    }
	
    @GetMapping("/with-retryable-annotation-recoverable")
    public void callRetryService3() throws SQLException {
        retriableService.retryWithRetryableAnnotation_RecoveredByOtherMethod("select from table");
    }
    
}
