import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import java.io.*;


import static org.junit.jupiter.api.Assertions.*;

class Ex2_1Test {

    public static final Logger logger = LoggerFactory.getLogger(Ex2_1Test.class);

    @Test
    void createTextFiles() {

        int n = 100;
        int seed = 100;
        int bound = 10;
        String temp = null;

        String[] files = Ex2_1.createTextFiles(n, seed, bound);
        try (BufferedReader reader = new BufferedReader(new FileReader(files[0]))) {
            temp = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertEquals(files.length, 100);
        assertEquals("Hello World!", temp);
    }

    @Test
    void getNumOfLines() {

        String[] s = new String[2];
        File newTextFile = new File("file_" + 1 + ".txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter("file_" + 1 + ".txt");
            PrintWriter pw = new PrintWriter(fw);
            pw.append("Hello World!\n");
            pw.close();
            fw.close();
            fw = new FileWriter("file_" + 2 + ".txt");
            pw = new PrintWriter(fw);
            pw.append("Hello World!\n");
            pw.append("Hello World!\n");
            pw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        s[0] = "file_" + 1 + ".txt";
        s[1] = "file_" + 2 + ".txt";

        assertEquals(Ex2_1.getNumOfLines(s), 3);
    }

    @Test
    void getNumOfLinesThreads() {

        String[] s = new String[2];
        File newTextFile = new File("file_" + 1 + ".txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter("file_" + 1 + ".txt");
            PrintWriter pw = new PrintWriter(fw);
            pw.append("Hello World!\n");
            pw.close();
            fw.close();
            fw = new FileWriter("file_" + 2 + ".txt");
            pw = new PrintWriter(fw);
            pw.append("Hello World!\n");
            pw.append("Hello World!\n");
            pw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        s[0] = "file_" + 1 + ".txt";
        s[1] = "file_" + 2 + ".txt";


        long regularTime,threadsTime;
        long startTime, endTime;

        startTime = System.nanoTime();
        int regularLines = Ex2_1.getNumOfLines(s);
        endTime = System.nanoTime();
        regularTime = endTime - startTime;

        startTime = System.nanoTime();
        int threadsLines = Ex2_1.getNumOfLinesThreads(s);
        endTime = System.nanoTime();
        threadsTime = endTime - startTime;

        assertAll("Check list:",
                ()-> assertEquals(3,regularLines),
                ()-> assertEquals(3,threadsLines),
                ()-> assertTrue(threadsTime>regularTime),
                ()-> logger.info(()-> "\nRegular time: " + regularTime + "\nThreads time: " + threadsTime ),
                ()-> logger.info(()-> "threadsLines: " +threadsLines + "\nregularLines: "+ regularLines));
    }

    @Test
    void getNumOfLinesThreadPool() {

        int n = 1000;
        int seed = 10000;
        int bound = 10000;
        String[] files = Ex2_1.createTextFiles(n,seed,bound);

        long regularTime,threadsTime,threadPoolTime;
        long startTime, endTime;

        startTime = System.nanoTime();
        int regularLines = Ex2_1.getNumOfLines(files);
        endTime = System.nanoTime();
        regularTime = endTime - startTime;

        startTime = System.nanoTime();
        int threadsLines = Ex2_1.getNumOfLinesThreads(files);
        endTime = System.nanoTime();
        threadsTime = endTime - startTime;

        startTime = System.nanoTime();
        int threadPoolLines = Ex2_1.getNumOfLinesThreadPool(files);
        endTime = System.nanoTime();
        threadPoolTime = endTime - startTime;

        assertAll("Check list:",
                ()-> assertEquals(5016713,regularLines),
                ()-> assertEquals(regularLines,threadsLines),
                ()-> assertEquals(threadsLines,threadPoolLines),
                ()-> assertTrue(regularTime>threadsTime),
                ()-> assertTrue(threadsTime>threadPoolTime),
                ()-> logger.info(()-> "\nRegular time: " + regularTime + "\nThreads time: " + threadsTime + "\nThreadPool time: " + threadPoolTime ),
                ()-> logger.info(()-> "\nthreadPoolLines: " + threadPoolLines + "\nthreadsLines: " + threadsLines + "\nregularLines: " + regularLines ));
    }
}