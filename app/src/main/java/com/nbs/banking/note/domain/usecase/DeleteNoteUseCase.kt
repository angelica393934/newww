package com.nbs.banking.note.domain.usecase

import com.nbs.banking.note.domain.SampleRepository
import javax.inject.Inject

interface DeleteNoteUseCase {
    suspend operator fun invoke(id: Long)
}

class DeleteNoteUseCaseImpl @Inject constructor(
    private val sampleRepository: SampleRepository,
) : DeleteNoteUseCase {
    override suspend fun invoke(id: Long) = sampleRepository.delete(id)

}