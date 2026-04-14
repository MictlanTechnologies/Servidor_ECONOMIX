# Cambios Requeridos en Android Studio para Presupuestos, Categorías y Etiquetas

## 📋 Resumen de Cambios Backend

El backend ahora soporta:
- ✅ **Etiquetas (Tags)**: Usuarios pueden crear etiquetas personalizadas (#comida, #urgente, etc.)
- ✅ **Presupuestos**: Monto máximo por categoría, mes y año (único)
- ✅ **Asociación Gasto-Ingreso**: Cada gasto se asocia a UN ingreso
- ✅ **Guardado Compuesto**: Endpoints `/gastos/with-tags` e `/ingresos/with-tags`

---

## 🔄 Cambios en Android

### 1. **Crear Nuevas Entidades**

#### a) `Etiqueta.kt` (modelo local)
```kotlin
@Entity(tableName = "etiquetas")
data class Etiqueta(
    @PrimaryKey
    val idEtiqueta: Int = 0,
    val idUsuario: Int,
    val nombre: String,
    val slug: String, // normalizado automáticamente en backend
    val createdAt: String
)
```

#### b) `GastoEtiqueta.kt` (tabla de relación M2M)
```kotlin
@Entity(
    tableName = "gasto_etiquetas",
    foreignKeys = [
        ForeignKey(entity = Gasto::class, parentColumns = ["idGastos"], childColumns = ["idGastos"]),
        ForeignKey(entity = Etiqueta::class, parentColumns = ["idEtiqueta"], childColumns = ["idEtiqueta"])
    ]
)
data class GastoEtiqueta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idGastos: Int,
    val idEtiqueta: Int
)
```

#### c) `Presupuesto.kt`
```kotlin
@Entity(
    tableName = "presupuestos",
    indices = [Index(value = ["idUsuario", "idCategoria", "mes", "anio"], unique = true)]
)
data class Presupuesto(
    @PrimaryKey
    val idPresupuesto: Int = 0,
    val idUsuario: Int,
    val idCategoria: Int, // FK a Categoria
    val montoMaximo: BigDecimal,
    val mes: Int,
    val anio: Int,
    val createdAt: String
)
```

#### d) Modificar `Gasto.kt`
Agregar estos campos:
```kotlin
@Entity
data class Gasto(
    // ... campos existentes ...
    
    @ColumnInfo(name = "idIngresos")
    val idIngresos: Int, // NUEVO: FK a Ingreso (obligatorio)
    
    // Relación para cargar etiquetas asociadas
    @Relation(
        parentColumn = "idGastos",
        entityColumn = "idEtiqueta",
        associateBy = Junction(GastoEtiqueta::class)
    )
    val etiquetas: List<Etiqueta> = emptyList() // NUEVO
)
```

#### e) Crear `Categoria.kt` (tabla unificada, OPCIONAL si ya tienes separadas)
```kotlin
@Entity(tableName = "categorias")
data class Categoria(
    @PrimaryKey
    val idCategoria: Int = 0,
    val idUsuario: Int,
    val tipo: String, // "GASTO" o "INGRESO"
    val nombre: String,
    val descripcion: String? = null,
    val color: String? = null,
    val createdAt: String
)
```

---

### 2. **Actualizar DTOs**

#### a) `GastoDto.kt`
```kotlin
data class GastoDto(
    val idGastos: Int? = null,
    val idUsuario: Int,
    val descripcionGasto: String,
    val articuloGasto: String,
    val montoGasto: BigDecimal,
    val fechaGastos: LocalDate,
    val periodoGastos: String,
    val idCategoriaPresupuesto: Int, // OBLIGATORIO
    val idIngreso: Int, // NUEVO: OBLIGATORIO
    val etiquetas: List<String>? = null // NUEVO: opcional
)
```

#### b) `IngresoDto.kt`
```kotlin
data class IngresoDto(
    val idIngresos: Int? = null,
    val idUsuario: Int,
    val montoIngreso: BigDecimal,
    val periodicidadIngreso: String,
    val fechaIngresos: LocalDate,
    val descripcionIngreso: String,
    val etiquetas: List<String>? = null // NUEVO: opcional
)
```

#### c) `EtiquetaDto.kt` (NUEVO)
```kotlin
data class EtiquetaDto(
    val idEtiqueta: Int? = null,
    val idUsuario: Int,
    val nombre: String,
    val slug: String,
    val createdAt: String? = null
)
```

#### d) `PresupuestoDto.kt` (NUEVO)
```kotlin
data class PresupuestoDto(
    val idPresupuesto: Int? = null,
    val idUsuario: Int,
    val idCategoria: Int,
    val montoMaximo: BigDecimal,
    val mes: Int,
    val anio: Int,
    val createdAt: String? = null
)
```

#### e) `GastoWithTagsRequestDto.kt` (NUEVO)
```kotlin
data class GastoWithTagsRequestDto(
    val movimiento: GastoDto,
    val etiquetas: List<String>? = null
)
```

#### f) `IngresoWithTagsRequestDto.kt` (NUEVO)
```kotlin
data class IngresoWithTagsRequestDto(
    val movimiento: IngresoDto,
    val etiquetas: List<String>? = null
)
```

---

### 3. **Actualizar DAOs (Room)**

#### a) Crear `EtiquetaDao.kt`
```kotlin
@Dao
interface EtiquetaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEtiqueta(etiqueta: Etiqueta)
    
    @Query("SELECT * FROM etiquetas WHERE idUsuario = :idUsuario")
    suspend fun getEtiquetasPorUsuario(idUsuario: Int): List<Etiqueta>
    
    @Query("SELECT * FROM etiquetas WHERE idUsuario = :idUsuario AND slug = :slug")
    suspend fun getEtiquetaBySlug(idUsuario: Int, slug: String): Etiqueta?
    
    @Delete
    suspend fun deleteEtiqueta(etiqueta: Etiqueta)
}
```

#### b) Crear `GastoEtiquetaDao.kt`
```kotlin
@Dao
interface GastoEtiquetaDao {
    @Insert
    suspend fun insertGastoEtiqueta(gastoEtiqueta: GastoEtiqueta)
    
    @Query("DELETE FROM gasto_etiquetas WHERE idGastos = :idGastos")
    suspend fun deleteByIdGastos(idGastos: Int)
    
    @Query("""
        SELECT e.* FROM etiquetas e
        INNER JOIN gasto_etiquetas ge ON e.idEtiqueta = ge.idEtiqueta
        WHERE ge.idGastos = :idGastos
    """)
    suspend fun getEtiquetasByGasto(idGastos: Int): List<Etiqueta>
}
```

#### c) Crear `PresupuestoDao.kt`
```kotlin
@Dao
interface PresupuestoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresupuesto(presupuesto: Presupuesto)
    
    @Query("SELECT * FROM presupuestos WHERE idUsuario = :idUsuario")
    suspend fun getPresupuestosPorUsuario(idUsuario: Int): List<Presupuesto>
    
    @Query("""
        SELECT * FROM presupuestos 
        WHERE idUsuario = :idUsuario AND idCategoria = :idCategoria 
        AND mes = :mes AND anio = :anio
    """)
    suspend fun getPresupuesto(
        idUsuario: Int,
        idCategoria: Int,
        mes: Int,
        anio: Int
    ): Presupuesto?
}
```

---

### 4. **Actualizar APIs/Retrofit**

#### a) `GastoApi.kt` - Agregar endpoint con tags
```kotlin
@POST("/economix/api/gastos/with-tags")
suspend fun guardarGastoConEtiquetas(
    @Body request: GastoWithTagsRequestDto
): Response<GastoDto>
```

#### b) `IngresoApi.kt` - Agregar endpoint con tags
```kotlin
@POST("/economix/api/ingresos/with-tags")
suspend fun guardarIngresoConEtiquetas(
    @Body request: IngresoWithTagsRequestDto
): Response<IngresoDto>
```

#### c) Crear `EtiquetaApi.kt` (NUEVO)
```kotlin
@GET("/economix/api/etiquetas")
suspend fun obtenerEtiquetas(): Response<List<EtiquetaDto>>

@POST("/economix/api/etiquetas")
suspend fun crearEtiqueta(@Body nombre: String): Response<EtiquetaDto>

@DELETE("/economix/api/etiquetas/{idEtiqueta}")
suspend fun eliminarEtiqueta(@Path("idEtiqueta") idEtiqueta: Int): Response<Unit>
```

#### d) Crear `PresupuestoApi.kt` (NUEVO)
```kotlin
@GET("/economix/api/presupuestos")
suspend fun obtenerPresupuestos(): Response<List<PresupuestoDto>>

@POST("/economix/api/presupuestos")
suspend fun crearPresupuesto(@Body presupuesto: PresupuestoDto): Response<PresupuestoDto>

@PUT("/economix/api/presupuestos/{idPresupuesto}")
suspend fun actualizarPresupuesto(
    @Path("idPresupuesto") idPresupuesto: Int,
    @Body presupuesto: PresupuestoDto
): Response<PresupuestoDto>
```

---

### 5. **Actualizar Repositorios**

#### a) `GastoRepository.kt` - Agregar método con tags
```kotlin
suspend fun guardarConEtiquetas(
    gastoDto: GastoDto,
    etiquetas: List<String>?
): Result<GastoDto> {
    return try {
        val request = GastoWithTagsRequestDto(
            movimiento = gastoDto,
            etiquetas = etiquetas
        )
        val response = gastoApi.guardarGastoConEtiquetas(request)
        if (response.isSuccessful && response.body() != null) {
            Result.Success(response.body()!!)
        } else {
            Result.Error("Error al guardar gasto: ${response.code()}")
        }
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error desconocido")
    }
}
```

#### b) `IngresoRepository.kt` - Agregar método con tags
```kotlin
suspend fun guardarConEtiquetas(
    ingresoDto: IngresoDto,
    etiquetas: List<String>?
): Result<IngresoDto> {
    return try {
        val request = IngresoWithTagsRequestDto(
            movimiento = ingresoDto,
            etiquetas = etiquetas
        )
        val response = ingresoApi.guardarIngresoConEtiquetas(request)
        if (response.isSuccessful && response.body() != null) {
            Result.Success(response.body()!!)
        } else {
            Result.Error("Error al guardar ingreso: ${response.code()}")
        }
    } catch (e: Exception) {
        Result.Error(e.message ?: "Error desconocido")
    }
}
```

#### c) Crear `EtiquetaRepository.kt` (NUEVO)
```kotlin
class EtiquetaRepository(
    private val etiquetaApi: EtiquetaApi,
    private val etiquetaDao: EtiquetaDao
) {
    suspend fun obtenerEtiquetas(): Result<List<EtiquetaDto>> {
        return try {
            val response = etiquetaApi.obtenerEtiquetas()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error al obtener etiquetas")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de red")
        }
    }
    
    suspend fun crearEtiqueta(nombre: String): Result<EtiquetaDto> {
        return try {
            val response = etiquetaApi.crearEtiqueta(nombre)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al crear etiqueta")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error de red")
        }
    }
}
```

#### d) Crear `PresupuestoRepository.kt` (NUEVO)
Similar a EtiquetaRepository pero para presupuestos

---

### 6. **Actualizar ViewModels/UseCases**

#### a) En `GastoFormularioViewModel` o similar
```kotlin
fun guardarGastoConEtiquetas(
    gastoDto: GastoDto,
    etiquetas: List<String>
) {
    viewModelScope.launch {
        _uiState.value = GastoUiState.Loading
        val resultado = gastoRepository.guardarConEtiquetas(gastoDto, etiquetas)
        when (resultado) {
            is Result.Success -> {
                _uiState.value = GastoUiState.Success(resultado.data)
            }
            is Result.Error -> {
                _uiState.value = GastoUiState.Error(resultado.exception)
            }
        }
    }
}
```

---

### 7. **Actualizar UI (Fragmentos/Activities)**

#### a) En tu formulario de Gastos:
```kotlin
// 1. Campo de selector de Ingreso (NUEVO - obligatorio)
// Cargar lista de ingresos y mostrar spinner/dropdown

// 2. Campo de etiquetas (NUEVO - opcional)
// Mostrar chips con etiquetas existentes
// Permitir agregar nuevas escribiendo y presionando +

// 3. Al guardar:
val gastoDto = GastoDto(
    idUsuario = usuarioId,
    descripcionGasto = descripcion,
    articuloGasto = articulo,
    montoGasto = monto,
    fechaGastos = fecha,
    periodoGastos = periodo,
    idCategoriaPresupuesto = categoriaSeleccionada.id,
    idIngreso = ingresoSeleccionado.id, // NUEVO
    etiquetas = listOfEtiquetas // NUEVO
)

gastoViewModel.guardarGastoConEtiquetas(gastoDto, etiquetas)
```

#### b) Crear UI de Etiquetas
```kotlin
// Mostrar etiquetas como chips
// Permitir:
// - Seleccionar etiquetas existentes
// - Crear nuevas etiquetas al escribir
// - Remover etiquetas seleccionadas

// Ejemplo con Material Chips:
ChipGroup {
    etiquetas.forEach { etiqueta ->
        Chip(
            text = etiqueta.nombre,
            onClose = { removerEtiqueta(etiqueta) }
        )
    }
}
```

---

## 🔐 Consideraciones de Seguridad

1. **El backend obtiene el usuarioId de la sesión**, no del request
   - En Android: El token/sesión debe enviarse en cada request
   - Usa interceptores de Retrofit para agregar headers de autenticación

2. **Validaciones en backend**:
   - Verifica que el usuario sea dueño de la categoría/etiqueta/presupuesto
   - No confía en el idUsuario del request body

---

## 📝 Checklist de Implementación

- [ ] Crear entidades (Etiqueta, GastoEtiqueta, Presupuesto, Categoria)
- [ ] Crear DAOs para Room
- [ ] Actualizar DTOs (agregar campos a Gasto/Ingreso, crear nuevos)
- [ ] Crear APIs Retrofit (endpoints con-tags, CRUD de etiquetas/presupuestos)
- [ ] Actualizar repositorios con métodos saveWithTags
- [ ] Actualizar ViewModels
- [ ] Actualizar UI (formularios, selectors, chips)
- [ ] Probar integración completa

---

## 🧪 Flujo de Prueba

1. **Crear un ingreso** (ya existe)
2. **Crear un gasto**:
   - Seleccionar ingreso asociado (NUEVO)
   - Seleccionar categoría
   - Ingresar monto, fecha, descripción
   - Agregar etiquetas (opcional)
   - Guardar → usa `/gastos/with-tags`
3. **Verificar en backend**:
   - Gasto guardado con idIngresos
   - Etiquetas creadas y asociadas
   - Validación de presupuesto funcionando

---

Si tienes dudas sobre algún paso específico, pregunta y te explico más. 🚀
