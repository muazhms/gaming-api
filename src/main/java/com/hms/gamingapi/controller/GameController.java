package com.hms.gamingapi.controller;

import com.hms.gamingapi.model.Game;
import com.hms.gamingapi.service.GameService;
import org.springframework.http.ResponseEntity;
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
}
