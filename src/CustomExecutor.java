import java.util.concurrent.*;

public class CustomExecutor<V> {
    private static ThreadPoolExecutor threadpool;
    private int corePoolSize;
    private final int maxPoolSize;
    private PriorityBlockingQueue<Runnable> queue;
    private int currentMax;
    private int[] priorityArray;

    private boolean flag;


    /**
     * CustomExecutor constructor
     */
    public CustomExecutor() {
        corePoolSize = Runtime.getRuntime().availableProcessors() / 2;
        maxPoolSize = Runtime.getRuntime().availableProcessors() - 1;
        queue = new PriorityBlockingQueue<Runnable>();
        priorityArray = new int[4];
        this.flag = true;
        threadpool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 300L, TimeUnit.MILLISECONDS, queue) {

            /**
             * Override ThreadPoolExecutor beforeExecute() to update currentMax value
             * when Thread extracts a new task from the queue, calling the synchronized method setPriorityArray()
             *
             * @param t the thread that will run task {@code r}
             * @param r the task that will be executed
             */
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                setPriorityArray();
            }
        };
    }

    /**
     * Factory method to submit the given task to the thread-pool
     *
     * @param task the task that will be executed
     * @return the tasks future
     */
    private <V> Future<V> submitTask(Task<V> task) {
        RunnableFuture<V> taskRunabble = task;
        this.priorityArray[task.getPriority().getPriorityValue()]++;
        threadpool.execute(taskRunabble);
        return taskRunabble;
    }

    /**
     * This method submits a new task to the thread-pool using the factory method
     *
     * @param task the task to submit
     * @return submitTask() return future for the task
     */
    public Future<V> submit(Task task) {
        return submitTask(task);
    }

    /**
     * This method initialize a new task from the given callable&tasktype and submits it to the thread-pool using the factory method
     *
     * @param c        the callable that will be injected to the task
     * @param priority the tasktype priority that will be injected to the task
     * @return submitTask() return future for the task
     */
    public Future<V> submit(Callable c, TaskType priority) {
        Task t1 = Task.createTask(c, priority);
        return submitTask(t1);
    }

    /**
     * This method will shut down the thread-pool and wait for all the threads to finish their work
     */
    public void gracefullyTerminate() {
        threadpool.shutdown();
        while (!threadpool.isTerminated()) {
            try {
                threadpool.awaitTermination(300, TimeUnit.MILLISECONDS); //waiting half min each time.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        currentMax = 0;
    }

    /**
     * Synchronized method to update the priority array and currentMax value
     */
    private synchronized void setPriorityArray() {
        flag = true;
        for (int i = 0; i < priorityArray.length && flag; i++) {
            if (priorityArray[i] != 0) {
                flag = false;
                currentMax = i;
                priorityArray[i] -= 1;
            }
        }
    }

    /**
     * This method sets a new value for corePoolSize iff the new value is less than maxPoolSize
     *
     * @param corePoolSize new corePoolSize value
     */
    public void setCorePoolSize(int corePoolSize) {
        if (currentMax >= maxPoolSize)
            this.corePoolSize = corePoolSize;
    }

    public int getCurrenctMax() {
        return this.currentMax;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getCurrentMax() {
        return currentMax;
    }

    public ThreadPoolExecutor getThreadpool() {
        return threadpool;
    }
}
