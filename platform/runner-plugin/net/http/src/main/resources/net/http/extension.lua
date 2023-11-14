function extension()
    local internal = _internal

    return function()
        local export = {
            requests = { }
        }

        function export.execute(requests)
            return internal.execute(requests)
        end

        function export.requests.get(req)
            local config = export.config.get()
            return {
                method = "GET",
                url = (config.base_url or "") .. (req.url or ""),
                headers = req.headers or {}
            }
        end

        function export.get(req)
            return single_request(export.requests.get(req))
        end

        function export.requests.post(req)
            local config = export.config.get()
            return {
                method = "POST",
                url = (config.base_url or "") .. (req.url or ""),
                headers = req.headers or {},
                json = req.json
            }
        end

        function export.post(req)
            return single_request(export.requests.post(req))
        end

        function export.requests.patch(req)
            local config = export.config.get()
            return {
                method = "PATCH",
                url = (config.base_url or "") .. (req.url or ""),
                headers = req.headers or {},
                json = req.json
            }
        end

        function export.patch(req)
            return single_request(export.requests.patch(req))
        end

        function export.requests.put(req)
            local config = export.config.get()
            return {
                method = "PUT",
                url = (config.base_url or "") .. (req.url or ""),
                headers = req.headers or {},
                json = req.json
            }
        end

        function export.put(req)
            return single_request(export.requests.put(req))
        end

        function export.requests.delete(req)
            local config = export.config.get()
            return {
                method = "DELETE",
                url = (config.base_url or "") .. (req.url or ""),
                headers = req.headers or {}
            }
        end

        function export.delete(req)
            return single_request(export.requests.delete(req))
        end

        function single_request(request)
            local err, responses = export.execute({ request })

            if err ~= nil then
                return err, nil
            end

            assert(#responses == 1)
            return nil, responses[1]
        end

        return export
    end
end