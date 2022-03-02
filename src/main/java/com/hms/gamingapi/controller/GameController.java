package com.hms.gamingapi.controller;

import com.hms.gamingapi.model.FileUploadResponse;
import com.hms.gamingapi.model.Game;
import com.hms.gamingapi.model.PageApiResponse;
import com.hms.gamingapi.model.SearchRequest;
import com.hms.gamingapi.service.GameService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value = "game")
    public ResponseEntity<Mono<List<Game>>> getGames() {
        return ResponseEntity.ok(gameService.getAllGames());
    }

    @GetMapping(value = "game/{id}")
    public ResponseEntity<Mono<Game>> getGame(@PathVariable("id") String id) {
        return ResponseEntity.ok(gameService.getGameById(id));
    }

    @PostMapping(value = "game")
    public ResponseEntity<Mono<Game>> saveGame(@RequestBody Game newGame) {
        return ResponseEntity.ok(gameService.addGame(newGame));
    }

    @PutMapping(value = "game/publish")
    public ResponseEntity<Mono<Game>> publishGame(@RequestBody Game game) {
        return ResponseEntity.ok(gameService.publishGame(game));
    }

    @PutMapping(value = "game/reject")
    public ResponseEntity<Mono<Game>> rejectGame(@RequestBody Game game) {
        return ResponseEntity.ok(gameService.rejectGame(game));
    }

    @PostMapping(value = "search")
    public ResponseEntity<Mono<PageApiResponse>> searchGames(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "8") int pageSize,
            @RequestParam(defaultValue = "publishedDateTime") String sortBy,
            @RequestBody SearchRequest searchRequest
    ) {
        return ResponseEntity.ok(gameService.searchGames(pageNo, pageSize, sortBy, searchRequest));
    }

    @GetMapping(value = "admin/search")
    public ResponseEntity<Mono<PageApiResponse>> adminSearchGames(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "8") int pageSize,
            @RequestParam(defaultValue = "publishedDateTime") String sortBy
    ) {
        return ResponseEntity.ok(gameService.adminSearch(pageNo, pageSize, sortBy));
    }

    @PostMapping(value = "/game-file-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Mono<FileUploadResponse>> uploadFile(@RequestPart("gameFile") Mono<FilePart> gameFile) {
        return ResponseEntity.ok().body(gameService.uploadFile(gameFile));
    }

    @GetMapping(value = "/game-file-unzip/{fileName}")
    public ResponseEntity<Mono<FileUploadResponse>> unzipGameFile(@PathVariable("fileName") String fileName) {
        return ResponseEntity.ok(gameService.unzipFile(fileName));
    }

    @GetMapping(value = "/game-file/download")
    public Mono<ResponseEntity<ByteArrayResource>> buildFileDownload(@RequestParam("gameFile") String gameFile) {
        return gameService.gameFileDownload(gameFile);
    }

    @PostMapping(value = "/game-poster-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<Mono<FileUploadResponse>> uploadPoster(@RequestPart("imageFile") Mono<FilePart> imageFile) {
        return ResponseEntity.ok().body(gameService.uploadPoster(imageFile));
    }
}
