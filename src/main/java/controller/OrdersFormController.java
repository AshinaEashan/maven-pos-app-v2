package controller;

import bo.custom.CustomerBo;
import bo.custom.impl.CustomerBoImpl;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dao.custom.CustomerDao;
import dao.custom.ItemDao;
import dao.custom.OrderDetailsDao;
import dao.custom.OrderDao;
import dao.custom.impl.CustomerDaoImpl;
import dao.custom.impl.ItemDaoImpl;
import dao.custom.impl.OrderDetailDaoImpl;
import dao.custom.impl.OrderDaoImpl;
import dto.CustomerDto;
import dto.ItemDto;
import dto.OrderDetailsDto;
import dto.OrderDto;
import dto.tm.OrderDetailsTm;
import dto.tm.OrdersTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrdersFormController {

    public BorderPane ordersPane;
    public TreeTableColumn option;
    public JFXTreeTableView<OrdersTm> ordersTbl;
    public JFXTreeTableView<OrderDetailsTm> orderDetailsTbl;
    @FXML
    private JFXTextField orderIdSearch;

    @FXML
    private TreeTableColumn orderId;

    @FXML
    private TreeTableColumn date;

    @FXML
    private TreeTableColumn amount;

    @FXML
    private TreeTableColumn custName;

    @FXML
    private TreeTableColumn itemCode;

    @FXML
    private TreeTableColumn itemName;

    @FXML
    private TreeTableColumn qty;

    @FXML
    private TreeTableColumn unitPrice;

    @FXML
    private TreeTableColumn total;

    OrderDetailsDao orderDetailsDao = new OrderDetailDaoImpl();

    OrderDao orderDao = new OrderDaoImpl();

    private CustomerBo customerBo = new CustomerBoImpl();
    ItemDao itemDao = new ItemDaoImpl();
    List<OrderDetailsDto> orderDetails;

    ObservableList<OrdersTm> orderTable ;
    public void initialize(){
        orderId.setCellValueFactory(new TreeItemPropertyValueFactory<>("orderID"));
        date.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
        amount.setCellValueFactory(new TreeItemPropertyValueFactory<>("amount"));
        custName.setCellValueFactory(new TreeItemPropertyValueFactory<>("custName"));
        option.setCellValueFactory(new TreeItemPropertyValueFactory<>("btn"));

        itemCode.setCellValueFactory(new TreeItemPropertyValueFactory<>("itemCode"));
        itemName.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        qty.setCellValueFactory(new TreeItemPropertyValueFactory<>("qty"));
        unitPrice.setCellValueFactory(new TreeItemPropertyValueFactory<>("unitPrice"));
        total.setCellValueFactory(new TreeItemPropertyValueFactory<>("total"));

        loadOrderTable();


        ordersTbl.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            try{
                loadOrderDetailsTable(newValue.getValue());
            }catch (NullPointerException e){
                new Alert(Alert.AlertType.INFORMATION,"Select A Order").show();
            }

        });
        orderIdSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<OrdersTm> ordersTms = FXCollections.observableArrayList();
            for (OrdersTm order : orderTable) {
                if (order.getOrderID().contains(newValue)) {
                    ordersTms.add(order);
                }
            }
            ordersTbl.setShowRoot(true);
            TreeItem<OrdersTm> treeObject = new RecursiveTreeItem<>(ordersTms, RecursiveTreeObject::getChildren);
            ordersTbl.setRoot(treeObject);
            ordersTbl.setShowRoot(false);


        });

    }

    private void loadOrderDetailsTable(OrdersTm order) {
        try {
            List<ItemDto> itemDetails = itemDao.allItem();
            ObservableList<OrderDetailsTm> orderDetailsTable = FXCollections.observableArrayList();

            for (OrderDetailsDto detailsDto: orderDetails) {
                String itemName1 = " ";
                for (int i = 0; i < itemDetails.size(); i++) {
                    if (detailsDto.getItemCode().equals(itemDetails.get(i).getCode())){
                        itemName1 = itemDetails.get(i).getDesc();
                        break;
                    }
                }
                if (order.getOrderID().equals(detailsDto.getOrderId())){
                    orderDetailsTable.add(new OrderDetailsTm(
                            detailsDto.getItemCode(),
                            itemName1,
                            detailsDto.getQty(),
                            detailsDto.getUnitePrice(),
                            detailsDto.getQty() * detailsDto.getUnitePrice()
                    ));
                }

            }

            TreeItem<OrderDetailsTm> treeObject = new RecursiveTreeItem<>(orderDetailsTable, RecursiveTreeObject::getChildren);
            orderDetailsTbl.setRoot(treeObject);
            orderDetailsTbl.setShowRoot(false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    private void loadOrderTable() {

        try {
             orderTable = FXCollections.observableArrayList();
            List<OrderDto> orders = orderDao.allOrders();
            orderDetails = orderDetailsDao.getAllOrderDetails();
            List<CustomerDto> customerDtos = customerBo.allCustomers();



            for (OrderDto order: orders) {
                Button btn = new Button("Delete");

                double amount = 0;
                String custName = " ";

                for (int i = 0; i < orderDetails.size(); i++) {
                    if (order.getOrderId().equals(orderDetails.get(i).getOrderId())){
                        amount += orderDetails.get(i).getUnitePrice() * orderDetails.get(i).getQty();
                    }
                }
                for (CustomerDto cust:customerDtos) {
                    if (order.getCustomerId().equals(cust.getId())){
                        custName = cust.getName();
                        break;
                    }
                }

                OrdersTm ordersTm = new OrdersTm(
                        order.getOrderId(),
                        order.getDate(),
                        amount,
                        custName,
                        btn
                );
                btn.setOnAction(actionEvent -> {
                    deleteOrder(ordersTm.getOrderID());
                });
                orderTable.add(ordersTm);


            }

            TreeItem<OrdersTm> treeObject = new RecursiveTreeItem<>(orderTable, RecursiveTreeObject::getChildren);
            ordersTbl.setRoot(treeObject);
            ordersTbl.setShowRoot(false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void deleteOrder(String orderID)  {
        try {
            refreshBtn();
            boolean isDeleted =  orderDao.deleteOrder(orderID);
            if (isDeleted){

                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadOrderTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    void BackButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) ordersPane.getScene().getWindow();
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
    void RefreshButtonOnAction(ActionEvent event) {
        refreshBtn();
    }
    private void refreshBtn(){
        orderIdSearch.clear();
        orderDetailsTbl.setRoot(null);
    }

}
