package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * Contrato (Interfaz) para el servicio de almacenamiento de imágenes.
 * Abstrae a los controladores de la plataforma tecnológica subyacente que aloja los archivos.
 */
public interface ImageService {

    /**
     * Sube un archivo multimedia a un servidor o nube y devuelve la URL pública para su acceso.
     * @param file Archivo multipart recibido desde un formulario web.
     * @return URL absoluta y pública del archivo subido.
     * @throws IOException Si ocurre un error en la transmisión de datos.
     */
    String uploadImage(MultipartFile file) throws IOException;
}