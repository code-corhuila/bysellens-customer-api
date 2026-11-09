package com.bysellens.customer;import org.springframework.data.annotation.Id;import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection="clientes") public class Cliente {
 @Id public Long id;
 public String nombre,telefono,email,direccion;public boolean activo=true;
}
