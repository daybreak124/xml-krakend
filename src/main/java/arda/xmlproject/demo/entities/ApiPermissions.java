package arda.xmlproject.demo.entities;

public enum ApiPermissions {
    COUNTRY, NUMBER, TEMPERATURE, ISBN, CALCULATOR, USER, PRODUCTS,
    USER_ADM,


    PRODUCTS_ADM, PRODUCTS_DEL, PRODUCTS_PATCH,
    PRODUCTS_POST, PRODUCTS_GET

    // Bazı yetkiler obsolete, krakend json dosyası ve spring security anotasyonları kontrol edilerek silinmeli
}
