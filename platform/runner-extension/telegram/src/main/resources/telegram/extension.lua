function extension()
    local http = require_plugin('net.http')

    return function()
        local export = {
            message = { },
            webhook = { }
        }

        function export.message.send(message)
            local cfg = export.config.get()

            local err, resp = http.post({
                url = cfg.base_url .. '/bot' .. cfg.bot_token .. '/sendMessage',
                json = {
                    ['chat_id'] = message.chat_id,
                    ['text'] = message.text,
                    ['disable_notification'] = true,
                    ['disable_web_page_preview'] = true,
                    ['protect_content'] = true
                }
            })

            print(err)
            print(resp)

            for k, v in pairs(resp.content) do
                print(k, v)
            end

            return err, resp.content
        end

        function export.webhook.info()
            local cfg = export.config.get()
            local err, resp = http.post({
                url = cfg.base_url .. '/bot' .. cfg.bot_token .. '/getWebhookInfo'
            })

            print(err)
            print(res)

            for k, v in pairs(resp.content) do
                print(k, v)
            end

            return err, resp.content
        end

        return export
    end
end