package com.bysellens.customer;

import java.sql.DriverManager;
import java.util.Objects;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
class CustomerMigration implements ApplicationRunner {
 private final MongoTemplate mongo; private final ConfigurableApplicationContext context;
 @Value("${CUSTOMER_MIGRATE:false}") boolean migrate;
 @Value("${CUSTOMER_MIGRATION_CHECK:true}") boolean check;
 @Value("${MIGRATION_DB_URL:}") String url;
 @Value("${MIGRATION_DB_USER:}") String user;
 @Value("${MIGRATION_DB_PASSWORD:}") String password;
 CustomerMigration(MongoTemplate mongo,ConfigurableApplicationContext context){this.mongo=mongo;this.context=context;}
 public void run(ApplicationArguments args) throws Exception {
  var markers=mongo.getCollection("migration_status");
  var marker=new Document("_id","postgres_customer_v1");
  if(!migrate){if(check && markers.find(marker).first()==null)throw new IllegalStateException("Migre los clientes antes de iniciar Customer");return;}
  if(markers.find(marker).first()!=null)throw new IllegalStateException("La migracion ya fue completada");
  long max=0,count=0;
  try(var conn=DriverManager.getConnection(url,user,password)){
   conn.setReadOnly(true);conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_REPEATABLE_READ);conn.setAutoCommit(false);
   try(var statement=conn.createStatement();var rows=statement.executeQuery("SELECT id,nombre,telefono,email,direccion,activo FROM customer.clientes ORDER BY id")){
    var target=mongo.getCollection("clientes");
    while(rows.next()){
     long id=rows.getLong("id");max=Math.max(max,id);count++;
     var doc=new Document("_id",id).append("nombre",rows.getString("nombre")).append("telefono",rows.getString("telefono")).append("email",rows.getString("email")).append("direccion",rows.getString("direccion")).append("activo",rows.getBoolean("activo"));
     var existing=target.find(new Document("_id",id)).first();
     if(existing==null)target.insertOne(doc);
     else for(String key:new String[]{"nombre","telefono","email","direccion","activo"})if(!Objects.equals(doc.get(key),existing.get(key)))throw new IllegalStateException("Conflicto de cliente en MongoDB; migracion detenida");
    }
   }
   conn.commit();
  }
  if(mongo.getCollection("clientes").countDocuments()!=count)throw new IllegalStateException("Los clientes de MongoDB no coinciden con PostgreSQL");
  mongo.getCollection("counters").updateOne(new Document("_id","clientes"),new Document("$max",new Document("seq",max)),new com.mongodb.client.model.UpdateOptions().upsert(true));
  markers.insertOne(marker.append("count",count));
  System.out.println("Migracion de clientes completada: "+count);
  context.close();
 }
}
