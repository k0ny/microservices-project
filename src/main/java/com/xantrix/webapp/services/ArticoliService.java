package com.xantrix.webapp.services;

import org.springframework.data.domain.Pageable;
import com.xantrix.webapp.dtos.*;
import com.xantrix.webapp.entities.*;
import java.util.List;

public interface ArticoliService {
     public List<ArticoliDto> SelByDescrizione(String descrizione);
     public List<ArticoliDto> SelByDescrizione(String descrizione, Pageable paegeable);
     public ArticoliDto SelByCodArt(String codart);
     public Articoli SelByCodArt2(String codart); //per l'eliminazione
     public ArticoliDto SelByBarcode(String barcode);
     public void DelArticolo(Articoli articolo);
     public void InsArticolo(Articoli articolo);
     public void CleanCaches(); // pulizia di tutta la cache
}
