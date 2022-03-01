package com.hms.gamingapi.dao;

import com.hms.gamingapi.dao.custom.GameRepositoryCustom;
import com.hms.gamingapi.model.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends ReactiveMongoRepository<Game, String>, GameRepositoryCustom {

}
