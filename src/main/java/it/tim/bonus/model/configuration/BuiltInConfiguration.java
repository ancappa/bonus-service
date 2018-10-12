package it.tim.bonus.model.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alongo on 26/04/18.
 */
@ConfigurationProperties(prefix = "builtin")
@Data
@Component
public class BuiltInConfiguration {

    private String keystorepath;
    private String httpsProtocols;
    private String keystoreAlias;
    private String keystorePass;
    

    
    public BuiltInConfiguration() {
	}

	public BuiltInConfiguration(String keystorepath) {
		super();
		this.keystorepath = keystorepath;
	}

	@Data
    public static class AmountsConfiguration{
        private List<Double> values;
        private Double defaultValue;
        private String promoInfo;
    }

    @Data
    public static class TermsAndConditionsConfiguration{
        private String title;
        private String text;
    }

}
