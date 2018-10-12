package it.tim.bonus.web.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import it.tim.bonus.common.headers.TimHeaders;
import it.tim.bonus.common.headers.TimSession;
import it.tim.bonus.integration.client.BonusClient;
import it.tim.bonus.model.configuration.BuiltInConfiguration;
import it.tim.bonus.model.exception.BadRequestException;
import it.tim.bonus.model.web.BonusAdjustRequest;
import it.tim.bonus.service.BonusService;
import it.tim.bonus.web.BonusController;

/**
 * Created by alongo on 30/04/18.
 */
@RunWith(MockitoJUnitRunner.class)
//Tested as in-service integration test
public class BonusControllerTest {

    @Mock
    TimHeaders timHeaders;

    @Mock
    TimSession timHeadersSession;

    @Mock
    BuiltInConfiguration configuration;

    @Mock
    BonusClient bonusClient;

    BonusService bonusService;
    BonusController controller;

    @Before
    public void init(){
    
        
        bonusService = new BonusService(bonusClient);
        controller = new BonusController(bonusService);
    }

    @After
    public void cleanup(){
        Mockito.reset(timHeaders, configuration);
        Mockito.reset(bonusClient);

    }

    @Test(expected = BadRequestException.class)
    public void bonusadjustKoOnInvalidRequest() throws Exception {
    	
        controller.bonusadjust(new BonusAdjustRequest(), null, null, null, null, null, null);
    }
  
}