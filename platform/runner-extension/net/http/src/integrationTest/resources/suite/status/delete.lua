local http = require('net.http')

local config = http.config.get()
local port = config.port

local status_codes = { 200, 201, 400, 404, 500 }

for _, status_code in ipairs(status_codes) do
    local err, response = http.delete("http://localhost:" .. port .. "/v1/status?code=" .. status_code)
    assert(err == nil)
    assert(response ~= nil)
    assert(response.statusCode == status_code)
end