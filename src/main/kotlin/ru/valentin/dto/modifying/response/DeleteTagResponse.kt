package ru.valentin.dto.modifying.response

data class DeleteTagResponse(
    val tagId: Long,
    val deletedTasks: Set<Long>
)