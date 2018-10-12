package it.tim.bonus.common.headers;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by alongo on 09/05/18.
 */
@Slf4j
public class TimHeadersExtractor {

    private TimHeadersExtractor() {}

    public static void populateHeaders(TimHeaders timHeaders, ObjectMapper objectMapper, HttpServletRequest request){

        if(timHeaders == null) throw new IllegalArgumentException("timHeaders input object must not be null");

        //Transient
        log.debug("HTTP-HEADERS received from client:");
        for(TransientHeaderName headerName : TransientHeaderName.values()) {
            timHeaders.setHeader(headerName, request.getHeader(headerName.value()));
                     
            log.debug("Key : " + headerName + " Value : " + request.getHeader(headerName.value()));
           
        }
        
        //Session
        String sessionHeader = request.getHeader(TimHeaders.SESSION_HEADER);

    }


    

}
