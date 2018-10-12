package it.tim.bonus.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.tim.bonus.aspects.Loggable;
import it.tim.bonus.model.integration.*;
import it.tim.bonus.model.web.*;
import it.tim.bonus.service.BonusService;
import it.tim.bonus.validation.BonusControllerValidator;

/**
 * Created by alongo on 13/04/18.
 */
@RestController
@RequestMapping("/api")
@Api("Controller exposing pin operations")
public class BonusController {

    private BonusService bonusService;

    private static final DateTimeFormatter AUTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    
    @Autowired
    public BonusController(BonusService bonusService) {
        this.bonusService = bonusService;
    }
    
    @RequestMapping(method = RequestMethod.POST, value = "/bonusadjust" , produces = "application/json")
    @ApiOperation(value = "Refill operation with scratch card", response = BonusResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Refill success"),
            @ApiResponse(code = 400, message = "Missing or wrong mandatory parameters"),
            @ApiResponse(code = 404, message = "Wrong card number or card not found"),
            @ApiResponse(code = 401, message = "Not authorized due to max attempts reached"),
            @ApiResponse(code = 500, message = "Generic error"),
    })
    @Loggable
    public BonusResponse bonusadjust( @RequestBody BonusAdjustRequest request, 
    		                                @RequestHeader HttpHeaders headers,
    		                                @RequestHeader(value = "businessID", required = false) String xBusinessId,    		
											@RequestHeader(value = "messageID", required = false) String xMessageID,    		
											@RequestHeader(value = "transactionID", required = false) String xTransactionID,    		
											@RequestHeader(value = "channel", required = false) String xChannel,    		
											@RequestHeader(value = "sourceSystem", required = false) String xSourceSystem   		
								    	  )
    {
    	
        BonusControllerValidator.validateBonusAdjustRequest(request);
        
        AdjusteResponse bonusResp = bonusService.manageAdjustBonus(request.getMsisdn(),request.getPrize(),request.getSubSys(),headers);
        
        return new BonusResponse(bonusResp.getStatus(), bonusResp.getEsito(), bonusResp.getDescription(),bonusResp.getInteractionDate());
        

    }
    
    
    @RequestMapping(method = RequestMethod.POST, value = "/bonuscommit" , produces = "application/json")
    @ApiOperation(value = "Refill operation with scratch card", response = BonusResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Refill success"),
            @ApiResponse(code = 400, message = "Missing or wrong mandatory parameters"),
            @ApiResponse(code = 404, message = "Wrong card number or card not found"),
            @ApiResponse(code = 401, message = "Not authorized due to max attempts reached"),
            @ApiResponse(code = 500, message = "Generic error"),
    })
    @Loggable
    public BonusResponse bonuscommit( @RequestHeader HttpHeaders headers,
    		                                @RequestHeader(value = "businessID", required = false) String xBusinessId,    		
											@RequestHeader(value = "messageID", required = false) String xMessageID,    		
											@RequestHeader(value = "transactionID", required = false) String xTransactionID,    		
											@RequestHeader(value = "channel", required = false) String xChannel,    		
											@RequestHeader(value = "sourceSystem", required = false) String xSourceSystem   		
								    	  )
    {
    	
        CommitAdjusteResponse bonusResp = bonusService.manageCommitBonus(headers);
        
        return new BonusResponse(bonusResp.getStatus(), bonusResp.getEsito(), bonusResp.getDescription(),bonusResp.getInteractionDate());
        

    }
    
    

    public String composeHeaderDateTime(){
    	
        LocalDateTime bankAuthDate = LocalDateTime.now();
        String authDate = bankAuthDate.format(AUTH_DATE_FORMATTER);
        return authDate;
    }
}
