package ru.liga.adapter;

import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FnAdapterTest {

    private FnAdapter adapter;

    @Before
    public void init() {
        adapter = new FnAdapter(new FnConnectionParameteres(
                "http://192.168.199.129:9080/wsi/FNCEWS40MTOM/",
                "wasadmin",
                "qwerty$4",
                "os",
                "/cbrf"
        ));
    }


    @Test
    public void loadFileFromFilenetById() throws Exception {
        CbrfDocument cbrfDocument = adapter.load("{06C3790A-BC6A-4B1D-A9EF-5F0F623D4EA1}");
        assertThat(cbrfDocument.name()).isEqualTo("testDoc");
        assertThat(cbrfDocument.byteStream()).isNotNull();
        assertThat(cbrfDocument.size()).isEqualTo(20943);
    }

    @Test
    public void saveFileFromFilenet() throws Exception {
        String guid = adapter.save(new CbrfDocument("Notice", new FileInputStream("C:\\apache-maven-3.5.2\\NOTICE")), "/cbrf/a/b/c");
        assertThat(guid).isNotBlank();
    }


}