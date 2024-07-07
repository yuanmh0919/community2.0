package com.yuanmh.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @Author: Yuanmh
 * @Date: 下午10:30 2024/6/28
 * @Describe: 阻塞队列测试
 */

public class BlockingQueueTests {
    public static void main(String[] args) {
        //创建阻塞队列
        BlockingQueue queue = new ArrayBlockingQueue(20);
        new Thread(new Producer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
        new Thread(new Consumer(queue)).start();
    }
}

/**
 * 阻塞队列生产者类
 */
class Producer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            //生产100个数据
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName() + " 生产: " + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * 阻塞队列消费者类
 */
class Consumer implements Runnable {

    private BlockingQueue<Integer> queue;

    public Consumer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            //只要有数据就一直消费
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName() + " 消费: " + queue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}