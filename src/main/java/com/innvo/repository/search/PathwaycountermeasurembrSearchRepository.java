package com.innvo.repository.search;

import com.innvo.domain.Pathwaycountermeasurembr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Pathwaycountermeasurembr entity.
 */
public interface PathwaycountermeasurembrSearchRepository extends ElasticsearchRepository<Pathwaycountermeasurembr, Long> {
}
