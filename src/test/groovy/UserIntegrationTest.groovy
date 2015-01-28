import com.porterhead.user.api.ApiUser
import org.apache.commons.lang.RandomStringUtils

import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.is
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertThat
import static org.junit.Assert.assertThat

class UserIntegrationTest extends BaseIntegrationTst {

    
    
    public void testSignUpUser() {
        def signupResponse = httpSignUpUser(getCreateUserRequest(createRandomUserName(), TEST_PASSWORD))
        assertEquals(201, signupResponse.status)
        assertTrue(signupResponse.responseData["oauth2AccessToken"] != null)
        assertTrue(signupResponse.responseData["apiUser"] != null)
    }


    public void testInvalidRequest() {
        try {
            httpSignUpUser(getNoRootUserRequest(createRandomUserName(), TEST_PASSWORD))
            fail("Expected 400 response")
        } catch (Exception e) {
            assertEquals(400, e.response.status)
        }
    }

    public void testUsernameCase() {
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = httpGetAuthToken(username.toUpperCase(), TEST_PASSWORD)
        assertEquals(200, loginResponse.status)
        assertTrue(loginResponse.responseData["access_token"] != null)
    }

    public void testPasswordTooShort() {
        try {
            httpSignUpUser(getCreateUserRequest(createRandomUserName(), "1234"))
            fail("Expected 400 response")
        } catch (Exception e) {
            assertEquals(400, e.response.status)
        }
    }

    public void testUsernameAlreadyExists() {
        try {
            String username = createRandomUserName();
            httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
            httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
            fail("Expected 409 response")
        } catch (Exception e) {
            assertEquals(409, e.response.status)
        }
    }

    public void testLogin() {
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = httpGetAuthToken(username, TEST_PASSWORD)
        assertEquals(200, loginResponse.status)
        assertTrue(loginResponse.responseData["access_token"] != null)
        assertTrue(loginResponse.responseData["token_type"].equals('bearer'))
        assertTrue(loginResponse.responseData["refresh_token"] != null)
        assertTrue(loginResponse.responseData["expires_in"] != null)
    }

    public void testLoginWithSpecialCharacters() {
        def emailAddress = RandomStringUtils.randomAlphanumeric(3) + "+" + RandomStringUtils.randomAlphanumeric(3) + "@example.com"
        def password = "@34 !?1123++"
        httpSignUpUser(getCreateUserRequest(emailAddress, password))
        def loginResponse = httpGetAuthToken(emailAddress, password)
        assertEquals(200, loginResponse.status)
        assertTrue(loginResponse.responseData["access_token"] != null)
        assertTrue(loginResponse.responseData["token_type"].equals('bearer'))
        assertTrue(loginResponse.responseData["refresh_token"] != null)
        assertTrue(loginResponse.responseData["expires_in"] != null)
    }

    public void testUserDoesNotExist() {
        try {
            def username = createRandomUserName()
            httpGetAuthToken(username, TEST_PASSWORD)
            fail("Expected 400 response")
        } catch (Exception e) {
            assertEquals(400, e.response.status)
        }
    }

    public void testInvalidPasswordOnLogin() {
        try {
            def username = createRandomUserName()
            httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
            httpGetAuthToken(username, 'WRONG_PASSWORD')
            fail("Expected 400 response")
        } catch (Exception e) {
            assertEquals(400, e.response.status)
        }
    }

    public void testGetUserByEmailAddress() {
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = httpGetAuthToken(username, TEST_PASSWORD)
        def getUserResponse = httpGetUser(loginResponse.responseData["access_token"], username)
        assertEquals(200, getUserResponse.status)
        assertTrue(getUserResponse.responseData["emailAddress"].equals(username.toLowerCase()))
    }

     public void testMe() {
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = httpGetAuthToken(username, TEST_PASSWORD)
        def getUserResponse = httpGetMe(loginResponse.responseData["access_token"])
        assertEquals(200, getUserResponse.status)
        assertTrue(getUserResponse.responseData["emailAddress"].equals(username.toLowerCase()))
    }

    public void testUpdateUser() {
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = httpGetAuthToken(username, TEST_PASSWORD)
        def getUserResponse = httpGetMe(loginResponse.responseData["access_token"])

        def updatedUserName = createRandomUserName();
        def updateRequest = getUpdateUserRequest("FOO", "BAR", updatedUserName)
        def updateUserResponse = httpUpdateUser(loginResponse.responseData["access_token"], getUserResponse.responseData["id"], updateRequest)
        assertEquals(200, updateUserResponse.status)
        getUserResponse = httpGetUser(loginResponse.responseData["access_token"], getUserResponse.responseData["id"])
        assertEquals(200, getUserResponse.status)
        assertThat(getUserResponse.responseData["firstName"], is("FOO"))
        assertThat(getUserResponse.responseData["lastName"], is("BAR"))
        assertThat(getUserResponse.responseData["emailAddress"], is(updatedUserName))
    }

    public void testUpdateRefreshesMeResource() {
        def username = createRandomUserName()
        httpSignUpUser(getCreateUserRequest(username, TEST_PASSWORD))
        def loginResponse = httpGetAuthToken(username, TEST_PASSWORD)
        def getUserResponse = httpGetMe(loginResponse.responseData["access_token"])

        def updatedUserName = createRandomUserName();
        def updateRequest = getUpdateUserRequest("FOO", "BAR", updatedUserName)
        httpUpdateUser(loginResponse.responseData["access_token"], getUserResponse.responseData["id"], updateRequest)
        def getMeResponse = httpGetMe(loginResponse.responseData["access_token"])
        assertTrue(getMeResponse.responseData["emailAddress"].equals(updatedUserName))
        assertTrue(getMeResponse.responseData["firstName"].equals("FOO"))
        assertTrue(getMeResponse.responseData["lastName"].equals("BAR"))


    }
}