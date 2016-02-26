package com.baldy.marklogic.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.baldy.marklogic.test.entity.Taxi;
import com.baldy.marklogic.test.service.TaxiService;
import com.google.common.collect.Maps;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfig.class)
@TestExecutionListeners({MarkLogicTestExecutionListener.class, DependencyInjectionTestExecutionListener.class})
public class MarkLogicIntegrationTest {

    private static Logger LOG = LoggerFactory.getLogger(MarkLogicIntegrationTest.class);

    @Autowired
    private TaxiService taxiService;

    @Test
    public void testSave() {
        Taxi taxi = new Taxi();
        taxi.setOperator("Mark Martinez");
        taxi.setPlateNo("WAR668");
        taxi.setId(7l);

        Map<String, String> locs = Maps.newHashMap();
        locs.put("loc1", "value1");
        locs.put("loc2", "value2");
        taxi.setLocations(locs);

        taxi.setBinaryData("Some binary data".getBytes());

        taxiService.save(taxi);
    }

    @Test
    public void testFindOne() {
        //This should have been saved by the test execution listener
        Taxi savedTaxi = taxiService.findOne("taxi/taxi7");
        assertNotNull(savedTaxi);
        LOG.info("Got saved taxi: {}", savedTaxi);
    }

    @Test
    public void testKeyValueSearch() {
        LOG.info("Listing taxis by operator.");
        List<Taxi> taxisByOperator = taxiService.keyValueQuery("operator", "Mark Martinez");
        assertNotNull(taxisByOperator);
        assertTrue(taxisByOperator.size() > 0);

        for (Taxi searchResult : taxisByOperator) {
            LOG.info("Search result taxi={}", searchResult);
        }
    }

    @Test
    public void testPaginatedKeyValueSearch() {
        //Paginated key value query
        PageRequest page = new PageRequest(1, 1);
        Page<Taxi> taxisByOperator2 = taxiService.keyValueQuery("operator", "Mark Martinez", page);
        assertNotNull(taxisByOperator2);
        assertTrue(taxisByOperator2.getSize() > 0);
        LOG.info("Total results={}, total pages={}", taxisByOperator2.getTotalElements(), taxisByOperator2.getTotalPages());
        for (Taxi searchResult : taxisByOperator2) {
            LOG.info("Search result taxi={}", searchResult);
        }
    }

    @Test
    public void testFindAll() {
        //Find all (this will find by entity's enumerated collections)
        List<Taxi> alltaxis = taxiService.findAll();
        assertNotNull(alltaxis);
        assertTrue(alltaxis.size() > 0);
        LOG.info("All taxis count={}, taxis={}", alltaxis.size(), alltaxis);
    }

    @Test
    public void testPaginatedFindAll() {
        PageRequest page = new PageRequest(1, 1);
        Page<Taxi> taxis = taxiService.findAll(page);
        assertNotNull(taxis);
        assertTrue(taxis.getSize() > 0);
        LOG.info("Total results={}, total pages={}", taxis.getTotalElements(), taxis.getTotalPages());
        for (Taxi searchResult : taxis) {
            LOG.info("Paginated findAll(Pageable) search result taxi={}", searchResult);
        }
    }

}
