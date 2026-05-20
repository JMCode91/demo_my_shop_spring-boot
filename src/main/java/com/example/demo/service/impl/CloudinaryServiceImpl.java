package com.example.demo.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Implementación de ImageService utilizando la plataforma de nube "Cloudinary".
 * Integra el SDK oficial de Cloudinary para procesar y alojar las imágenes de los productos.
 */
@Service
public class CloudinaryServiceImpl implements ImageService {

    @Autowired
    private Cloudinary cloudinary;

    @Override
    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file) throws IOException {
        // Ejecuta la subida sincrónica del archivo a la nube
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        // Extrae y retorna la URL segura proporcionada por la plataforma
        return uploadResult.get("url").toString();
    }
}