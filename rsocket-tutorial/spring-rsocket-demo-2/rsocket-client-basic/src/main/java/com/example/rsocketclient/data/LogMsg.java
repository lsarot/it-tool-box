package com.example.rsocketclient.data;

import lombok.Data;
import org.springframework.boot.logging.LogLevel;

//@Data   //declaramos métodos propios para que el ide no nos dé warnings por todos lados
public class LogMsg {

	//private Class loggerClass;

    private String loggerName;
    
    private LogLevel level;
	    //org.springframework.boot.logging.LogLevel.FATAL    	NOT BOUND TO LOGGING FRAMEWORK | we can save lines of code by using logger.log(level, msg)
	    //org.apache.logging.log4j.Level.FATAL               				BOUND TO LOG4J | we can save lines of code by using logger.log(level, msg) | error serializing
	    //org.apache.logging.slf4j.Log4jLogger.WARN_INT      	BOUND TO LOG4J | doesn't has FATAL level
	    //java.util.logging.Level.FINE :(                    						NOT STANDARD
	    //org.slf4j.                                         									ONLY INTERFACE
    
    private String msg;
    
    private String exception;

    public LogMsg() {}
    
    public LogMsg(String loggerName, LogLevel level, String msg) {
        this.loggerName = loggerName;
        this.level = level;
        this.msg = msg;
    }

    public LogMsg(String loggerName, LogLevel level, String msg, String exception) {
        this.loggerName = loggerName;
        this.level = level;
        this.msg = msg;
        this.exception = exception;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getMsg() {
        return msg;
    }

    public String getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "LogMsg{" +
                "loggerName='" + loggerName + '\'' +
                ", level=" + level +
                ", msg='" + msg + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}
