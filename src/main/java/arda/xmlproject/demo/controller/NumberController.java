package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.services.NumberConversionInfo;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/numbers")
@XmlRootElement
public class NumberController {

    private final NumberConversionInfo numberConversionInfo;

    public NumberController(NumberConversionInfo numberConversionInfo) {
        this.numberConversionInfo = numberConversionInfo;
    }

    @PreAuthorize("hasAuthority('number')")
    @GetMapping(value = "/{code}", produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<NumberToWordsResponse> full(@PathVariable int code) {

        String result = numberConversionInfo.numberToWords(code);

        NumberToWordsResponse numberToWordsResponse = new NumberToWordsResponse();

        numberToWordsResponse.setNumberToWordsResult(result);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .header("Vary", "Accept")
                .body(numberToWordsResponse);
    }


    // JSON DÖNDÜR
    @PreAuthorize("hasAuthority('number')")
    @GetMapping(value = "/json/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NumberToWordsResponse> fullJson(@PathVariable int code) {

        String result = numberConversionInfo.numberToWords(code);
        NumberToWordsResponse numberToWordsResponse = new NumberToWordsResponse();


        numberToWordsResponse.setNumberToWordsResult(result);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .header("Vary", "Accept")
                .body(numberToWordsResponse);
    }


//    @GetMapping("/{code}")
//    public String full(@PathVariable int code) {
//        return numberConversionInfo.numberToWords(code);
//    }
}