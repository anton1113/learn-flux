package com.arash.flux.service;

import com.arash.flux.model.Profile;
import com.arash.flux.repository.ProfileRepository;
import com.arash.flux.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Predicate;

@Log4j2
@RequiredArgsConstructor
@DataMongoTest
@Import(ProfileService.class)
public class ProfileServiceTest {

    private final ProfileService profileService;
    private final ProfileRepository profileRepository;

    @Test
    public void getAll() {
        Flux<Profile> saved = profileRepository.saveAll(Flux.just(new Profile(null, "Josh"), new Profile(null, "Matt"), new Profile(null, "Jane")));
        Flux<Profile> composite = profileService.all().thenMany(saved);
        Predicate<Profile> match = profile -> saved.any(savedItem -> savedItem.equals(profile)).block();
        StepVerifier
                .create(composite)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .expectNextMatches(match)
                .verifyComplete();
    }

    @Test
    public void delete() {
        String test = "test";
        Mono<Profile> deleted = profileService
                .create(test)
                .flatMap(saved -> profileService.delete(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> profile.getEmail().equalsIgnoreCase(test))
                .verifyComplete();
    }

    @Test
    public void update() throws Exception {
        Mono<Profile> saved = this.profileService
                .create("test")
                .flatMap(p -> this.profileService.update(p.getId(), "test1"));
        StepVerifier
                .create(saved)
                .expectNextMatches(p -> p.getEmail().equalsIgnoreCase("test1"))
                .verifyComplete();
    }

    @Test
    public void getById() {
        String test = UUID.randomUUID().toString();
        Mono<Profile> deleted = this.profileService
                .create(test)
                .flatMap(saved -> this.profileService.get(saved.getId()));
        StepVerifier
                .create(deleted)
                .expectNextMatches(profile -> StringUtils.hasText(profile.getId()) && test.equalsIgnoreCase(profile.getEmail()))
                .verifyComplete();
    }
}
