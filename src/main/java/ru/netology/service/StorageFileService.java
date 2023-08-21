package ru.netology.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.dto.response.FileRS;
import ru.netology.exception.InputDataException;
import ru.netology.exception.UnauthorizedException;
import ru.netology.model.StorageFile;
import ru.netology.model.User;
import ru.netology.repository.AuthenticationRepository;
import ru.netology.repository.StorageFileRepository;
import ru.netology.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StorageFileService {

    private StorageFileRepository storageFileRepository;
    private AuthenticationRepository authenticationRepository;
    private UserRepository userRepository;

    public boolean uploadFile(String authToken, String filename, byte[] fileContent) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Upload file: Unauthorized access for token '{}'", authToken);
            throw new UnauthorizedException("Unauthorized access: Upload file");
        }

        try {
            StorageFile storageFile = new StorageFile(filename, LocalDateTime.now(), (long) fileContent.length, fileContent, user);
            storageFileRepository.save(storageFile);
            log.info("File uploaded successfully. User: {}", user.getUsername());
            return true;
        } catch (Exception e) {
            log.error("Upload file failed due to an exception: {}", e.getMessage());
            throw new InputDataException("Upload file failed: An exception occurred");
        }
    }

    @Transactional
    public void deleteFile(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Delete file: Unauthorized access for token '{}'", authToken);
            throw new UnauthorizedException("Unauthorized access: Delete file");
        }

        storageFileRepository.deleteByUserAndFilename(user, filename);

        if (storageFileRepository.existsByUserAndFilename(user, filename)) {
            log.error("Failed to delete file '{}'", filename);
            throw new InputDataException("Failed to delete file");
        }
        log.info("File deleted successfully. User: {}", user.getUsername());
    }

    public byte[] downloadFile(String authToken, String filename) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Download file: Unauthorized access for token '{}'", authToken);
            throw new UnauthorizedException("Unauthorized access: Download file");
        }

        final StorageFile file = storageFileRepository.findByUserAndFilename(user, filename);
        if (file == null) {
            log.error("File '{}' not found for download", filename);
            throw new InputDataException("File not found for download");
        }
        log.info("File downloaded successfully. User: {}", user.getUsername());
        return file.getFileContent();
    }

    @Transactional
    public void editFileName(String authToken, String filename, String newFileName) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Edit file name: Unauthorized access for token '{}'", authToken);
            throw new UnauthorizedException("Unauthorized access: Edit file name");
        }
        if (newFileName != null) {
            storageFileRepository.editFileNameByUser(user, filename, newFileName);
            log.info("File name edited successfully. User: {}", user.getUsername());
        } else {
            log.error("Invalid input data for editing file name");
            throw new InputDataException("Invalid input data: Edit file name");
        }
    }

    public List<FileRS> getAllFiles(String authToken, Integer limit) {
        final User user = getUserByAuthToken(authToken);
        if (user == null) {
            log.error("Get all files: Unauthorized access for token '{}'", authToken);
            throw new UnauthorizedException("Unauthorized access: Get all files");
        }
        log.info("Retrieved all files successfully. User: {}", user.getUsername());
        return storageFileRepository.findAllByUser(user).stream()
                .map(file -> new FileRS(file.getFilename(), file.getSize()))
                .collect(Collectors.toList());
    }

    private User getUserByAuthToken(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            final String authTokenWithoutBearer = authToken.split(" ")[1];
            final String username = authenticationRepository.getUsernameByToken(authTokenWithoutBearer);
            return userRepository.findByUsername(username);
        }
        return null;
    }
}