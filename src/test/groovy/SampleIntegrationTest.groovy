/**
 * Created by iainporter on 14/10/2014.
 */
class SampleIntegrationTest extends BaseIntegrationTst {

    public void testHierarchicalRole() {
        //sign up a user with role of ROLE_USER
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        //login and get the oauth token
        def loginResponse = httpGetAuthToken(username, TEST_PASSWORD)
        //get the resource that requires a role of ROLE_GUEST
        def sampleResponse = httpGetSample(loginResponse.responseData["access_token"])
        assertEquals(200, sampleResponse.status)
        assertTrue(sampleResponse.responseData["message"].equals("You are authorized to access"))
    }
}
