package com.innvo.repository.search;

import com.innvo.domain.Weapon;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Weapon entity.
 */
public interface WeaponSearchRepository extends ElasticsearchRepository<Weapon, Long> {
}
