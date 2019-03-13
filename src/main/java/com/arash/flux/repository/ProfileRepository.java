package com.arash.flux.repository;

import com.arash.flux.model.Profile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProfileRepository extends ReactiveMongoRepository<Profile, String> {
}
