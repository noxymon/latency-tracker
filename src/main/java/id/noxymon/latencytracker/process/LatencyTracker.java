package id.noxymon.latencytracker.process;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LatencyTracker {

    private final JdbcTemplate   jdbcTemplate;
    private final InfluxDBClient influxDBClient;
    private final MetricRegistry metricRegistry = new MetricRegistry();
    @Value("${application.measurement.repeat}")
    private       Integer        repeat;
    @Value("${application.measurement.query}")
    private       String         queryString;
    @Value("${application.influx.bucket}")
    private       String         influxBucket;
    @Value("${application.influx.org}")
    private       String         influxorg;

    @Scheduled(cron = "* */5 * * * *")
    public void trackByQuery() throws InterruptedException {
        log.info("Started !");
        Histogram histogram = metricRegistry.histogram("queryLatency");
        for (int i = 0; i < repeat; i++) {
            LocalTime start = LocalTime.now();
            List<Map<String, Object>> queryResultList = jdbcTemplate.queryForList(queryString);
            System.out.println(queryResultList.size());
            LocalTime end = LocalTime.now();
            long elapsed = calculateElapsed(start, end);
            histogram.update(elapsed);
            Thread.sleep(1000);
        }
        writeToInflux(histogram);
    }

    private long calculateElapsed(LocalTime start, LocalTime end) {
        return end.toNanoOfDay() / 1000000 - start.toNanoOfDay() / 1000000;
    }

    private void writeToInflux(Histogram histogram) {
        Point point = Point.measurement("latency")
                           .addField("query_time", histogram.getSnapshot().getMean())
                           .time(Instant.now(), WritePrecision.NS);
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {
            writeApi.writePoint(influxBucket, influxorg, point);
        }
    }
}
