package it.tim.bonus.service;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;

import it.tim.bonus.integration.client.BonusClient;
import it.tim.bonus.model.configuration.Constants;
import it.tim.bonus.model.exception.GenericException;
import it.tim.bonus.model.integration.AdjusteResponse;
import it.tim.bonus.model.integration.CommitAdjusteResponse;

@RunWith(MockitoJUnitRunner.class)
public class BonusServiceTest {


    @Mock
    private BonusClient bonusClient;


    private BonusService service;

    @Before
    public void init(){
        service = new BonusService(bonusClient);
    }

    @After
    public void cleanup(){
        Mockito.reset(bonusClient);
    }

    @Test(expected = GenericException.class)
    public void manageAdjustBonusIncompleteResponse() {

    	AdjusteResponse reserve = service.manageAdjustBonus(
                "3400000001",
                "1000.00",
                Constants.Subsystems.MYTIMAPP.toString(),
                new HttpHeaders()
        );

        Assert.assertNotNull(reserve);

    }
    
    public void manageCommitBonusIncompleteResponse() {

    	CommitAdjusteResponse reserve = service.manageCommitBonus(
                new HttpHeaders()
        );

        Assert.assertNotNull(reserve);

    }
}