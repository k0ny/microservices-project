package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.repository.ArticoliRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ArticoliServiceImpl implements ArticoliService
{

    @Autowired
    ArticoliRepository articoliRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ArticoliDto> SelByDescrizione(String descrizione)
    {
        String filter = "%" + descrizione.toUpperCase() + "%"; //Se il client passa "vino", diventa "%VINO%". Serve a fare una ricerca case-insensitive sql
        List<Articoli> articoli = articoliRepository.selByDescrizioneLike(filter);

        List<ArticoliDto> articoliDto = articoli
                .stream()//stream API di Java per trasformare ogni Articoli in ArticoliDto.
                .map(source -> modelMapper.map(source, ArticoliDto.class)) //libreria che converte automaticamente campi con lo stesso nome da un oggetto all’altro.
                .collect(Collectors.toList());//materializzi lo stream in una Lista (List<ArticoliDto>)
        return articoliDto;
    }

    @Override
    public List<ArticoliDto> SelByDescrizione(String descrizione, Pageable paegeable)
    {
        String filter = "%" + descrizione.toUpperCase() + "%"; //Se il client passa "vino", diventa "%VINO%". Serve a fare una ricerca case-insensitive sql
        List<Articoli> articoli = articoliRepository.findByDescrizioneLike(filter, paegeable);

        List<ArticoliDto> articoliDto = articoli
                .stream()//stream API di Java per trasformare ogni Articoli in ArticoliDto.
                .map(source -> modelMapper.map(source, ArticoliDto.class)) //libreria che converte automaticamente campi con lo stesso nome da un oggetto all’altro.
                .collect(Collectors.toList());//materializzi lo stream in una Lista (List<ArticoliDto>)
        return articoliDto;
    }

    @Override
    public ArticoliDto SelByCodArt(String codart)
    {
        Articoli articoli = this.SelByCodArt2(codart); //uso il SelByCodArt2 per evitare duplicazioni
        return this.ConvertToDto(articoli);
    }

    @Override
    public Articoli SelByCodArt2(String codart) //per l'liminazione
    {
        return articoliRepository.findByCodArt(codart);
    }

    @Override
    public ArticoliDto SelByBarcode(String barcode)
    {
        Articoli articoli = articoliRepository.SelByEan(barcode);
        return this.ConvertToDto(articoli);
    }

    @Override
    @Transactional
    public void DelArticolo(Articoli articolo) {
        articoliRepository.delete(articolo);
    }

    @Override
    @Transactional
    public void InsArticolo(Articoli articolo)
    {
        articolo.setDescrizione(articolo.getDescrizione().toUpperCase());
        articoliRepository.save(articolo);
    }

    //Metodo per convertire un Entity Articolo in ArticoloDTO con model mapper
    private ArticoliDto ConvertToDto(Articoli articoli)
    {
        ArticoliDto articoliDto = null;

        if(articoli != null){
            articoliDto = modelMapper.map(articoli, ArticoliDto.class); //riversa tutti i dati Entity nel Dto
        }
        return articoliDto;
    }
}
