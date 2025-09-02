package com.xantrix.webapp.entities.exceptions;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse
{
    private LocalDate date;
    private int code;
    private String message;
}
