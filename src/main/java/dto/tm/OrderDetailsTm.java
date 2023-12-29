package dto.tm;


import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderDetailsTm extends RecursiveTreeObject<OrderDetailsTm> {
    private String itemCode;
    private String name;
    private int qty;
    private double unitPrice;
    private double total;
}
