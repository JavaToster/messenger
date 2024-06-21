package com.example.Messenger.repositories.redis.languageOfApp;

import com.example.Messenger.models.redis.LanguageOfApp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageOfAppRepository extends CrudRepository<LanguageOfApp, Integer> {
    Optional<LanguageOfApp> findByType(String type);
}
