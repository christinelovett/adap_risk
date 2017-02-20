package com.innvo.repository.search;

import com.innvo.domain.Target;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Target entity.
 */
public interface TargetSearchRepository extends ElasticsearchRepository<Target, Long> {
}
