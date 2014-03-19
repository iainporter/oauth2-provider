oauth2.verify = {}

/**
 * Sends an email to user for verification
 */
oauth2.verify.request_email = function (email, callback) {
  oauth2.post(
    'v1.0/verify/tokens',
    {
      'emailAddress' : email
    },
    function (response) {
      console.log(response)
      callback()
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}

/**
 * Validate an email address.
 */
oauth2.verify.verify = function (token, callback) {
  oauth2.post(
    'v1.0/verify/tokens/' + token,
    {},
    function (response) {
      callback()
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}

