package gr.ntua.cn.zannis.bargains;

import gr.ntua.cn.zannis.bargains.algorithm.FilterStrength;
import gr.ntua.cn.zannis.bargains.algorithm.OutlierFinder;
import gr.ntua.cn.zannis.bargains.client.impl.SkroutzRestClient;
import gr.ntua.cn.zannis.bargains.client.misc.Utils;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Category;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Product;
import gr.ntua.cn.zannis.bargains.client.persistence.entities.Sku;
import gr.ntua.cn.zannis.bargains.client.responses.impl.SearchResults;
import gr.ntua.cn.zannis.bargains.client.responses.meta.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Simple command line app demonstrating application usage.
 *
 * @author zannis <zannis.kal@gmail.com>
 */
public class Main {
    public static void main(String[] args) {

        Logger log = LoggerFactory.getLogger(Main.class.getCanonicalName());

        try {
            Utils.initPropertiesFiles();
            if (SkroutzRestClient.get() != null) {
                boolean exit = false;
                Scanner sc = new Scanner(System.in);
                sc.useDelimiter("\n");
                while (!exit) {
                    System.out.print("Εισάγετε προϊόν για αναζήτηση : ");
                    String query = sc.next();
                    SearchResults results = SkroutzRestClient.get().search(query);
                    // get first category and keep looking
                    System.out.println("Βρέθηκαν προϊόντα σαν αυτό που ζητήσατε στις παρακάτω κατηγορίες, επιλέξτε μία για να συνεχίσετε : ");
                    int i = 1;
                    for (Category c : results.getCategories()) {
                        System.out.println(i++ + " : " + c.getName());
                    }
                    int categoryId = sc.nextInt() - 1;
                    assert categoryId > 0;
                    Page<Sku> skuPage = SkroutzRestClient.get().searchSkusFromCategory(results.getCategories().get(categoryId), query);
                    List<Sku> skuList = SkroutzRestClient.get().getAllResultsAsList(skuPage);
                    System.out.println("Βρέθηκαν τα εξής προϊόντα, παρακαλώ επιλέξτε αυτό που επιθυμείτε : ");
                    i = 1;
                    for (Sku s : skuList) {
                        System.out.println(i++ + " : " + s.getName());
                    }
                    int skuId = sc.nextInt() - 1;
                    assert skuId > 0 && skuId <= skuList.size();
                    Page<Product> productsPage = SkroutzRestClient.get().getProductsFromSku(skuList.get(skuId));
                    List<Product> productList = SkroutzRestClient.get().getAllResultsAsList(productsPage);
                    List<Float> prices = productList.stream().map(Product::getPrice).collect(Collectors.toList());
                    OutlierFinder finder = new OutlierFinder(prices, FilterStrength.RELAXED);
                    if (finder.getLowOutliers().isEmpty()) {
                        System.out.println("Δυστυχώς το προϊόν δεν βρίσκεται σε προσφορά. Δοκιμάστε ξανά στο μέλλον!");
                        System.out.println(finder.getNormalizedMean());
                        System.out.println(finder.getHighOutliers());
                    } else {
                        System.out.println("Είστε τυχεροί! Το προϊόν υπάρχει σε προσφορά στα " + finder.getLowOutliers().get(0) + " ευρώ! Είναι "
                                + finder.getBargainPercentage() + "% πιο φθηνό από τη μέση τιμή, η οποία είναι στα "
                                + finder.getNormalizedMean() + " ευρώ.");
                    }
                    System.out.println("Γράψτε exit για έξοδο, διαφορετικά πατήστε Enter");
                    if (sc.next().equals("exit")) {
                        exit = true;
                    }
                }
                sc.close();
//                List<Category> categories = client.getAllCategories();
//                System.out.println(categories.size());
            }

        } catch (IOException e) {
            log.error("Υπήρξε πρόβλημα στο άνοιγμα αρχείου.", e);
        }
    }
}
