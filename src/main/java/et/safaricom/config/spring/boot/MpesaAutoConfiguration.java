package et.safaricom.config.spring.boot;

import et.safaricom.Mpesa;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MpesaProperties.class)
public class MpesaAutoConfiguration {
    @Bean
    public Mpesa mpesa(MpesaProperties properties) {
        return new Mpesa(
                properties.getConsumerKey(),
                properties.getConsumerSecret(),
                et.safaricom.config.Configuration.builder()
                        .baseUrl(properties.getBaseUrl())
                        .logLevel(properties.getLogLevel())
                        .connectionTimeout(properties.getConnectionTimeout())
                        .maxRetries(properties.getMaxRetries())
                        .retryOnConnectionFailure(properties.isRetryOnConnectionFailure())
                        .build()
                );
    }
}
