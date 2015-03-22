package gr.ntua.cn.zannis.bargains.client;

import org.junit.Test;

public class SkroutzRestClientTest {

    @Test
    public void testGetProductById() throws Exception {
        //example response for json
        String jsonResponse = "{\"product\":{\"id\":18427940,\"name\":\"MSATA SSD 500GB Samsung MZ-840E MTE500BW\"," +
                "\"sku_id\":4424708,\"shop_id\":2281,\"category_id\":88,\"availability\":\"4 \\u03ad\\u03c9\\u03c2 7 " +
                "\\u03b7\\u03bc\\u03ad\\u03c1\\u03b5\\u03c2\",\"click_url\":" +
                "\"https://www.skroutz.gr/products/show/18427940?client_id=2xpcTRCsFIXFy893wkfOcA%3D%3D&from=api\"," +
                "\"shop_uid\":\"CT7034\",\"price\":226.98}}\n";


    }
}