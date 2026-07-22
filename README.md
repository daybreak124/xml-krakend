Bot Detector cache size: son x sayıdakileri cachele
shared cache: her cihazın ortak cache'i olması için. sunucu taraflı caching herhalde
max rate: global max rate limit
client max rate: client için max rate limit

##
Universal SOAP controller ve SoapInvoker dinamik olarak, ayrı ayrı sınıf eklenmeden 
API erişimi sağlar
##

## DOSYA İNDİRME
WSDL dosyası indirme örneği: curl -o src/main/resources/wsdl/TempConvert.wsdl "https://www.w3schools.com/xml/tempconvert.asmx?WSDL"
(veya direkt WSDL dosyasını indirip sürüklemek)
sonra ise: ./gradlew wsdl2java ile sınıflar oluşur
sonra application.properties içine gerekli bilgiler yazılır

soap.services.{}.wsdl=<URL>
soap.services.{}.serviceClass=<wsdl'den üretildikten sonraki PAKET ismi>
soap.services.{}.portMethod=<port döndüren, constructor ismi>
##


## KULLANIM ##
Postman - POST request
http://localhost:8080/auth/login
Body:
{
"username": "admin",
"password": "1234"
}
veya admin yerine user
Gelen token Auth kısmında Baerer Token kısmına eklenmeli.
Aşağıdaki URL'lere GET request atılmalı
-------------------------
GLOBAL Endpoint:
Girilen URL'ye göre dinamik olarak istek atılır ve cevap gelir
Cachelenene kadar biraz daha yavaş.
http://localhost:8081/global/number/numberToWords/15
http://localhost:8081/global/country/fullCountryInfo/TR
http://localhost:8081/global/number/numberToDollars/14.50
http://localhost:8081/global/isbn/IsValidISBN10/0470059029
http://localhost:8081/global/temperature/CelsiusToFahrenheit/25
AGGREGATE
http://localhost:8081/global/aggregate/number/numberToWords/15/country/fullCountryInfo/TR
-------------------------
SINGLE Endpoint:
Belirlenen cevaplar belirlenen URL'lerde
http://localhost:8081/single/country/TR
http://localhost:8081/single/number/15
AGGREGATE
http://localhost:8081/single/aggregate/TR/15
-------------------------