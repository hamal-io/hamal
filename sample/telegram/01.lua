tg = require('telegram')

tg.config.update({
    base_url = 'https://api.telegram.org', -- should be set by default
    token = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'
})

local err, resp = tg.webhook.info()
for k,v in pairs(resp) do print(k,v) end


tg.message.send({
    chat_id = '-XXXXXXXXXXXX',
    text = 'Invoked via adhoc function'
})