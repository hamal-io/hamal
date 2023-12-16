function extension()
    local email_plugin = require_plugin('net.smtp')
    return function()
        local export = { }

        function export.create(cfg)
            local cfg = cfg or { }

            local instance = { }

            function instance.send(mail)
                local mail = mail or { }

            end

            return instance
        end
        return export
    end
end