package org.example.controller;

import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/resources")
public class FileInternalController {

    @Autowired
    private FileService fileService;

    @GetMapping("/url/{*filePath}")
    public String getFileUrl(@PathVariable String filePath,
                             @RequestParam(defaultValue = "3600") int expiry) {
        return fileService.getFileUrl(filePath, expiry);
    }
}