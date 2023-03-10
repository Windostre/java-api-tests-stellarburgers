package site.nomoreparties.stellarburgers.tests.user;

import clients.UserData;
import helpers.Checks;
import helpers.Steps;
import helpers.Utils;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserLoginTests extends Steps {
    private final Utils utils = new Utils();
    private final Checks check = new Checks();
    private String accessToken;
    private UserData basicUserData;

    @Before
    @Step("Выполить предварительные действия для тестов по авторизации")
    public void setUp() throws InterruptedException {
        basicUserData = utils.generateRandomUser();
        ValidatableResponse response = createUser(basicUserData);
        accessToken = response.extract().path("accessToken");
    }

    @After
    @Step("Удалить тестовые данные")
    public void tearDown() throws InterruptedException {
        if (accessToken == null || "".equals(accessToken)) {
            return;
        }
        deleteUser(accessToken);
    }

    @Test
    @DisplayName("Авторизация пользователя. Успешно")
    @Description("Проверяет, что пользователя успешно авторизуется с валидными паролем и почтой." +
            "Получен статус 200 и сообщение получен accessToken и refreshToken")
    public void loginUserSuccessReturnStatus200ok() throws InterruptedException {
        UserData loginData = new UserData()
                .addEmail(basicUserData.getEmail())
                .addPassword(basicUserData.getPassword());

        ValidatableResponse response = login(loginData);
        check.userLoginSuccessfully(response);

    }


    @Test
    @DisplayName("Авторизация пользователя. Неверный email. Провал")
    @Description("Проверяет, что нельзя авторизоваться с неправильной почтой." +
            "Получен статус 401 и сообщение об ошибке")
    public void loginUserFailsWrongEmailReturnStatus401Unauthorized() throws InterruptedException {
        UserData loginData = new UserData()
                .addEmail(utils.generateRandomEmail())
                .addPassword(basicUserData.getPassword());

        ValidatableResponse response = login(loginData);

        check.userLoginFailed(response);
        check.userLoginFailMessageIsCorrect(response);

    }

    @Test
    @DisplayName("Авторизация пользователя. Неверный пароль. Провал")
    @Description("Проверяет, что нельзя авторизоваться с неправильным паролем." +
            "Получен статус 401 и сообщение об ошибке")
    public void loginUserFailsWrongPasswordReturnStatus401Unauthorized() throws InterruptedException {
        UserData loginData = new UserData()
                .addEmail(basicUserData.getEmail())
                .addPassword(utils.generateRandomPassword());

        ValidatableResponse response = login(loginData);

        check.userLoginFailed(response);
        check.userLoginFailMessageIsCorrect(response);

    }

    @Test
    @DisplayName("Авторизация пользователя. Пустой email. Провал")
    @Description("Проверяет, что нельзя авторизоваться без указания почты." +
            "Получен статус 401 и сообщение об ошибке")
    public void loginUserFailsEmptyEmailReturnStatus401Unauthorized() throws InterruptedException {
        UserData loginData = new UserData()
                .addEmail("")
                .addPassword(basicUserData.getPassword());

        ValidatableResponse response = login(loginData);

        check.userLoginFailed(response);
        check.userLoginFailMessageIsCorrect(response);
    }

    @Test
    @DisplayName("Авторизация пользователя. Пустой пароль. Провал")
    @Description("Проверяет, что нельзя авторизоваться без указания пароля." +
            "Получен статус 401 и сообщение об ошибке")
    public void loginUserFailsEmptyPasswordReturnStatus401Unauthorized() throws InterruptedException {
        UserData loginData = new UserData()
                .addEmail(basicUserData.getEmail())
                .addPassword("");

        ValidatableResponse response = login(loginData);

        check.userLoginFailed(response);
        check.userLoginFailMessageIsCorrect(response);
    }

}
