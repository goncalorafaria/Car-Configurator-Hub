package CCH.controller.gestaoDeConfiguracao;

import CCH.CarConfiguratorHubApplication;
import CCH.business.Configuracao;
import CCH.business.GestaoDeConfiguracao;
import CCH.exception.EncomendaRequerOutrosComponentes;
import CCH.exception.EncomendaTemComponentesIncompativeis;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class EncomendaController {
    @FXML public Button back;
    @FXML public TextField nome;
    @FXML public TextField id;
    @FXML public TextField morada;
    @FXML public TextField pais;
    @FXML public TextField email;



    private static Configuracao configuracao;
    public static void setConfiguracao(Configuracao newConfiguracao) {
        configuracao = newConfiguracao;
    }

    private GestaoDeConfiguracao gestaoDeConfiguracao = CarConfiguratorHubApplication.getCch().getGestaoDeConfiguracao();

    @FXML
    public void criarEncomenda() {
        try {
            gestaoDeConfiguracao.criarEncomenda(
                    configuracao,
                    nome.getText(),
                    id.getText(),
                    morada.getText(),
                    pais.getText(),
                    email.getText()
            );

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Sucesso");
            alert.setHeaderText("A Encomenda foi criada com sucesso");
            alert.setContentText(configuracao.getNome() + ", " + configuracao.getPreco() + "€");

            alert.showAndWait();
        } catch (EncomendaRequerOutrosComponentes e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Outros componentes são necessários!");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
        } catch (EncomendaTemComponentesIncompativeis e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Alguns componentes são incompatíveis!");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
        }
    }

    @FXML
    public void back() {
        back.getScene().getWindow().hide();
    }
}