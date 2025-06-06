package org.jnjeaaaat.snms.domain.auth.repository;

import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, Long> {

    Optional<RedisToken> findByAccessToken(String accessToken);

}
