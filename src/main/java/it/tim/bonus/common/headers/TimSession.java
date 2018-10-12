package it.tim.bonus.common.headers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TimSession {
	private String tiid;
    private String userAccount;
    @JsonProperty("cf_piva")
    private String userReference;
    private String dcaCoockie;
    private String accountType;
    
}
