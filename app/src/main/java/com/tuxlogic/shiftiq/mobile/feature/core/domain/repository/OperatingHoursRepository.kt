package com.tuxlogic.shiftiq.mobile.feature.core.domain.repository

import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursDay
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursException
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursExceptionInput
import com.tuxlogic.shiftiq.mobile.feature.core.domain.model.OperatingHoursScope

interface OperatingHoursRepository {
    suspend fun getWeekly(scope: OperatingHoursScope, id: String): AppResult<List<OperatingHoursDay>>
    suspend fun updateWeekly(scope: OperatingHoursScope, id: String, days: List<OperatingHoursDay>): AppResult<List<OperatingHoursDay>>
    suspend fun resetBranchHours(branchId: String): AppResult<Unit>
    suspend fun getExceptions(scope: OperatingHoursScope, id: String): AppResult<List<OperatingHoursException>>
    suspend fun createException(scope: OperatingHoursScope, id: String, input: OperatingHoursExceptionInput): AppResult<OperatingHoursException>
    suspend fun deleteException(scope: OperatingHoursScope, id: String, exceptionId: String): AppResult<Unit>
}
