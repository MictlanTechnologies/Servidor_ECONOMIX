CREATE TABLE IF NOT EXISTS tbl_chatbot_conversation (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  mensaje_usuario VARCHAR(1500) NOT NULL,
  respuesta_ia TEXT,
  fecha_hora DATETIME NOT NULL,
  resumen_datos_usado VARCHAR(2000),
  nivel_riesgo_financiero VARCHAR(20)
);
