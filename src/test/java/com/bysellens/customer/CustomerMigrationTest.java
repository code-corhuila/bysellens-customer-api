package com.bysellens.customer;
import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;
import java.sql.DriverManager;import com.mongodb.client.MongoClients;import org.bson.Document;import org.junit.jupiter.api.Test;import org.springframework.boot.DefaultApplicationArguments;import org.springframework.context.ConfigurableApplicationContext;import org.springframework.data.mongodb.core.MongoTemplate;import org.testcontainers.containers.MongoDBContainer;import org.testcontainers.containers.PostgreSQLContainer;import org.testcontainers.junit.jupiter.Container;import org.testcontainers.junit.jupiter.Testcontainers;
@Testcontainers(disabledWithoutDocker=true)
class CustomerMigrationTest {
 @Container static MongoDBContainer mongo=new MongoDBContainer("mongo:7");
 @Container static PostgreSQLContainer<?> pg=new PostgreSQLContainer<>("postgres:17-alpine");
 @Test void copiesIdsAndDoesNotDuplicateOnRetry() throws Exception {
  try(var db=DriverManager.getConnection(pg.getJdbcUrl(),pg.getUsername(),pg.getPassword());var s=db.createStatement()) {s.execute("CREATE SCHEMA customer");s.execute("CREATE TABLE customer.clientes(id bigint PRIMARY KEY,nombre text,telefono text,email text,direccion text,activo boolean)");s.execute("INSERT INTO customer.clientes VALUES (42,'Ejemplo','123','test@example.com','direccion',true)");}
  try(var client=MongoClients.create(mongo.getReplicaSetUrl())){
   var template=new MongoTemplate(client,"customer_test");var migration=new CustomerMigration(template,mock(ConfigurableApplicationContext.class));migration.migrate=true;migration.url=pg.getJdbcUrl();migration.user=pg.getUsername();migration.password=pg.getPassword();
   migration.run(new DefaultApplicationArguments(new String[0]));
   assertEquals(1,template.getCollection("clientes").countDocuments());assertNotNull(template.getCollection("clientes").find(new Document("_id",42L)).first());assertEquals(42L,((Number)template.getCollection("counters").find(new Document("_id","clientes")).first().get("seq")).longValue());
   assertThrows(IllegalStateException.class,()->migration.run(new DefaultApplicationArguments(new String[0])));assertEquals(1,template.getCollection("clientes").countDocuments());
  }
 }
}
