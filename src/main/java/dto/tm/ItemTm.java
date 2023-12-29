package dto.tm;

import com.jfoenix.controls.JFXButton;
import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ItemTm {
    private String code;
    private String desc;
    private double unitPrice;
    private int qty;
    private Button btn;
}
