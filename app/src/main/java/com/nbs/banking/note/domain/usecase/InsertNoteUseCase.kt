package com.nbs.banking.note.domain.usecase

import com.nbs.banking.note.domain.SampleRepository
import com.nbs.banking.note.domain.model.SampleNote
import javax.inject.Inject

interface InsertNoteUseCase {
    suspend operator fun invoke(note: SampleNote): Long
}

class InsertNoteUseCaseImpl @Inject constructor(
    private val sampleRepository: SampleRepository,
) : InsertNoteUseCase {
    override suspend fun invoke(note: SampleNote): Long = sampleRepository.insert(note)

}