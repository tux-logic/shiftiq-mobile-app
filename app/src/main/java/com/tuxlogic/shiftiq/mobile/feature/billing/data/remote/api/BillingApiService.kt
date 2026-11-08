package com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.api

import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.CreateQuoteRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.DirectCheckoutRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.EmitVoucherRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.MercadoPagoCheckoutRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.MercadoPagoPreferenceRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.MercadoPagoPreferenceResponseDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.QuoteResourceDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.RegisterPaymentRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.UpdateQuoteDiscountRequestDto
import com.tuxlogic.shiftiq.mobile.feature.billing.data.remote.dto.VoucherResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface BillingApiService {

    // --- Quotes Endpoints ---

    @POST("api/v1/quotes")
    suspend fun createQuote(
        @Body request: CreateQuoteRequestDto
    ): Response<QuoteResourceDto>

    @GET("api/v1/quotes")
    suspend fun getQuotes(
        @Query("branchId") branchId: String
    ): Response<List<QuoteResourceDto>>

    @GET("api/v1/quotes/{id}")
    suspend fun getQuoteById(
        @Path("id") id: String
    ): Response<QuoteResourceDto>

    @PUT("api/v1/quotes/{id}")
    suspend fun updateQuoteDiscount(
        @Path("id") id: String,
        @Body request: UpdateQuoteDiscountRequestDto
    ): Response<QuoteResourceDto>

    @POST("api/v1/quotes/{id}/approvals")
    suspend fun approveQuote(
        @Path("id") id: String
    ): Response<QuoteResourceDto>

    @POST("api/v1/quotes/{id}/cancellations")
    suspend fun cancelQuote(
        @Path("id") id: String
    ): Response<QuoteResourceDto>

    // --- Vouchers Endpoints ---

    @POST("api/v1/vouchers")
    suspend fun emitVoucher(
        @Body request: EmitVoucherRequestDto
    ): Response<VoucherResourceDto>

    @GET("api/v1/vouchers")
    suspend fun getVouchers(
        @Query("branchId") branchId: String
    ): Response<List<VoucherResourceDto>>

    @GET("api/v1/vouchers/{voucherId}")
    suspend fun getVoucherById(
        @Path("voucherId") voucherId: String
    ): Response<VoucherResourceDto>

    @POST("api/v1/vouchers/{voucherId}/payments")
    suspend fun registerPayment(
        @Path("voucherId") voucherId: String,
        @Body request: RegisterPaymentRequestDto
    ): Response<VoucherResourceDto>

    @DELETE("api/v1/vouchers/{voucherId}/payments/{paymentId}")
    suspend fun deletePayment(
        @Path("voucherId") voucherId: String,
        @Path("paymentId") paymentId: String
    ): Response<Unit>

    // --- Checkouts & Mercado Pago Endpoints ---

    @POST("api/v1/checkouts")
    suspend fun directCheckout(
        @Body request: DirectCheckoutRequestDto
    ): Response<VoucherResourceDto>

    @POST("api/v1/payments/mercadopago/preferences")
    suspend fun createMercadoPagoPreference(
        @Body request: MercadoPagoPreferenceRequestDto
    ): Response<MercadoPagoPreferenceResponseDto>

    @POST("api/v1/checkouts/mercadopago")
    suspend fun confirmMercadoPagoCheckout(
        @Body request: MercadoPagoCheckoutRequestDto
    ): Response<VoucherResourceDto>
}
