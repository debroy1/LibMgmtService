package com.roy.auth.atlas;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserItemRepository extends MongoRepository<UserItem, String> {

	Optional<UserItem> findByEmail(String email);

}
