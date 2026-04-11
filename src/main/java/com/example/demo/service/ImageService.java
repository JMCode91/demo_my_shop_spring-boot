package com.example.demo.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface ImageService {

    // Contrato: Cualquier servicio de imágenes debe saber subir un archivo y devolver una URL
    String uploadImage(MultipartFile file) throws IOException;

}