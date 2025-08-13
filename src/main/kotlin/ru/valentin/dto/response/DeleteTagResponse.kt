package ru.valentin.dto.response

data class DeleteTagResponse(
    val tagId: Long,
    val deletedTasks: Set<Long>
)