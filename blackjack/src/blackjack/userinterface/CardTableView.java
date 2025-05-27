package blackjack.userinterface;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.InputStream;

public class CardTableView {

    /** Xóa tất cả các lá bài hiện có trên HBox */
    public static void clear(HBox box) {
        box.getChildren().clear();
    }

    /** Thêm một lá bài vào HBox dựa trên đường dẫn resource hình ảnh */
    public static void addCard(HBox box, String imagePath) {
        try (InputStream in = CardTableView.class.getResourceAsStream(imagePath)) {
            if (in == null) {
                System.err.println("Image resource not found: " + imagePath);
                return;
            }
            ImageView iv = new ImageView(new Image(in));
            iv.setFitWidth(70);
            iv.setFitHeight(100);
            box.getChildren().add(iv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Hiển thị danh sách các lá bài trong HBox */
    public static void showCards(HBox box, java.util.List<?> cards) {
        clear(box);
        for (Object c : cards) {
            // Giả sử mỗi đối tượng 'c' có phương thức getImagePath()
            try {
                String imagePath = (String) c.getClass().getMethod("getImagePath").invoke(c);
                addCard(box, imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
