package com.hms.gamingapi.service;

import com.hms.gamingapi.dao.GameRepository;
import com.hms.gamingapi.model.Game;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public Mono<List<Game>> getAllGames() {
        return gameRepository.findAll().collectList();
    }

    public Mono<Game> getGameById(String id) {
        return gameRepository.findById(id);
    }
}