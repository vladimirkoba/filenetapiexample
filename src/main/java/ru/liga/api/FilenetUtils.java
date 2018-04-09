package ru.liga.api;


import com.filenet.api.collection.ContentElementList;
import com.filenet.api.constants.AutoUniqueName;
import com.filenet.api.constants.DefineSecurityParentage;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.*;
import com.filenet.api.util.UserContext;
import com.google.common.base.Splitter;
import ru.liga.adapter.CbrfDocument;

import javax.security.auth.Subject;
import java.util.List;
import java.util.stream.Collectors;

public class FilenetUtils {

    /**
     * Подключиться к файлнету и получить ObjectStore - базовый объект для работы с файлнетом
     *
     * @param ceUri    - ссылка на CE WS Definition. Для сферы обычный путь - http://192.168.199.129:9080/wsi/FNCEWS40MTOM/
     * @param login    - логин технического пользователя
     * @param password - пароль технического пользователя
     * @param osName   - имя ObjectStore
     * @return
     */
    public static ObjectStore connectAndGetObjectStore(String ceUri, String login, String password, String osName) {
        Connection conn = Factory.Connection.getConnection(ceUri);
        Subject sub = UserContext.createSubject(conn, login, password, null);
        UserContext.get().pushSubject(sub);
        Domain dom = Factory.Domain.getInstance(conn, null);
        return Factory.ObjectStore.fetchInstance(dom, osName, null);
    }

    /**
     * @param os         ObjectStore
     * @param doc        документ, который нужно поместить в папку
     * @param folderPath путь до папки, куда нужно поместить документ
     */
    public static void moveDocumentToFolder(ObjectStore os, Document doc, String folderPath) {
        Folder fo = Factory.Folder.fetchInstance(os, folderPath, null);
        ReferentialContainmentRelationship rcr;
        rcr = fo.file(doc, AutoUniqueName.AUTO_UNIQUE,
                doc.get_Name(), DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
        rcr.save(RefreshMode.NO_REFRESH);
    }

    /**
     * Проверяет, существует ли в файлнете папка. Если не существует - создает её
     *
     * @param os                       ObjectStore
     * @param rootFolderPath           Корневая папка для всех документов. В тестовом примере - /cbrf
     * @param destinationFilenetFolder Папка, куда нужно положить документ
     */
    public static void createFolderHierarchyIfNeeded(ObjectStore os, String rootFolderPath, String destinationFilenetFolder) {
        if (isFolderExist(os, destinationFilenetFolder)) {
            return;
        }
        List<String> folderNamesExcludeRoot = folderNamesExlcludeRoot(destinationFilenetFolder);
        Folder rootFolder = Factory.Folder.fetchInstance(os, rootFolderPath, null);
        for (String folderName : folderNamesExcludeRoot) {
            if (!isFolderExist(os, rootFolderPath + "/" + folderName)) {
                rootFolder = rootFolder.createSubFolder(folderName);
                rootFolder.save(RefreshMode.REFRESH);
            }
            rootFolderPath = rootFolder.get_PathName();
        }
    }

    /**
     * Создает документ в указанном OS. По умолчанию документ не размещается ни в какой папке (попадает в папку Unfiled Documents в FEM)
     * @param os - ObjectStore
     * @param cbrfDocument - документ с необходимой информацией
     * @return
     */
    public static Document createDocWithContent(ObjectStore os, CbrfDocument cbrfDocument) {
        Document doc = Factory.Document.createInstance(os, null);
        doc.getProperties().putValue("DocumentTitle", cbrfDocument.name());
        doc.set_MimeType("application/octet-stream");
        ContentElementList cel = createContentElements(cbrfDocument);
        if (cel != null)
            doc.set_ContentElements(cel);
        return doc;
    }

    private static List<String> folderNamesExlcludeRoot(String destinationFilenetFolder) {
        return Splitter.on("/")
                .omitEmptyStrings()
                .splitToList(destinationFilenetFolder).stream()
                .skip(1)
                .collect(Collectors.toList());
    }

    private static boolean isFolderExist(ObjectStore os, String destinationFilenetFolder) {
        try {
            Factory.Folder.fetchInstance(os, destinationFilenetFolder, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private static ContentElementList createContentElements(CbrfDocument cbrfDocument) {
        ContentElementList cel = Factory.ContentElement.createList();
        ContentTransfer ctNew = createContentTransfer(cbrfDocument);
        cel.add(ctNew);
        return cel;
    }

    private static ContentTransfer createContentTransfer(CbrfDocument cbrfDocument) {
        ContentTransfer ctNew = Factory.ContentTransfer.createInstance();
        ctNew.setCaptureSource(cbrfDocument.byteStream());
        ctNew.set_RetrievalName(cbrfDocument.name());
        return ctNew;
    }

}
