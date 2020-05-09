package xyz.ioc;

public class Pixel {

    private int x;
    private int y;
    private int red;
    private int blue;
    private int green;
    private int rgb;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getRgb() {
        return rgb;
    }

    public void setRgb(int rgb) {
        this.rgb = rgb;
    }

    public static class Builder{

        private int x;
        private int y;
        private int red;
        private int blue;
        private int green;
        private int rgb;

        public Builder atX(int x){
            this.x = x;
            return this;
        }

        public Builder atY(int y){
            this.y = y;
            return this;
        }

        public Builder atRed(int red){
            this.red = red;
            return this;
        }

        public Builder atGreen(int green){
            this.green = green;
            return this;
        }

        public Builder atBlue(int blue){
            this.blue = blue;
            return this;
        }

        public Builder rgb(int x){
            this.rgb = rgb;
            return this;
        }

        public Pixel build(){
            Pixel pixel = new Pixel();
            pixel.x = this.x;
            pixel.y = this.y;
            pixel.red = this.red;
            pixel.green = this.green;
            pixel.blue = this.blue;
            pixel.rgb = this.rgb;
            return pixel;
        }


    }

}
