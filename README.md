# Miau Planner AI

Aplicación Android desarrollada en Kotlin con Jetpack Compose para funcionar como asistente personal de planificación, bienestar y recetas. El proyecto combina una experiencia amigable con temática gatuna, lógica de organización y consumo de datos externos para enriquecer la experiencia del usuario.

## Descripción general

Miau Planner AI está pensado como un asistente personal orientado a la productividad y el bienestar, con una interfaz alegre y un enfoque centrado en el usuario. Hasta el momento, el proyecto incluye:

- una base de datos local de recetas generada desde una API externa
- conexión con Spoonacular para buscar recetas por categoría o ingredientes
- extracción de ingredientes e instrucciones desde JSON de respuesta
- almacenamiento local de cada receta en archivos JSON dentro del directorio de la app
- una estructura modular inicial para continuar agregando lógica de IA, planificación y recomendaciones

Aunque aún se encuentra en desarrollo, ya cuenta con la base técnica para continuar ampliando la app con funcionalidades de asistencia inteligente.

## Stack tecnológico

- Android Studio
- Kotlin
- Jetpack Compose
- Coroutines
- OkHttp
- kotlinx.serialization
- Jsoup
- Gradle

## Estructura del proyecto

```text
miau-planner-ai/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/temp_miau/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── data/
│   │   │   │   │   ├── DatasetBuilder.kt
│   │   │   │   │   └── SpoonacularClient.kt
│   │   │   │   ├── model/
│   │   │   │   │   └── Recipe.kt
│   │   │   │   └── ui/
│   │   │   │       └── theme/
│   │   │   └── res/
│   │   └── ...
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
├── gradlew.bat
├── gradle.properties
├── local.properties
├── README.md
└── ...
```

## ¿Qué ya está implementado?

### 1. Generación de dataset local de recetas

En `DatasetBuilder.kt` se implementa un proceso que:

- recorre consultas de búsqueda como `chicken`, `pasta`, `salad`, `beef`, `dessert`, etc.
- consulta la API de Spoonacular con el endpoint `complexSearch`
- extrae el campo `results`
- procesa cada receta para obtener:
  - título
  - ingredientes
  - instrucciones
  - URL fuente
- guarda cada resultado en archivos JSON dentro de `context.filesDir/recipe_dataset`

Esto permite crear un dataset local de prueba para alimentar la app sin depender siempre de la red.

### 2. Cliente de Spoonacular

En `SpoonacularClient.kt` existe una clase que:

- realiza una llamada a la API de Spoonacular
- obtiene una receta por término de búsqueda
- parsea el JSON con `kotlinx.serialization`
- guarda la receta como JSON localmente
- devuelve un objeto `Recipe` para uso posterior en la app

### 3. Modelo `Recipe`

El modelo `Recipe.kt` define una estructura simple con:

- `title`
- `ingredients: List<String>`
- `instructions: List<String>`
- `sourceUrl`

### 4. Punto de entrada principal

`MainActivity.kt` invoca la construcción del dataset al iniciar la app, con un flujo básico de prueba para verificar que la integración con la API y el guardado local funciona correctamente.

## Flujo actual de la app

1. La app inicia desde `MainActivity`.
2. Se ejecuta `DatasetBuilder`.
3. El builder consulta varias búsquedas a Spoonacular.
4. Cada resultado se convierte en una receta y se guarda localmente como archivo JSON.
5. La app puede reutilizar esos datos para lógica posterior de sugerencias, planificación o recomendaciones.

## Requisitos para ejecutar el proyecto

- Android Studio Hedgehog o superior
- JDK 11+
- Android SDK con API 24+ o la configuración del proyecto
- Dispositivo físico o emulador
- Conexión a internet para consultar la API externa

## Cómo clonar y ejecutar

```bash
git clone https://github.com/tu-usuario/miau-planner-ai.git
cd miau-planner-ai
```

Luego abre la carpeta en Android Studio y sincroniza Gradle.

Haz clic en Run para compilar la app en un emulador o dispositivo real.

## Cómo obtener una API para replicar el proyecto

El proyecto usa Spoonacular para obtener recetas. Para que funcione correctamente, necesitas una clave de API.

### Paso 1: Crear una cuenta en Spoonacular

1. Ingresa a https://spoonacular.com/food-api
2. Crea una cuenta o inicia sesión
3. Busca la sección de API o developer dashboard
4. Genera una nueva clave de API

### Paso 2: Copiar la clave

Una vez creada, guarda tu API key en un lugar seguro. Por ejemplo:

```text
YOUR_API_KEY
```

### Paso 3: Configurar la clave en el proyecto

Actualmente el código de ejemplo tiene claves hardcodeadas en los archivos:

- `app/src/main/java/com/example/temp_miau/data/DatasetBuilder.kt`
- `app/src/main/java/com/example/temp_miau/data/SpoonacularClient.kt`

Para replicar el proyecto de forma segura y profesional, lo recomendable es no dejar la clave en el código fuente. En su lugar, puedes guardarla en `local.properties` o pasarla por `BuildConfig`.

Ejemplo con `local.properties`:

```properties
spoonacularApiKey=TU_CLAVE_AQUI
```

Y luego leerla desde Kotlin antes de hacer las llamadas a Spoonacular.

### Paso 4: Mantenerla segura

- No subas la clave a GitHub
- Agrega `local.properties` a tu `.gitignore`
- Evita publicarla en repositorios públicos
- Para producción, usa variables de entorno o un backend seguro

> Importante: las claves de API pueden tener límites de uso según el plan. Para prototipos y pruebas, la versión gratuita suele ser suficiente, pero si el proyecto crece debes revisar el plan disponible.

## Recomendación de seguridad

El proyecto actualmente usa `apiKey` en hardcode para pruebas rápidas. Para una versión más estable y segura, se recomienda mover la clave a:

- `local.properties`
- `BuildConfig`
- un backend intermedio para ocultar la clave

Esto protege el acceso y evita exponer credenciales en el repositorio.

## Estado actual

El proyecto se encuentra en una fase de desarrollo inicial con bases funcionales para:

- consumo de API externa
- parseo y almacenamiento local de datos
- preparación para agregar funcionalidades de IA y planificación diaria

## Próximos pasos sugeridos

- crear una interfaz de usuario en Compose para mostrar recetas y sugerencias
- agregar autenticación de usuario
- integrar IA para recomendaciones personalizadas
- guardar hábitos, recordatorios y metas de bienestar
- mejorar la gestión de dataset y caché local
- centralizar configuración de API para evitar claves embebidas en código

## Licencia

Este proyecto se encuentra en desarrollo y puede adaptarse según el uso del equipo o la finalidad académica del mismo.

## Autor / equipo

Proyecto desarrollado dentro del contexto de investigación e innovación en IA, planificación personal y bienestar digital.
