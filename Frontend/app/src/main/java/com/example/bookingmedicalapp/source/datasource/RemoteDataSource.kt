package com.example.bookingmedicalapp.source.datasource

import com.example.bookingmedicalapp.model.*
import com.example.bookingmedicalapp.network.ApiClient
import io.reactivex.Observable

class RemoteDataSource : IRemoteDataSource {
    private val apiServices = ApiClient.apiService

    companion object {
        @Volatile
        private var INSTANCE: RemoteDataSource? = null

        // Phương thức để lấy instance
        fun getInstance(): RemoteDataSource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteDataSource().also { INSTANCE = it }
            }
        }
    }


    override fun login(request: LoginRequest): Observable<LoginBody> {
        return apiServices.login(request)
    }

    override fun sendCode(request: SendCodeRequest): Observable<SendCodeBody> {
        return apiServices.sendCode(request)
    }

    override fun verifyCode(request: VerifyCodeRequest): Observable<VerifyCodeBody> {
        return apiServices.verifyCode(request)
    }

    override fun signUp(request: SignUpRequest): Observable<SignUpBody> {
        return apiServices.signUp(request)
    }

    override fun createProfile(request: CreateProfileRequest): Observable<CreateProfileBody> {
        return apiServices.createProfile(request)
    }

    override fun searchResult(): Observable<BasePatientResponse<List<SearchResultBody>>> {
        return apiServices.searchResult()
    }

    override fun getHomePatientInfo(): Observable<BasePatientResponse<PatientHomeResponse>> {
        return apiServices.getHomePatientInfo()
    }

    override fun getSpecialistHome(): Observable<BasePatientResponse<List<SpecialistHomeResponse>>> {
        return apiServices.getSpecialistHome()
    }

    override fun topDoctor(): Observable<BasePatientResponse<List<TopDoctorResponse>>> {
        return apiServices.topDoctor()
    }

    override fun doctorDetail(request: DoctorDetailRequest): Observable<BasePatientResponse<DoctorDetailResponse>> {
        return apiServices.doctorDetail(request)
    }

    override fun allSpecialist(): Observable<BasePatientResponse<List<AllSpecialistResponse>>> {
        return apiServices.allSpecialist()
    }

    override fun specialistDetail(request: SpecialistDetailRequest): Observable<BasePatientResponse<List<SpecialistDetailResponse>>> {
        return apiServices.specialistDetail(request)
    }

    override fun getSpecialist(request: SpecialistDetailRequest): Observable<BasePatientResponse<SpecialistResponse>> {
        return apiServices.getSpecialist(request)
    }

    override fun getAllComments(request: CommentRequest): Observable<BasePatientResponse<List<CommentResponse>>> {
        return apiServices.getAllComments(request)
    }

    override fun getAllReply(request: ReplyRequest): Observable<BasePatientResponse<List<ReplyResponse>>> {
        return apiServices.getAllReply(request)
    }

    override fun createAppointment(request: AppointmentRequest): Observable<AppointmentResponse> {
        return apiServices.createAppointment(request)
    }

    override fun checkDoctorAppointment(request: DoctorDetailRequest): Observable<BasePatientResponse<List<CheckAppointmentResponse>>> {
        return apiServices.checkDoctorAppointment(request)
    }


}