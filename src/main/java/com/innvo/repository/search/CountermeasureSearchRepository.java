package com.innvo.repository.search;

import com.innvo.domain.Countermeasure;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Countermeasure entity.
 */
public interface CountermeasureSearchRepository extends ElasticsearchRepository<Countermeasure, Long> {
}
