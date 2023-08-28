function create_extension_factory()
    local internal = _internal

    return function()
        local export = { }

        function export.execute(requests)
            return internal.execute(requests)
        end

        function export.get(url, args)
            return single_request({
                method = "GET",
                url = url,
                headers = {},
                data = {}
            })
        end

        function export.post(url, args)
            return single_request({
                method = "POST",
                url = url,
                headers = {},
                data = {}
            })
        end

        function export.patch(url, args)
            return single_request({
                method = "PATCH",
                url = url,
                headers = {},
                data = {}
            })
        end

        function export.put(url, args)
            return single_request({
                method = "PUT",
                url = url,
                headers = {},
                data = {}
            })
        end

        function export.delete(url, args)
            return single_request({
                method = "DELETE",
                url = url,
                headers = {},
                data = {}
            })
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