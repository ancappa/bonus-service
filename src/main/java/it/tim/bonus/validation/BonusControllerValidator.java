package it.tim.bonus.validation;

import java.util.function.Predicate;

import it.tim.bonus.model.exception.BadRequestException;
import it.tim.bonus.model.web.BonusAdjustRequest;

/**
 * Created by alongo on 30/04/18.
 */
public class BonusControllerValidator {

    BonusControllerValidator() {}

    public static void validateBonusAdjustRequest(BonusAdjustRequest request) {

        boolean valid = validateStrings(CommonValidators.validPhoneNumber, request.getMsisdn())
                && request.getPrize()!=null
                && request.getSubSys()!=null;
        
        if(!valid)
            throw new BadRequestException("Missing/Wrong parameters in ReservationRequest");

    }

    //UTIL

    private static boolean validateStrings(Predicate<String> predicate, String... strings){
        for(String s : strings){
            if(!predicate.test(s))
                return false;
        }
        return true;
    }

}
