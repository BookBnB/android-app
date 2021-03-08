package com.example.bookbnb.network


import android.content.Context
import android.util.Log
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
    @POST("sesiones/google")
    suspend fun authenticateWithGoogle(@Body usuarioDTO: GoogleLoginDTO): LoginResponse

    @POST("sesiones")
    suspend fun authenticate(@Body usuarioDTO: LoginDTO): LoginResponse

    @POST("usuarios")
    suspend fun register(@Body registerDTO: RegisterDTO): RegisterResponse

    @POST("usuarios/google")
    suspend fun registerWithGoogle(@Body registerDTO: GoogleRegisterDTO): RegisterResponse

    @GET("usuarios/{id}")
    suspend fun getUser(@Header("Authorization") token: String,
                        @Path("id") userId: String): User

    @PUT("usuarios/{id}")
    suspend fun editPerfil(@Header("Authorization") userToken: String,
                                      @Path("id") userId: String,
                                      @Body token: User)

    @PUT("usuarios/{id}/dispositivos")
    suspend fun saveNotificationToken(@Header("Authorization") userToken: String,
                                      @Path("id") userId: String,
                                      @Body user: NotificationTokenDTO)

    @PUT("usuarios/{email}/recuperacion")
    suspend fun sendEmailRecuperacion(@Path("email") email: String)

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

    @GET("usuarios/{id}/reservas")
    suspend fun getReservasByUserId(@Header("Authorization") token: String,
                                           @Path("id") userId: String) : List<Reserva>

    @GET("usuarios/bulk")
    suspend fun getUsersInfoById(@Header("Authorization") token: String,
                                           @Query("id") id: List<String>) : List<Usuario>

    @PUT("reservas/{id}/aprobacion")
    suspend fun aceptarReserva(@Header("Authorization") token: String,
                                 @Path("id") reservaId: String) : ReservaAceptadaResponse

    @GET("usuarios/{id}/recomendaciones")
    suspend fun getRecomendaciones(@Header("Authorization") token: String,
                                   @Path("id") userId : String) : List<Publicacion>

    @GET("publicaciones")
    suspend fun searchPublicaciones(@Header("Authorization") token: String,
                                    @Query("coordenadas[latitud]") latitud: Double,
                                    @Query("coordenadas[longitud]") longitud: Double,
                                    @Query("tipoDeAlojamiento") tipoAlojamiento: String?,
                                    @Query("cantidadDeHuespedes") cantHuespedes: Int,
                                    @Query("precioPorNocheMinimo") minPrice: Float,
                                    @Query("precioPorNocheMaximo") maxPrice: Float,
                                    @Query("fechaInicio") fechaInicio: String?,
                                    @Query("fechaFin") fechaFin: String?,
                                    @Query("estado") estado: String = "Creada") : List<Publicacion>

    @GET("publicaciones/{id}/preguntas")
    suspend fun getPreguntasPublicacion(@Header("Authorization") token: String,
                                        @Path("id") publicacionId: String) : List<Pregunta>

    @GET("publicaciones/{id}/calificaciones")
    suspend fun getCalificacionesPublicacion(@Header("Authorization") token: String,
                                        @Path("id") publicacionId: String) : List<Calificacion>

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

    @PUT("reservas/{id}/cancelacion")
    suspend fun cancelarReserva(
        @Header("Authorization") token: String,
        @Path("id") reservaId: String
    ): Unit

    @PUT("reservas/{id}/rechazo")
    suspend fun rechazarReserva(
        @Header("Authorization") token: String,
        @Path("id") reservaId: String
    ): Unit

    @POST("publicaciones/{idPublicacion}/calificaciones")
    suspend fun calificarPublicacion(
        @Header("Authorization") token: String,
        @Path("idPublicacion") publicacionId: String,
        @Body calificacionDTO: Calificacion
    ): Unit
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

    suspend fun saveNotificationToken(notificationToken: String) {
        val userToken = SessionManager(context).fetchAuthToken()
        val userId = SessionManager(context).getUserId()
        if (userToken.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        safeApiCall(Dispatchers.IO) { retrofitService.saveNotificationToken(userToken, userId!!, NotificationTokenDTO(notificationToken)) }
    }

    suspend fun sendEmailRecuperacion(email: String): ResultWrapper<Unit> {
        return safeApiCall(Dispatchers.IO) { retrofitService.sendEmailRecuperacion(email) }
    }

    suspend fun editPerfil(user: User) : ResultWrapper<Unit>{
        val sessMsg = SessionManager(context)
        val token = sessMsg.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.editPerfil(token,
            sessMsg.getUserId()!!, user) }
    }

    suspend fun getUser(userId: String) : ResultWrapper<User>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getUser(token, userId) }
    }

    suspend fun getCalificaciones(publicacionId: String) : ResultWrapper<List<Calificacion>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getCalificacionesPublicacion(token, publicacionId) }
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

    suspend fun getReservasByUserId(userId: String) : ResultWrapper<List<Reserva>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getReservasByUserId(token, userId) }
    }

    suspend fun getUsersInfoById(usersId: List<String>) : ResultWrapper<List<Usuario>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getUsersInfoById(token, usersId) }
    }

    suspend fun getPublicacionesInfoById(publicacionesIds: List<String>): ResultWrapper<List<Publicacion>> {
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        // TODO: Replace with bulk call to avoid multiple GETS
        val publicaciones = mutableListOf<Publicacion>()
        publicacionesIds.forEach{
            when (val response = getPublicacionById(it)) {
                is ResultWrapper.Success -> publicaciones.add(response.value)
            }
        }
        return ResultWrapper.Success<List<Publicacion>>(publicaciones)
    }

    suspend fun aceptarReserva(reservaId: String) : ResultWrapper<ReservaAceptadaResponse>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.aceptarReserva(token, reservaId) }
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

    suspend fun cancelarReserva(id: String): ResultWrapper<Unit> {
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.cancelarReserva(token, id) }
    }

    suspend fun rechazarReserva(id: String): ResultWrapper<Unit> {
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()) {
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.rechazarReserva(token, id) }
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

    suspend fun calificarPublicacion(publicacionId: String, rating: Float, resenia: String) : ResultWrapper<Unit> {
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        val calificacionDTO = Calificacion(rating, resenia)
        return safeApiCall(Dispatchers.IO) { retrofitService.calificarPublicacion(token, publicacionId, calificacionDTO) }
    }

    suspend fun getRecomendaciones(): ResultWrapper<List<Publicacion>>{
        val sessMsg = SessionManager(context)
        val token = sessMsg.fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        return safeApiCall(Dispatchers.IO) { retrofitService.getRecomendaciones(token, sessMsg.getUserId()!!) }
    }

    suspend fun searchPublicaciones(coordenadas: Coordenada,
                                    tipoAlojamiento: String?,
                                    cantHuespedes: Int,
                                    minPrice: Float,
                                    maxPrice: Float,
                                    fechaInicio: Date?,
                                    fechaFin: Date?)
            : ResultWrapper<List<Publicacion>>{
        val token = SessionManager(context).fetchAuthToken()
        if (token.isNullOrEmpty()){
            throw Exception("No hay una sesión establecida")
        }
        val startDate = fechaInicio?.let { SimpleDateFormat(DATE_ISO_FORMAT, Locale.ROOT).format(fechaInicio) }
        val endDate = fechaFin?.let { SimpleDateFormat(DATE_ISO_FORMAT, Locale.ROOT).format(fechaFin) }
        return safeApiCall(Dispatchers.IO) { retrofitService.searchPublicaciones(token,
            coordenadas.latitud,
            coordenadas.longitud,
            if (tipoAlojamiento == "Todos") null else tipoAlojamiento, //If 'Todos'is selected then i pass null to api
            cantHuespedes,
            minPrice,
            maxPrice,
            startDate,
            endDate)
        }
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

    suspend fun register(googleToken: String, rol: String): ResultWrapper<RegisterResponse> {
        val registracion = GoogleRegisterDTO(googleToken, rol)
        val response = safeApiCall(Dispatchers.IO) { retrofitService.registerWithGoogle(registracion) }
        return response
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

    suspend fun authenticate(googleToken: String): ResultWrapper<LoginResponse> {
        val user = GoogleLoginDTO(googleToken)
        return safeApiCall(Dispatchers.IO) { retrofitService.authenticateWithGoogle(user) }
    }

    suspend fun authenticate(username: String, password: String): ResultWrapper<LoginResponse> {
        val user = LoginDTO(username, password)
        return safeApiCall(Dispatchers.IO) { retrofitService.authenticate(user) }
    }

    private suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                Log.d("BookBnbApiService", "ERROR MSG: ${throwable.message}", throwable)
                Log.d("BookBnbApiService", "ERROR CAUSE: ${throwable.cause}")
                throwable.printStackTrace()
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val code = throwable.code()
                        val errorResponse = convertErrorBody(throwable)
                        Log.d("BookBnbApiService", "HTTP ERROR CODE: ${throwable.code()}")
                        Log.d("BookBnbApiService", "HTTP ERROR RESPONSE: ${errorResponse?.toString()}")
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
