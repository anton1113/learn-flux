package com.arash.flux.service;

import com.arash.flux.event.ProfileCreatedEvent;
import com.arash.flux.model.Profile;
import com.arash.flux.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ApplicationEventPublisher publisher;
    private final ProfileRepository profileRepository;

    public Flux<Profile> all() {
        return profileRepository.findAll();
    }

    public Mono<Profile> get(String id) {
        return profileRepository.findById(id);
    }

    public Mono<Profile> update(String id, String email) {
        return profileRepository
                .findById(id)
                .map(p -> new Profile(p.getId(), email))
                .flatMap(profileRepository::save);
    }

    public Mono<Profile> delete(String id) {
        return profileRepository
                .findById(id)
                .flatMap(p -> profileRepository.deleteById(id).thenReturn(p));
    }

    public Mono<Profile> create(String email) {
        return profileRepository
                .save(new Profile(null, email))
                .doOnSuccess(profile -> publisher.publishEvent(new ProfileCreatedEvent(profile)));
    }
}
