function extension_create()
    local export = { }

    function export.create(cfg)
        local plugin = require_plugin('net.http')

        local cfg = cfg or {}
        local base_url = cfg.base_url or ''

        local instance = {
            requests = { }
        }

        local function single_request(request)
            local err, responses = plugin.execute({ request })

            if err ~= nil then
                return err, nil
            end

            assert(#responses == 1)
            return nil, responses[1]
        end

        function instance.execute(requests)
            return plugin.execute(requests)
        end

        function instance.requests.get(req)
            return plugin.requests.get({
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                produces = "JSON",
                consumes = "JSON"
            })
        end

        function instance.get(req)
            return single_request(instance.requests.get(req))
        end

        function instance.requests.post(req)
            return plugin.requests.post({
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                body = req.body,
                produces = "JSON",
                consumes = "JSON"
            })
        end

        function instance.post(req)
            return single_request(instance.requests.post(req))
        end

        function instance.requests.put(req)
            return plugin.requests.put({
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                body = req.body,
                produces = "JSON",
                consumes = "JSON"
            })
        end

        function instance.put(req)
            return single_request(instance.requests.put(req))
        end

        function instance.requests.patch(req)
            return plugin.requests.patch({
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                body = req.body,
                produces = "JSON",
                consumes = "JSON"
            })
        end

        function instance.patch(req)
            return single_request(instance.requests.patch(req))
        end

        function instance.requests.delete(req)
            return plugin.requests.delete({
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                produces = "JSON",
                consumes = "JSON"
            })
        end

        function instance.delete(req)
            return single_request(instance.requests.delete(req))
        end

        return instance
    end

    return export
end