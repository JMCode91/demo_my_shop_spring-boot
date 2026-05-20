package com.example.demo.service;

import java.util.Map;

/**
 * Contrato (Interfaz) para el motor de generación de documentos PDF.
 * Abstrae la tecnología subyacente que transforma las plantillas web en documentos imprimibles.
 */
public interface PdfService {
    
    /**
     * Recibe el nombre de una plantilla Thymeleaf y un mapa de datos dinámicos,
     * fusiona ambos, y genera un archivo PDF en memoria listo para ser descargado.
     * * @param templateName Nombre del archivo HTML en 'src/main/resources/templates'.
     * @param data Mapa clave-valor con la información dinámica a inyectar (Ej: "order", OrderObject).
     * @return El documento PDF codificado en un array de bytes.
     */
    byte[] generatePdfFromHtml(String templateName, Map<String, Object> data);
}