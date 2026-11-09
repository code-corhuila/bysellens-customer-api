package com.bysellens.customer;import jakarta.persistence.*;
@Entity @Table(name="clientes") public class Cliente {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String nombre,telefono,email,direccion;public boolean activo=true;
}