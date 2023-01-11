import org.junit.jupiter.api.Test;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void createTask() {

        Callable<String> b = ()-> "Hello World";
        Task<String> task = Task.createTask(b);

        assertSame(task.getCallable(), b);
        assertSame(task.getPriority(), TaskType.OTHER);
    }

    @Test
    void testCreateTask() {

        Callable<Integer> a = () -> 1 + 1;
        TaskType priorityA = TaskType.COMPUTATIONAL;
        Task<Integer> task = Task.createTask(a, priorityA);

        assertSame(task.getCallable(), a);
        assertSame(task.getPriority(), priorityA);
    }

    @Test
    void testEquals() {

        var task1 = Task.createTask(() -> {
            double a = 5;
            double b = 2;
            return a/b;
        },TaskType.COMPUTATIONAL);

        var task2 = Task.createTask(() -> {
            double a = 5;
            double b = 2;
            return a/b;
        },TaskType.COMPUTATIONAL);

        var task3 = Task.createTask(() -> {
            double a = 5;
            double b = 2;
            return a/b;
        });

        assertTrue(task1.equals(task2));
        assertFalse(task1.equals(task3));

    }

    @Test
    void call() throws Exception {
        Task task1 = null;
        Callable<Integer> c = () -> 1+1;
        Task task2 = Task.createTask(c);
        assertThrows(Exception.class,()-> task1.call());

        int a = c.call();
        int b = (int) task2.call();
        assertEquals(a,b);
    }

    @Test
    void compareTo() {
        Callable<Integer> a = ()-> 1+1;

        Task<Integer> task1 = Task.createTask(a,TaskType.COMPUTATIONAL);
        Task<Integer> task2 = Task.createTask(a,TaskType.COMPUTATIONAL);
        Task<Integer> task3 = Task.createTask(a,TaskType.OTHER);

        assertEquals(0, task1.compareTo(task2));
        assertTrue(task1.compareTo(task3) < 0);
    }
}