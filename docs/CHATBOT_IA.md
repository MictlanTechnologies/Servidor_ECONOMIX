# Chatbot IA ECONOMIX

## Configuración segura
- Variable obligatoria: `OPENAI_API_KEY`.
- Variables opcionales:
  - `OPENAI_MODEL` (default `gpt-5.5`)
  - `OPENAI_MAX_OUTPUT_TOKENS` (default `700`)
  - `OPENAI_TIMEOUT_SECONDS` (default `20`)

## Endpoint
`POST /economix/api/chatbot/message`

Request:
```json
{ "idUsuario": 1, "mensaje": "¿Cómo voy con mis gastos?", "contextoOpcional": "mes actual" }
```

## Datos usados por IA
Solo datos agregados: ingresos, gastos, balance neto, tasa de ahorro, categorías, presupuestos excedidos/cercanos y estadísticas inferenciales del backend.

## Límites
- Solo educación financiera personal dentro de ECONOMIX.
- No inversiones, créditos, apuestas o cripto.
- Si faltan datos, devuelve `datosInsuficientes=true`.

## Fallback
Si OpenAI falla, el backend responde un análisis básico sin IA.
