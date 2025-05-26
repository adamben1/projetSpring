package com.example.projetSpring.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${app.upload.dir:${user.home}/uploads}")
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Impossible de stocker un fichier vide");
            }

            // Créer le répertoire d'upload s'il n'existe pas
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Générer un nom de fichier unique pour éviter les écrasements
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // Chemin complet du fichier
            Path destinationFile = uploadPath.resolve(
                            Paths.get(newFilename))
                    .normalize().toAbsolutePath();

            // Vérifier que le chemin est bien dans le répertoire d'upload (sécurité)
            if (!destinationFile.getParent().equals(uploadPath.toAbsolutePath())) {
                throw new RuntimeException("Impossible de stocker le fichier en dehors du répertoire courant");
            }

            // Copier le fichier vers la destination
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Retourner le chemin relatif pour stockage en BDD
            return "/uploads/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Échec du stockage du fichier", e);
        }
    }

    public void delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // Extraire le nom du fichier de l'URL
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadDir).resolve(filename);

            // Supprimer le fichier s'il existe
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Échec de la suppression du fichier", e);
        }
    }
}