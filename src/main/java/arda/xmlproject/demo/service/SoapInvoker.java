package arda.xmlproject.demo.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Service
public class SoapInvoker {

    private final Map<String, Object> portCache = new HashMap<>();

    private final Environment env;

    public SoapInvoker(Environment env) {
        this.env = env;
    }


    public Object invoke(String serviceName, String operation, String param) throws Exception {
        Object port = getPort(serviceName);
        Method method = findMethod(port, operation);
        Object convertedParam = convertParam(method.getParameterTypes()[0], param);

        return method.invoke(port, convertedParam);
    }

    // Number converter BigInt veya integer istiyor
    private Object convertParam(Class<?> targetType, String param) {
        if (targetType == BigInteger.class) {
            return new BigInteger(param);
        } else if (targetType == BigDecimal.class) {
            return new BigDecimal(param);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(param);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(param);
        } else {
            return param;
        }
    }



    private Object getPort(String serviceName) throws Exception {
        if (portCache.containsKey(serviceName)) {
            return portCache.get(serviceName);
        }

        // application.properties dosyasındaki değerler
        String wsdlUrl = env.getProperty("soap.services." + serviceName + "." + "wsdl");
        String serviceClassName = env.getProperty("soap.services." + serviceName + "." + "serviceClass");
        String portMethodName = env.getProperty("soap.services." + serviceName + "." + "portMethod");

        if (wsdlUrl == null || serviceClassName == null || portMethodName == null) {
            throw new Exception("Sınıf veya port bulunamadı");
        }


        // Class.forName string ile metotu bulur
        Class<?> serviceClass = Class.forName(serviceClassName);

        // API servisinin yeni instance'ını oluşturur. NumberConversion service = new...
        Object serviceInstance = serviceClass.getConstructor(URL.class).newInstance(new URL(wsdlUrl));

        // getNumberConversionSoap veya getCountryInfoServiceSoap gibi port döndüren metot bul
        // NumberConversionSoapType gibi
        Method portMethod = serviceClass.getMethod(portMethodName);

        // Yukarıda gelen metotun portunu invokelar
        // NumberConversionSoapType port = serviceInstance.getNumberConversionSoap()
        Object port = portMethod.invoke(serviceInstance);

        portCache.put(serviceName, port);
        return port;
    }

    private Method findMethod(Object port, String operation) throws NoSuchMethodException {
        for (Method m : port.getClass().getMethods()) {
            if (m.getName().equalsIgnoreCase(operation) && m.getParameterCount() == 1) {
                return m;
            }
        }
        throw new NoSuchMethodException("Metot bulunamadı");
    }
}