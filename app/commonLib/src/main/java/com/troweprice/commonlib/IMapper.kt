package com.troweprice.commonlib

interface IMapper<I,O> {
    fun map(input: I): O
}