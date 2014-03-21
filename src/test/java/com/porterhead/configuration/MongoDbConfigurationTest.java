package com.porterhead.configuration;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ActiveProfiles("default")
@ContextConfiguration(classes = {PropertiesConfiguration.class, MongoDbConfiguration.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class MongoDbConfigurationTest {

    static {
        configureJavaArguments();
    }

    private static final String PROPERTY_OVERRIDE_KEY = "PROPERTY_FILE_LOCATION";
    private static final String PROPERTY_OVERRIDE_FILE = "properties/app-default.properties";

    @Value("${mongo.db.server}")
    private String mongoDbServer;
    @Value("${mongo.db.port}")
    private int mongoDbPort;
    @Value("${mongo.db.name}")
    private String mongoDbName;
    @Value("${mongo.db.logon}")
    private String mongoUserCollectionLogon;
    @Value("${mongo.db.password}")
    private String mongoUserCollectionPassword;

    @Autowired
    private Environment environment;
    private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void mongoTemplateIsValid() {
        MongoTemplate mongoTemplate = applicationContext.getBean(MongoTemplate.class);
        assertThat(mongoTemplate, is(not(Matchers.<Object>nullValue())));
    }

    public static void configureJavaArguments() {
        String path = MongoDbConfigurationTest.class.getClassLoader().getResource("").getPath();
        System.setProperty(PROPERTY_OVERRIDE_KEY, path + PROPERTY_OVERRIDE_FILE);
    }

    @Test
    public void defaultValues() {
        if (environment.getProperty(SPRING_PROFILES_ACTIVE) == null) {
            assertThat(mongoDbServer, is("localhost"));
            assertThat(mongoDbPort, is(37017));
            assertThat(mongoDbName, is("oauth-provider"));
            assertThat(mongoUserCollectionLogon, is(""));
            assertThat(mongoUserCollectionPassword, is(""));
        } else if (environment.getProperty(SPRING_PROFILES_ACTIVE).equals("dev")) {
            assertThat(mongoDbServer, is("localhost"));
            assertThat(mongoDbPort, is(37017));
            assertThat(mongoDbName, is("oauth-provider"));
            assertThat(mongoUserCollectionLogon, is(""));
            assertThat(mongoUserCollectionPassword, is(""));
        } else if (environment.getProperty(SPRING_PROFILES_ACTIVE).equals("local")) {
            assertThat(mongoDbServer, is("localhost"));
            assertThat(mongoDbPort, is(27017));
            assertThat(mongoDbName, is("oauth-provider"));
            assertThat(mongoUserCollectionLogon, notNullValue());
            assertThat(mongoUserCollectionPassword, notNullValue());
        }
    }

    @Test
    public void testActiveProfile() {
        for (String profile : environment.getActiveProfiles()) {
            assertThat(profile, is("default"));
        }
    }

    @Test
    public void testJavaParameter() {

        String propertyOverrideFileName = environment.getProperty(PROPERTY_OVERRIDE_KEY);
        assertThat(propertyOverrideFileName, notNullValue());
        assertThat(propertyOverrideFileName, containsString(PROPERTY_OVERRIDE_FILE));
    }
}
