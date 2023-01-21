package site.nomoreparties.stellarburgers.tests.order;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import site.nomoreparties.stellarburgers.clients.OrderData;
import site.nomoreparties.stellarburgers.clients.UserData;
import site.nomoreparties.stellarburgers.helpers.Checks;
import site.nomoreparties.stellarburgers.helpers.Steps;
import site.nomoreparties.stellarburgers.helpers.Utils;

public class OrderCreationAuthorizedTests extends Steps {
    private final Utils utils = new Utils();
    private OrderData orderData;
    private UserData userData;
    private String accessToken;
    private Checks check = new Checks();


    @Before
    @Step("Выполить предварительные действия для тестов по созданию заказа")
    public void setUp() {
        RestAssured.baseURI = BURGER_BASE_URI;
        userData = utils.generateRandomUser();

        ValidatableResponse createResponse = createUser(userData);
        accessToken = createResponse.extract().path("accessToken").toString().substring(7);

    }

    @After
    @Step("Удалить тестовые данные")
    public void tearDown() {
        if (accessToken == null || "".equals(accessToken)) {
            return;
        }
        deleteUser(accessToken);
    }

    @Test
    @DisplayName("Создание заказа. Пользователь авторизован. Успешно")
    @Description("Проверяет, что можно создать заказ авторизованному пользователю с указанием валидных ингридиентов. " +
            "В ответе возвращен заказ со списком ингридиентов, данные пользователя, дата создания и изменения, id, наименование, номер и цена заказа")
    public void createOrderSuccessReturnStatus200ok() {
        orderData = utils.generateValidIngredientsList(getIngredients());
        ValidatableResponse response = createOrderAuthorized(accessToken, orderData);

        String ownerName = response.extract().path("order.owner.name");
        String ownerEmail = response.extract().path("order.owner.email");

        check.orderCreatedSuccessfully(response);
        check.orderCreatedHasName(response);
        check.orderCreatedHasOrderId(response);
        check.orderCreatedHasIngredients(response);
        check.orderCreatedHasUserData(response, ownerName, ownerEmail);
        check.orderCreatedHasDateCreatedAt(response);
        check.orderCreatedHasDateUpdatedAt(response);
        check.orderCreatedHasStatusDone(response);
        check.orderCreatedHasOrderNumber(response);
        check.orderCreatedHasPrice(response);

    }
}
