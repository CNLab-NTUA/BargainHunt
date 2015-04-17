package gr.ntua.cn.zannis.bargains;

import gr.ntua.cn.zannis.bargains.algorithm.FilterStrength;
import gr.ntua.cn.zannis.bargains.algorithm.OutlierFinder;
import gr.ntua.cn.zannis.bargains.client.exceptions.UnexpectedInputException;
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
    public static void main(String[] args) throws UnexpectedInputException {

        Logger log = LoggerFactory.getLogger(Main.class.getCanonicalName());
        FilterStrength strength;

        try {
            Utils.initPropertiesFiles();
            if (SkroutzRestClient.get() != null) {
                boolean exit = false;
                Scanner textScanner = new Scanner(System.in);
//                textScanner.useDelimiter("\n");
                Scanner intScanner = new Scanner(System.in);
//                intScanner.useDelimiter("\n");
                while (!exit) {
                    System.out.print("Πατήστε 1 για χαλαρό φίλτρο, 2 για το κανονικό και 3 για αυστηρό: ");
                    int input = intScanner.nextInt();
                    switch (input) {
                        case 1:
                            strength = FilterStrength.RELAXED;
                            break;
                        case 2:
                            strength = FilterStrength.NORMAL;
                            break;
                        case 3:
                            strength = FilterStrength.STRONG;
                            break;
                        default:
                            throw new UnexpectedInputException();
                    }
                    System.out.print("Εισάγετε προϊόν για αναζήτηση : ");
                    String query = textScanner.nextLine();
                    SearchResults results = SkroutzRestClient.get().search(query);
                    // get first category and keep looking
                    System.out.println("Βρέθηκαν προϊόντα σαν αυτό που ζητήσατε στις παρακάτω κατηγορίες, επιλέξτε μία για να συνεχίσετε : ");
                    int i = 1;
                    for (Category c : results.getCategories()) {
                        System.out.println(i++ + " : " + c.getName());
                    }
                    int categoryId = intScanner.nextInt() - 1;
                    assert categoryId > 0 ;
                    Page<Sku> skuPage = SkroutzRestClient.get().searchSkusFromCategory(results.getCategories().get(categoryId), query);
                    List<Sku> skuList = SkroutzRestClient.get().getAllResultsAsList(skuPage);
                    System.out.println("Βρέθηκαν τα εξής προϊόντα, παρακαλώ επιλέξτε αυτό που επιθυμείτε : ");
                    i = 1;
                    for (Sku s : skuList) {
                        System.out.println(i++ + " : " + s.getName());
                    }
                    int skuId = intScanner.nextInt() - 1;
                    assert skuId > 0 && skuId <= skuList.size();
                    Page<Product> productsPage = SkroutzRestClient.get().getProductsFromSku(skuList.get(skuId));
                    List<Product> productList = SkroutzRestClient.get().getAllResultsAsList(productsPage);
                    List<Float> prices = productList.stream().map(Product::getPrice).collect(Collectors.toList());
                    OutlierFinder finder = new OutlierFinder(prices, strength);
                    if (finder.getLowOutliers().isEmpty()) {
                        System.out.println("Δυστυχώς το προϊόν δεν βρίσκεται σε προσφορά. Δοκιμάστε ξανά στο μέλλον!");
                        System.out.println("Βρέθηκαν " + prices.size() + " προϊόντα:");
                        System.out.println("Χαμηλότερη τιμή: " + finder.getMin());
                        System.out.println("Κανονικοποιημένη μέση τιμή: " + finder.getNormalizedMean());
                        System.out.println("Υπερβολικά υψηλές τιμές: " + finder.getHighOutliers());
                    } else {
                        System.out.println("Είστε τυχεροί! Το προϊόν υπάρχει σε προσφορά στα " + finder.getLowOutliers().get(0) + " ευρώ! Είναι "
                                + finder.getBargainPercentage() + "% πιο φθηνό από τη μέση τιμή, η οποία είναι στα "
                                + finder.getNormalizedMean() + " ευρώ.");
                    }
                    System.out.println("Γράψτε exit για έξοδο, διαφορετικά πατήστε Enter");
                    if (textScanner.nextLine().equals("exit")) {
                        exit = true;
                    }
                }
                textScanner.close();
                intScanner.close();
            }

        } catch (IOException e) {
            log.error("Υπήρξε πρόβλημα στο άνοιγμα αρχείου.", e);
        }
    }
}
