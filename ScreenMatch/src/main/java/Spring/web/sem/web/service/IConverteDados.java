package Spring.web.sem.web.service;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);

}
