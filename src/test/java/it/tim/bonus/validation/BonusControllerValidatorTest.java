package it.tim.bonus.validation;

import org.junit.Test;

import it.tim.bonus.model.configuration.Constants;
import it.tim.bonus.model.exception.BadRequestException;
import it.tim.bonus.model.web.BonusAdjustRequest;

/**
 * Created by alongo on 30/04/18.
 */
public class BonusControllerValidatorTest {

    @Test
    public void validatePrivateConstructor() throws Exception {
        new BonusControllerValidator();
    }

    @Test
    public void validateBonusAdjustRequestOk() throws Exception {
    	BonusAdjustRequest request = new BonusAdjustRequest(
    			"3400000001",
                "1111111111111111",
                Constants.Subsystems.MYTIMAPP.name());
    	
        BonusControllerValidator.validateBonusAdjustRequest(request);
    }

    @Test(expected = BadRequestException.class)
    public void validateBonusAdjustRequestNoMSISDN() throws Exception {
    	
    	BonusAdjustRequest request = new BonusAdjustRequest(
    			null,
                "1111111111111111",
                Constants.Subsystems.MYTIMAPP.name());
    	
    	BonusControllerValidator.validateBonusAdjustRequest(request);
    }


    @Test(expected = BadRequestException.class)
    public void validateBonusAdjustRequestNoSubSys() throws Exception {
    	BonusAdjustRequest request = new BonusAdjustRequest(
    			"3400000001",
                "1111111111111111",
                null);
    	
        BonusControllerValidator.validateBonusAdjustRequest(request);
    }
    
    @Test(expected = BadRequestException.class)
    public void validateBonusAdjustRequestNoPrize() throws Exception {
    	BonusAdjustRequest request = new BonusAdjustRequest(
    			"3400000001",
                null,
                Constants.Subsystems.MYTIMAPP.name());
    	
        BonusControllerValidator.validateBonusAdjustRequest(request);
    }
}