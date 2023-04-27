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
import java.util.function.Consumer;

@Log4j2
@Component
public class TradeAssembler extends BaseAssembler implements Consumer<Throwable>, RunnableLifeCycleListener {

    @Autowired
    TradeProcessor tradeProcessor;

    @Autowired
    RiskProcessor riskProcessor;
    Thread mainThread;


    @Override
    public void addFeeder() {

    }



    @Override
    protected void onTrigger() {
        tradeProcessor.start();
        for (int i = 0; i < 10; i++) {
            tradeProcessor.feed(Arrays.asList(i+""));
        }
        //shutdown but continue to process what's left in the task queue.
        tradeProcessor.stop();
        try {
            System.out.println("awit stop");
            riskProcessor.awaitStop();
            System.out.println("stopped");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("process finished");
    }

    @Override
    protected void onCreate(Context context) {
        addProcessor(tradeProcessor);
        addProcessor(riskProcessor);
        tradeProcessor.addDownStream(riskProcessor);

        tradeProcessor.addExceptionListener(this);
        riskProcessor.addExceptionListener(this);
        tradeProcessor.addRunnableLifecycleListener(this);
        riskProcessor.addRunnableLifecycleListener(this);
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
        log.error("error :", e);
        System.out.println("stop now");
        tradeProcessor.stopNow();
        riskProcessor.stopNow();
    }

    @Override
    public void onStop(RunnableLifeCycle lifeCycle) {
        log.info("stop, {}",lifeCycle);
    }
}
