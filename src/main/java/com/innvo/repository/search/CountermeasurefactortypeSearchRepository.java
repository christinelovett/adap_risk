package com.innvo.repository.search;

import com.innvo.domain.Countermeasurefactortype;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Countermeasurefactortype entity.
 */
public interface CountermeasurefactortypeSearchRepository extends ElasticsearchRepository<Countermeasurefactortype, Long> {
}
