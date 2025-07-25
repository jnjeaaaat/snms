package org.jnjeaaaat.domain.auth.repository;

import org.jnjeaaaat.domain.auth.entity.RedisSms;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisSmsRepository extends CrudRepository<RedisSms, String> {

}
