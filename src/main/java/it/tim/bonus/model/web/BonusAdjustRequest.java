package it.tim.bonus.model.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by alongo on 30/04/18.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BonusAdjustRequest {

    @ApiModelProperty(notes = "Utente utilizzatore per cui prenotare il codice PIN", required = true)
    private String msisdn;
    @ApiModelProperty(notes = "importo bonus trasferito", required = true)
    private String prize;
    @ApiModelProperty(notes = "Canale da cui proviene la richiesta", required = true)
    private String subSys;
}
