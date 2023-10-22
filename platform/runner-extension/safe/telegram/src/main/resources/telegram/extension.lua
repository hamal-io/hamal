function extension()
    local http = require('net.http')

    return function()
        local export = {
            webhook = { }
        }

        function export.test(message)
            print('about to send message to telegram: ' .. message)
        end

        function export.webhook.get_info()
            err, res = http.post('https://api.telegram.org/bot................../getWebhookInfo')
            print(err)
            print(res)
        end

        return export
    end
end