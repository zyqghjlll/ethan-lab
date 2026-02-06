package common.spring.boot.autoconfigure;

import common.utils.DefaultIdGenerator;
import common.utils.IdGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class IdGeneratorAutoConfiguration {
    private static final Log log = LogFactory.getLog(IdGeneratorAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean(IdGenerator.class)
    public IdGenerator idGenerator() {
        return new DefaultIdGenerator();
    }
}
