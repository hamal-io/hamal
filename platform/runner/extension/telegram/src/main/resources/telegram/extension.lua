function extension_create()
    local http = require_plugin('net.http')
    local export = { }

    function export.create(cfg)
        local instance = {
            message = { },
            webhook = { }
        }

        local base_url = cfg.base_url or 'https://api.telegram.org'
        local bot_token = cfg.bot_token or error('bot_token has to be set')

        function instance.message.send(cmd)
            cmd = cmd or {}
            local err, resp = http.post({
                url = base_url .. '/bot' .. cfg.bot_token .. '/sendMessage',
                json = {
                    ['chat_id'] = cmd.chat_id,
                    ['text'] = cmd.text,
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

        function instance.webhook.set(cmd)
            cmd = cmd or {}
            local err, resp = http.post({
                url = base_url .. '/bot' .. bot_token .. '/setWebhook',
                json = {
                    ['url'] = cmd.url
                }
            })
            return err, resp.content
        end

    end

    return export
end