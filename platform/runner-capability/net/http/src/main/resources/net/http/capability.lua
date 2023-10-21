function plugin()
    local internal = _internal

    return function()
        local export = {
            requests = { }
        }

        function export.execute(requests)
            return internal.execute(requests)
        end

        function export.requests.get(url, args)
            return {
                method = "GET",
                url = url,
                headers = {}
            }
        end

        function export.get(url, args)
            return single_request(export.requests.get(url, args))
        end

        function export.requests.post(url, args)
            return {
                method = "POST",
                url = url,
                headers = {},
                data = {}
            }
        end

        function export.post(url, args)
            return single_request(export.requests.post(url, args))
        end

        function export.requests.patch(url, args)
            return {
                method = "PATCH",
                url = url,
                headers = {},
                data = {}
            }
        end

        function export.patch(url, args)
            return single_request(export.requests.patch(url, args))
        end

        function export.requests.put(url, args)
            return {
                method = "PUT",
                url = url,
                headers = {},
                data = {}
            }
        end

        function export.put(url, args)
            return single_request(export.requests.put(url, args))
        end

        function export.requests.delete(url, args)
            return {
                method = "DELETE",
                url = url,
                headers = {}
            }
        end

        function export.delete(url, args)
            return single_request(export.requests.delete(url, args))
        end

        function single_request(request)
            local err, responses = export.execute({ request })

            if err ~= nil then
                return nil, err
            end
            assert(#responses == 1)
            return nil, responses[1]
        end

        return export
    end
end