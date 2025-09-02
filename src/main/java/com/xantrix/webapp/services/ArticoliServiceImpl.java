package com.xantrix.webapp.services;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import com.xantrix.webapp.repository.ArticoliRepository;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@CacheConfig(cacheNames={"articoli"})
@Log
public class ArticoliServiceImpl implements ArticoliService
{

    @Autowired
    ArticoliRepository articoliRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    CacheManager cacheManager;

    @Override
    @Cacheable //il risutlato verra inserito nella cache articoli creata con la class
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
    @Cacheable
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
    @Cacheable(value = "articolo", key = "#codart", sync = true) //nuova cache articolo, dove la chiave è codart
    public ArticoliDto SelByCodArt(String codart)
    {
        Articoli articoli = this.SelByCodArt2(codart); //uso il SelByCodArt2 per evitare duplicazioni
        return this.ConvertToDto(articoli);
    }

    @Override
    @Cacheable(value = "articolo", key = "#codart", sync = true) //nuova cache articolo, dove la chiave è codart
    public Articoli SelByCodArt2(String codart) //per l'liminazione
    {
        return articoliRepository.findByCodArt(codart);
    }

    @Override
    @Cacheable(value = "barcode", key = "#barcode", sync = true) //nuova cache barcode, dove la chiave è barcode
    public ArticoliDto SelByBarcode(String barcode)
    {
        Articoli articoli = articoliRepository.SelByEan(barcode);
        return this.ConvertToDto(articoli);
    }

    @Override
    @Transactional
    @Caching(evict = { //evict serve ad indicare cosa bisogna eliminare
            @CacheEvict(cacheNames="articoli", allEntries = true), //dobbiamo eliminare tutto poiche non abbiamo modo di identificare singolo articolo
            //@CacheEvict(cacheNames="barcode",key = "#articolo.barcode[0].barcode"),//eliminiamo singolo barcode di inserimento
            @CacheEvict(cacheNames="articolo",key = "#articolo.codArt")
            })
    public void DelArticolo(Articoli articolo)
    {
        articoliRepository.delete(articolo);
        this.EvictCache(articolo.getBarcode());//cancello barcode dalla cache barcode
    }

    @Override
    @Transactional
    @Caching(evict = { //evict serve ad indicare cosa bisogna eliminare
            @CacheEvict(cacheNames="articoli", allEntries = true), //dobbiamo eliminare tutto poiche non abbiamo modo di identificare singolo articolo
            //@CacheEvict(cacheNames="barcode",key = "#articolo.barcode[0].barcode"),//eliminiamo singolo barcode di inserimento
            @CacheEvict(cacheNames="articolo",key = "#articolo.codArt")
            })
    public void InsArticolo(Articoli articolo)
    {
        articolo.setDescrizione(articolo.getDescrizione().toUpperCase());
        articoliRepository.save(articolo);
        this.EvictCache(articolo.getBarcode());//cancello barcode dalla cache barcode
    }

    //metodo ottimizzato per eliminare singolo barcode dalla cache dei barcode usando cacheManager
    private void EvictCache(Set<Barcode> Ean)
    {
        Ean.forEach((Barcode barcode) -> {
            log.info("Eliminazione cache barcode: "+ barcode.getBarcode());
            cacheManager.getCache("barcode").evict(barcode.getBarcode());
        });
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

    //Metodo Pulizia totale Cache

    @Override
    public void CleanCaches()
        {
            Collection<String> items = cacheManager.getCacheNames();
            items.forEach((item) -> {
                log.info(String.format("Eliminazione cache %s", item));
                cacheManager.getCache(item).clear();
            });
        }

}//class