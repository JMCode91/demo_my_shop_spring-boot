package com.example.demo.repository;

import com.example.demo.domain.Invoice;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para la entidad Invoice (Factura).
 * Proporciona las operaciones CRUD básicas de forma automática mediante Spring Data JPA.
 */
@Repository
public interface InvoiceRepository extends CrudRepository<Invoice, Long> {
}