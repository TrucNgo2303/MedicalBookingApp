package com.example.bookingmedicalapp.source.datasource

import com.example.bookingmedicalapp.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

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

    fun appointmentStatus(request: StatusRequest): Observable<BasePatientResponse<List<StatusResponse>>>

    fun doctorDetail(request: DoctorDetailRequest): Observable<BasePatientResponse<DoctorDetailResponse>>

    fun allSpecialist(): Observable<BasePatientResponse<List<AllSpecialistResponse>>>

    fun specialistDetail(request: SpecialistDetailRequest): Observable<BasePatientResponse<List<SpecialistDetailResponse>>>

    fun getSpecialist(request: SpecialistDetailRequest): Observable<BasePatientResponse<SpecialistResponse>>

    fun getAllComments(request: CommentRequest): Observable<BasePatientResponse<List<CommentResponse>>>

    fun getAllReply(request: ReplyRequest): Observable<BasePatientResponse<List<ReplyResponse>>>

    fun createAppointment(request: AppointmentRequest): Observable<AppointmentResponse>

    fun checkDoctorAppointment(request: DoctorDetailRequest): Observable<BasePatientResponse<List<CheckAppointmentResponse>>>

    fun getAppointmentPatientDetail(request: AppointmentIdRequest): Observable<BasePatientResponse<AppointmentDetailPatientResponse>>

    //Doctor
    fun numOfAppointment(): Observable<BaseDoctorResponse<NumOfAppointment>>

    fun todayAppointment(): Observable<BaseDoctorResponse<List<TodayAppointmentResponse>>>

    fun appointmentDetail(request: AppointmentDetailRequest): Observable<AppointmentDetailResponse>

    fun preliminaryDetail(request: AppointmentDetailRequest): Observable<BaseDoctorResponse<PreliminaryDetailResponse>>

    fun addPrescriptions(request: AddPrescriptionsRequest): Observable<NormalResponse>

    fun updatePrescriptions(request: UpdatePrescriptionsRequest): Observable<NormalResponse>

    fun updatePreliminary(request: UpdatePreliminaryRequest): Observable<NormalResponse>

    fun deletePrescriptions(request: PrescriptionsRequest): Observable<NormalResponse>

    fun doctorSchedule(request: AppointmentDateRequest): Observable<BaseDoctorResponse<List<TodayAppointmentResponse>>>

    fun getAllMedicines(): Observable<BaseDoctorResponse<List<MedicinesResponse>>>

}