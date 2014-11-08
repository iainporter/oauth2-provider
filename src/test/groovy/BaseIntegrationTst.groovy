import org.apache.commons.lang.RandomStringUtils
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

class BaseIntegrationTst extends GroovyTestCase {

    private def BASE_URL = "http://localhost:8080/oauth2-provider/"
    protected def USER_PATH = "v1.0/users"
    protected def ME_PATH = "v1.0/me"
    private def OAUTH_TOKEN_PATH = "oauth/token"
    private def restClient;
    def TEST_PASSWORD = "password123"
    def BASIC_AUTH_TOKEN = 'MzUzYjMwMmM0NDU3NGY1NjUwNDU2ODdlNTM0ZTdkNmE6Mjg2OTI0Njk3ZTYxNWE2NzJhNjQ2YTQ5MzU0NTY0NmM='
    def static SUCCESS = 200

    def SignUp registration = new SignUp(getRestClient(), USER_PATH)

    RESTClient getRestClient() {
        if (restClient == null)
            restClient = new RESTClient(BASE_URL);
        return restClient;
    }

    protected String getNoRootUserRequest(String username, String password) {
        return """{"userName":""" + '\"' + username + '\"' + ""","password":""" + '\"' + password + '\"' + """}"""
    }

    protected String createRandomUserName() {
        return RandomStringUtils.randomAlphabetic(8) + "@example.com";
    }

    protected String getUpdateUserRequest(String firstName, String lastName, String emailAddress) {
        return """{"firstName": """ + '\"' + firstName + '\"' + ""","lastName":""" + '\"' + lastName + '\"' + ""","emailAddress":""" + '\"' + emailAddress + '\"' + """}"""
    }

    protected Object httpSignUpUser(def jsonPayload) {
        System.out.println("payload" + jsonPayload)
        return getRestClient().post(path: USER_PATH, contentType: ContentType.JSON, headers: ['Authorization': 'Basic ' + BASIC_AUTH_TOKEN], body: jsonPayload)
    }

    protected Object httpGetAuthToken(String username, String password) {
        return getRestClient().post(path: OAUTH_TOKEN_PATH, contentType: ContentType.JSON, headers: ['Authorization': 'Basic ' + BASIC_AUTH_TOKEN], query: ['grant_type': 'password', 'username': username, 'password': password])
    }

    protected String getCreateUserRequest(String username, String password) {
        return "{\"user\":{" + getJsonNameValue("emailAddress", username) + "}," + getJsonNameValue("password", password) + "}"
    }

    protected Object httpGetUser(def authToken, def userId) {
        def path = USER_PATH + "/" + userId
        return getRestClient().get(path: path, contentType: ContentType.JSON, headers: ['Authorization': "Bearer " + authToken])
    }

    protected Object httpUpdateUser(def authToken, def userId, def payload) {
        def path = USER_PATH + "/" + userId
        return getRestClient().put(path: path, contentType: ContentType.JSON, headers: ['Authorization': "Bearer " + authToken], body: payload)
    }

    protected Object httpGetMe(def authToken) {
        def path = ME_PATH
        return getRestClient().get(path: ME_PATH, contentType: ContentType.JSON, headers: ['Authorization': "Bearer " + authToken])
    }

    protected Object httpGetSample(def authToken) {
        return getRestClient().get(path: "v1.0/samples", contentType: ContentType.JSON, headers: ['Authorization': "Bearer " + authToken])
    }

    private String getJsonNameValue(String name, Object value) {
        return '\"' + name + '\":\"' + value + '\"'
    }


    static void assertHttpResponseSuccess(def response) {
        assertEquals SUCCESS, response.status
    }

    static void assertResponseEqualsValue(def response, def field, def value) {
        assertTrue(response.responseData[field] == value)
    }
}