package xyz.ioc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final String SAMPLE_DIR = File.separatorChar + "samples" + File.separatorChar;

    public static void main(String[] args){
        try {

            String dir = System.getProperty("user.dir");
            Path path = Paths.get(dir + SAMPLE_DIR + "room.jpg");
            BufferedImage image = ImageIO.read(path.toFile());

            int size = Integer.parseInt(args[0]);
            Integer[][] kernel = getDynamicKernel(size);

            for(int x = 0; x < kernel.length; x++) {
                for (int y = 0; y < kernel[x].length; y++) {
                    System.out.print(kernel[x][y] + ":");
                }
                System.out.println("");
            }


            int total = getTotal(kernel);

            List<Pixel> pixels = new ArrayList<Pixel>();
            for(int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgb = image.getRGB(x, y);

                    List<List<Pixel>> grid = getDynamicGrid(x ,y, size, image);
                    List<Pixel> product = new ArrayList<Pixel>();
                    for(int n = 0; n < grid.size(); n++){
                        for(int m = 0; m < grid.get(n).size(); m++){
                            Pixel node = grid.get(n).get(m);
                            Color c2 = new Color(node.getRgb());
                            int r2 = c2.getRed();
                            int g2 = c2.getGreen();
                            int b2 = c2.getBlue();

                            int br = kernel[n][m] * r2;
                            int bg = kernel[n][m] * g2;
                            int bb = kernel[n][m] * b2;

                            Pixel node2 = new Pixel.Builder()
                                    .atX(node.getX())
                                    .atY(node.getY())
                                    .atRed(br)
                                    .atGreen(bg)
                                    .atBlue(bb)
                                    .build();

                            product.add(node2);
                        }
                    }

                    int sumr = 0;
                    int sumg = 0;
                    int sumb = 0;

                    for(Pixel pixel : product){
                        sumr += pixel.getRed();
                        sumg += pixel.getGreen();
                        sumb += pixel.getBlue();
                    }

                    int avgr = Math.round(sumr / total);
                    int avgg = Math.round(sumg / total);
                    int avgb = Math.round(sumb / total);

//                    System.out.println("avgs: " + avgr + ", " + avgg + ", " + avgb);

                    Pixel pixel = new Pixel.Builder()
                            .atX(x)
                            .atY(y)
                            .atRed(avgr)
                            .atGreen(avgg)
                            .atBlue(avgb)
                            .build();

                    pixels.add(pixel);
                }
            }


            BufferedImage result = new BufferedImage(
                    image.getWidth(),
                    image.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics2D graphic = result.createGraphics();
            graphic.drawImage(image, 0, 0, Color.WHITE, null);

            for (int n = 0; n < pixels.size(); n++) {
                Pixel xspace = pixels.get(n);
                Color c = new Color(xspace.getRed(), xspace.getGreen(), xspace.getBlue());
                result.setRGB(xspace.getX(), xspace.getY(), c.getRGB());
            }

            System.out.println("processing: " + pixels.size());

            File tempFile = new File(dir + SAMPLE_DIR + "output" + File.separatorChar + "final.png");
            Path filepath = tempFile.toPath();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( result, "png", baos );
            baos.flush();

            try (OutputStream os = Files.newOutputStream(filepath)) {
                os.write(baos.toByteArray());
            }catch(Exception e){
                System.out.println("unable to write file");
            }

            baos.close();

            System.out.println("image complete: " + filepath);

        }catch(Exception e){
            e.printStackTrace();
            ///////////////////////////////////////////////////////////////////////////////////
        }
    }

    private static List<List<Pixel>> getDynamicGrid(int x, int y, int size, BufferedImage image){
        List<List<Pixel>> grid = new ArrayList<List<Pixel>>();
        int bottom = -(int)Math.round(size);
        for(int n = 0; n < size; n++) {
            List<Pixel> pixels = new ArrayList<Pixel>();
            for (int q = 0; q < size; q++){
                pixels.add(getPixel(image, x + (bottom), y + (bottom)));
                bottom++;
            }
            grid.add(pixels);
            bottom = -(int)Math.round(size);
        }
        return grid;
    }

    /**
     * 1x1
     * 2
     *
     * 2x2
     * 1 2
     * 2 1
     *
     * 3x3
     * 1 2 1
     * 2 4 2
     * 1 2 1
     *
     * 4x4
     * 1 2 2 1
     * 2 4 4 2
     * 2 4 4 2
     * 1 2 2 1
     *
     * 5x5
     * 1 2 4 2 1
     * 2 4 8 4 2
     * 4 8 16 8 4
     * 2 4 8 4 2
     * 1 2 4 2 1
     */

    private static Integer[][] getDynamicKernel(int size){

        int integralx = 0;
        int integraly = 0;

        Integer[][] kernel = new Integer[size][size];

        for(int x = 0; x < size; x++){

            for(int y = 0; y < size; y++){
                if(size == 1){
                    kernel[x][y] = 2;
                }else if(size == 2) {

                    if(x == 0 && y == 0)
                        kernel[x][y] = 1;

                    if(x == 0 && y == 1 )
                        kernel[x][y] = 2;

                    if(x == 1 && y == 0)
                        kernel[x][y] = 2;

                    if(x == 1 && y == 1)
                        kernel[x][y] = 1;

                }else if(size > 2){

                    /**
                     * 5x5
                     * 1 2 4 2 1
                     * 2 4 8 4 2
                     * 4 8 16 8 4
                     * 2 4 8 4 2
                     * 1 2 4 2 1
                     */

                    kernel[x][y] = y + integraly + (integralx * 2);

                    if((x == 0 && y == 0) ||
                            (x == size -1 && y == size -1) ||
                            (x == 0 && y == size -1) ||
                            (x == size -1 && y == 0))
                        kernel[x][y] = 1;


                    if(y < size / 2){
                        integraly += 2;
                    }
                    if(y >= size /2){
                        integraly -= 2;
                    }
                }

            }

            integraly = 0;

            if(x < size / 2){
                integralx += 2;
            }

            if(x >= size /2){
                integralx = integralx - 2;
            }

//            System.out.println("");
        }
        return kernel;
    }

    private static int getTotal(Integer[][] kernel){
        int total = 0;
        for(int n = 0; n < kernel.length; n++)
            for(int m = 0; m < kernel[n].length; m++)
                total += kernel[n][m];

        return total;
    }

    private static Pixel getPixel(BufferedImage image, int x, int y){
        Pixel pixel = new Pixel.Builder()
                .atX(x)
                .atY(y)
                .build();
        try {
            int rgb = image.getRGB(x, y);
            pixel.setRgb(rgb);
        }catch (Exception e){
            //////////////////////////////////////////////
        }
        return pixel;
    }
}

