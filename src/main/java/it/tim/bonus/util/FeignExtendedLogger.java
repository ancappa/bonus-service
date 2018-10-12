package it.tim.bonus.util;

import static feign.Util.UTF_8;
import static feign.Util.decodeOrDefault;
import static feign.Util.valuesOrEmpty;
import static net.logstash.logback.marker.Markers.appendEntries;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import feign.Request;
import feign.Response;
import feign.Util;

public class FeignExtendedLogger extends feign.Logger {

	private final Logger logger;
	
	public FeignExtendedLogger() {
		this(feign.Logger.class);
	}

	public FeignExtendedLogger(Class<?> clazz) {
		this(LoggerFactory.getLogger(clazz));
	}

	public FeignExtendedLogger(String name) {
		this(LoggerFactory.getLogger(name));
	}

	FeignExtendedLogger(Logger logger) {
		this.logger = logger;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void logRequest(String configKey, Level logLevel, Request request) {
		String method = request.method();
		String url = request.url();
		HashMap map = new HashMap();
		map.put("feign_method", request.method());
		map.put("feign_url", request.url());
				
        HashMap<String,String> localHeaders = new HashMap<>();
		if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
			for (String field : request.headers().keySet()) {
				for (String value : valuesOrEmpty(request.headers(), field)) {
					map.put("feign_" + field, value);
					localHeaders.put(field, value);
				}
			}
			if( logger.isDebugEnabled()) {
		    	logger.debug(appendEntries(map), "Feign HTTP/1.1 request - Headers ["+localHeaders.toString()+"]" );	
		    }
			int bodyLength = 0;
			if (request.body() != null) {
				bodyLength = request.body().length;
			    if( logger.isDebugEnabled()) {
			    	logger.debug(appendEntries(map), "Feign HTTP/1.1 request - Body Lenght ["+bodyLength+"]" );	
			    }
				
				if (logLevel.ordinal() >= Level.FULL.ordinal()) {
					String bodyText = request.charset() != null ? new String(request.body(), request.charset()) : null;
					map.put("feign_body", bodyText != null ? bodyText : "Binary data");
					if( logger.isDebugEnabled()) {
				    	logger.debug(appendEntries(map), "Feign HTTP/1.1 request - Body ["+bodyText+"]");	
				    }
				}
			}
		}
		logger.info(appendEntries(map), "Feign HTTP/1.1 request - " + method + " " + url);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime) throws IOException {
		String reason = response.reason() != null && logLevel.compareTo(Level.NONE) > 0 ? " " + response.reason() : "";
		int status = response.status();
		HashMap map = new HashMap();
		map.put("feign_reason", reason);
		map.put("feign_status", status);
		map.put("feign_elapsed_time", elapsedTime);
		map.put("feign_method", response.request().method());
		map.put("feign_url", response.request().url());
		
		if (logLevel.ordinal() >= Level.HEADERS.ordinal()) {
			HashMap<String,String> localHeaders = new HashMap<>();
			for (String field : response.headers().keySet()) {
				for (String value : valuesOrEmpty(response.headers(), field)) {
					map.put("feign_" + field, value);
					localHeaders.put(field, value);
				}
			}
			if( logger.isDebugEnabled()) {
		    	logger.debug(appendEntries(map), "Feign HTTP/1.1 response - Headers ["+localHeaders.toString()+"]" );	
		    }
			int bodyLength = 0;
			if (response.body() != null && !(status == 204 || status == 205)) {
				// HTTP 204 No Content "...response MUST NOT include a message-body"
				// HTTP 205 Reset Content "...response MUST NOT include an entity"
				byte[] bodyData = Util.toByteArray(response.body().asInputStream());
				bodyLength = bodyData.length;
				if (logLevel.ordinal() >= Level.FULL.ordinal() && bodyLength > 0) {
					String bodyText = decodeOrDefault(bodyData, UTF_8, "Binary data");
					map.put("feign_body", bodyText);
					if( logger.isDebugEnabled()) {
				    	logger.debug(appendEntries(map), "Feign HTTP/1.1 response - Body ["+bodyText+"]" );	
				    }
				}
				map.put("feign_bodyLength", bodyLength);
				if( logger.isDebugEnabled()) {
			    	logger.debug(appendEntries(map), "Feign HTTP/1.1 response - Body Lenght ["+bodyLength+"]");	
			    }
				logger.info(appendEntries(map), "Feign HTTP/1.1 response. Url ["+response.request().method()+" "+response.request().url()+"] Status ["+status+"] Reason ["+reason+"] Elapsed time ["+elapsedTime+"] ms");
				return response.toBuilder().body(bodyData).build();
			} else {
				map.put("feign_bodyLength", bodyLength);
				if( logger.isDebugEnabled()) {
			    	logger.debug(appendEntries(map), "Feign HTTP/1.1 response - Body Lenght ["+bodyLength+"]");
			    }
			}
		}
		logger.info(appendEntries(map), "Feign HTTP/1.1 response. Url ["+response.request().method()+" "+response.request().url()+"] Status ["+status+"] Reason ["+reason+"] Elapsed time ["+elapsedTime+"] ms");
		return response;
	}

	@Override
	protected void log(String configKey, String format, Object... args) {
		// Not using SLF4J's support for parameterized messages (even though it would be
		// more efficient) because it would
		// require the incoming message formats to be SLF4J-specific.
		// logger.info(append("time", 238 ), "log message");

		logger.info(String.format(methodTag(configKey) + format, args));

	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
		HashMap map = new HashMap();
		map.put("feign_exception,class", ioe.getClass().getSimpleName());
		map.put("feign_exception.message", ioe.getMessage());
		map.put("feign_elapsed_time", elapsedTime);
		if (logLevel.ordinal() >= Level.FULL.ordinal()) {
	      StringWriter sw = new StringWriter();
	      ioe.printStackTrace(new PrintWriter(sw));
	      map.put("feign_exception,stacktrace", sw.toString());
		}
		logger.error(appendEntries(map), "Feign HTTP/1.1 error. Exception class ["+ioe.getClass().getSimpleName()+"] exception message ["+ioe.getMessage()+"] Elapsed Time ["+elapsedTime+"]" );
	    return ioe;
	  }
}
