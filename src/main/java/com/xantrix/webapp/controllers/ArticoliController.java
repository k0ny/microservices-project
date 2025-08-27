package com.xantrix.webapp.controllers;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.services.ArticoliService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/articoli")
@Log
public class ArticoliController
{
    @Autowired
    private ArticoliService articoliService;

    @SneakyThrows
    @GetMapping(value = "/cerca/barcode/{ean}", produces = "application/json")
    public ResponseEntity<ArticoliDto> listArtByEan(@PathVariable("ean") String Ean)
    {
        log.info(String.format("**** Articolo con barcode %s ****", Ean));
        ArticoliDto articolo = articoliService.SelByBarcode(Ean);

        if(articolo == null){ //se non trovo articolo con barcode restituisco un 404
            String ErrMsg = String.format("Il barcode %s non e' stato trovato!", Ean);
            log.warning(ErrMsg);

            throw new NotFoundException(ErrMsg);//funziona con annotazione SneakyThrows
        }
        return new ResponseEntity<ArticoliDto>(articolo, HttpStatus.OK); //else OK 200
    }

    @SneakyThrows
    @GetMapping(value = "/cerca/codice/{codart}", produces = "application/json")
    public ResponseEntity<ArticoliDto> listArtByCodArt(@PathVariable("codart") String CodArt)
    {
        log.info(String.format("**** Articolo con codice %s ****", CodArt));
        ArticoliDto articolo = articoliService.SelByCodArt(CodArt);

        if(articolo == null){ //se non trovo articolo con barcode restituisco un 404
            String ErrMsg = String.format("L'articolo con codice %s non e' stato trovato!", CodArt);
            log.warning(ErrMsg);

            throw new NotFoundException(ErrMsg);//funziona con annotazione SneakyThrows
        }
        return new ResponseEntity<ArticoliDto>(articolo, HttpStatus.OK); //else OK 200
    }

    @SneakyThrows
    @GetMapping (value = "/cerca/descrizione/{filter}", produces = "application/json")
    public ResponseEntity<List<ArticoliDto>> listArtByDesc(@PathVariable("filter") String Filter)
    {
        log.info(String.format("**** Articoli con Descrizione %s ****", Filter));
        List<ArticoliDto> articoli = articoliService.SelByDescrizione(Filter);

        if(articoli.isEmpty()){ //se non trovo articoli con il filtro restituisco un 404
            String ErrMsg = String.format("Non e' stato trovato alcun articolo avente descrizione %s", Filter);
            log.warning(ErrMsg);

            throw new NotFoundException(ErrMsg);//funziona con annotazione SneakyThrows
        }
        return new ResponseEntity<List<ArticoliDto>>(articoli, HttpStatus.OK); //else OK 200
    }
}
