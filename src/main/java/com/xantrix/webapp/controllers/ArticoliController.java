package com.xantrix.webapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.InfoMsg;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.exceptions.BindingException;
import com.xantrix.webapp.exceptions.DuplicateException;
import com.xantrix.webapp.exceptions.NotFoundException;
import com.xantrix.webapp.services.ArticoliService;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/articoli")
@Api(value = "alphashop", tags="Controller Operazioni di gestione dati articoli") //per ottenere informazioni sulla documentazione Swagger
@Log
public class ArticoliController
{
    @Autowired
    private ArticoliService articoliService;

    @Autowired
    private ResourceBundleMessageSource errMessage;

    // Ricerca per Barcode
    @ApiOperation(
            value="Ricerca l'articolo per BARCODE",
            notes = "Restituisce i dati dell'articolo in formato JSON",
            response = Articoli.class,
            produces = "application/json")
    @ApiResponses(value =
            {   @ApiResponse(code = 200, message = "L'articolo cercato è stato trovato!"),
                @ApiResponse(code = 404, message = "L'articolo cercato NON è stato trovato!"),
                @ApiResponse(code = 403, message = "Non sei AUTORIZZATO ad accedere alle informazioni"),
                @ApiResponse(code = 401, message = "Non sei AUTENTICATO")
            })
    @SneakyThrows
    @GetMapping(value = "/cerca/barcode/{ean}", produces = "application/json")
    public ResponseEntity<ArticoliDto> listArtByEan(@ApiParam("Barcode univoco dell'articolo") @PathVariable("barcode") String Barcode)
            throws NotFoundException
    {
        log.info(String.format("**** Articolo con barcode %s ****", Barcode));
        ArticoliDto articolo = articoliService.SelByBarcode(Barcode);

        if(articolo == null){ //se non trovo articolo con barcode restituisco un 404
            String ErrMsg = String.format("Il barcode %s non e' stato trovato!", Barcode);
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

    @SneakyThrows
    @PostMapping(value = "/inserisci", produces = "application/json")
    public ResponseEntity<InfoMsg> createArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult) //@Valid fa la validazione dell'input
    {
        log.info("Salviamo l'articolo con codice " + articolo.getCodArt());

        //controllo validità dati articolo
        if (bindingResult.hasErrors())
        {
            String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
            log.warning(MsgErr);
            throw new BindingException(MsgErr);
        }

        //Disabilitare se si vuole gestire anche la modifica
        ArticoliDto checkArt =  articoliService.SelByCodArt(articolo.getCodArt());

        if (checkArt != null)
        {
            String MsgErr = String.format("Articolo %s presente in anagrafica! "
                    + "Impossibile utilizzare il metodo POST", articolo.getCodArt());

            log.warning(MsgErr);
            throw new DuplicateException(MsgErr);
        }

        articoliService.InsArticolo(articolo);

        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                "Inserimento Articolo Eseguita con successo!"), HttpStatus.CREATED);
    }

    // ------------------- MODIFICA ARTICOLO ------------------------------------
    @SneakyThrows
    @RequestMapping(value = "/modifica", method = RequestMethod.PUT)
    public ResponseEntity<InfoMsg> updateArt(@Valid @RequestBody Articoli articolo, BindingResult bindingResult)
    {
        log.info("Modifichiamo l'articolo con codice " + articolo.getCodArt());
        if (bindingResult.hasErrors())
        {
            String MsgErr = errMessage.getMessage(bindingResult.getFieldError(), LocaleContextHolder.getLocale());
            log.warning(MsgErr);
            throw new BindingException(MsgErr);
        }

        ArticoliDto checkArt =  articoliService.SelByCodArt(articolo.getCodArt());
        if (checkArt == null)
        {
            String MsgErr = String.format("Articolo %s non presente in anagrafica! "
                    + "Impossibile utilizzare il metodo PUT", articolo.getCodArt());
            log.warning(MsgErr);
            throw new NotFoundException(MsgErr);
        }
        articoliService.InsArticolo(articolo);
        return new ResponseEntity<InfoMsg>(new InfoMsg(LocalDate.now(),
                "Modifica Articolo Eseguita con successo!"), HttpStatus.CREATED);
    }

    // ------------------- ELIMINAZIONE ARTICOLO ------------------------------------
    @SneakyThrows
    @DeleteMapping(value = "/elimina/{codart}", produces = "application/json" )
    public ResponseEntity<?> deleteArt(@PathVariable("codart") String CodArt)
    {
        log.info("Eliminiamo l'articolo con codice " + CodArt);
        Articoli articolo = articoliService.SelByCodArt2(CodArt);
        if (articolo == null)
        {
            String MsgErr = String.format("Articolo %s non presente in anagrafica!",CodArt);
            log.warning(MsgErr);
            throw new NotFoundException(MsgErr);
        }

        articoliService.DelArticolo(articolo);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();

        responseNode.put("code", HttpStatus.OK.toString());
        responseNode.put("message", "Eliminazione Articolo " + CodArt + " Eseguita Con Successo");

        return new ResponseEntity<>(responseNode, new HttpHeaders(), HttpStatus.OK);

    }

}
