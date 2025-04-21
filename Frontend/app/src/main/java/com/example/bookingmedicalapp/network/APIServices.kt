package com.example.bookingmedicalapp.network


import com.example.bookingmedicalapp.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface APIServices {

    //Authorization
    @POST("/auth/login/")
    fun login(@Body request: LoginRequest): Observable<LoginBody>

    @POST("/auth/forget-password")
    fun sendCode(@Body request: SendCodeRequest): Observable<SendCodeBody>

    @POST("/auth/verify-code")
    fun verifyCode(@Body request: VerifyCodeRequest): Observable<VerifyCodeBody>

    @POST("/auth/sign-up")
    fun signUp(@Body request: SignUpRequest): Observable<SignUpBody>

    @POST("/auth/create-profile")
    fun createProfile(@Body request: CreateProfileRequest): Observable<CreateProfileBody>

    //Patient
    @GET("/patient/patient-home")
    fun getHomePatientInfo(): Observable<BasePatientResponse<PatientHomeResponse>>

    @GET("/patient/specialists")
    fun getSpecialistHome(): Observable<BasePatientResponse<List<SpecialistHomeResponse>>>

    @GET("/patient/top-doctors")
    fun topDoctor(): Observable<BasePatientResponse<List<TopDoctorResponse>>>

    @GET("/patient/search-result")
    fun searchResult(): Observable<BasePatientResponse<List<SearchResultBody>>>

    @POST("/patient/doctor-detail")
    fun doctorDetail(@Body request: DoctorDetailRequest): Observable<BasePatientResponse<DoctorDetailResponse>>

    @GET("/patient/all-specialists")
    fun allSpecialist(): Observable<BasePatientResponse<List<AllSpecialistResponse>>>

    @POST("/patient/get-specialist")
    fun getSpecialist(@Body request: SpecialistDetailRequest): Observable<BasePatientResponse<SpecialistResponse>>

    @POST("/patient/get-doctor-in-specialist")
    fun specialistDetail(@Body request: SpecialistDetailRequest): Observable<BasePatientResponse<List<SpecialistDetailResponse>>>

    @POST("/patient/get-all-comments")
    fun getAllComments(@Body request: CommentRequest): Observable<BasePatientResponse<List<CommentResponse>>>

    @POST("/patient/get-all-reply")
    fun getAllReply(@Body request: ReplyRequest): Observable<BasePatientResponse<List<ReplyResponse>>>


}