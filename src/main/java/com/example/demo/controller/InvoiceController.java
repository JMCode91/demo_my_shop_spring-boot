package com.example.demo.controller;

import com.example.demo.domain.Order;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador responsable de la generación y descarga de Facturas.
 * Conecta los datos de un pedido con el motor de generación de PDFs.
 */
@Controller
public class InvoiceController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Genera una factura en formato PDF para un pedido específico y fuerza 
     * su descarga en el navegador del cliente.
     * @param id Identificador único del pedido.
     * @return Entidad de respuesta HTTP configurada con el archivo PDF en bytes.
     */
    @GetMapping("/invoice/download/{id}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable("id") Long id) {
        // 1. Buscamos el pedido en la base de datos
        Order order = orderRepository.findById(id).orElse(null);
        
        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Preparamos los datos para la plantilla
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);

        // 3. Generamos el PDF
        byte[] pdfBytes = pdfService.generatePdfFromHtml("invoice", data);

        // 4. Configuramos la respuesta del navegador para que sea una descarga
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=factura_" + order.getId() + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}