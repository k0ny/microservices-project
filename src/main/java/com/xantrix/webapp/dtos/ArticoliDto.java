package com.xantrix.webapp.dtos;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class ArticoliDto implements Serializable
{
    private static final long serialVersionUID = -5193838648780749601L;//per usare Hazel
    private String codArt;
    private String descrizione;
    private String um;
    private String codStat;
    private Integer pzCart;
    private double pesoNetto;
    private String idStatoArt;
    private Date dataCreazione;
    private double prezzo = 0;

    private Set<BarcodeDto> barcode = new HashSet<>();
    private IngredientiDto ingredienti;
    private IvaDto iva;
    private CategoriaDto famAssort;
}
