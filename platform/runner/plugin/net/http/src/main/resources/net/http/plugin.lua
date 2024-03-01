function plugin_factory_create()
    local internal = _internal

    return function()
        local export = {
            requests = { }
        }

        function export.execute(requests)
            return internal.execute(requests)
        end

        function export.requests.get(req)
            return {
                method = "GET",
                url = req.url or error('url not set'),
                headers = req.headers or {}
            }
        end

        function export.requests.post(req)
            return {
                method = "POST",
                url = req.url or error('url not set'),
                headers = req.headers or {},
                json = req.json
            }
        end

        function export.requests.patch(req)
            return {
                method = "PATCH",
                url = req.url or error('url not set'),
                headers = req.headers or {},
                json = req.json
            }
        end

        function export.requests.put(req)
            return {
                method = "PUT",
                url = req.url or error('url not set'),
                headers = req.headers or {},
                json = req.json
            }
        end

        function export.requests.delete(req)
            return {
                method = "DELETE",
                url = req.url or error('url not set'),
                headers = req.headers or {}
            }
        end

        return export
    end
end