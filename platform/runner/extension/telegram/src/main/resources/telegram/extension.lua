function extension_factory_create()
    local http = require_plugin('net.http')
    return function()
        local export = { }

        function export.create(cfg)
            local instance = {
                message = { },
                webhook = { }
            }

            local base_url = cfg.base_url or 'https://api.telegram.org'
            local bot_token = cfg.bot_token or error('bot_token has to be set')

            function instance.message.send(message)
                local err, resp = http.post({
                    url = base_url .. '/bot' .. cfg.bot_token .. '/sendMessage',
                    json = {
                        ['chat_id'] = message.chat_id,
                        ['text'] = message.text,
                        ['disable_notification'] = true,
                        ['disable_web_page_preview'] = true,
                        ['protect_content'] = true
                    }
                })

                return err, resp.content
            end

            function instance.webhook.info()
                local err, resp = http.post({
                    url = base_url .. '/bot' .. bot_token .. '/getWebhookInfo'
                })
                return err, resp.content
            end

            return instance
        end

        return export
    end
end