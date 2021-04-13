package id.noxymon.latencytracker.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxConfig {
    @Value("${application.influx.bucket}")
    private String influxBucket;
    @Value("${application.influx.org}")
    private String influxorg;
    @Value("${application.influx.token}")
    private String influxToken;
    @Value("${application.influx.url}")
    private String influxUrl;

    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(influxUrl, influxToken.toCharArray(), influxorg, influxBucket);
    }
}
