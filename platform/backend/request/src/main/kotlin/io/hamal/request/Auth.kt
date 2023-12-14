package io.hamal.request

import io.hamal.lib.domain.vo.Email
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.domain.vo.Web3Address
import io.hamal.lib.domain.vo.Web3Signature

interface LogInEmailReq {
    val email: Email
    val password: Password
}

interface ChallengeMetaMaskReq {
    val address: Web3Address
}

interface LogInMetaMaskReq {
    val address: Web3Address
    val signature: Web3Signature
}