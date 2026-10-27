package com.nbs.banking.core.base

interface LocalDataSource

interface RemoteDataSource

interface BaseRepository {
    val remoteDataSource: RemoteDataSource?
        get() = null
    val localDataSource: LocalDataSource?
        get() = null
}
