package com.example.demo.repository;

import com.example.demo.security.Role; // <--- OJO: Importamos desde security
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
}