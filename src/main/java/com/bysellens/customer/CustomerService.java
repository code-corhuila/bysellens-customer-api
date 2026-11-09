package com.bysellens.customer;
import java.util.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import com.bysellens.common.DomainException;
@Service public class CustomerService {
 private final ClienteRepository repo;public CustomerService(ClienteRepository repo){this.repo=repo;}
 public List<Cliente> list(){return repo.findByActivoTrueOrderByIdAsc();}
 public Cliente get(long id){return repo.findById(id).orElseThrow(DomainException::missing);}
 @Transactional public Cliente save(Long id,CustomerController.Input r){var c=id==null?new Cliente():get(id);c.nombre=r.nombre().trim();c.telefono=r.telefono();c.email=r.email();c.direccion=r.direccion();c.activo=r.activo();return repo.save(c);}
 @Transactional public void delete(long id){var c=get(id);c.activo=false;repo.save(c);}
}