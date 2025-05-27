package org.jnjeaaaat.snms.domain.auth.repository;

import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisTokenRepository extends CrudRepository<RedisToken, String> {

}
