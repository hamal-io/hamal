local http = require('net.http')

local err, response = http.execute({
    http.requests.get("/v1/status?code=204"),
    http.requests.post("/v1/status?code=204"),
    http.requests.patch("/v1/status?code=204"),
    http.requests.put("/v1/status?code=204"),
    http.requests.delete("/v1/status?code=204"),
})

assert(err == nil)
assert(response ~= nil)
assert(#response == 5)

for idx = 1, 5 do
    assert(response[idx].content == nil)
end
