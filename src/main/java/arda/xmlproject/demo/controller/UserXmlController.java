package arda.xmlproject.demo.controller;

import arda.xmlproject.demo.entities.UserEntity;
import arda.xmlproject.demo.repositories.UserRepository;
import com.example.users.UserInfo;
import com.example.users.GetUserResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserXmlController {

    private final UserRepository userRepository;

    public UserXmlController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/{id}/xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<GetUserResponse> getUserXml(@PathVariable Long id) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + id));

        UserInfo info = new UserInfo();
        info.setId(entity.getId());
        info.setSsn(entity.getSsn());
        info.setUsername(entity.getUsername());
        info.setName(entity.getName());
        info.setLastName(entity.getLastName());
        if (entity.getRole() != null) {
            info.setRole(entity.getRole().name());
        }

        if (entity.getPermissions() != null && !entity.getPermissions().isEmpty()) {
            info.getPermissions().addAll(
                    entity.getPermissions().stream().map(Enum::name).collect(Collectors.toList())
            );
        }

        info.setAccountCreatedAt(convertToXMLGregorianCalendar(entity.getAccountCreatedAt()));
        info.setLastAccessAt(convertToXMLGregorianCalendar(entity.getLastAccessAt()));

        GetUserResponse response = new GetUserResponse();
        response.setUser(info);

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