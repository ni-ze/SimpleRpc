package com.zeni.rpc.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;

public class InFlightRequests implements Closeable {
    private static final long TIMEOUT_SEC = 10L;
    //异步请求，用于背压【同步请求，有天然的背压机制】
    private final Semaphore semaphore = new Semaphore(10);

    private final Map<Integer, ResponseFuture> futureMap = new ConcurrentHashMap<>();

    //还需要一个定时任务兜底
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledFuture scheduledFuture;

    public InFlightRequests() {
        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this::removeTimeoutFutures, TIMEOUT_SEC, TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    public void put(ResponseFuture responseFuture) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(TIMEOUT_SEC, TimeUnit.SECONDS)) {
            futureMap.put(responseFuture.getRequestId(), responseFuture);
        } else {
            throw new TimeoutException();
        }
    }

    //无参方法才可以被lambda表达式引用
    private void removeTimeoutFutures() {
        futureMap.entrySet().removeIf(entry -> {
            if (System.nanoTime() - entry.getValue().getTimestamp() > TIMEOUT_SEC * 1000000000L) {
                semaphore.release();
                return true;
            } else {
                return false;
            }
        });
    }

    public ResponseFuture remove(int requestId) {
        ResponseFuture responseFuture = futureMap.remove(requestId);
        if (responseFuture != null) {
            semaphore.release();
        }
        return responseFuture;
    }


    /**
     * 有线程池时，需要用close方法搞一个关闭机制。
     */
    @Override
    public void close() throws IOException {
        scheduledFuture.cancel(true);
        scheduledExecutorService.shutdown();
    }
}
