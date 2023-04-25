package com.general.etl.assembler;

import com.general.etl.core.BaseAssembler;
import com.general.etl.core.Context;
import com.general.etl.core.RunnableLifeCycle;
import com.general.etl.core.RunnableLifeCycleListener;
import com.general.etl.processor.RiskProcessor;
import com.general.etl.processor.TradeProcessor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Phaser;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

@Log4j2
@Component
public class TradeAssembler extends BaseAssembler implements Consumer<Throwable>, RunnableLifeCycleListener {

    @Autowired
    TradeProcessor tradeProcessor;

    @Autowired
    RiskProcessor riskProcessor;
    private CountDownLatch countDownLatch;


    @Override
    public void addFeeder() {

    }

    @Override
    protected void onTrigger(Context context) {
        tradeProcessor.start(context);
        for (int i = 0; i < 10; i++) {
            tradeProcessor.feed(Arrays.asList(i+""));
        }
        tradeProcessor.notifyStop();
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("process finished");
    }

    @Override
    protected void onCreate() {
        addProcessor(tradeProcessor);
        addProcessor(riskProcessor);
        tradeProcessor.addDownStream(riskProcessor);

        tradeProcessor.addExceptionListener(this);
        riskProcessor.addExceptionListener(this);
        tradeProcessor.addRunnableLifecycleListener(this);
        riskProcessor.addRunnableLifecycleListener(this);
        countDownLatch = new CountDownLatch(2);
    }
    @Override
    protected void onDestroy() {

    }

    @Override
    public boolean isCreated() {
        return false;
    }

    @Override
    public void accept(Throwable e) {
        tradeProcessor.stopImmediately();
        countDownLatch.countDown();
    }

    @Override
    public void onStop(RunnableLifeCycle lifeCycle) {
        log.info("stop, {}",lifeCycle);
        countDownLatch.countDown();
    }
}
