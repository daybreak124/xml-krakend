Bot Detector cache size: son x sayıdakileri cachele
shared cache: her cihazın ortak cache'i olması için. sunucu taraflı caching herhalde
max rate: global max rate limit
client max rate: client için max rate limit
propagate_claims: tokendeki claimleri backende header olarak iletmek için
input_headers: gelen header'ı kullanmak/erişmek için izin

Application.properties'de aşağıda özel olarak belirtilmiş linkler dinamik API çağrısı için


## KURULUM
Öncelikle konsolda ./gradlew clean build yapılmalı
"docker compose up" ile docker başlar. sonuna --build eklenince containerlar oluşturulur


Vault ilk kez çalıştırılınca initializelanması gerekir.
(terminalde ise. eğer dockerden yapılıyorsa atlanabilir) docker exec -it vault sh
export VAULT_ADDR=http://127.0.0.1:8200
vault operator init


Bu komut 5 unseal key ve 1 root token üretir. Bunlar not alınmalı.


Vault her container restartında kilitlenir ve geri açılması gerekir. Üretilen 5 key'den herhangi 3 tanesiyle unseal yapılır:
(terminalde ise. eğer dockerden yapılıyorsa atlanabilir) docker exec -it vault sh
export VAULT_ADDR=http://127.0.0.1:8200
vault operator unseal   # 1. key
vault operator unseal   # 2. key
vault operator unseal   # 3. key


JWT secret key Vault'a kaydedilir:
vault kv put -mount=secret demo \
security.jwt.secret-key="SECRET_KEY_BURAYA"


Kontrol etmek için: vault kv get -mount=secret demo


vault operator init çıktısındaki Initial Root Token, Spring Boot tarafında Vault'a erişim için kullanılıyor.
application.properties dosyasına eklenmesi lazım:
Proje kök dizininde .env dosyası oluşturulmalı ve içine:
VAULT_TOKEN=VAULT_TOKEN_BURAYA(hvs.**********...)
^koyulmalı


^Bunları yaptıktan sonra uygulama çalışır
KrakenD Gateway: http://localhost:8081
Grafana: http://localhost:3000 (default olarak username / password = admin / admin)
Prometheus: http://localhost:9090
Vault UI: http://localhost:8200/ui


## DATABASE
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

application.properties dosyasındaki bu ayarları kendi db'nize uyarlayın
MySQL dışında başka bir db kullanılacaksa son tarafın değiştirilmesi gerekir.
MySQL kullanılacaksa sadece ilk 3ü değiştirilir

docker exec -it vault sh
export VAULT_ADDR=http://127.0.0.1:8200
vault operator unseal
1 2 ve 3. key

## BACKEND İLE İLGİLİ BAZI BİLGİLER
Universal SOAP controller ve SoapInvoker dinamik olarak, ayrı ayrı endpoint ayarlamadan  API erişimi sağlar
##

## DOSYA İNDİRME
WSDL dosyası indirme örneği: curl -o src/main/resources/wsdl/TempConvert.wsdl "https://www.w3schools.com/xml/tempconvert.asmx?WSDL"
(veya direkt WSDL dosyasını indirip sürüklemek)
sonra ise: ./gradlew wsdl2java ile sınıflar oluşur
sonra application.properties içine gerekli bilgiler yazılır ve/veya backendde endpoint belirlenir

soap.services.{}.wsdl=<URL>
soap.services.{}.serviceClass=<wsdl'den üretildikten sonraki PAKET ismi>
soap.services.{}.portMethod=<port döndüren, constructor ismi>
##


## KULLANICI OLUŞTURMA (VE ADMİN YAPMA)
Kayıt olduktan sonra mysql'de (veya hangi db kullanılıyorsa orada):
UPDATE table
SET role = "ADMIN" WHERE id = kullanıcının_idsi
ile kullanıcı admin yapılmalı.
-------------------------
GLOBAL Endpoint:
Girilen URL'ye göre dinamik olarak istek atılır ve cevap gelir
Cachelenene kadar biraz daha yavaş.
Response sınıfı yerine String map'i döndürebilir
http://localhost:8081/global/number/numberToWords/15
http://localhost:8081/global/country/fullCountryInfo/TR
http://localhost:8081/global/number/numberToDollars/14.50
http://localhost:8081/global/isbn/IsValidISBN10/048665088X
http://localhost:8081/global/temperature/CelsiusToFahrenheit/25

Çift parametre:
http://localhost:8081/globaldouble/calculator/multiply/2/3
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

## UPDATELEME
Backenddeki her değişiklikte maalesef docker compose down ve docker compose up --build gerekir
Sadece spring buildlanırsa da olur:
(docker compose build spring-boot veya docker compose up --build spring-boot)