package et.safaricom.payment.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Reference Data
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceData {
    @NotBlank
    private String Key;
    @NotBlank
    private String Value;
}
