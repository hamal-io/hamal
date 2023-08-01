local function createExtensionFactory()
    local internal = extension.import("eth")
    return function()
        return {
            call = function(arg1)
                print("calling")
                print(internal.get_block)
            end
        }
    end
end

ethFactory = createExtensionFactory()
_G.extension = nil

print("invoked")