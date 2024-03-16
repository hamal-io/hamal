local hamal_provider_create = function(opts)
    url = opts.url or 'https://eth.w3p.hamal.io'

    eth_plugin = require_plugin('web3.evm')

    return {
        name = 'hamal',
        url = url,
        get_block_by_number = function(block_number)

        end,

        batch = {
            get_block = function(block)
                return { type = "get_block", block = block }
            end
        }
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
                get_block_by_number = provider.get_block_by_number
            }
        end
    }
end