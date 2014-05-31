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

