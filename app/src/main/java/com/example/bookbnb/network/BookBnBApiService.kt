package com.example.bookbnb.network


import android.content.Context
import com.example.bookbnb.models.CustomLocation
import com.example.bookbnb.models.Publicacion
import com.example.bookbnb.utils.SessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.io.IOException
import java.lang.Exception

enum class BookBnBApiStatus { LOADING, ERROR, DONE }
private const val BASE_URL: String = com.example.bookbnb.BuildConfig.SERVER_URL

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BookBnBApiService {
    @POST("sesiones")
    suspend fun authenticate(@Body usuarioDTO: LoginDTO) : LoginResponse

    @POST("users")
    suspend fun register(@Body registerDTO: RegisterDTO) : RegisterResponse

    @POST("lugares/direcciones/consulta")
    suspend fun getLocations(@Header("Authorization") token: String, @Body registerDTO: LocationDTO) : List<CustomLocation>

    @POST("")
    suspend fun search(@Header("Authorization") token: String, @Body locationDTO: LocationDTO) : List<Publicacion>
}

class BookBnBApi(var context: Context) {
    val retrofitService : BookBnBApiService by lazy {
        retrofit.create(BookBnBApiService::class.java) }

    suspend fun search(location: String) : ResultWrapper<List<Publicacion>>{
        val locationDTO = LocationDTO(location, 5)
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.search(token, locationDTO) }
    }

    suspend fun getLocations(location: String) : ResultWrapper<List<CustomLocation>>{
        val locationDTO = LocationDTO(location, 5)
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getLocations(token, locationDTO) }
    }

    suspend fun register(email: String,
                         password: String,
                         nombre: String,
                         apellido: String,
                         telefono: String?,
                         ciudad: String?,
                         rol: String) : ResultWrapper<RegisterResponse>{
        val registracion =  RegisterDTO(email, password, nombre, apellido, telefono, ciudad, rol)
        val response = safeApiCall(Dispatchers.IO) { retrofitService.register(registracion) }
        return response
    }

    suspend fun authenticate(username: String, password: String) : ResultWrapper<LoginResponse> {
        val user = LoginDTO(username, password)
        try{
            val response = safeApiCall(Dispatchers.IO) { retrofitService.authenticate(user) }
            return response
        }
        catch (e: Exception){
            //TODO: ???
            var error = e.message
            throw (e)
        }
    }

    private suspend fun <T> safeApiCall(dispatcher: CoroutineDispatcher, apiCall: suspend () -> T): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        ResultWrapper.GenericError(code, errorResponse)
                    }
                    else -> {
                        ResultWrapper.GenericError(null, null)
                    }
                }
            }
        }
    }

    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
        return try {
            throwable.response()?.errorBody()?.source()?.let {
                val moshiAdapter = moshi.adapter(ErrorResponse::class.java)
                moshiAdapter.fromJson(it)
            }
        } catch (exception: Exception) {
            null
        }
    }
}
