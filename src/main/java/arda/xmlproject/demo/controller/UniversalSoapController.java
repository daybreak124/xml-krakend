package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.services.SoapInvoker;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/universal")
public class UniversalSoapController {

    private final SoapInvoker invoker;

    public UniversalSoapController(SoapInvoker invoker) {
        this.invoker = invoker;
    }

    // Gelen URL requesti 3 parçaya böler ve ona göre servis/işlem/parametre olarak ayırıyor. Sonra ise invoke methodunu çağırıyor
    @GetMapping(value = "/{services}/{operation}/{param}", produces = MediaType.APPLICATION_XML_VALUE)
    // @PreAuthorize("hasAuthority('admin')") // Bunun ile sadece adminler bu methoda erişebilir
    public ResponseEntity<?> call(@PathVariable String services, @PathVariable String operation, @PathVariable String param)
            throws Exception {

        Object result = invoker.invoke(services, operation, param);

        if (result instanceof String) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                    .header("Vary", "Accept")
                    .body(Map.of("result", result));
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .header("Vary", "Accept")
                .body(result);
    }



    @GetMapping(value = "/{services}/{operation}/{param1}/{param2}", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> callTwoParameters(@PathVariable String services, @PathVariable String operation, @PathVariable String param1, @PathVariable String param2)
            throws Exception {

        Object result = invoker.invokeTwoParameters(services, operation, param1, param2);

        if (result instanceof String) {
            return ResponseEntity.ok().cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                    .header("Vary", "Accept")
                    .body(Map.of("result", result));
        }

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(5, TimeUnit.MINUTES))
                .header("Vary", "Accept")
                .body(result);
    }
}