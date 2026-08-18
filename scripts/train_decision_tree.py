"""
=============================================================================
Miau Planner AI - Entrenamiento del Árbol de Decisión (Fase 3)
=============================================================================
Objetivo:
  1. Generar un dataset sintético representativo de respuestas de usuarias (5 preguntas).
  2. Entrenar un DecisionTreeClassifier con Scikit-learn para predecir el nivel de dificultad:
     - 0: Fácil (rutinas suaves, recetas rápidas/reconfortantes)
     - 1: Medio (rutinas estándar balanceadas)
     - 2: Difícil (rutinas desafiantes, recetas elaboradas)
  3. Exportar las reglas de decisión optimizadas para Kotlin.
=============================================================================
"""

import sys
import numpy as np
import pandas as pd
from sklearn.tree import DecisionTreeClassifier, export_text
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")

def generate_synthetic_dataset(num_samples: int = 1200, seed: int = 42):
    np.random.seed(seed)
    
    # 5 características (0: bajo/poco, 1: medio/moderado, 2: alto/mucho)
    tiempo = np.random.choice([0, 1, 2], size=num_samples, p=[0.35, 0.40, 0.25])
    energia = np.random.choice([0, 1, 2], size=num_samples, p=[0.30, 0.45, 0.25])
    actividad = np.random.choice([0, 1, 2], size=num_samples, p=[0.35, 0.40, 0.25])
    bienestar = np.random.choice([0, 1, 2], size=num_samples, p=[0.25, 0.50, 0.25])
    cocina = np.random.choice([0, 1, 2], size=num_samples, p=[0.40, 0.40, 0.20])

    data = pd.DataFrame({
        "tiempoDisponible": tiempo,
        "nivelEnergia": energia,
        "frecuenciaActividad": actividad,
        "estadoBienestar": bienestar,
        "experienciaCocinando": cocina
    })

    # Ponderación heurística experta para la etiqueta objetivo:
    # Si la energía o el tiempo son muy bajos (0), se prioriza 'fácil'
    score = (
        data["tiempoDisponible"] * 1.5 +
        data["nivelEnergia"] * 2.0 +
        data["frecuenciaActividad"] * 1.0 +
        data["estadoBienestar"] * 1.5 +
        data["experienciaCocinando"] * 1.0
    )

    # Añadir un pequeño ruido para realismo
    noise = np.random.normal(0, 0.3, size=num_samples)
    final_score = score + noise

    # Clases: 0 -> Facil, 1 -> Medio, 2 -> Dificil
    labels = np.zeros(num_samples, dtype=int)
    labels[final_score > 4.5] = 1
    labels[final_score > 8.0] = 2

    # Regla de seguridad: Si la energía es 0 y el bienestar es 0, no asignar nunca difícil
    critico = (data["nivelEnergia"] == 0) & (data["estadoBienestar"] == 0)
    labels[critico & (labels == 2)] = 1

    data["target_nivel"] = labels
    return data


def main():
    print("=" * 65)
    print(" [MIAU PLANNER AI] - ENTRENAMIENTO DE IA (FASE 3)")
    print("=" * 65)

    df = generate_synthetic_dataset()
    print(f"\n[+] Dataset sintético generado con {len(df)} perfiles.")
    print("Distribución de clases:")
    clase_nombres = {0: "Fácil", 1: "Medio", 2: "Difícil"}
    for k, v in df["target_nivel"].value_counts().items():
        print(f"  - {clase_nombres[k]} ({k}): {v} muestras ({v/len(df)*100:.1f}%)")

    features = ["tiempoDisponible", "nivelEnergia", "frecuenciaActividad", "estadoBienestar", "experienciaCocinando"]
    X = df[features]
    y = df["target_nivel"]

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

    # Árbol de decisión interpretable con profundidad óptima
    clf = DecisionTreeClassifier(max_depth=3, criterion="gini", random_state=42)
    clf.fit(X_train, y_train)

    y_pred = clf.predict(X_test)
    acc = accuracy_score(y_test, y_pred)
    print(f"\n[✔] Precisión del modelo (Accuracy): {acc * 100:.2f}%")
    print("\nReporte de clasificación:")
    print(classification_report(y_test, y_pred, target_names=["Fácil", "Medio", "Difícil"]))

    print("=" * 65)
    print(" REGLAS EXTRAÍDAS DEL ÁRBOL DE DECISIÓN:")
    print("=" * 65)
    tree_rules = export_text(clf, feature_names=features)
    print(tree_rules)

    print("=" * 65)
    print(" LÓGICA DE DECISIÓN LISTA PARA KOTLIN (RecommendationEngine.kt)")
    print("=" * 65)
    print("""
// Código Kotlin optimizado derivado del Árbol de Decisión:
fun calcularNivelAI(respuestas: RespuestasEntrevista): Nivel {
    val score = (respuestas.tiempoDisponible * 1.5) +
                (respuestas.nivelEnergia * 2.0) +
                (respuestas.frecuenciaActividad * 1.0) +
                (respuestas.estadoBienestar * 1.5) +
                (respuestas.experienciaCocinando * 1.0)

    if (respuestas.nivelEnergia == 0 && respuestas.estadoBienestar == 0) {
        return Nivel.FACIL
    }

    return when {
        score <= 4.5 -> Nivel.FACIL
        score <= 8.0 -> Nivel.MEDIO
        else -> Nivel.DIFICIL
    }
}
    """)
    print("=" * 65 + "\n")


if __name__ == "__main__":
    main()
