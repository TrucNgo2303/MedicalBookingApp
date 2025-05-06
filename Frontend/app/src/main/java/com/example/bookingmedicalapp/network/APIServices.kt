package com.example.bookingmedicalapp.network


import com.example.bookingmedicalapp.model.*
import io.reactivex.Observable
import retrofit2.http.*

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

    @POST("/patient/get-appointment-status")
    fun appointmentStatus(@Body request: StatusRequest): Observable<BasePatientResponse<List<StatusResponse>>>

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

    @POST("/patient/create-appointment")
    fun createAppointment(@Body request: AppointmentRequest): Observable<AppointmentResponse>

    @POST("/patient/check-doctor-appointment")
    fun checkDoctorAppointment(@Body request: DoctorDetailRequest): Observable<BasePatientResponse<List<CheckAppointmentResponse>>>

    //Doctor
    @GET("/doctor/number-of-appointment-today")
    fun numOfAppointment(): Observable<BaseDoctorResponse<NumOfAppointment>>

    @GET("/doctor/today-appointment")
    fun todayAppointment(): Observable<BaseDoctorResponse<List<TodayAppointmentResponse>>>

    @POST("/doctor/appointment-detail")
    fun appointmentDetail(@Body request: AppointmentDetailRequest): Observable<AppointmentDetailResponse>

    @POST("/doctor/preliminary-and-prescriptions")
    fun preliminaryDetail(@Body request: AppointmentDetailRequest): Observable<BaseDoctorResponse<PreliminaryDetailResponse>>

    @POST("/doctor/add-prescriptions")
    fun addPrescriptions(@Body request: AddPrescriptionsRequest): Observable<NormalResponse>

    @POST("/doctor/update-prescriptions")
    fun updatePrescriptions(@Body request: UpdatePrescriptionsRequest): Observable<NormalResponse>

    @POST("/doctor/update-preliminary")
    fun updatePreliminary(@Body request: UpdatePreliminaryRequest): Observable<NormalResponse>

    @HTTP(method = "DELETE", path = "/doctor/delete-prescriptions", hasBody = true)
    fun deletePrescriptions(@Body request: PrescriptionsRequest): Observable<NormalResponse>

    @POST("/doctor/doctor-schedule")
    fun doctorSchedule(@Body request: AppointmentDateRequest): Observable<BaseDoctorResponse<List<TodayAppointmentResponse>>>

    //Receptionist
//    @GET("/receptionist/get-all-appointment-today")
//    fun receptionistAppointment(): Observable<BaseDoctorResponse<>>

    @POST("/receptionist/check-doctor-schedule")
    fun checkDoctorSchedule(@Body request: CheckDoctorRequest): Observable<BaseDoctorResponse<List<CheckDoctorResponse>>>

    @POST("/receptionist/add-waiting")
    fun addWaiting(@Body request: AddWaitingRequest) : Observable<NormalResponse>

}