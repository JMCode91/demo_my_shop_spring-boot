package com.example.demo.service.impl;

import com.example.demo.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 * Implementación del motor PDF utilizando 'Flying Saucer' (xhtmlrenderer).
 * Esta tecnología requiere que la plantilla fuente sea estrictamente XHTML válido 
 * (todas las etiquetas deben estar cerradas, ej: <br/>, <img/>).
 */
@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public byte[] generatePdfFromHtml(String templateName, Map<String, Object> data) {
        
        // Prepara el contexto para que Thymeleaf inyecte los datos del mapa
        Context context = new Context();
        context.setVariables(data);

        // 1. Pide a Thymeleaf que procese la plantilla y devuelva un String con el HTML final
        String htmlContent = templateEngine.process(templateName, context);

        // 2. Transforma el String HTML a un documento PDF binario
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
            
            return outputStream.toByteArray();
            
        } catch (Exception e) {
            // Se lanza una RuntimeException para detener el flujo en caso de error de renderizado
            throw new RuntimeException("Error fatal interno al generar el PDF de la factura", e);
        }
    }
}