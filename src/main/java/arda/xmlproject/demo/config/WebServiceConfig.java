package arda.xmlproject.demo.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true); // soap:address'i request URL'e göre otomatik ayarlar
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    // Bean adı = "UsersService" -> WSDL şu adreste yayınlanır: /ws/UsersService.wsdl
    @Bean(name = "UsersService")
    public SimpleWsdl11Definition usersWsdl() {
        SimpleWsdl11Definition definition = new SimpleWsdl11Definition();
        definition.setWsdl(new ClassPathResource("wsdl/users.wsdl"));
        return definition;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // wsdl2java'nın ürettiği JAXB sınıflarının paketi
        marshaller.setContextPath("com.example.users");
        return marshaller;
    }
}