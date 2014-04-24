import org.apache.commons.lang.RandomStringUtils
import groovyx.net.http.ContentType

class SignUp {

    def TEST_PASSWORD = "password123"
    def path
    def restClient
    def authToken

    protected SignUp(def restClient, def path) {
        this.path = path
        this.restClient = restClient
    }

    protected SignUp(def restClient, def path, def authToken) {
        this.path = path
        this.restClient = restClient
        this.authToken = authToken
    }
    
    public String createRandomUserName() {
        return RandomStringUtils.randomAlphabetic(8) + "@example.com";
    }

    public String createUserRequest(def username, def password) {
        return "{\"user\":{"  \
          + getJsonNameValue("emailAddress", username)  \
           + ","  \
             + getJsonNameValue("password", password) + "}"
    }

    public String getJsonNameValue(def name, def value) {
        return '\"' + name + '\":\"' + value + '\"'
    }

    public AuthCredentials httpSignUp() {
        httpSignUp(authToken)
    }
        
    public AuthCredentials httpSignUp(def authToken) {
        def payload = createUserRequest(createRandomUserName(), TEST_PASSWORD)
        def signUpResponse = restClient.post(
                path: path,
                contentType: ContentType.JSON,
                headers: ['Authorization': 'Basic ' + authToken],
                body: payload)

        return new AuthCredentials() {
            @Override
            def getUserId() {
                return signUpResponse.responseData["apiUser"]["id"]
            }

            @Override
            def getAuthToken() {
                return signUpResponse.responseData["oauth2AccessToken"]["access_token"]
            }
        }
    }
}