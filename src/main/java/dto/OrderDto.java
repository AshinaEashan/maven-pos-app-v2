package dto;


import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class OrderDto {
    private String orderId;
    private String date;
    private String customerId;
    private List<OrderDetailsDto> list;
}
