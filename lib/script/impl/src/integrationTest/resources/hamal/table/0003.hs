local sys = {
    func = {
        list = {
            nested = {
                value = 23
            }
        }
    }
}

assert(sys.funcs.list.nested.value == 23)