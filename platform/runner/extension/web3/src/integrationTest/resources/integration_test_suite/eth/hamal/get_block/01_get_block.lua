eth = require('web3.eth').create({
    url = context.env.test_url
})

err, block = eth.get_block('0x1284810')
assert(err == nil)

assert(block.base_fee_per_gas == "0xcdeac4a94")
assert(block.extra_data == "0x0000000000000000546974616e2028746974616e6275696c6465722e78797a29")
assert(block.gas_limit == "0x1c9c380")
assert(block.gas_used == "0xa0adf6")
assert(block.hash == "0xbe564f59e089bd7fac45b40a4bb0295338a0074d4eaf675937d2498b43ef401c")
assert(block.logs_bloom == "0x40b30d48e16500351a2210288d8a9a2310d82f744f40041100995012d02a434245553590c42a6c61ebb0384248132177e3259218ba4269f0f6f52d9052a8ec29e87247394a228c3fbd02f76f942304f59d2641c1517589284e813d42a0e61b48981d042216061e5c101cdac482625c65f2776c682e105721c64dc09218eb10057c36b2da2a1443c8004f325a622209c554724d97fb4b30eb7436304ae25226238b0456923796a2905f04f4d4b9da049815518640e4acba0fae7d348c1a0094f4ed57302244b50004a82888c240ad3ace7d7200af942b0052e4573bba1054a844b172a0041c49962a510425d7a440a8c3dcf9b4080a7e064008bd281070937c0d")
assert(block.miner == "0x4838B106FCe9647Bdf1E7877BF73cE8B0BAD5f97")
assert(block.mix_hash == "0x312b2e833821b694a1789d9efc7618a3a600cc06c7e4ed4757860c998e69081b")
assert(block.number == "0x1284810")
assert(block.parent_hash == "0x3da1516115fd35fbe3c4d83f7422bf7d5f973b08f09028ea18ae27346181da52")
assert(block.receipts_root == "0x1f47a6666ca0f3038ec6e0fc236f58b2aad8933efc24e7f9c28d66c39cc2a45e")
assert(block.sha3_uncles == "0x1dcc4de8dec75d7aab85b567b6ccd41ad312451b948a7413f0a142fd40d49347")
assert(block.size == "0xfbec")
assert(block.state_root == "0xfaca9e1209e82e72e5700512d0cfedcb156baca691b1cc56e80ea72e4d26dbaf")
assert(block.timestamp == "0x65eff103")
assert(block.total_difficulty == "0xc70d815d562d3cfa955")

assert(#block.transactions == 3)

tx = block.transactions[3]
assert(tx.block_hash == "0xbe564f59e089bd7fac45b40a4bb0295338a0074d4eaf675937d2498b43ef401c")
assert(tx.block_number == "0x1284810")
assert(tx.from == "0xae2Fc483527B8EF99EB5D9B44875F005ba1FaE13")
assert(tx.gas == "0x1e206")
assert(tx.gas_price == "0xf8bd73008")
assert(tx.max_priority_fee_per_gas == "0xf8bd73002")
assert(tx.max_fee_per_gas == "0xf8bd73008")
assert(tx.hash == "0xb67449ad108cceea3d9885f55ad7088e8b0b422d9224e3d3788a920cd91686d4")
assert(tx.input == "0x7c3a58cb30368ceb2d194740b144eab4c2da8a917dcb052e9224")
assert(tx.nonce == "0x220af4")
assert(tx.to == "0x6b75d8AF000000e20B7a7DDf000Ba900b4009A80")
assert(tx.transaction_index == "0x1e")
assert(tx.value == "0xe9085ce10")
assert(tx.type == "0x2")

assert(#tx.access_list == 3)

ali = tx.access_list[3]
assert(ali.address == '0xC02aaA39b223FE8D0A0e5C4F27eAD9083C756Cc2')
assert(#ali.storage_keys == 2)
assert(ali.storage_keys[1] == '0x12d741df36a3fad898658096a29c6b006f513bbaf43105ee7958ce2396ea1dc2')
assert(ali.storage_keys[2] == '0x12231cd4c753cb5530a43a74c45106c24765e6f81dc8927d4f4be7e53315d5a8')

assert(block.transactions_root == "0xc1d16b79ea93af57f9bd17595370f9800cc8de32cc1f4e850df43d694e264a22")
assert(#block.withdrawals == 2)

wd = block.withdrawals[2]
assert(wd.index == "0x24593f7")
assert(wd.validator_index == "0x11015f")
assert(wd.address == "0xB9D7934878B5FB9610B3fE8A5e441e8fad7E293f")
assert(wd.amount == "0x115a335")
assert(block.withdrawals_root == "0x79373dfb0ff17072206ea0dae4942afa3791a9699aeacdb74db8a4f8b62b374f")