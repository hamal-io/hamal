function plugin_create(internal)
    local export = {
        requests = { }
    }

    function export.execute(requests)
        return internal.execute(requests)
    end

    function export.requests.get(req)
        return {
            method = "GET",
            url = req.url or __throw_illegal_argument__('url not set'),
            produces = req.produces or __throw_illegal_argument__('produces not set'),
            consumes = req.consumes or __throw_illegal_argument__('consumes not set'),
            consumes_error = req.consumes_error or req.consumes,
            headers = req.headers or {}
        }
    end

    function export.requests.post(req)
        return {
            method = "POST",
            url = req.url or __throw_illegal_argument__('url not set'),
            produces = req.produces or __throw_illegal_argument__('produces not set'),
            consumes = req.consumes or __throw_illegal_argument__('consumes not set'),
            consumes_error = req.consumes_error or req.consumes,
            headers = req.headers or {},
            body = req.body
        }
    end

    function export.requests.patch(req)
        return {
            method = "PATCH",
            url = req.url or __throw_illegal_argument__('url not set'),
            produces = req.produces or __throw_illegal_argument__('produces not set'),
            consumes = req.consumes or __throw_illegal_argument__('consumes not set'),
            consumes_error = req.consumes_error or req.consumes,
            headers = req.headers or {},
            body = req.body
        }
    end

    function export.requests.put(req)
        return {
            method = "PUT",
            url = req.url or __throw_illegal_argument__('url not set'),
            produces = req.produces or __throw_illegal_argument__('produces not set'),
            consumes = req.consumes or __throw_illegal_argument__('consumes not set'),
            consumes_error = req.consumes_error or req.consumes,
            headers = req.headers or {},
            body = req.body
        }
    end

    function export.requests.delete(req)
        return {
            method = "DELETE",
            url = req.url or __throw_illegal_argument__('url not set'),
            produces = req.produces or __throw_illegal_argument__('produces not set'),
            consumes = req.consumes or __throw_illegal_argument__('consumes not set'),
            consumes_error = req.consumes_error or req.consumes,
            headers = req.headers or {}
        }
    end

    return export
end