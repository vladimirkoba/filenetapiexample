package ru.liga.adapter;

import com.filenet.api.constants.AutoClassify;
import com.filenet.api.constants.CheckinType;
import com.filenet.api.constants.PropertyNames;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Document;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.Id;
import ru.liga.api.FilenetUtils;


/**
 * API для работы с файлнетом
 */
public class FnAdapter {
    private final FnConnectionParameteres connectionParameters;
    /**
     *
     * @param connectionParameters параметры для подключения файлнета
     */
    public FnAdapter(FnConnectionParameteres connectionParameters) {
        this.connectionParameters = connectionParameters;
    }

    /**
     * Сохраняет документ в указанную папку. Если папки не существует - создает её.
     * @param cbrfDocument документ для сохранения
     * @param destinationFilenetFolder целевая папка
     * @return GUID документа
     */
    public String save(CbrfDocument cbrfDocument, String destinationFilenetFolder) {
        ObjectStore os = currentObjectStore();
        FilenetUtils.createFolderHierarchyIfNeeded(os, connectionParameters.rootFolderName(), destinationFilenetFolder);
        Document docWithContent = saveDocumentToFn(os, cbrfDocument);
        FilenetUtils.moveDocumentToFolder(os, docWithContent, destinationFilenetFolder);
        return docWithContent.get_Id().toString();
    }


    /**
     * Выгружает контент из файлнета
     * @param guid GUID контента
     * @return документ с нужными параметрами
     */
    public CbrfDocument load(String guid) {
        Document doc = Factory.Document.getInstance(currentObjectStore(), null, new Id(guid));
        doc.refresh(new String[]{PropertyNames.CONTENT_ELEMENTS, PropertyNames.CONTENT_SIZE, PropertyNames.NAME});
        return new CbrfDocument(doc.get_Name(), doc.get_ContentSize().longValue(), doc.accessContentStream(0));
    }


    private Document saveDocumentToFn(ObjectStore os, CbrfDocument cbrfDocument) {
        Document docWithContent = FilenetUtils.createDocWithContent(os, cbrfDocument);
        docWithContent.checkin(AutoClassify.AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);
        docWithContent.save(RefreshMode.REFRESH);
        return docWithContent;
    }

    private ObjectStore currentObjectStore() {
        return FilenetUtils.connectAndGetObjectStore(
                connectionParameters.ceUrl(),
                connectionParameters.login(),
                connectionParameters.password(),
                connectionParameters.osName());
    }
}
