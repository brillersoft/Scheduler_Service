package com.hanogi.batch.quartz.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.hanogi.batch.services.ISchedulerService;

/**
 * @author Abhishek
*/

@Slf4j
@Component
public class SchedulerStartUpHandler implements ApplicationRunner {

    @Autowired
    private ISchedulerService schedulerService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
           // schedulerService.startAllActiveSchedulers();
        } catch (Exception ex) {
        }
    }
}
