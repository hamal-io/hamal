-- As a workspace admin, I can access my own state

http = require('net.http').create({
    base_url = context.env.test_api
})

err, res = http.get({
    url = '/v1/funcs/1/states/correlationId',
    headers = { Authorization = 'Bearer ' .. context.env.token }
})
assert(err == nil)
assert(res.status_code == 200)
assert(res.content_type == 'application/json;charset=UTF-8')

correlated_state = res.content

assert(correlated_state.correlation.id == 'correlationId')
assert(correlated_state.correlation.func.id == '1')
assert(correlated_state.state.value == 1337)
