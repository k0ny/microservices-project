package com.xantrix.webapp.services;

import org.springframework.data.domain.Pageable;
import com.xantrix.webapp.dtos.*;
import com.xantrix.webapp.entities.*;
import java.util.List;

public interface ArticoliService {
     List<ArticoliDto> SelByDescrizione(String descrizione);
     List<ArticoliDto> SelByDescrizione(String descrizione, Pageable paegeable);
     ArticoliDto SelByCodArt(String codart);
     ArticoliDto SelByBarcode(String barcode);
     void DelArticolo(Articoli articolo);
     void InsArticolo(Articoli articolo);
}
