local http = require_plugin 'net.http'
local config = http.config.get()
assert(config.base_url ~= nil)