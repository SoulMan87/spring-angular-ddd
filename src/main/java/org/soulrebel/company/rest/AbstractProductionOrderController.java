package org.soulrebel.company.rest;

import org.soulrebel.company.model.CompleteRequest;
import org.soulrebel.company.model.RenameRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

@Validated
@RequestMapping(value = "/api/production-orders")
public abstract class AbstractProductionOrderController {

    public static final String REL_RENAME = "rename";
    public static final String REL_SUBMIT = "submit";
    public static final String REL_ACCEPT = "accept";

    @PostMapping(value = "/{id}/rename")
    abstract ResponseEntity<?> rename(@PathVariable Long id, @RequestBody RenameRequest request);

    @PostMapping(value = "/{id}/submit")
    abstract ResponseEntity<?> submit(@PathVariable Long id);


    @PostMapping(value = "/{id}/accept")
    abstract ResponseEntity<?> accept(@PathVariable Long id, @RequestBody CompleteRequest request);

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    protected void handleValidation(Exception exception) {
        throw new ResponseStatusException (HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage ());
    }

}
