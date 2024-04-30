package com.example.Messenger.repositories.cache;

import com.example.Messenger.models.cache.LanguageOfApp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LanguageOfAppRepository extends CrudRepository<LanguageOfApp, Integer> {
    Optional<LanguageOfApp> findByType(String type);
}
