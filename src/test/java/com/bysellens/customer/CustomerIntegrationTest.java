package com.bysellens.customer;
import org.junit.jupiter.api.*;import static org.junit.jupiter.api.Assertions.*;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.test.context.DynamicPropertySource;import org.springframework.test.context.DynamicPropertyRegistry;import org.testcontainers.containers.MongoDBContainer;import org.testcontainers.junit.jupiter.*;
@SpringBootTest(properties={"JWT_SECRET=test-secret-at-least-thirty-two-bytes-long","CUSTOMER_MIGRATION_CHECK=false"}) @Testcontainers(disabledWithoutDocker=true)
class CustomerIntegrationTest {
 @Container static MongoDBContainer db=new MongoDBContainer("mongo:7");
 @DynamicPropertySource static void db(DynamicPropertyRegistry r){r.add("spring.data.mongodb.uri",db::getReplicaSetUrl);r.add("spring.data.mongodb.username",()->"");r.add("spring.data.mongodb.password",()->"");}
 @Autowired CustomerService service;
 @Test void createUpdateAndLogicalDeletePreserveNumericIdentity(){var c=service.save(null,new CustomerController.Input("Cliente prueba","3000000000","test@example.com","Direccion ficticia",true));assertNotNull(c.id);assertEquals(c.id,service.get(c.id).id);var updated=service.save(c.id,new CustomerController.Input("Actualizado","3000000000","test@example.com","Direccion ficticia",true));assertEquals("Actualizado",updated.nombre);service.delete(c.id);assertFalse(service.get(c.id).activo);assertTrue(service.list().isEmpty());}
}
