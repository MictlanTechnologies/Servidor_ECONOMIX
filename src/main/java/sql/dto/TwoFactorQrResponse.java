package sql.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorQrResponse {
    private String perfilUsuario;
    private String secreto2fa;
    private String otpauthUrl;
    private String qrCodeUrl;
}
