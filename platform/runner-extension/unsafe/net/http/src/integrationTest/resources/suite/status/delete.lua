local http = require('net.http')

local status_codes = { 200, 201, 400, 404, 500 }

for _, status_code in ipairs(status_codes) do
    local err, response = http.delete("/v1/status?code=" .. status_code)
    assert(err == nil)
    assert(response ~= nil)
    assert(response.status_code == status_code)
end