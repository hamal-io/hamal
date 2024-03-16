local hamal_provider_create = function(opts)
    url = opts.url or 'https://eth.w3p.hamal.io'

    evm = require_plugin('web3.evm')

    batch = {
        get_block = function(block) evm.get_block(block) end
    }

    return {
        name = 'hamal',
        url = url,
        get_block = function(block)
            err, responses = export.execute({ export.request.get_block(block) })
            if err ~= nil then
                return err, nil
            end
            return nil, responses[1]
        end
    }
end

local function abi_decode_parameter(type, value)
    print("Do some decoding of " .. type)
end

function extension_create()
    return {
        create = function(options)
            opts = options or {}
            opts.provider = hamal_provider_create(opts)

            local provider = opts.provider
            return {
                chain_id = 1,
                provider = provider,
                abi = {
                    type = {
                        uint8 = {
                            name = 'uint8'
                        },
                        uint256 = {
                            name = 'uint256'
                        }
                    },
                    decode_parameter = abi_decode_parameter
                },
                get_block = provider.get_block
            }
        end
    }
end