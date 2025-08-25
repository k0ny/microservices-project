package com.xantrix.webapp.dtos;
import lombok.Data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ArticoliDto {
    private String codArt;
    private String descrizione;
    private String um;
    private String codStat;
    private Integer pzCart;
    private double pesoNetto;
    private String idStatoArt;
    private Date dataCreaz;
    private double prezzo = 0;

    private Set<BarcodeDto> barcode = new HashSet<>();
    private IngredientiDto ingredienti;
    private IvaDto iva;
    private CategoriaDto famAssort;
}
