package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.service.CountryInfo;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.oorsprong.websamples.TCountryInfo;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/countries")
@XmlRootElement
public class CountryController {

    private final CountryInfo countryInfo;

    public CountryController(CountryInfo countryInfo) {
        this.countryInfo = countryInfo;
    }


    @GetMapping("/{code}/capital")
    public String capital(@PathVariable String code) {
        return countryInfo.getCapital(code);
    }


//    Cache control headerı yok
//    @GetMapping("/{code}/full")
//    public TCountryInfo full(@PathVariable String code) {
//        return countryInfo.getFullInfo(code);
//    }

    /**
     JSON DÖNDÜRMEK İÇİN PRODUCES PARAMETRELİNİ SİLİP
     HEADERDAKİ VARY/ACCEPT SİLİNMELİ
     SONRA KRAKEND.JSON DOSYASINDAKİ XML PARÇALARI SİLİNMELİ
     **/

    // CACHE CONTROL HEADERLARI OLMADAN CACHELENMİYOR

    @GetMapping(value = "/{code}/full", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<TCountryInfo> full(@PathVariable String code) {
        TCountryInfo result = countryInfo.getFullInfo(code);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .header("Vary", "Accept")
                .body(result);
    }




    // JSON DÖNDÜRÜYOR
    @GetMapping(value = "/json/{code}/full")
    public ResponseEntity<TCountryInfo> fullJson(@PathVariable String code) {
        TCountryInfo result = countryInfo.getFullInfo(code);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .header("Vary", "Accept")
                .body(result);
    }

}