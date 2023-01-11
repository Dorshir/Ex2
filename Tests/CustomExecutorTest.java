import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

class CustomExecutorTest {


    @Test
    void submit() {
        CustomExecutor c1 = new CustomExecutor<>();
        Task<Integer> taskLowPriority = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            Thread.sleep(1000);
            return sum;
        }, TaskType.OTHER);
        Task<Integer> taskMaxPriority = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 5; i++) {
                sum += i;
            }
            Thread.sleep(1000);
            return sum;
        }, TaskType.COMPUTATIONAL);
        Future<Integer>[] sumTaskArray = new Future[1000];
        int currMax=0;
        for (int i = 0; i < 60; i++) {
            sumTaskArray[i] = c1.submit(taskLowPriority);
        }
        for (int i = 60; i < 1000; i++) {
            sumTaskArray[i] = c1.submit(taskMaxPriority);
        }
        int[] sum = new int[1000];
        try {
            int count = 0;
            for (int i = 0; i < 1000; i++) {
                sum[i] = sumTaskArray[i].get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        c1.gracefullyTerminate();
        assertEquals(c1.getThreadpool().getCompletedTaskCount(),1000);
    }


    @Test
    void setCorePoolSize() {
        CustomExecutor c1 = new CustomExecutor<>();
        int max = c1.getMaxPoolSize();
        int lastCorePoolSize = c1.getCorePoolSize();
        c1.setCorePoolSize(max+5);
        assertEquals(lastCorePoolSize,c1.getCorePoolSize());
        c1.gracefullyTerminate();
    }

    @Test
    void gracefullyTerminate() {
        CustomExecutor c1 = new CustomExecutor<>();
        Task<Integer> taskLowPriority = Task.createTask(() -> {
            int sum = 0;
            for (int i = 1; i <= 10; i++) {
                sum += i;
            }
            Thread.sleep(1000);
            return sum;
        }, TaskType.OTHER);
        Future<Integer>[] sumTaskArray = new Future[1000];
        for (int i = 0; i < 1000; i++) {
            sumTaskArray[i] = c1.submit(taskLowPriority);
        }
        int[] sum = new int[1000];
        try {
            int count = 0;
            for (int i = 0; i < 1000; i++) {
                sum[i] = sumTaskArray[i].get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        c1.gracefullyTerminate();
        int currMax = c1.getCurrenctMax();
        assertEquals(0,currMax); //check after gracefullyTerminate() been use , the queue is empty(currMax = 0)
    }
}