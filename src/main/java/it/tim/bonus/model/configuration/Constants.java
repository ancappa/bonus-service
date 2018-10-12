package it.tim.bonus.model.configuration;

import java.util.Arrays;

/**
 * Created by alongo on 30/04/18.
 */
public class Constants {

    public enum  Subsystems{
        MYTIMAPP, MYTIMWEB;

        public static boolean contains(String str) {
            return str != null
                    && Arrays.stream(values())
                    .map(Enum::toString)
                    .anyMatch(sub -> sub.equalsIgnoreCase(str));
        }
    }

}
