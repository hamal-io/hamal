local sys = {
    func = {
        list = {
            nested = {
                value = 23
            }
        }
    }
}

assert(sys.func.list.nested.value == 23)