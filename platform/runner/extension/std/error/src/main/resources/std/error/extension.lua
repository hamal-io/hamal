function extension_create()
    local export = {}

    function export.create()

        local instance = { }

        function instance.internal(msg)
            __error_internal__(msg)
        end

        function instance.not_found(msg)
            __error_not_found__(msg)
        end

        function instance.invalid_state(msg)
            __error_invalid_state__(msg)
        end

        function instance.illegal_argument(msg)
            __error_illegal_argument__(msg)
        end

        return instance
    end

    return export
end