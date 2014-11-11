oauth2.user = {}


/**
 * Create a user
 *
 * @param {string}
 * @param {string}
 * @param {string}
 * @param {string}
 * @param {function}
 */
oauth2.user.create = function (firstName, emailAddress, password, lastName, callback) {


  oauth2.postClientAuth(
    'v1.0/users',
    {user :
      {
        "firstName" : firstName,
        "emailAddress" : emailAddress
      },
    "password" : password
    },
    function (response) {
      oauth2.cookie.set('authToken', response.oauth2AccessToken.access_token)
      oauth2.cookie.set('userId', response.apiUser.id)
      oauth2.cookie.set('email', emailAddress)
      callback()
    },
    function(jqXHR, textStatus) {
      console.log(jqXHR)
      callback(jqXHR)
    })

}

oauth2.clientAuth = function (clientId, secret) {
  var hash = [clientId + ':' + secret]
  return CryptoJS.enc.Base64.stringify(hash)
}


/**
 * Get user info
 * @param {function}
 */
oauth2.user.download = function (callback) {

  oauth2.get(
    'v1.0/me',
    {},
    function (response) {
      console.log(response)
      // If the cached version is the same as the most recent
      // version, just return. Else, we will run the callback.
      if (store.get('userResponse') === JSON.stringify(response)) {
        console.log('cached')
        return false
      }


      store.set('userResponse', JSON.stringify(response))

      oauth2.user.user = response

      if (callback)
        callback()
    },
    function(jqXHR, textStatus) {
      if (callback)
        callback(jqXHR)
    })

}

/**
 * Get user info
 * @param {function}
 */
oauth2.user.get = function (callback) {


  var userResponse = store.get('userResponse')


  if (userResponse) {
    var response = JSON.parse(userResponse)
    oauth2.user.user = response.user
    // We still download the latest data in the background to make sure
    // cache is current. But we return immediately.
    oauth2.user.download(callback)
    return callback()
  }


  oauth2.user.download(callback)

}

/**
 * @return {bool}
 */
oauth2.user.is_logged_in = function () {
  return !!oauth2.cookie.get('authToken')
}

/**
 * Log the user in
 * @param {string}
 * @param {string}
 * @param {function} Callback. First parameter is error, if any.
 */
oauth2.user.login = function (email, password, callback) {

  oauth2.login(
    'oauth/token',
      {
      "username" : email,
      "password" : password,
      "grant_type" : "password"
      },
    function (response) {
        console.log(response)
      oauth2.cookie.set('authToken', response.access_token)
      //oauth2.cookie.set('userId', response.apiUser.id)
      oauth2.cookie.set('email', email)
      callback()

    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })

}



/**
 * Delete the users cookies.
 */
oauth2.user.logout = function () {
  oauth2.cookie.remove('authToken')
  oauth2.cookie.remove('userId')
  oauth2.cookie.remove('email')
  store.clear()
  window.location = 'index.html'
}

/**
 * Delete the users cookies.
 */
oauth2.user.reset_password = function (token, password, callback) {
  oauth2.post(
    'v1.0/password/tokens/' + token,
      {
      "password" : password
      },
    function (response) {
      callback()
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}

/**
 * Delete the users cookies.
 */
oauth2.user.send_reset_email = function (email, callback) {
  oauth2.post(
    'v1.0/password/tokens',
      {
      "emailAddress" : email
      },
    function (response) {
      callback()

    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}



/**
 * Update first name
 * @param {function}
 */
oauth2.user.updateName = function (value, callback) {

  oauth2.put(
    'v1.0/users/' + oauth2.cookie.get('userId'),
      {
      "emailAddress" : oauth2.cookie.get('email'),
      "firstName" : value
      },
    function (response) {
      console.log(response)
      if (callback)
        callback()
      // Clear user cache
      oauth2.user.download()
    },
    function(jqXHR, textStatus) {
      if (callback)
        callback(jqXHR)
      // Clear user cache
      oauth2.user.download()
    })
}

