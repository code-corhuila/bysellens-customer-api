package com.bysellens.customer;
import org.junit.jupiter.api.*;import static org.junit.jupiter.api.Assertions.*;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.test.context.DynamicPropertySource;import org.springframework.test.context.DynamicPropertyRegistry;import org.testcontainers.containers.PostgreSQLContainer;import org.testcontainers.junit.jupiter.*;
@SpringBootTest(properties="JWT_SECRET=test-secret-at-least-thirty-two-bytes-long") @Testcontainers(disabledWithoutDocker=true)
class CustomerIntegrationTest {
 @Container static PostgreSQLContainer<?> db=new PostgreSQLContainer<>("postgres:17-alpine");
 @DynamicPropertySource static void db(DynamicPropertyRegistry r){r.add("spring.datasource.url",()->db.getJdbcUrl()+"&currentSchema=customer");r.add("spring.datasource.username",db::getUsername);r.add("spring.datasource.password",db::getPassword);}
 @Autowired CustomerService service;
 @Test void logicalDeletePreservesHistoricalIdentity(){var c=service.save(null,new CustomerController.Input("Cliente prueba","3000000000","test@example.com","Direccion ficticia",true));service.delete(c.id);assertFalse(service.get(c.id).activo);assertTrue(service.list().isEmpty());}
}