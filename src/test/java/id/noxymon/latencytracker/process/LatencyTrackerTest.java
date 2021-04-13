package id.noxymon.latencytracker.process;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LatencyTrackerTest {

    @Autowired
    private LatencyTracker latencyTracker;

    @Test
    public void testifWork() throws InterruptedException {
        latencyTracker.trackByQuery();
    }
}