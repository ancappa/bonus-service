package it.tim.bonus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.DefaultFeignLoggerFactory;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignLoggerFactory;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import it.tim.bonus.util.FeignExtendedLogger;

@SpringBootApplication
@EnableFeignClients
@EnableHystrix
@EnableHystrixDashboard
@EnableAspectJAutoProxy
public class BonusApplication extends WebMvcConfigurerAdapter {


    public static void main(String[] args) {
        SpringApplication.run(BonusApplication.class, args);
    }
  
    @Bean
    public FeignLoggerFactory feignLoggerFactory() {
    	return new DefaultFeignLoggerFactory(new FeignExtendedLogger());
    }
    

    @Bean
    @SuppressWarnings("unused")
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(true);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

 

}
