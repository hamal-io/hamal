function plugin()

    return function()
        local export = { }

        function export.test(message)
            print('about to send message to telegram: ' .. message)
        end

        return export
    end
end