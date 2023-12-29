package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderTm extends RecursiveTreeObject<OrderTm> {
    private String Code;
    private String Desc;
    private int Qty;
    private double Amount;
    private JFXButton btn;
}
