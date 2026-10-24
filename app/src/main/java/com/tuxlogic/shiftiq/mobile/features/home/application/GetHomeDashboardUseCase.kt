package com.tuxlogic.shiftiq.mobile.features.home.application

import com.tuxlogic.shiftiq.mobile.features.home.domain.HomeRepository
import com.tuxlogic.shiftiq.mobile.features.home.domain.model.HomeDashboardData
import javax.inject.Inject

class GetHomeDashboardUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): Result<HomeDashboardData> {
        return repository.getHomeDashboardData(forceRefresh)
    }
}
