package it.tim.bonus.model.integration;

import lombok.Data;

@Data
public class AdjustRequest {

    private String msisdn;
    private String amount;
    private String subSys;
    private String paymentType;
	private String interactionDate;
	
}
