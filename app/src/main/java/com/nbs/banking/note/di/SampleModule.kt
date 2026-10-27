package com.nbs.banking.note.di

import com.nbs.banking.note.data.SampleRepositoryImpl
import com.nbs.banking.note.data.local.SampleLocalDataSource
import com.nbs.banking.note.data.local.SampleLocalDataSourceImpl
import com.nbs.banking.note.domain.SampleRepository
import com.nbs.banking.note.domain.usecase.DeleteNoteUseCase
import com.nbs.banking.note.domain.usecase.DeleteNoteUseCaseImpl
import com.nbs.banking.note.domain.usecase.GetAllSampleUseCase
import com.nbs.banking.note.domain.usecase.GetAllSampleUseCaseImpl
import com.nbs.banking.note.domain.usecase.InsertNoteUseCase
import com.nbs.banking.note.domain.usecase.InsertNoteUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SampleModule {

    @Binds
    abstract fun bindSampleLocalDataSource(
        impl: SampleLocalDataSourceImpl
    ): SampleLocalDataSource

    @Binds
    abstract fun bindSampleRepository(
        impl: SampleRepositoryImpl
    ): SampleRepository

    @Binds
    abstract fun bindGetAllSampleUseCase(
        impl: GetAllSampleUseCaseImpl
    ): GetAllSampleUseCase

    @Binds
    abstract fun bindInsertNoteUseCase(
        impl: InsertNoteUseCaseImpl
    ): InsertNoteUseCase

    @Binds
    abstract fun bindDeleteNoteUseCase(
        impl: DeleteNoteUseCaseImpl
    ): DeleteNoteUseCase
}