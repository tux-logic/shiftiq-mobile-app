package com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.repository

import com.tuxlogic.shiftiq.mobile.core.common.dispatchers.DispatcherProvider
import com.tuxlogic.shiftiq.mobile.core.common.result.AppError
import com.tuxlogic.shiftiq.mobile.core.common.result.AppResult
import com.tuxlogic.shiftiq.mobile.core.common.result.map
import com.tuxlogic.shiftiq.mobile.core.datastore.SessionDataStore
import com.tuxlogic.shiftiq.mobile.core.model.BranchId
import com.tuxlogic.shiftiq.mobile.core.network.safeApiCall
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.mapper.toDomain
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.api.BillingApiService
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.CreateQuoteRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.DirectCheckoutRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.EmitVoucherRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.MercadoPagoCheckoutRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.MercadoPagoPreferenceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.RegisterPaymentRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.infrastructure.remote.dto.UpdateQuoteDiscountRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.DocumentType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentMethod
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.PaymentPreference
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Quote
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.QuoteId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.Voucher
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherId
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.model.VoucherType
import com.tuxlogic.shiftiq.mobile.feature.billing.domain.repository.BillingRepository
import com.tuxlogic.shiftiq.mobile.feature.operations.domain.model.WorkOrderId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BillingRepositoryImpl @Inject constructor(
    private val billingApiService: BillingApiService,
    private val sessionDataStore: SessionDataStore,
    private val dispatchers: DispatcherProvider
) : BillingRepository {

    override suspend fun getQuotes(branchId: BranchId?): AppResult<List<Quote>> =
        withContext(dispatchers.io) {
            val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
            if (effectiveBranchId == null) {
                return@withContext AppResult.Failure(
                    AppError.Validation(details = "branchId", message = "No active branch selected")
                )
            }
            safeApiCall {
                billingApiService.getQuotes(branchId = effectiveBranchId.toString())
            }.map { dtos ->
                dtos.map { it.toDomain() }
            }
        }

    override suspend fun getQuoteById(id: QuoteId): AppResult<Quote> =
        withContext(dispatchers.io) {
            safeApiCall {
                billingApiService.getQuoteById(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun createQuote(
        workOrderId: WorkOrderId,
        branchId: BranchId,
        discountPercentage: Double
    ): AppResult<Quote> = withContext(dispatchers.io) {
        val request = CreateQuoteRequestDto(
            workOrderId = workOrderId.toString(),
            branchId = branchId.toString(),
            discountPercentage = discountPercentage
        )
        safeApiCall {
            billingApiService.createQuote(request)
        }.map { it.toDomain() }
    }

    override suspend fun updateQuoteDiscount(
        id: QuoteId,
        discountPercentage: Double
    ): AppResult<Quote> = withContext(dispatchers.io) {
        val request = UpdateQuoteDiscountRequestDto(
            discountPercentage = discountPercentage
        )
        safeApiCall {
            billingApiService.updateQuoteDiscount(id.toString(), request)
        }.map { it.toDomain() }
    }

    override suspend fun approveQuote(id: QuoteId): AppResult<Quote> =
        withContext(dispatchers.io) {
            safeApiCall {
                billingApiService.approveQuote(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun cancelQuote(id: QuoteId): AppResult<Quote> =
        withContext(dispatchers.io) {
            safeApiCall {
                billingApiService.cancelQuote(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun getVouchers(branchId: BranchId?): AppResult<List<Voucher>> =
        withContext(dispatchers.io) {
            val effectiveBranchId = branchId ?: sessionDataStore.activeBranchId.first()
            if (effectiveBranchId == null) {
                return@withContext AppResult.Failure(
                    AppError.Validation(details = "branchId", message = "No active branch selected")
                )
            }
            safeApiCall {
                billingApiService.getVouchers(branchId = effectiveBranchId.toString())
            }.map { dtos ->
                dtos.map { it.toDomain() }
            }
        }

    override suspend fun getVoucherById(id: VoucherId): AppResult<Voucher> =
        withContext(dispatchers.io) {
            safeApiCall {
                billingApiService.getVoucherById(id.toString())
            }.map { it.toDomain() }
        }

    override suspend fun emitVoucher(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String
    ): AppResult<Voucher> = withContext(dispatchers.io) {
        val request = EmitVoucherRequestDto(
            quoteId = quoteId.toString(),
            type = type.name,
            customerDocumentType = customerDocumentType.name,
            customerDocumentNumber = customerDocumentNumber,
            customerName = customerName
        )
        safeApiCall {
            billingApiService.emitVoucher(request)
        }.map { it.toDomain() }
    }

    override suspend fun registerPayment(
        voucherId: VoucherId,
        amount: Double,
        method: PaymentMethod
    ): AppResult<Voucher> = withContext(dispatchers.io) {
        val request = RegisterPaymentRequestDto(
            amount = amount,
            method = method.name
        )
        safeApiCall {
            billingApiService.registerPayment(voucherId.toString(), request)
        }.map { it.toDomain() }
    }

    override suspend fun deletePayment(
        voucherId: VoucherId,
        paymentId: PaymentId
    ): AppResult<Unit> = withContext(dispatchers.io) {
        safeApiCall {
            billingApiService.deletePayment(voucherId.toString(), paymentId.toString())
        }
    }

    override suspend fun directCheckout(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String,
        method: PaymentMethod
    ): AppResult<Voucher> = withContext(dispatchers.io) {
        val request = DirectCheckoutRequestDto(
            quoteId = quoteId.toString(),
            type = type.name,
            customerDocumentType = customerDocumentType.name,
            customerDocumentNumber = customerDocumentNumber,
            customerName = customerName,
            method = method.name
        )
        safeApiCall {
            billingApiService.directCheckout(request)
        }.map { it.toDomain() }
    }

    override suspend fun createMercadoPagoPreference(
        quoteId: QuoteId,
        type: VoucherType?,
        customerDocumentType: DocumentType?,
        customerDocumentNumber: String?,
        customerName: String?
    ): AppResult<PaymentPreference> = withContext(dispatchers.io) {
        val request = MercadoPagoPreferenceRequestDto(
            quoteId = quoteId.toString(),
            type = type?.name,
            customerDocumentType = customerDocumentType?.name,
            customerDocumentNumber = customerDocumentNumber,
            customerName = customerName
        )
        safeApiCall {
            billingApiService.createMercadoPagoPreference(request)
        }.map { it.toDomain() }
    }

    override suspend fun confirmMercadoPagoCheckout(
        quoteId: QuoteId,
        type: VoucherType,
        customerDocumentType: DocumentType,
        customerDocumentNumber: String,
        customerName: String,
        paymentId: String
    ): AppResult<Voucher> = withContext(dispatchers.io) {
        val request = MercadoPagoCheckoutRequestDto(
            quoteId = quoteId.toString(),
            type = type.name,
            customerDocumentType = customerDocumentType.name,
            customerDocumentNumber = customerDocumentNumber,
            customerName = customerName,
            paymentId = paymentId
        )
        safeApiCall {
            billingApiService.confirmMercadoPagoCheckout(request)
        }.map { it.toDomain() }
    }
}
