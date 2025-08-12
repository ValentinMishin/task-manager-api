package ru.valentin.dto.delete

data class DeleteTagResponse(
    val tagId: Long,
    val deletedTasks: Set<Long>
)