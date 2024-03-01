function extension_create()
    local http_plugin = require_plugin('net.http')
    local export = { }

    function export.create(cfg)
        local cfg = cfg or {}
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

        function instance.execute(requests)
            return http_plugin.execute(requests)
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

        function instance.requests.post(req)
            return {
                method = "POST",
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                json = req.json
            }
        end

        function instance.post(req)
            return single_request(instance.requests.post(req))
        end

        function instance.requests.put(req)
            return {
                method = "PUT",
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                json = req.json
            }
        end

        function instance.put(req)
            return single_request(instance.requests.put(req))
        end

        function instance.requests.patch(req)
            return {
                method = "PATCH",
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
                json = req.json
            }
        end

        function instance.patch(req)
            return single_request(instance.requests.patch(req))
        end

        function instance.requests.delete(req)
            return {
                method = "DELETE",
                url = base_url .. (req.url or ''),
                headers = req.headers or {},
            }
        end

        function instance.delete(req)
            return single_request(instance.requests.delete(req))
        end

        return instance
    end

    return export
end