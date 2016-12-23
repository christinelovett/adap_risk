package com.innvo.repository.search;

import com.innvo.domain.Countermeasurefactor;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Countermeasurefactor entity.
 */
public interface CountermeasurefactorSearchRepository extends ElasticsearchRepository<Countermeasurefactor, Long> {
}
