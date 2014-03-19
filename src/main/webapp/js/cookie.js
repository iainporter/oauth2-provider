/**
 * Holds cookie methods
 */
oauth2.cookie = {}

/**
 * Get the value of a cookie.
 * @param {string}
 * @return {string}
 */
oauth2.cookie.get = function (name) {
  var pairs = document.cookie.split(/\; /g)
  var cookie = {}
  for (var i in pairs) {
    var parts = pairs[i].split(/\=/)
    cookie[parts[0]] = unescape(parts[1])
  }
  return cookie[name]
}

/**
 * Delete a cookie
 * @param {string}
 */
oauth2.cookie.remove = function (name) {
  document.cookie = name + '=; expires=Thu, 01 Jan 1970 00:00:01 GMT;'
}

/**
 * Set a cookie
 * @param {string}
 * @param {string}
 */
oauth2.cookie.set = function (name, value) {
  // document.cookie = "name=value[; expires=UTCString][; domain=domainName][; path=pathName][; secure]"; 
  document.cookie = name + '=' + value;
}

