function extension()
    local internal = _internal

    return function()
        local export = {
            requests = { }
        }

        function export.execute(requests)
            return internal.execute(requests)
        end

        function export.requests.get(url, options)
            local config = export.config.get()
            local base_url = config.base_url or ""
            options = options or {}
            return {
                method = "GET",
                url = base_url .. url,
                headers = options.headers
            }
        end

        function export.get(url, options)
            return single_request(export.requests.get(url, options))
        end

        function export.requests.post(url, options)
            local config = export.config.get()
            local base_url = config.base_url or ""
            options = options or {}
            return {
                method = "POST",
                url = base_url .. url,
                headers = options.headers,
                json = options.json
            }
        end

        function export.post(url, options)
            return single_request(export.requests.post(url, options or {}))
        end

        function export.requests.patch(url, options)
            local config = export.config.get()
            local base_url = config.base_url or ""
            options = options or {}
            return {
                method = "PATCH",
                url = base_url .. url,
                headers = options.headers,
                json = options.json
            }
        end

        function export.patch(url, options)
            return single_request(export.requests.patch(url, options))
        end

        function export.requests.put(url, options)
            local config = export.config.get()
            local base_url = config.base_url or ""
            options = options or {}
            return {
                method = "PUT",
                url = base_url .. url,
                headers = options.headers,
                json = options.json
            }
        end

        function export.put(url, options)
            return single_request(export.requests.put(url, options))
        end

        function export.requests.delete(url, options)
            local config = export.config.get()
            local base_url = config.base_url or ""
            options = options or {}
            return {
                method = "DELETE",
                url = base_url .. url,
                headers = options.headers
            }
        end

        function export.delete(url, options)
            return single_request(export.requests.delete(url, options))
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