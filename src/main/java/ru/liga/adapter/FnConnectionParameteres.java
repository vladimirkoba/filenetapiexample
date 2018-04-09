package ru.liga.adapter;


/**
 * Параметры для подключения к файлнету
 */
public class FnConnectionParameteres {
    private final String ceUrl;
    private final String login;
    private final String password;
    private final String osName;
    private final String rootFolderName;

    /**
     *
     * @param ceUrl    - ссылка на CE WS Definition. Для сферы обычный путь - http://192.168.199.129:9080/wsi/FNCEWS40MTOM/
     * @param login    - логин технического пользователя
     * @param password - пароль технического пользователя
     * @param osName   - имя ObjectStore
     * @param rootFolderName - имя корневой папке, в которую будут класться все документы и подпапки. В примере это /cbrf
     */
    public FnConnectionParameteres(String ceUrl, String login, String password, String osName, String rootFolderName) {
        this.ceUrl = ceUrl;
        this.login = login;
        this.password = password;
        this.osName = osName;
        this.rootFolderName = rootFolderName;
    }

    public String ceUrl() {
        return ceUrl;
    }

    public String login() {
        return login;
    }

    public String password() {
        return password;
    }

    public String osName() {
        return osName;
    }

    public String rootFolderName() {
        return rootFolderName;
    }
}
