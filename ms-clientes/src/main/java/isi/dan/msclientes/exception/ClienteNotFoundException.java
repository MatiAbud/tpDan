package isi.dan.msclientes.exception;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(String msg) {
        super(msg);
    }
}
