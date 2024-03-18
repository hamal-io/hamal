local hamal_provider_create = function(opts)
    url = opts.url or 'http://web3-eth-proxy-1:10000/eth'

    evm = require_plugin('web3.evm')

    batch = {
        get_block = function(block)
            evm.get_block(block)
        end
    }

    return {
        name = 'hamal',
        url = url,
        get_blocks = function(blocks)
            requests = {}
            for _, block in ipairs(blocks) do
                table.insert(requests, evm.get_block(block))
            end

            err, responses = evm.execute({
                url = url,
                requests = requests
            })
            if err ~= nil then
                return err, nil
            end

            result = {}
            for _, response in ipairs(responses) do
                table.insert(result, response.result)
            end
            return nil, result
        end
    }
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
                    }
                },

                get_block = function(block)
                    err, blocks = provider.get_blocks({block})
                    if err ~= nil then
                        return err, nil
                    end
                    if blocks == nil or #blocks == 0 then
                        return nil, nil
                    end
                    return nil, blocks[1]
                end,

                get_blocks = provider.get_blocks
            }
        end
    }
end