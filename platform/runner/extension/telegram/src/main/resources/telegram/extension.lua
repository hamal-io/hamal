function extension_create()
    local export = { }

    function export.create(cfg)
        local http = require('net.http').create({
            base_url = cfg.base_url or 'https://api.telegram.org',
        })
        local bot_token = cfg.bot_token or error('bot_token has to be set')

        local instance = {}

        function instance.send_message(cmd)
            cmd = cmd or {}
            local err, resp = http.post({
                url = '/bot' .. bot_token .. '/sendMessage',
                json = {
                    ['chat_id'] = cmd.chat_id,
                    ['text'] = cmd.text,
                    ['disable_notification'] = true,
                    ['disable_web_page_preview'] = true,
                    ['protect_content'] = true
                }
            })

            -- FIXME validate response

            return err, resp.content
        end

        --function instance.webhook_info()
        --    local err, resp = http.post({
        --        url = base_url .. '/bot' .. bot_token .. '/getWebhookInfo'
        --    })
        --    return err, resp.content
        --end
        --
        --function instance.set_webhook(cmd)
        --    cmd = cmd or {}
        --    local err, resp = http.post({
        --        url = base_url .. '/bot' .. bot_token .. '/setWebhook',
        --        json = {
        --            ['url'] = cmd.url
        --        }
        --    })
        --    return err, resp.content
        --end

        return instance
    end

    return export
end