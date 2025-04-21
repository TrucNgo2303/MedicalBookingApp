package com.example.bookingmedicalapp.source.datasource

import com.example.bookingmedicalapp.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface IRemoteDataSource {

    //Authorization
    fun login(request: LoginRequest): Observable<LoginBody>

    fun sendCode(request: SendCodeRequest): Observable<SendCodeBody>

    fun verifyCode(request: VerifyCodeRequest): Observable<VerifyCodeBody>

    fun signUp(request: SignUpRequest): Observable<SignUpBody>

    fun createProfile(request: CreateProfileRequest): Observable<CreateProfileBody>

    //Patient
    fun searchResult(): Observable<BasePatientResponse<List<SearchResultBody>>>

    fun getHomePatientInfo(): Observable<BasePatientResponse<PatientHomeResponse>>

    fun getSpecialistHome(): Observable<BasePatientResponse<List<SpecialistHomeResponse>>>

    fun topDoctor(): Observable<BasePatientResponse<List<TopDoctorResponse>>>

    fun doctorDetail(request: DoctorDetailRequest): Observable<BasePatientResponse<DoctorDetailResponse>>

    fun allSpecialist(): Observable<BasePatientResponse<List<AllSpecialistResponse>>>

    fun specialistDetail(request: SpecialistDetailRequest): Observable<BasePatientResponse<List<SpecialistDetailResponse>>>

    fun getSpecialist(request: SpecialistDetailRequest): Observable<BasePatientResponse<SpecialistResponse>>

    fun getAllComments(@Body request: CommentRequest): Observable<BasePatientResponse<List<CommentResponse>>>

    fun getAllReply(@Body request: ReplyRequest): Observable<BasePatientResponse<List<ReplyResponse>>>

}