package com.xantrix.webapp;

import com.xantrix.webapp.dtos.ArticoliDto;
import com.xantrix.webapp.dtos.BarcodeDto;
import com.xantrix.webapp.entities.Articoli;
import com.xantrix.webapp.entities.Barcode;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig
{
    @Bean
    public ModelMapper modelMapper()
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true); //se nel modello ci sono valori a null vengono ignorati
        modelMapper.addMappings(articoliMapping); //mappa gli articoli con articoliDto con metodo esterno

        modelMapper.addMappings(new PropertyMap<Barcode, BarcodeDto>() //mappa i Barcode con BarcodeDto con metodo interno
        {
            @Override
            protected void configure() {
                map().setIdTipoArt(source.getIdTipoArt());
            }
        });

        modelMapper.addConverter(articoliConverter);//va a verificare sela sorgente è null vengono convertiti in stringa vuota
        return modelMapper;
    }

    PropertyMap<Articoli, ArticoliDto> articoliMapping = new PropertyMap<Articoli, ArticoliDto>()
    {
        protected void configure()
        {
            map().setDataCreazione(source.getDataCreaz());//mappa DataCreazione (dto) con il valore dataCreaz (entita)
        }
    };

    Converter<String, String> articoliConverter = new Converter<String, String>() {
        @Override
        public String convert(MappingContext<String, String> mappingContext) {
            return mappingContext.getSource() == null ? "" : mappingContext.getSource().trim(); //traduce i null in stringa vuota
        }
    };
}
