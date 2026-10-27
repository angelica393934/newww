package com.nbs.banking.note.domain.usecase

import com.nbs.banking.note.domain.SampleRepository
import com.nbs.banking.note.domain.model.SampleNote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetAllSampleUseCase {
    operator fun invoke(): Flow<List<SampleNote>>
}

class GetAllSampleUseCaseImpl @Inject constructor(
    private val sampleRepository: SampleRepository,
) : GetAllSampleUseCase {
    override fun invoke(): Flow<List<SampleNote>> = sampleRepository.getAll()

}