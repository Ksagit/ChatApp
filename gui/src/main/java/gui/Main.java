package gui;

public class Main {
    public static void main(String[] args) {
        Client obj2 = new Client("127.0.0.1", 1234);
        new ChatGui(obj2);
    }
}