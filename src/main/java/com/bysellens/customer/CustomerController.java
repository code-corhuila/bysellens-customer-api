package com.bysellens.customer;
import java.util.*;import jakarta.validation.Valid;import jakarta.validation.constraints.*;import org.springframework.web.bind.annotation.*;import org.springframework.http.HttpStatus;
@RestController public class CustomerController {
 public record Input(@NotBlank @Size(max=120) String nombre,@NotBlank @Size(max=30) String telefono,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(max=255) String direccion,@NotNull Boolean activo){}
 private final CustomerService service;public CustomerController(CustomerService s){service=s;}
 @GetMapping("/api/clientes") public List<Cliente> list(){return service.list();}
 @GetMapping({"/api/clientes/{id}","/internal/clientes/{id}"}) public Cliente get(@PathVariable long id){return service.get(id);}
 @PostMapping("/api/clientes") @ResponseStatus(HttpStatus.CREATED) public Cliente create(@Valid @RequestBody Input r){return service.save(null,r);}
 @PutMapping("/api/clientes/{id}") public Cliente update(@PathVariable long id,@Valid @RequestBody Input r){return service.save(id,r);}
 @DeleteMapping("/api/clientes/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable long id){service.delete(id);}
}