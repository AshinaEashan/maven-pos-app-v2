package dto.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.control.Button;
import lombok.*;

import javax.swing.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrdersTm extends RecursiveTreeObject<OrdersTm> {
    private String orderID;
    private String date;
    private double amount;
    private String custName;
    private Button btn;

}
