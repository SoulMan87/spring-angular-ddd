package org.soulrebel.company.rest;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RequestMapping(value = "/api/productionOrders")
public abstract class AbstractProductionOrderController {

    public static final String REL_RENAME = "rename";
    public static final String REL_SUBMIT = "submit";
    public static final String REL_ACCEPT = "accept";

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    protected void handleValidation(Exception exception) {
        throw new ResponseStatusException (HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage ());
    }

}
