A sample OAuth provider using spring security
See <a href="http://porterhead.blogspot.co.uk/2014/05/securing-rest-services-with-spring.html">http://porterhead.blogspot.co.uk/2014/05/securing-rest-services-with-spring.html</a>


prerequisites: a running mongoDB instance on port 27017

to build: gradle clean build

to run: gradle tomcatRun

To register a new user:

 curl -X POST -H "Content-Type: application/json" \
 -H "Authorization: Basic MzUzYjMwMmM0NDU3NGY1NjUwNDU2ODdlNTM0ZTdkNmE6Mjg2OTI0Njk3ZTYxNWE2NzJhNjQ2YTQ5MzU0NTY0NmM=" \
  -d '{"user":{"emailAddress":"test@example.com"}, "password":"password"}' \
  'http://localhost:8080/oauth2-provider/v1.0/users'

Login:

curl -v -X POST \
-H "Content-Type: application/json" \
-H "Authorization: Basic MzUzYjMwMmM0NDU3NGY1NjUwNDU2ODdlNTM0ZTdkNmE6Mjg2OTI0Njk3ZTYxNWE2NzJhNjQ2YTQ5MzU0NTY0NmM=" \
'http://localhost:8080/oauth2-provider/oauth/token?grant_type=password&username=test@example.com&password=password'

to retrieve a user details:

curl -v -X GET \
-H "Content-Type: application/json" \
-H "Authorization: Bearer [your access token]" \
'http://localhost:8080/oauth2-provider/v1.0/me'

To set up for production:

1. Create a properties file in src/main/resources/properties called app-production.properties
2. Override all relevant properties from src/main/resources/properties/application.properties
3. Configure Mail Server by overriding values for mail server in app-production.properties
4. Build and deploy war. Add System variable to start up command -Dspring.profiles.active=production
5. Add MongoDB args to start up command-  -DMONGODB_HOST=localhost -DMONGODB_PORT=27017
Example command line args for using built-in tomcat instance:  ./gradlew tomcatRun -Dspring.profiles.active=production -DMONGODB_HOST=localhost -DMONGODB_PORT=27017
5. Restrict access to all protected URLs via SSL only
6. The velocity templates and html pages provided are basic and should be customised to your needs



