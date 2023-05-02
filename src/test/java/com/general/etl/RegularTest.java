package com.general.etl;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

public class RegularTest {

    @Test
    void  test(){
        CyclicBarrier cyclicBarrier = new CyclicBarrier(10);

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("wait "+ Thread.currentThread().getName());
                        int await = cyclicBarrier.await();
                        System.out.println(Thread.currentThread().getName() + "all done "+ await);
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }
    @Test
    void  test2(){
        Phaser phaser = new Phaser(10);
//        phaser.register
        System.out.println(phaser.getArrivedParties());
        System.out.println(phaser.getRegisteredParties());
        System.out.println(phaser.getUnarrivedParties());
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                        System.out.println("wait "+ Thread.currentThread().getName());

                        int await = phaser.arriveAndAwaitAdvance();
                        System.out.println(Thread.currentThread().getName() + "all done "+ await);
                }
            });
        }
    }
    @Test
    void  test4(){
        Semaphore semaphore = new Semaphore(10);
        semaphore.release(2000);
//        forkJoinPool.
    }
    @Test
    void  test3(){
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        RecursiveTask<String> recursiveTask = new RecursiveTask<>() {

            @Override
            protected String compute() {
                System.out.println("test");
                return "111";
            }
        };
        forkJoinPool.invoke(recursiveTask);
        String join = recursiveTask.join();
        System.out.println(join);
//        forkJoinPool.execute();
//        forkJoinPool.
    }
}
