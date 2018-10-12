package it.tim.bonus.controller.component;

import static net.logstash.logback.argument.StructuredArguments.value;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogCollectorComponent   implements Ordered {
	
	@Around("execution(public * it.tim.pin.web.*Controller.*(..))")
	public Object profile(ProceedingJoinPoint joinPoint) throws Throwable {
		
		InetAddress ip = InetAddress.getLocalHost();
	
		Object result=null;
		String executionResult=null;
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		MethodSignature signature = (MethodSignature) joinPoint.getStaticPart().getSignature();
		Method method = signature.getMethod();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Object[] args = joinPoint.getArgs();
		
		String body = "";
		String sessionID = "";
		String userAccount = "";
		String cf_piva = "";
		String sorgente = "";
		for (int i = 0; i < args.length; i++) {
			for (Annotation annotation : parameterAnnotations[i]) {

				if (annotation instanceof RequestBody) {	
					Object obj = args[i];
					body = obj.toString();
					continue;
				}
				if (annotation instanceof RequestHeader && "SessionID".equalsIgnoreCase(((RequestHeader) annotation).value())) {
					sessionID = (String) args[i];
					continue;
				}
				
			}
		}
		
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		String servizio = request.getMethod()+":"+request.getServletPath();
		
		try {
			result = joinPoint.proceed();
			
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
			executionResult=response.getStatus()+"";

		} catch (Exception ex) {			

			
			HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
			ResponseStatus annotation = findAnnotation(ex.getClass(), ResponseStatus.class);
			if (annotation != null) {
				status = annotation.code();
			}

			executionResult = status.value() + "";
			
			throw ex;
		} finally {
			stopWatch.stop();
			
			if(!("GET".equals(request.getMethod()) && "/ready".equals(request.getServletPath()))) {
				MDC.put("ID_Sessione", sessionID);
				MDC.put("Timestamp", getTimestamp());
				MDC.put("Sorgente", sorgente);
				MDC.put("Destinazione", ip.getHostAddress());
				MDC.put("Utenza", userAccount);
				MDC.put("Profilo_Utenza", "");
				MDC.put("Servizio", servizio);
				MDC.put("Tipo_Evento", method.getName());
				MDC.put("Evento", body);
				log.info("Response for API " + request.getMethod() + ":" + request.getServletPath() + " Method["
						+ method.getName() + "] in " + stopWatch.getTotalTimeMillis() + "ms " + "Result Code: ["
						+ executionResult + "]", value("OCPLOGDEST1", "LOGCOLLECTOR"));
			}

			MDC.clear();
		}
		return result;
	}

	@Override
	public int getOrder() {
		return 1000;
	}
	
	
	private String getTimestamp() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HHmm-ss");
		String strDate = sdf.format(cal.getTime());
		return strDate;
	}

	public static JWTToken decodeJWTString(String sessionJWTString) throws Exception {
		Jwt token = JwtHelper.decode(sessionJWTString);
		String decodedToken = token.getClaims();
		log.debug("decoded token=[" + decodedToken + "]");
		JWTToken jwt = new ObjectMapper().readValue(decodedToken, JWTToken.class);
		log.debug("JWT=[" + jwt + "]");
		return jwt;
	}

}


@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
class JWTToken {
    private String rifCliente;
	private String userAccount;
	private String sorgente;
		
}

