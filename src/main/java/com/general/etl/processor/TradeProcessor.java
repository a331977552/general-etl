package com.general.etl.processor;

import com.general.etl.core.AbstractProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@Log4j2
public class TradeProcessor  extends AbstractProcessor<String,String> {

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
        log.info("trade outputted {}",output);
    }

    @Override
    protected List<String> onProcess(String input) {
        fakeProcess();
        if (new Random().nextInt(10) == 3){
            throw new RuntimeException(" processing exception!!!");
        }
        log.info("trade processed: "+input);
        return Arrays.asList( "processed trade: "+input);
    }

    private void fakeProcess() {
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
