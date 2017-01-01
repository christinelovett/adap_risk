package com.innvo.repository.search;

import com.innvo.domain.Scenariopathwaymbr;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Scenariopathwaymbr entity.
 */
public interface ScenariopathwaymbrSearchRepository extends ElasticsearchRepository<Scenariopathwaymbr, Long> {
}
