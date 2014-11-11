/**
 * Singleton used for Namespace
 */
function oauth2() {
  
}

/**
 * Wrap the API so we can proxy calls while testing.
 */
oauth2.get = function (url, data, success, error) {
  
  var authorization = oauth2.cookie.get('authToken')
  
  var request = $.ajax({
    url: url,
    type: "GET",
    data: data,
    headers: {
      'Authorization' : 'Bearer ' + authorization
    },
    dataType: "json"
  })

  request.done(success)

  request.fail(error)
  
}


/**
 * Get a query string var
 * @param {string}
 * @return {string}
 */
oauth2.get_query = function (name) {
  var query = window.location.search.substring(1)
  var vars = query.split('&')
  for (var i = 0; i < vars.length; i++) {
      var pair = vars[i].split('=')
      if (decodeURIComponent(pair[0]) == name) {
          return decodeURIComponent(pair[1])
      }
  }
}


/**
 * Is the visitor on iPhone or Ipad?
 * @return {bool}
 */
oauth2.isIos = function () {
  return (navigator.userAgent.match(/iPad|iPhone|iPod/i) != null)
}

/**
 * Wrap the API so we can proxy calls while testing.
 */
oauth2.post = function (url, data, success, error) {
  
  $.ajax({
    url: url,
    type: "POST",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    dataType: "json",
    success : success,
    error : error
  })

  
}

/**
 * Post with authentication
 */
oauth2.postAuth = function (url, data, success, error) {
  
  var authorization = oauth2.cookie.get('authToken')
  
  $.ajax({
    url: url,
    type: "POST",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    headers: {
      'Authorization' : 'Bearer ' + authorization
    },
    dataType: "json",
    success : success,
    error : error
  })

  
}

/**
 * Post with client basic authentication
 */
oauth2.postClientAuth = function (url, data, success, error) {

    var authorization = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse('353b302c44574f565045687e534e7d6a' + ':' + '286924697e615a672a646a493545646c'));

  $.ajax({
    url: url,
    type: "POST",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    headers: {
      'Authorization' : 'Basic ' + authorization
    },
    dataType: "json",
    success : success,
    error : error
  })


}

/**
 * Post with client basic authentication
 */
oauth2.login = function (url, data, success, error) {

  var authorization =  CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse('353b302c44574f565045687e534e7d6a' + ':' + '286924697e615a672a646a493545646c'));
  $.ajax({
    url: url,
    type: "POST",
    accept: "application/json",
    data: data,
    headers: {
      'Authorization' :'Basic ' + authorization
    },
    dataType: "json",
    success : success,
    error : error
  })


}

/**
 * Wrap the API so we can proxy calls while testing.
 */
oauth2.put = function (url, data, success, error) {
  
    var authorization = oauth2.cookie.get('authToken')

  $.ajax({
    url: url,
    type: "PUT",
    contentType: "application/json", // send as JSON
    data: JSON.stringify(data),
    headers: {
      'Authorization' : 'Bearer ' + authorization
    },
    dataType: "json",
    success : success,
    error : error
  })

  
}


