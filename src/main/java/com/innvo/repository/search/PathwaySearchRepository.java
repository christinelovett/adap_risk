package com.innvo.repository.search;

import com.innvo.domain.Pathway;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Pathway entity.
 */
public interface PathwaySearchRepository extends ElasticsearchRepository<Pathway, Long> {
}
