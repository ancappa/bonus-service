package it.tim.bonus.integration.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import it.tim.bonus.integration.FeignConfiguration;

@FeignClient(
        name="bonusClient",
        url = "${integration.soap.bonusbasepath}"
        , configuration = FeignConfiguration.class
)
public interface BonusClient {
    @PostMapping(value = "/${integration.soap.bonusvalue}",  produces = "application/xml", consumes = "application/xml")
    String callOBJ(@RequestBody String request );
}
