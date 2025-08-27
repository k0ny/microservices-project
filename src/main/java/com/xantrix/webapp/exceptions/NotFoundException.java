package com.xantrix.webapp.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends Exception
{
    private static final long serialVersionUID = -8729169303699924451L;
    private String messaggio = "Element not found!";

    public NotFoundException() {
        super();
    }

    public NotFoundException(String messaggio) {
        super(messaggio);
        this.messaggio = messaggio;
    }
}
