package com.hms.gamingapi.controller;

import com.hms.gamingapi.model.Game;
import com.hms.gamingapi.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
