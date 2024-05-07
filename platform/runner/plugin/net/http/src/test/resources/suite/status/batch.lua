local http = require_plugin('net.http')

local status_codes = { 200, 201, 400, 404, 500 }

local url = context.env.test_url .. '/v1/status'

for _, status_code in ipairs(status_codes) do
    local err, response = http.execute({
        http.requests.get({ url = url .. "?code=" .. status_code, produces = "JSON", consumes = "JSON" }),
        http.requests.post({ url = url .. "?code=" .. status_code, produces = "JSON", consumes = "JSON" }),
        http.requests.patch({ url = url .. "?code=" .. status_code, produces = "JSON", consumes = "JSON" }),
        http.requests.put({ url = url .. "?code=" .. status_code, produces = "JSON", consumes = "JSON" }),
        http.requests.delete({ url = url .. "?code=" .. status_code, produces = "JSON", consumes = "JSON" }),
    })

    assert(err == nil)
    assert(response ~= nil)
    assert(#response == 5)

    for idx = 1, 5 do
        assert(response[idx].status_code == status_code)
    end

end