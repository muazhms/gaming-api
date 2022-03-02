package com.hms.gamingapi.service;

import com.hms.gamingapi.dao.GameRepository;
import com.hms.gamingapi.model.FileUploadResponse;
import com.hms.gamingapi.model.Game;
import com.hms.gamingapi.model.PageApiResponse;
import com.hms.gamingapi.model.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Value("${game.file.upload.base}")
    private String fileUploadPath;

    @Value("${game.poster.upload.base}")
    private String posterUploadPath;

    public Mono<List<Game>> getAllGames() {
        return gameRepository.findAll().collectList();
    }

    public Mono<Game> getGameById(String id) {
        return gameRepository.findById(id);
    }

    public Mono<Game> addGame(Game game) {
        game.setStatus("NEW");
        game.setCreatedDateTime(LocalDateTime.now());
        game.setLastUpdatedDateTime(LocalDateTime.now());
        return gameRepository.save(game);
    }

    public Mono<Game> publishGame(Game game) {
        game.setStatus("PUBLISHED");
        game.setLastUpdatedDateTime(LocalDateTime.now());
        return gameRepository.save(game);
    }

    public Mono<Game> rejectGame(Game game) {
        game.setStatus("REJECTED");
        game.setLastUpdatedDateTime(LocalDateTime.now());
        return gameRepository.save(game);
    }

    public Mono<PageApiResponse> searchGames(int pageNo, int pageSize, String sortBy, SearchRequest request) {
        return gameRepository.search(pageNo, pageSize, sortBy, request);
    }

    public Mono<PageApiResponse> adminSearch(int pageNo, int pageSize, String sortBy) {
        return gameRepository.adminSearch(pageNo, pageSize, sortBy);
    }

    public Mono<List<Game>> getDevGames(String devId) {
        return gameRepository.findByDeveloperId(devId).collectList();
    }

    public Mono<FileUploadResponse> uploadFile(Mono<FilePart> partFile) {
        return partFile.map(
                it -> {
                    String fileName = it.filename();
                    try {
                        it.transferTo(Paths.get(fileUploadPath + "/" + fileName))
                                .subscribe();
                        return new FileUploadResponse("S1000", fileName);
                    } catch (Exception e) {
                        return new FileUploadResponse("E1000", fileName);
                    }
                }
        );
    }

    public Mono<FileUploadResponse> unzipFile(String fileName) {
        String folderName = fileName.replace(".zip", "");
        Path source = Paths.get(fileUploadPath + "/" + fileName);
        Path target = Paths.get(fileUploadPath + "/" + folderName);

        try {
            unzipFolder(source, target);
            return Mono.just(new FileUploadResponse("S1000", folderName));
        } catch (IOException e) {
            e.printStackTrace();
            return Mono.just(new FileUploadResponse("E1000", folderName));
        }
    }

    public static void unzipFolder(Path source, Path target) throws IOException {

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();

            while (zipEntry != null) {

                boolean isDirectory = false;
                // example 1.1
                // some zip stored files and folders separately
                // e.g data/
                //     data/folder/
                //     data/folder/file.txt
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, target);

                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {

                    // example 1.2
                    // some zip stored file path only, need create parent directories
                    // e.g data/folder/file.txt
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }

                    // copy files, nio
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);

                    // copy files, classic
                    /*try (FileOutputStream fos = new FileOutputStream(newPath.toFile())) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }*/
                }

                zipEntry = zis.getNextEntry();

            }
            zis.closeEntry();

        }

    }

    // protect zip slip attack
    public static Path zipSlipProtect(ZipEntry zipEntry, Path targetDir)
            throws IOException {

        // test zip slip vulnerability
        // Path targetDirResolved = targetDir.resolve("../../" + zipEntry.getName());

        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }

        return normalizePath;
    }

    public Mono<ResponseEntity<ByteArrayResource>> gameFileDownload(String gameFile) {
        File file = new File(fileUploadPath + File.separator + gameFile + ".zip");
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + gameFile + ".zip\"");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        Path path = Paths.get(file.getAbsolutePath());
        try {
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return Mono.just(ResponseEntity
                    .ok()
                    .headers(header)
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource));
        } catch (IOException e) {
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
        }
    }

    public Mono<FileUploadResponse> uploadPoster(Mono<FilePart> posterFile) {
        return posterFile.map(
                it -> {
                    String fileName = it.filename();
                    try {
                        it.transferTo(Paths.get(posterUploadPath + "/" + fileName))
                                .subscribe();
                        return new FileUploadResponse("S1000", fileName);
                    } catch (Exception e) {
                        return new FileUploadResponse("E1000", fileName);
                    }
                }
        );
    }
}
