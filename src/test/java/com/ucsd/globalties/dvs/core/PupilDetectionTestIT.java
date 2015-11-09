package com.ucsd.globalties.dvs.core;

import com.ucsd.globalties.dvs.core.Photo.PhotoType;
import com.ucsd.globalties.dvs.core.tools.Pair;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An integration test for pupil detection.
 * This class runs pupil detection (and thus face and eye detection) on a list of manually
 * managed images. It currently asserts nothing, but when accuracy of pupil detection is
 * improved (and better/more pictures are taken), it can serve as a real integration test.
 * Maven does not, by default, compile integration tests. You may run this class individually,
 * or use the maven integration-test phase to run this class (with some settings, I think).
 *
 * @author Rahul
 */
@Slf4j
public class PupilDetectionTestIT {
    private static final List<Pair<String, String>> TEST_PAIRS = Arrays.asList(
            new Pair<String, String>("Andrei_1.jpg", "Andrei_2.jpg"),
            new Pair<String, String>("Sabit_1.jpg", "Sabit_2.jpg"),
            new Pair<String, String>("Heather_1.jpg", "Heather_2.jpg"),
            new Pair<String, String>("Rahul_1.jpg", "Rahul_3.jpg"),
            new Pair<String, String>("Xiangcheng_1.jpg", "Xiangcheng_2.jpg"),
            new Pair<String, String>("Shannon_1.jpg", "Shannon_2.jpg"),
            new Pair<String, String>("Keith_1.jpg", "Keith_2.jpg"),
            new Pair<String, String>("Teresa_1.jpg", "Teresa_2.jpg"),
            new Pair<String, String>("Daniel_2.jpg", "Daniel_3.jpg"),
            new Pair<String, String>("Gian_2.jpg", "Gian_3.jpg"),
            new Pair<String, String>("Leon_2.jpg", "Leon_3.jpg"));

    private static AtomicInteger counter = new AtomicInteger(0);
    private static int[] results = new int[TEST_PAIRS.size()];
    private static long timeTaken = 0;

    private static final ExecutorService threadpool = Executors.newFixedThreadPool(TEST_PAIRS.size() / 2);

    @Before
    public void setupAndClean() {
        Main.initOpenCV();
        for (Iterator<Pair<String, String>> it = TEST_PAIRS.iterator(); it.hasNext(); ) {
            Pair<String, String> picPair = it.next();
            File fst = new File(PupilDetectionTestIT.class.getResource("/pics/" + picPair.getLeft()).getFile());
            if (!fst.exists()) {
                log.warn("pic file: " + fst.getAbsolutePath() + " does not exist");
                it.remove();
                continue;
            }
            File snd = new File(PupilDetectionTestIT.class.getResource("/pics/" + picPair.getRight()).getFile());
            if (!fst.exists()) {
                log.warn("pic file: " + snd.getAbsolutePath() + " does not exist");
                it.remove();
            }
        }
    }

    @Test
    public void run() {
        long s = System.currentTimeMillis();
        for (int i = 0; i < TEST_PAIRS.size(); i++) {
            final int index = i;
            Runnable r = new Runnable() {
                public void run() {
                    Pair<String, String> test = TEST_PAIRS.get(index);
                    int found = 0;
                    try {
//                        Patient p = Patient.builder().name("Test" + index).build();
//                        File l = new File(PupilDetectionTestIT.class.getResource("/pics/" + test.getLeft()).getFile());
//                        File r = new File(PupilDetectionTestIT.class.getResource("/pics/" + test.getRight()).getFile());
//                        Photo pL = new Photo(l.getAbsolutePath(), p, PhotoType.VERTICAL);
//                        Photo pR = new Photo(r.getAbsolutePath(), p, PhotoType.HORIZONTAL);
//                        if (pL.getLeftEye().getPupil() != null) {
//                            found++;
//                        }
//                        if (pL.getRightEye().getPupil() != null) {
//                            found++;
//                        }
//                        if (pR.getLeftEye().getPupil() != null) {
//                            found++;
//                        }
//                        if (pR.getRightEye().getPupil() != null) {
//                            found++;
//                        }
                    } catch (Exception ex) {
                        log.error(String.format("Exception raised for pair %s, %s", test.getLeft(), test.getRight(), ex));
                    } finally {
                        results[index] = found;
                        counter.incrementAndGet();
                    }
                }
            };
            threadpool.execute(r);
        }
        // this is kind of lame, the real way to do this is a CountdownLatch but cba
        while (counter.get() < TEST_PAIRS.size()) {
            try {
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
        timeTaken = s;
        printResults();
    }

    public void printResults() {
        log.info("Time taken (s): " + (System.currentTimeMillis() - timeTaken) / 1000);
        int total = 0;
        for (int i = 0; i < results.length; i++) {
            total += results[i];
            Pair<String, String> pair = TEST_PAIRS.get(i);
            log.info("Pupil test for pair {}, {} found {}/4 pupils", pair.getLeft(), pair.getRight(), results[i]);
        }
        log.info("Test complete. Double check all results by looking at the images.");
        log.info("Found: {}/{}", total, TEST_PAIRS.size() * 4);
        if (total != TEST_PAIRS.size() * 4) {
            log.error("Expected: {} images, found: {}", TEST_PAIRS.size() * 4, total);
        }
    }
}
