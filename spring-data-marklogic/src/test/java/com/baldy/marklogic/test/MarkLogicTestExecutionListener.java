package com.baldy.marklogic.test;

import java.util.Map;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.baldy.marklogic.test.entity.Taxi;
import com.baldy.marklogic.test.service.TaxiService;
import com.google.common.collect.Maps;

public class MarkLogicTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        Taxi taxi = new Taxi();
        taxi.setOperator("Mark Martinez");
        taxi.setPlateNo("WAR668");
        taxi.setId(7l);

        Map<String, String> locs = Maps.newHashMap();
        locs.put("loc1", "value1");
        locs.put("loc2", "value2");
        taxi.setLocations(locs);

        taxi.setBinaryData("Some binary data".getBytes());

        TaxiService taxiService = testContext.getApplicationContext().getBean(TaxiService.class);
        taxiService.save(taxi);
    }

}
