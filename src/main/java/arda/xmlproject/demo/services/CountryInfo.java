package arda.xmlproject.demo.services;

import org.oorsprong.websamples.TCountryInfo;
import org.oorsprong.websamples_countryinfo.CountryInfoService;
import org.oorsprong.websamples_countryinfo.CountryInfoServiceSoapType;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class CountryInfo {

    private final CountryInfoServiceSoapType port;

    public CountryInfo() {
        try {
            URL wsdl = new URL(
                    "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL"
            );

            CountryInfoService service = new CountryInfoService(wsdl);
            this.port = service.getCountryInfoServiceSoap();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String getCapital(String countryCode) {
        return port.capitalCity(countryCode);
    }

    public TCountryInfo getFullInfo(String countryCode) {
        return port.fullCountryInfo(countryCode);
    }

    public CountryInfoServiceSoapType getPort() {
        return port;
    }
}