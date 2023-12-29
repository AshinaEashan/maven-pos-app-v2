package dto.tm;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.scene.control.Button;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CustomerTm  {
    private String id;
    private String name;
    private String address;
    private double salary;
    private Button btn;
}


//extends RecursiveTreeObject<CustomerTm>