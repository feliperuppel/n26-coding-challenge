package com.n26.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.n26.domain.Transaction;
import com.n26.domain.validator.TransactionValidator;
import com.n26.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    static Logger logger = LogManager.getLogger(TransactionController.class);

    private TransactionValidator validator;
    private TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionValidator validator, TransactionService transactionService){
        this.validator = validator;
        this.transactionService = transactionService;
    }


    @InitBinder
    protected void initBinder(WebDataBinder binder){
        binder.setValidator(validator);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public void registerTransaction(@Valid @RequestBody Transaction transaction){
        logger.info("Add request received : " + transaction);
        transactionService.add(transaction);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping
    public void clearTransactions(){
        logger.info("Clear request received");
        transactionService.deleteAll();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity validationErrorFound(MethodArgumentNotValidException e){
        logger.error("Validation Error Found: Status " + e.getBindingResult().getGlobalError().getCode());
        return ResponseEntity.status(Integer.valueOf(e.getBindingResult().getGlobalError().getCode())).build();
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidFormatException.class)
    private void notParsableException(Exception e){
        logger.error("Unprocessable Entity: Caused By " + e.getCause());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({JsonParseException.class, MismatchedInputException.class})
    private void invalidJson(Exception e){
        logger.error("Invalid Json: Caused By " + e.getCause());
    }
}
