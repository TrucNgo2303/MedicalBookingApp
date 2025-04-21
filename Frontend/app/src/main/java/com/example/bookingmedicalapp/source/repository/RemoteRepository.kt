package com.example.bookingmedicalapp.source.repository

import com.example.bookingmedicalapp.model.*
import com.example.bookingmedicalapp.source.datasource.IRemoteDataSource
import com.example.bookingmedicalapp.source.datasource.RemoteDataSource
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RemoteRepository : IRemoteDataSource {
    private val dataSource = RemoteDataSource.getInstance()

    companion object {
        @Volatile
        private var INSTANCE: RemoteRepository? = null

        fun getInstance(): RemoteRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RemoteRepository().also { INSTANCE = it }
            }
        }
    }


    private fun <T : Any> Observable<T>.applySchedulers(): Observable<T> {
        return this.subscribeOn(Schedulers.io()) // Chạy trên background thread
            .observeOn(AndroidSchedulers.mainThread()) // Nhận kết quả trên main thread

    }

    override fun login(request: LoginRequest): Observable<LoginBody> {
        return dataSource.login(request)
            .applySchedulers()
    }

    override fun sendCode(request: SendCodeRequest): Observable<SendCodeBody> {
        return dataSource.sendCode(request)
            .applySchedulers()
    }

    override fun verifyCode(request: VerifyCodeRequest): Observable<VerifyCodeBody> {
        return dataSource.verifyCode(request)
            .applySchedulers()
    }

    override fun signUp(request: SignUpRequest): Observable<SignUpBody> {
        return  dataSource.signUp(request)
            .applySchedulers()
    }

    override fun createProfile(request: CreateProfileRequest): Observable<CreateProfileBody> {
        return dataSource.createProfile(request)
            .applySchedulers()
    }

    override fun searchResult(): Observable<BasePatientResponse<List<SearchResultBody>>> {
        return dataSource.searchResult()
            .applySchedulers()
    }

    override fun getHomePatientInfo(): Observable<BasePatientResponse<PatientHomeResponse>> {
        return dataSource.getHomePatientInfo()
            .applySchedulers()
    }

    override fun getSpecialistHome(): Observable<BasePatientResponse<List<SpecialistHomeResponse>>> {
        return dataSource.getSpecialistHome()
            .applySchedulers()
    }

    override fun topDoctor(): Observable<BasePatientResponse<List<TopDoctorResponse>>> {
        return dataSource.topDoctor()
            .applySchedulers()
    }

    override fun doctorDetail(request: DoctorDetailRequest): Observable<BasePatientResponse<DoctorDetailResponse>> {
        return  dataSource.doctorDetail(request)
            .applySchedulers()
    }

    override fun allSpecialist(): Observable<BasePatientResponse<List<AllSpecialistResponse>>> {
        return dataSource.allSpecialist()
            .applySchedulers()
    }

    override fun specialistDetail(request: SpecialistDetailRequest): Observable<BasePatientResponse<List<SpecialistDetailResponse>>> {
        return dataSource.specialistDetail(request)
            .applySchedulers()
    }

    override fun getSpecialist(request: SpecialistDetailRequest): Observable<BasePatientResponse<SpecialistResponse>> {
        return dataSource.getSpecialist(request)
            .applySchedulers()
    }

    override fun getAllComments(request: CommentRequest): Observable<BasePatientResponse<List<CommentResponse>>> {
        return dataSource.getAllComments(request)
            .applySchedulers()
    }

    override fun getAllReply(request: ReplyRequest): Observable<BasePatientResponse<List<ReplyResponse>>> {
        return dataSource.getAllReply(request)
            .applySchedulers()
    }

}