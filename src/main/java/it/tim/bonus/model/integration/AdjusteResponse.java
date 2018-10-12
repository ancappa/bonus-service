package it.tim.bonus.model.integration;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data
public class AdjusteResponse {

	private String esito;
    private String description;
    private String status;
    private String interactionDate;
}
