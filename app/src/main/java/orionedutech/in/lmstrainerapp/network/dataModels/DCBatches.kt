package orionedutech.`in`.lmstrainerapp.network.dataModels

import orionedutech.`in`.lmstrainerapp.database.entities.Batch

data class DCBatches(
    val batches: List<Batch>,
    val success: String
)