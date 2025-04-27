import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFToOMR {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Hardcoded paths
        String pdfPath = "path\\to\\pdf";
        String imageOutputDir = "images\\file\\will\\go\\in\\this\\folder";
        String omrOutputDir = "omr\\will\\go\\into\\here"; // .mxl too 
        // Convert PDF to images
        List<String> imagePaths = convertPdfToImages(pdfPath, imageOutputDir);
        
        // Run Audiveris on each image
        for (String imagePath : imagePaths) {
            runAudiveris(imagePath, omrOutputDir);
        }

        System.out.println("✅ All pages processed. OMR files should be in: " + omrOutputDir);
    }

    private static List<String> convertPdfToImages(String pdfPath, String outputDir) throws IOException {
        List<String> imagePaths = new ArrayList<>();
        try (PDDocument document = PDDocument.load(new File(pdfPath))) {
            PDFRenderer renderer = new PDFRenderer(document);
            new File(outputDir).mkdirs();
            
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 300);
                String imagePath = outputDir + "/page_" + (i + 1) + ".png";
                ImageIO.write(image, "PNG", new File(imagePath));
                imagePaths.add(imagePath);
            }

            System.out.println("🖼️ Converted " + document.getNumberOfPages() + " pages to images.");
        }
        return imagePaths;
    }

    private static void runAudiveris(String imagePath, String outputDir) throws IOException, InterruptedException {
        new File(outputDir).mkdirs();
        
        ProcessBuilder pb = new ProcessBuilder(
            "path\\to\\audiveris.exe",
            "-batch",
            "-export",
            "-output", outputDir,
            imagePath
        );
        

        pb.inheritIO(); // Show Audiveris output in console
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("🎵 Audiveris processed: " + imagePath);
        } else {
            System.err.println("❌ Audiveris failed for: " + imagePath);
        }
    }
}
