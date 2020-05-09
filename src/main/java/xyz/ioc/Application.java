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

            Integer[][] kernel = getKernel();
            int total = getTotal(kernel);

            List<Pixel> pixels = new ArrayList<Pixel>();
            for(int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgb = image.getRGB(x, y);

                    List<List<Pixel>> grid = getGrid(image, x ,y);
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

                    System.out.println("avgs: " + avgr + ", " + avgg + ", " + avgb);

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
                System.out.println(xspace.getRed() + "," + xspace.getGreen() + "," + xspace.getBlue());
                Color c = new Color(xspace.getRed(), xspace.getGreen(), xspace.getBlue());
                result.setRGB(xspace.getX(), xspace.getY(), c.getRGB());
            }

            System.out.println("processing: " + pixels.size());

            File tempFile = new File(dir + SAMPLE_DIR + "output" + File.separatorChar + "final_product.png");
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

            System.out.println("image complete...");

        }catch(Exception e){
            e.printStackTrace();
            ///////////////////////////////////////////////////////////////////////////////////
        }
    }

    private static int getTotal(Integer[][] kernel){
        int total = 0;
        for(int n = 0; n < kernel.length; n++)
            for(int m = 0; m < kernel[n].length; m++)
                total += kernel[n][m];

        return total;
    }


    private static List<List<Pixel>> getGrid(BufferedImage image, int x, int y){
        List<List<Pixel>> grid = new ArrayList<List<Pixel>>();
        List<Pixel> yt = new ArrayList<Pixel>();
        yt.add(getColorNode(image, x - 2, y -2));
        yt.add(getColorNode(image, x -1 , y -2));
        yt.add(getColorNode(image, x , y -2));
        yt.add(getColorNode(image, x + 1, y -2));
        yt.add(getColorNode(image, x + 2, y -2));


        List<Pixel> yt2 = new ArrayList<Pixel>();
        yt2.add(getColorNode(image, x - 2, y -1));
        yt2.add(getColorNode(image, x -1 , y -1));
        yt2.add(getColorNode(image, x , y -1));
        yt2.add(getColorNode(image, x + 1, y -1));
        yt2.add(getColorNode(image, x + 2, y -1));


        List<Pixel> ym = new ArrayList<Pixel>();
        ym.add(getColorNode(image, x - 2, y));
        ym.add(getColorNode(image, x -1 , y));
        ym.add(getColorNode(image, x , y));
        ym.add(getColorNode(image, x + 1, y));
        ym.add(getColorNode(image, x + 2, y));


        List<Pixel> yb2 = new ArrayList<Pixel>();
        yb2.add(getColorNode(image, x - 2, y + 1));
        yb2.add(getColorNode(image, x -1 , y + 1));
        yb2.add(getColorNode(image, x , y + 1));
        yb2.add(getColorNode(image, x + 1, y + 1));
        yb2.add(getColorNode(image, x + 2, y + 1));


        List<Pixel> yb = new ArrayList<Pixel>();
        yb.add(getColorNode(image, x - 2, y + 2));
        yb.add(getColorNode(image, x -1 , y + 2));
        yb.add(getColorNode(image, x , y + 2));
        yb.add(getColorNode(image, x + 1, y + 2));
        yb.add(getColorNode(image, x + 2, y + 2));

        grid.add(yt);
        grid.add(yt2);
        grid.add(ym);
        grid.add(yb2);
        grid.add(yb);

        return grid;
    }

    private static List<List<Pixel>> getGridOld(BufferedImage image, int x, int y){
        List<List<Pixel>> grid = new ArrayList<List<Pixel>>();
        List<Pixel> yt = new ArrayList<Pixel>();
        yt.add(getColorNode(image, x - 1, y -1));
        yt.add(getColorNode(image, x, y -1));
        yt.add(getColorNode(image, x + 1, y -1));

        List<Pixel> ym = new ArrayList<Pixel>();
        ym.add(getColorNode(image, x - 1, y));
        ym.add(getColorNode(image, x, y));
        ym.add(getColorNode(image, x + 1, y));

        List<Pixel> yb = new ArrayList<Pixel>();
        yb.add(getColorNode(image, x - 1, y +1));
        yb.add(getColorNode(image, x, y +1));
        yb.add(getColorNode(image, x + 1, y +1));

        grid.add(yt);
        grid.add(ym);
        grid.add(yb);

        return grid;
    }

    private static Pixel getColorNode(BufferedImage image, int x, int y){
        Pixel pixel = new Pixel.Builder()
                .atX(x)
                .atY(y)
                .build();
        try {
            int rgb = image.getRGB(x, y);
            pixel.setRgb(rgb);
        }catch (Exception e){
            System.out.println("not valued it");
            //////////////////////////////////////////////
        }
        return pixel;
    }

    /**
     * 1 2 3 2 1
     * 2 3 4 3 2
     * 3 4 8 4 3
     * 2 3 4 3 2
     * 1 2 3 2 1
     */
    private static Integer[][] getKernel(){
        Integer[][] kernel = new Integer[5][5];
        kernel[0][0] = 1;
        kernel[1][0] = 2;
        kernel[2][0] = 3;
        kernel[3][0] = 2;
        kernel[4][0] = 1;

        kernel[0][1] = 2;
        kernel[1][1] = 3;
        kernel[2][1] = 4;
        kernel[3][1] = 3;
        kernel[4][1] = 2;

        kernel[0][2] = 3;
        kernel[1][2] = 4;
        kernel[2][2] = 8;
        kernel[3][2] = 4;
        kernel[4][2] = 3;

        kernel[0][3] = 2;
        kernel[1][3] = 3;
        kernel[2][3] = 4;
        kernel[3][3] = 3;
        kernel[4][3] = 2;

        kernel[0][4] = 1;
        kernel[1][4] = 2;
        kernel[2][4] = 3;
        kernel[3][4] = 2;
        kernel[4][4] = 1;

        return kernel;
    }

    private static Integer[][] getKernelOne(){
        Integer[][] kernel = new Integer[3][3];
        kernel[0][0] = 1;
        kernel[1][0] = 2;
        kernel[2][0] = 1;

        kernel[0][1] = 2;
        kernel[1][1] = 4;
        kernel[2][1] = 2;

        kernel[0][2] = 1;
        kernel[1][2] = 2;
        kernel[2][2] = 1;

        return kernel;
    }
}

