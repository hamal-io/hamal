function extension()
    local http_plugin = require_plugin('net.http')
    return function()
        local export = { }

        function export.create(cfg)
            local base_url = cfg.base_url or ''

            local instance = {
                requests = { }
            }

            local function single_request(request)
                local err, responses = http_plugin.execute({ request })

                if err ~= nil then
                    return err, nil
                end

                assert(#responses == 1)
                return nil, responses[1]
            end

            function instance.requests.get(req)
                return {
                    method = "GET",
                    url = base_url .. (req.url or ''),
                    headers = req.headers or {}
                }
            end

            function instance.get(req)
                return single_request(instance.requests.get(req))
            end

            return instance
        end

        return export
    end
end