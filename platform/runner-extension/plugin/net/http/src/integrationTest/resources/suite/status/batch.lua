local http = require('net.http')

local status_codes = { 200, 201, 400, 404, 500 }

for _, status_code in ipairs(status_codes) do
    local err, response = http.execute({
        http.requests.get("/v1/status?code=" .. status_code),
        http.requests.post("/v1/status?code=" .. status_code),
        http.requests.patch("/v1/status?code=" .. status_code),
        http.requests.put("/v1/status?code=" .. status_code),
        http.requests.delete("/v1/status?code=" .. status_code),
    })

    assert(err == nil)
    assert(response ~= nil)
    assert(#response == 5)

    for idx = 1, 5 do
        assert(response[idx].status_code == status_code)
    end

end