package com.innvo.repository.search;

import com.innvo.domain.Scenario;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Scenario entity.
 */
public interface ScenarioSearchRepository extends ElasticsearchRepository<Scenario, Long> {
}
