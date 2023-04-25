package com.general.etl.processor;

import com.general.etl.core.AbstractProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class RiskProcessor extends AbstractProcessor<String,String> {


    private void fakeOutput() {
        try {
            Thread.sleep(new Random().nextInt(590));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onOutput(List<String> output) {
        fakeOutput();
        log.info("risk outputted {}",output);
    }

    @Override
    protected List<String> onProcess(String input) {
        fakeProcess();
        log.info("risk processed: "+input);
        return Arrays.asList( "processed risk: "+input);
    }

    private void fakeProcess() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
