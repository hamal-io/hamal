local eth = require('web3.eth')

eth.config.update({
    host = 'http://localhost:9000'
})

local err, batch_result = eth.execute({
    eth.request.call({
        block = 12040752,
        to = "0x570febdf89c07f256c75686caca215289bb11cfc",
        data = "0x0902f1ac"
    })
})