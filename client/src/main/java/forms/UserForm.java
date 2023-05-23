package forms;

import console.BlankConsole;
import console.Console;
import console.Printable;
import exceptions.ExceptionInFileMode;
import network.User;
import utility.ScannerManager;

import java.util.Objects;
import java.util.Scanner;

public class UserForm extends Form<User>{
    private final Printable console;
    private final Scanner scanner;

    public UserForm(Printable console) {
        this.console = (ScannerManager.fileMode())
                ? new BlankConsole()
                : console;
        this.scanner = ScannerManager.getScanner();
    }

    @Override
    public User build() {
        return new User(
                askLogin(),
                askPassword()
        );
    }

    public boolean askIfLogin(){
        for(;;) {
            console.print("У вас есть аккаунт? {y|n}  ");
            String input = scanner.nextLine().trim().toLowerCase();
            switch (input){
                case "y", "yes", "да", "д" -> {
                    return true;
                }
                case "n", "no", "нет", "н" -> {
                    return false;
                }
                default -> console.printError("Ответ не распознан");
            }
        }
    }

    private String askLogin(){
        String login;
        while (true){
            console.println("Введите логин");
            login = scanner.nextLine().trim();
            if (login.isEmpty()){
                console.printError("Логин не может быть пустым");
                if (ScannerManager.fileMode()) throw new ExceptionInFileMode();
            }
            else{
                return login;
            }
        }
    }

    private String askPassword(){
        String pass;
        while (true){
            console.println("Введите пароль");
            pass = (Objects.isNull(System.console()))
                    ? scanner.nextLine().trim()
                    : new String(System.console().readPassword());
            if (pass.isEmpty()){
                console.printError("Пароль не может быть пустым");
                if (ScannerManager.fileMode()) throw new ExceptionInFileMode();
            }
            else{
                return pass;
            }
        }
    }
}
