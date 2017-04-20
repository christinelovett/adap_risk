package com.innvo.service;

import com.codahale.metrics.annotation.Timed;
import com.innvo.domain.*;
import com.innvo.repository.*;
import com.innvo.repository.search.*;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    @Inject
    private CategoryRepository categoryRepository;

    @Inject
    private CategorySearchRepository categorySearchRepository;

    @Inject
    private CountermeasureRepository countermeasureRepository;

    @Inject
    private CountermeasureSearchRepository countermeasureSearchRepository;

    @Inject
    private CountermeasurefactorRepository countermeasurefactorRepository;

    @Inject
    private CountermeasurefactorSearchRepository countermeasurefactorSearchRepository;

    @Inject
    private CountermeasurefactortypeRepository countermeasurefactortypeRepository;

    @Inject
    private CountermeasurefactortypeSearchRepository countermeasurefactortypeSearchRepository;

    @Inject
    private PathwayRepository pathwayRepository;

    @Inject
    private PathwaySearchRepository pathwaySearchRepository;

    @Inject
    private PathwaycountermeasurembrRepository pathwaycountermeasurembrRepository;

    @Inject
    private PathwaycountermeasurembrSearchRepository pathwaycountermeasurembrSearchRepository;

    @Inject
    private PathwaypathwaymbrRepository pathwaypathwaymbrRepository;

    @Inject
    private PathwaypathwaymbrSearchRepository pathwaypathwaymbrSearchRepository;

    @Inject
    private RecordtypeRepository recordtypeRepository;

    @Inject
    private RecordtypeSearchRepository recordtypeSearchRepository;

    @Inject
    private ScenarioRepository scenarioRepository;

    @Inject
    private ScenarioSearchRepository scenarioSearchRepository;

    @Inject
    private ScenariopathwaymbrRepository scenariopathwaymbrRepository;

    @Inject
    private ScenariopathwaymbrSearchRepository scenariopathwaymbrSearchRepository;

    @Inject
    private SubcategoryRepository subcategoryRepository;

    @Inject
    private SubcategorySearchRepository subcategorySearchRepository;

    @Inject
    private TargetRepository targetRepository;

    @Inject
    private TargetSearchRepository targetSearchRepository;

    @Inject
    private WeaponRepository weaponRepository;

    @Inject
    private WeaponSearchRepository weaponSearchRepository;

    @Inject
    private ElasticsearchTemplate elasticsearchTemplate;

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(Category.class, categoryRepository, categorySearchRepository);
        reindexForClass(Countermeasure.class, countermeasureRepository, countermeasureSearchRepository);
        reindexForClass(Countermeasurefactor.class, countermeasurefactorRepository, countermeasurefactorSearchRepository);
        reindexForClass(Countermeasurefactortype.class, countermeasurefactortypeRepository, countermeasurefactortypeSearchRepository);
        reindexForClass(Pathway.class, pathwayRepository, pathwaySearchRepository);
        reindexForClass(Pathwaycountermeasurembr.class, pathwaycountermeasurembrRepository, pathwaycountermeasurembrSearchRepository);
        reindexForClass(Pathwaypathwaymbr.class, pathwaypathwaymbrRepository, pathwaypathwaymbrSearchRepository);
        reindexForClass(Recordtype.class, recordtypeRepository, recordtypeSearchRepository);
        reindexForClass(Scenario.class, scenarioRepository, scenarioSearchRepository);
        reindexForClass(Scenariopathwaymbr.class, scenariopathwaymbrRepository, scenariopathwaymbrSearchRepository);
        reindexForClass(Subcategory.class, subcategoryRepository, subcategorySearchRepository);
        reindexForClass(Target.class, targetRepository, targetSearchRepository);
        reindexForClass(Weapon.class, weaponRepository, weaponSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.save((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.save(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }
}
