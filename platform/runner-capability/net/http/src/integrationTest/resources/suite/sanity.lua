local http = require 'net.http'
local config = http.config.get()
assert(config.port ~= nil)