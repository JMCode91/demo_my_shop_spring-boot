package com.example.demo.service;

import java.util.Map;

public interface PdfService {
    byte[] generatePdfFromHtml(String templateName, Map<String, Object> data);
}