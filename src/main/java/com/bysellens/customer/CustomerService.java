package com.bysellens.customer;
import java.util.*;import org.springframework.stereotype.Service;import org.springframework.data.mongodb.core.MongoTemplate;import org.springframework.data.mongodb.core.FindAndModifyOptions;import org.springframework.data.mongodb.core.query.Query;import org.springframework.data.mongodb.core.query.Criteria;import org.springframework.data.mongodb.core.query.Update;import org.bson.Document;import com.bysellens.common.DomainException;
@Service public class CustomerService {
 private final ClienteRepository repo;private final MongoTemplate mongo;public CustomerService(ClienteRepository repo,MongoTemplate mongo){this.repo=repo;this.mongo=mongo;}
 public List<Cliente> list(){return repo.findByActivoTrueOrderByIdAsc();}
 public Cliente get(long id){return repo.findById(id).orElseThrow(DomainException::missing);}
 public Cliente save(Long id,CustomerController.Input r){var c=id==null?new Cliente():get(id);if(id==null){var counter=mongo.findAndModify(Query.query(Criteria.where("_id").is("clientes")),new Update().inc("seq",1L),FindAndModifyOptions.options().upsert(true).returnNew(true),Document.class,"counters");c.id=((Number)counter.get("seq")).longValue();}c.nombre=r.nombre().trim();c.telefono=r.telefono();c.email=r.email();c.direccion=r.direccion();c.activo=r.activo();return repo.save(c);}
 public void delete(long id){var c=get(id);c.activo=false;repo.save(c);}
}
