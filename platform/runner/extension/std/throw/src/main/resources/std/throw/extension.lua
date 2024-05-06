function extension_create()
    local export = {}

    function export.create()

        local instance = { }

        function instance.internal(msg)
            __throw_internal__(msg)
        end

        function instance.not_found(msg)
            __throw_not_found__(msg)
        end

        function instance.illegal_state(msg)
            __throw_illegal_state__(msg)
        end

        function instance.illegal_argument(msg)
            __throw_illegal_argument__(msg)
        end

        return instance
    end

    return export
end