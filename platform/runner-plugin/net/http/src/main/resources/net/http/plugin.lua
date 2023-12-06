function plugin()
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

        --function export.get(req)
        --    return single_request(export.requests.get(req))
        --end

        function export.requests.post(req)
            return {
                method = "POST",
                url = req.url or error('url not set'),
                headers = req.headers or {},
                json = req.json
            }
        end

        --function export.post(req)
        --    return single_request(export.requests.post(req))
        --end

        function export.requests.patch(req)
            return {
                method = "PATCH",
                url = req.url or error('url not set'),
                headers = req.headers or {},
                json = req.json
            }
        end

        --function export.patch(req)
        --    return single_request(export.requests.patch(req))
        --end

        function export.requests.put(req)
            return {
                method = "PUT",
                url = req.url or error('url not set'),
                headers = req.headers or {},
                json = req.json
            }
        end

        --function export.put(req)
        --    return single_request(export.requests.put(req))
        --end

        function export.requests.delete(req)
            return {
                method = "DELETE",
                url = req.url or error('url not set'),
                headers = req.headers or {}
            }
        end

        --function export.delete(req)
        --    return single_request(export.requests.delete(req))
        --end

        --function single_request(request)
        --    local err, responses = export.execute({ request })
        --
        --    if err ~= nil then
        --        return err, nil
        --    end
        --
        --    assert(#responses == 1)
        --    return nil, responses[1]
        --end

        return export
    end
end