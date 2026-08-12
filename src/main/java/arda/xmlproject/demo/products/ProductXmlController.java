package arda.xmlproject.demo.products;

import com.example.products.GetProductResponse;
import com.example.products.ProductInfo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

@RestController
@RequestMapping("/api/products")
public class ProductXmlController {

    private final ProductRepository productRepository;

    public ProductXmlController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<GetProductResponse> getProductXml(@PathVariable Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ürün bulunamadı: " + id));

        ProductInfo info = new ProductInfo();
        info.setId(entity.getId());
        info.setProductName(entity.getProductName());
        info.setCategory(entity.getCategory());
        info.setPrice(entity.getPrice());
        info.setStockQuantity(entity.getStockQuantity());
        info.setSku(entity.getSku());
        info.setBrand(entity.getBrand());
        info.setDescription(entity.getDescription());
        info.setIsActive(entity.getIsActive());

        info.setCreatedAt(convertToXMLGregorianCalendar(entity.getCreatedAt()));

        GetProductResponse response = new GetProductResponse();
        response.setProduct(info);

        return ResponseEntity.ok(response);
    }

    private XMLGregorianCalendar convertToXMLGregorianCalendar(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        try {
            GregorianCalendar gcal = GregorianCalendar.from(localDateTime.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        } catch (Exception e) {
            return null;
        }
    }
}