package org.jnjeaaaat.entity.repository;

import org.jnjeaaaat.entity.RedisToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, Long> {

    Optional<RedisToken> findByAccessToken(String accessToken);

}
