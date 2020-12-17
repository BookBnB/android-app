package com.example.bookbnb.network


import android.content.Context
import com.example.bookbnb.models.*
import com.example.bookbnb.utils.SessionManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


interface BookBnBApiService {
    @POST("sesiones")
    suspend fun authenticate(@Body usuarioDTO: LoginDTO): LoginResponse

    @POST("users")
    suspend fun register(@Body registerDTO: RegisterDTO): RegisterResponse

    @POST("lugares/direcciones/consulta")
    suspend fun getLocations(
        @Header("Authorization") token: String,
        @Body locationDTO: LocationDTO
    ): List<CustomLocation>

    @POST("publicaciones")
    suspend fun createPublicacion(
        @Header("Authorization") token: String,
        @Body publicacionDTO: Publicacion
    ): CrearPublicacionResponse

    @POST("lugares/ciudades/consulta")
    suspend fun getCities(
        @Header("Authorization") token: String,
        @Body locationDTO: LocationDTO
    ): List<CustomLocation>

    @GET("publicaciones/{id}")
    suspend fun getPublicationById(@Header("Authorization") token: String,
                                   @Path("id") publicacionId: String) : Publicacion

    @GET("usuarios/{id}/publicaciones")
    suspend fun getPublicationsByAnfitrionId(@Header("Authorization") token: String,
                                             @Path("id") anfitrionId: String) : List<Publicacion>

    @GET("publicaciones/{id}/reservas")
    suspend fun getReservasByPublicacionId(@Header("Authorization") token: String,
                                             @Path("id") publicacionId: String) : List<Reserva>

    @GET("usuarios/bulk")
    suspend fun getUsersInfoById(@Header("Authorization") token: String,
                                           @Query("id") id: String) : List<Usuario>

    @GET("publicaciones")
    suspend fun searchPublicaciones(@Header("Authorization") token: String,
                                    @Query("coordenadas[latitud]") latitud: Double,
                                    @Query("coordenadas[longitud]") longitud: Double,
                                    @Query("tipoDeAlojamiento") tipoAlojamiento: String?,
                                    @Query("cantidadDeHuespedes") cantHuespedes: Int,
                                    @Query("precioPorNocheMinimo") minPrice: Float,
                                    @Query("precioPorNocheMaximo") maxPrice: Float) : List<Publicacion>

    @GET("publicaciones")
    suspend fun searchByCityCoordinates(@Header("Authorization") token: String,
                                        @Query("coordenadas[latitud]") latitud: Double,
                                        @Query("coordenadas[longitud]") longitud: Double) : List<Publicacion>


    @GET("publicaciones/{id}/preguntas")
    suspend fun getPreguntasPublicacion(@Header("Authorization") token: String,
                                        @Path("id") publicacionId: String) : List<Pregunta>

    @POST("publicaciones/{id}/preguntas")
    suspend fun postPreguntaPublicacion(
        @Header("Authorization") token: String,
        @Path("id") publicacionId: String,
        @Body preguntaDTO: Pregunta
    ): Pregunta

    @POST("publicaciones/{idPublicacion}/preguntas/{idPregunta}/respuesta")
    suspend fun postRespuestaPreguntaPublicacion(
        @Header("Authorization") token: String,
        @Path("idPublicacion") publicacionId: String,
        @Path("idPregunta") preguntanId: String,
        @Body respuestaDTO: Respuesta
    ): Pregunta

    @POST("reservas")
    suspend fun reservarPublicacion(
        @Header("Authorization") token: String,
        @Body reservaDTO: ReservaDTO
    ): ReservarPublicacionResponse
}

class BookBnBApi(var context: Context) {

    companion object{
        private const val BASE_URL: String = com.example.bookbnb.BuildConfig.SERVER_URL
        private const val DATE_ISO_FORMAT: String = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .build()

        private val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(BASE_URL)
            .build()
    }

    private val retrofitService: BookBnBApiService by lazy {
        retrofit.create(BookBnBApiService::class.java)
    }

    suspend fun getPreguntas(publicacionId: String) : ResultWrapper<List<Pregunta>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getPreguntasPublicacion(token, publicacionId) }
    }

    suspend fun realizarPregunta(publicacionId: String, pregunta: String) : ResultWrapper<Pregunta>{
        val preguntaDTO = Pregunta(null,null, pregunta, null,null)
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.postPreguntaPublicacion(token, publicacionId, preguntaDTO) }
    }

    suspend fun responderPregunta(publicacionId: String, preguntaId: String, respuesta: String)  : ResultWrapper<Pregunta> {
        val respuestaDTO = Respuesta(null, respuesta, null)
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.postRespuestaPreguntaPublicacion(token,
            publicacionId,
            preguntaId,
            respuestaDTO) }
    }

    suspend fun getPublicationsByAnfitrionId(anfitrionId: String) : ResultWrapper<List<Publicacion>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getPublicationsByAnfitrionId(token, anfitrionId) }
    }

    suspend fun getReservasByPublicacionId(publicacionId: String) : ResultWrapper<List<Reserva>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getReservasByPublicacionId(token, publicacionId) }
    }

    suspend fun getUsersInfoById(usersId: String) : ResultWrapper<List<Usuario>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getUsersInfoById(token, usersId) }
    }

    suspend fun reservarPublicacion(
        publicacionId: String,
        startDate: Date,
        endDate: Date
    ) : ResultWrapper<ReservarPublicacionResponse> {
        val reservaDTO = ReservaDTO(publicacionId,
            SimpleDateFormat(DATE_ISO_FORMAT).format(startDate),
            SimpleDateFormat(DATE_ISO_FORMAT).format(endDate))
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.reservarPublicacion(token, reservaDTO) }
    }

    suspend fun createPublicacion(
        titulo: String,
        desc: String,
        price: Float,
        location: CustomLocation,
        cantHuespedes: Int,
        imagesURLs: List<String>,
        tipoAlojamiento: String
    ) : ResultWrapper<CrearPublicacionResponse> {
        val publicacionDTO = Publicacion(
            titulo = titulo,
            descripcion = desc,
            precioPorNoche = price,
            direccion = location,
            cantidadDeHuespedes = cantHuespedes,
            imagenes = imagesURLs.map{ url -> CustomImage(url) },
            tipoDeAlojamiento = tipoAlojamiento
        )
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.createPublicacion(token, publicacionDTO) }
    }

    suspend fun getPublicacionById(publicacionId: String) : ResultWrapper<Publicacion> {
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getPublicationById(token, publicacionId) }
    }

    suspend fun searchPublicaciones(coordenadas: Coordenada,
                                    tipoAlojamiento: String?,
                                    cantHuespedes: Int,
                                    minPrice: Float,
                                    maxPrice: Float)
            : ResultWrapper<List<Publicacion>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.searchPublicaciones(token,
            coordenadas.latitud,
            coordenadas.longitud,
            if (tipoAlojamiento == "Todos") null else tipoAlojamiento, //If Todos is selected then i pass null to api
            cantHuespedes,
            minPrice,
            maxPrice)
        }
    }

    suspend fun searchByCityCoordinates(coordenadas: Coordenada) : ResultWrapper<List<Publicacion>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.searchByCityCoordinates(token, coordenadas.latitud, coordenadas.longitud) }
    }

    suspend fun getCities(location: String): ResultWrapper<List<CustomLocation>> {
        val locationDTO = LocationDTO(location, 5)
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getCities(token, locationDTO) }
    }

    suspend fun getLocations(location: String): ResultWrapper<List<CustomLocation>> {
        val locationDTO = LocationDTO(location, 5)
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getLocations(token, locationDTO) }
    }

    suspend fun register(
        email: String,
        password: String,
        nombre: String,
        apellido: String,
        telefono: String?,
        ciudad: String?,
        rol: String
    ): ResultWrapper<RegisterResponse> {
        val registracion = RegisterDTO(email, password, nombre, apellido, telefono, ciudad, rol)
        val response = safeApiCall(Dispatchers.IO) { retrofitService.register(registracion) }
        return response
    }

    suspend fun authenticate(username: String, password: String): ResultWrapper<LoginResponse> {
        val user = LoginDTO(username, password)
        try {
            return safeApiCall(Dispatchers.IO) { retrofitService.authenticate(user) }
        } catch (e: Exception) {
            //TODO: ???
            throw (e)
        }
    }

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
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
