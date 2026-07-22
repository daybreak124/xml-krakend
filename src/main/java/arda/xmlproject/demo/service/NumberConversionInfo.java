package arda.xmlproject.demo.service;

import com.dataaccess.webservicesserver.NumberConversion;
import com.dataaccess.webservicesserver.NumberConversionSoapType;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.net.URL;

@Service
public class NumberConversionInfo {

    private final NumberConversionSoapType port;

    public NumberConversionInfo() {
        try {
            URL wsdl = new URL(
                    "https://www.dataaccess.com/webservicesserver/numberconversion.wso?WSDL"
            );

            NumberConversion service = new NumberConversion(wsdl);
            this.port = service.getNumberConversionSoap();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String numberToWords(int number) {
        return port.numberToWords(BigInteger.valueOf(number));
    }
}