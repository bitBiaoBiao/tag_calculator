package com.mip.roaring.tagcalc.velocity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author ZhengWenbiao
 * @Date 2019/5/31 15:41
 **/
@Component
public class QueueContext {

    private static final Logger logger = LoggerFactory.getLogger(QueueContext.class);

    @Autowired
    private CalculatorService rescreen;

    @PostConstruct
    public void init() {
        rescreen.init(new detailThread("roaringQueueThread"));
    }

    class detailThread extends Thread {

        private final LinkedBlockingQueue<Long> roaringBitmapHandleQueue = new LinkedBlockingQueue<>();

        detailThread(String name) {

            super(name);
        }

        public LinkedBlockingQueue<Long> getRoaringBitmapHandleQueue() {

            return roaringBitmapHandleQueue;
        }

        /**
         * 队列入队，id代表 待生成对应roaringBitmap集的人群包
         *
         * @param id
         */
        public void queuePut(Long id) {

            roaringBitmapHandleQueue.add(id);
        }

        @Override
        public void run() {

            while (true) {
                try {
                    Long id = roaringBitmapHandleQueue.take();
                    processHandleQueue(id);
                } catch (InterruptedException e) {
                    logger.error("Queue handle thread exiting due to interruption", e);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        private void processHandleQueue(Long id) throws Exception {

            rescreen.scan(id);
        }
    }
}
