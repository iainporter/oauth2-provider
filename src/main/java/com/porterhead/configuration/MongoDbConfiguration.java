package com.porterhead.configuration;

import com.porterhead.oauth2.mongodb.OAuth2AccessTokenRepository;
import com.porterhead.oauth2.mongodb.OAuth2AuthenticationReadConverter;
import com.porterhead.oauth2.mongodb.OAuth2RefreshTokenRepository;
import com.porterhead.user.UserRepository;
import com.mongodb.Mongo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableMongoRepositories(basePackageClasses = {
        UserRepository.class,
        OAuth2RefreshTokenRepository.class,
        OAuth2AccessTokenRepository.class
})
public class MongoDbConfiguration extends AbstractMongoConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(MongoDbConfiguration.class);
    private static final String MONGO_DB_SERVER = "mongo.db.server";
    private static final String MONGO_DB_PORT = "mongo.db.port";
    private static final String MONGO_DB_NAME = "mongo.db.name";
    private static final String MONGO_DB_LOGON = "mongo.db.logon";
    private static final String MONGO_DB_PASSWORD = "mongo.db.password";
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${" + MONGO_DB_SERVER + "}")
    private String mongoServer;
    @Value("${" + MONGO_DB_PORT + "}")
    private int mongoPort;
    @Value("${" + MONGO_DB_NAME + "}")
    private String mongoDBName;
    @Value("${" + MONGO_DB_LOGON + "}")
    private String mongoDbLogin;
    @Value("${" + MONGO_DB_PASSWORD + "}")
    private String mongoDbPassword;

    @Override
    protected String getDatabaseName() {
        return mongoDBName;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new Mongo(mongoServer, mongoPort);
    }

    @Override
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        if (!StringUtils.isEmpty(mongoDbLogin)) {
            LOG.info("Configuring mongoTemplate with credentials.");
            MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongo(), mongoDBName, new UserCredentials(mongoDbLogin, mongoDbPassword));
            return new MongoTemplate(mongoDbFactory, mappingMongoConverter());
        } else {
            LOG.info("Configuring mongoTemplate without credentials.");
            MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongo(), mongoDBName);
            return new MongoTemplate(mongoDbFactory, mappingMongoConverter());
        }
    }


    @Override
    @Bean
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        OAuth2AuthenticationReadConverter converter = new OAuth2AuthenticationReadConverter();
        converterList.add(converter);
        return new CustomConversions(converterList);
    }



    private String getContextProperty(final String propertyKey) {
        return applicationContext.getEnvironment().getProperty(propertyKey);
    }
}