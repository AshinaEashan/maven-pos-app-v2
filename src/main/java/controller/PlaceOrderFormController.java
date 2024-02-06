package controller;

import bo.custom.CustomerBo;
import bo.custom.impl.CustomerBoImpl;
import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.OrderTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDao;
import dao.custom.impl.CustomerDaoImpl;
import dao.custom.impl.ItemDaoImpl;
import dao.custom.impl.OrderDaoImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PlaceOrderFormController {

    public BorderPane placeOrderPane;
    @FXML
    private JFXTreeTableView<OrderTm> tblCart;

    @FXML
    private JFXComboBox<?> customerID;

    @FXML
    private JFXComboBox<?> itemCode;

    @FXML
    private JFXTextField customerName;

    @FXML
    private JFXTextField itemDesc;

    @FXML
    private Label orderID;

    @FXML
    private JFXTextField unitPrice;

    @FXML
    private JFXTextField qty;

    @FXML
    private TreeTableColumn orderCode;

    @FXML
    private TreeTableColumn orderDesc;

    @FXML
    private TreeTableColumn orderQty;

    @FXML
    private TreeTableColumn orderAmount;

    @FXML
    private TreeTableColumn orderOption;

    @FXML
    private Label totLabel;

    private CustomerBo customerBo = new CustomerBoImpl();
    private ItemDao itemDao = new ItemDaoImpl();

    private OrderDao orderDao = new OrderDaoImpl();
    private ObservableList<OrderTm> tmList = FXCollections.observableArrayList();

    private List<CustomerDto> customerDtos;
    private List<ItemDto> itemDtos;
    private List<OrderDto> orderDtos;
    private double tot = 0;

    public void initialize(){
        orderCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("Code"));
        orderDesc.setCellValueFactory(new TreeItemPropertyValueFactory<>("Desc"));
        orderQty.setCellValueFactory(new TreeItemPropertyValueFactory<>("Qty"));
        orderAmount.setCellValueFactory(new TreeItemPropertyValueFactory<>("Amount"));
        orderOption.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        generateID();

        loadCustomers();
        loadItems();

        customerID.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, id) -> {
            for (CustomerDto dto : customerDtos) {
                if (dto.getId().equals(id)){
                    customerName.setText(dto.getName());
                }
            }
        });
        itemCode.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, code) -> {
            for (ItemDto dto : itemDtos){
                if (dto.getCode().equals(code)){
                    itemDesc.setText(dto.getDesc());
                    unitPrice.setText(String.valueOf(dto.getUnitPrice()));
                }
            }
        });
        customerName.setEditable(false);
    }

    private void generateID() {
        try {
            orderDtos = orderDao.allOrders();
            if (orderDtos != null) {
                String orderId = orderDtos.get(orderDtos.size() - 1).getOrderId();
                int num = Integer.parseInt(orderId.split("[D]")[1]);
                num++;
                orderID.setText(String.format("D%03d",num));
            }else{
                orderID.setText("D001");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadItems() {
        try {
            itemDtos = itemDao.allItem();
            ObservableList list = FXCollections.observableArrayList();
            for (ItemDto item : itemDtos) {
                list.add(item.getCode());
            }
            itemCode.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void loadCustomers() {
        try {
            customerDtos = customerBo.allCustomers();
            ObservableList list = FXCollections.observableArrayList();
            for (CustomerDto custDto : customerDtos){
                list.add(custDto.getId());
            }
            customerID.setItems(list);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void AddToCartOnAction(ActionEvent event) {
       try {
           if (!qty.getText().isEmpty() && qty.getText() != null) {
               double amount = Double.parseDouble(unitPrice.getText()) * Double.parseDouble(qty.getText());
               JFXButton btn = new JFXButton("Delete");
               btn.setStyle("-fx-font-weight: bold");
               btn.setBackground(Background.fill(Color.rgb(65,165,230)));

               OrderTm orderTm = new OrderTm(
                       itemCode.getValue().toString(),
                       itemDesc.getText(),
                       Integer.parseInt(qty.getText()),
                       amount,
                       btn
               );
               btn.setOnAction((actionEvent -> {
                   tmList.remove(orderTm);
                   tot -= orderTm.getAmount();
                   tblCart.refresh();
                   totLabel.setText(String.format("%.2f",tot));
               }));

               boolean isExist = false;

               for (OrderTm order:tmList) {
                   if (order.getCode().equals(orderTm.getCode())){
                       order.setQty(order.getQty()+orderTm.getQty());
                       order.setAmount(order.getAmount()+orderTm.getAmount());
                       isExist = true;
                       tot += orderTm.getAmount();
                   }
               }

               if (!isExist){
                   tmList.add(orderTm);
                   tot+= orderTm.getAmount();
               }
               customerID.setEditable(false);
           }
           TreeItem<OrderTm> treeObject = new RecursiveTreeItem<>(tmList, RecursiveTreeObject::getChildren);
           tblCart.setRoot(treeObject);
           tblCart.setShowRoot(false);

           totLabel.setText(String.valueOf(String.format("%.2f",tot)));
       }catch (NullPointerException e){
           new Alert(Alert.AlertType.INFORMATION, "Enter Values").show();
       }
    }

    @FXML
    void BackButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) placeOrderPane.getScene().getWindow();
        try {
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
            stage.centerOnScreen();
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void PlaceOrderOnAction(ActionEvent event) {
        try{
            boolean verified = true;
            if (customerName.getText().isEmpty()){
                new Alert(Alert.AlertType.INFORMATION, "Select A Customer").show();
                verified = false;
            }
            if (!tmList.isEmpty() && verified){
                List<OrderDetailsDto> orderDetailsList = new ArrayList<>();
                for (OrderTm orders: tmList ) {
                    OrderDetailsDto orderDetails = new OrderDetailsDto(
                            orderID.getText(),
                            orders.getCode(),
                            Integer.parseInt(String.valueOf(orders.getQty())),
                            orders.getAmount() / orders.getQty()
                    );
                    orderDetailsList.add(orderDetails);
                }

                OrderDto orderDto = new OrderDto(
                        orderID.getText(),
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd")),
                        customerID.getValue().toString(),
                        orderDetailsList
                );
                try {
                    boolean orderPlaced = orderDao.saveOrder(orderDto);
                    if (orderPlaced) {
                        new Alert(Alert.AlertType.INFORMATION, "Order Placed!").show();
                        Refresh();

                    }else{
                        new Alert(Alert.AlertType.ERROR,"Something went wrong!").show();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }

            }
        }catch (NullPointerException e){
            new Alert(Alert.AlertType.INFORMATION, "Enter Values").show();
        }
    }

    private void Refresh() {
        tblCart.setRoot(null);
        customerID.getSelectionModel().clearSelection();
        customerName.setText(null);
        itemCode.getSelectionModel().clearSelection();
        itemDesc.setText(null);
        unitPrice.setText(null);
        qty.setText(null);
        totLabel.setText(null);
        generateID();
    }

    public void RefreshButtonOnAction(ActionEvent actionEvent) {
        Refresh();
    }
}
