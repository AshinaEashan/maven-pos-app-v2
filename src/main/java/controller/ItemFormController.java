package controller;

import com.jfoenix.controls.JFXTextField;
import dto.ItemDto;
import dto.tm.ItemTm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import dao.ItemModel;
import dao.impl.ItemModelImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public class ItemFormController {

    @FXML
    private BorderPane itemPane;

    @FXML
    private JFXTextField itemCodeTxt;

    @FXML
    private JFXTextField itesmDescTxt;

    @FXML
    private JFXTextField itemUnitPriceTxt;

    @FXML
    private JFXTextField itemQtyTxt;

    @FXML
    private JFXTextField itemSearchTxt;

    @FXML
    private TableView<ItemTm> tblItem;

    @FXML
    private TableColumn colCode;

    @FXML
    private TableColumn colDesc;

    @FXML
    private TableColumn colUnitPrice;

    @FXML
    private TableColumn colQty;

    @FXML
    private TableColumn colOption;

    private ItemModel itemModel = new ItemModelImpl();
    private ObservableList<ItemTm> tmList;
    public void initialize(){
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colOption.setCellValueFactory(new PropertyValueFactory<>("btn"));
        loadCustomerTable();

        tblItem.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            setData(newValue);
        });

        itemSearchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            ObservableList<ItemTm> itemTms = FXCollections.observableArrayList();
            for (ItemTm item : tmList) {
                if (item.getCode().contains(newValue) || item.getDesc().contains(newValue)) {
                    itemTms.add(item);
                }
            }
            tblItem.setItems(itemTms);
        });

    }

    private void setData(ItemTm newValue) {
        if (newValue != null){
            itemCodeTxt.setEditable(false);
            itemCodeTxt.setText(newValue.getCode());
            itesmDescTxt.setText(newValue.getDesc());
            itemUnitPriceTxt.setText(String.valueOf(newValue.getUnitPrice()));
            itemQtyTxt.setText(String.valueOf(newValue.getQty()));
        }
    }

    private void deleteCustomer(String code) {
        try {
            boolean isDeleted = itemModel.deleteItem(code);
            if (isDeleted){
                new Alert(Alert.AlertType.INFORMATION,"Item Deleted!").show();
                loadCustomerTable();
            }else{
                new Alert(Alert.AlertType.ERROR,"Something Went Wrong").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadCustomerTable() {
        tmList = FXCollections.observableArrayList();

        try {
            List<ItemDto> dtoList = itemModel.allItem();

            for (ItemDto dto : dtoList){
                Button btn = new Button("Delete");

                ItemTm item = new ItemTm(
                        dto.getCode(),
                        dto.getDesc(),
                        dto.getUnitPrice(),
                        dto.getQty(),
                        btn
                );

                btn.setOnAction(actionEvent -> {
                    deleteCustomer(item.getCode());
                });

                tmList.add(item);
            }

            tblItem.setItems(tmList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void BackButtonOnAction(ActionEvent event) {
        Stage stage = (Stage) itemPane.getScene().getWindow();
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
    void ItemSaveButtonOnAction(ActionEvent event) {
        try {
            boolean isSaved = itemModel.saveItem(new ItemDto(
                    itemCodeTxt.getText(),
                    itesmDescTxt.getText(),
                    Double.parseDouble(itemUnitPriceTxt.getText()),
                    Integer.parseInt(itemQtyTxt.getText())
            ));
            if(isSaved){
                new Alert(Alert.AlertType.INFORMATION,"Item Saved Successfully");
                loadCustomerTable();
                clearFields();
            }
        } catch (SQLIntegrityConstraintViolationException ex){
            new Alert(Alert.AlertType.ERROR,"Duplicate Entry").show();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException | RuntimeException e) {
            new Alert(Alert.AlertType.INFORMATION,"Empty Fields..!").show();
        }
    }


    @FXML
    void ItemUpdateButtonOnAction(ActionEvent event) {
        try {
            boolean isUpdated = itemModel.updateItem(new ItemDto(
                    itemCodeTxt.getText(),
                    itesmDescTxt.getText(),
                    Double.parseDouble(itemUnitPriceTxt.getText()),
                    Integer.parseInt(itemQtyTxt.getText())
            ));
            if (isUpdated){
                new Alert(Alert.AlertType.INFORMATION,"Customer Updated!").show();
                loadCustomerTable();
                clearFields();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }catch(ClassNotFoundException | RuntimeException e){
            new Alert(Alert.AlertType.INFORMATION,"Select a Item.").show();
        }
    }

    @FXML
    void RefreshButtonOnAction(ActionEvent event) {
        loadCustomerTable();
        tblItem.refresh();
        clearFields();
    }

    private void clearFields() {
        tblItem.refresh();
        itemCodeTxt.setEditable(true);
        itemCodeTxt.clear();
        itesmDescTxt.clear();
        itemQtyTxt.clear();
        itemUnitPriceTxt.clear();
        itemSearchTxt.clear();
    }

}
