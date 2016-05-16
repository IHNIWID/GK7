
import java.awt.Color;
import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jakub Tomczuk
 */
public class Binarization {
   
   static private int[] histogram(BufferedImage image) {
        int[] histogram = new int[256];
        int rgb;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                rgb = image.getRGB(x, y);
                histogram[(((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3]++;
            }
        }
        return histogram;
    }
 

   static public void customThreshold(BufferedImage image, int threshold) {
        int rgb;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                rgb = image.getRGB(x, y);
                rgb = (((rgb >> 16) & 0xFF) + ((rgb >> 8) & 0xFF) + (rgb & 0xFF)) / 3;
                if (rgb < threshold) {
                    image.setRGB(x, y, new Color(0, 0, 0).getRGB());
                } else {
                    image.setRGB(x, y, new Color(255, 255, 255).getRGB());
                }
            }
        }
    }
   
    static public void percentBlackSelection(BufferedImage image, double percent) {
        int[] histogram = histogram(image);
        int threshold, black = (int) (percent * image.getWidth() * image.getHeight());
        for (threshold = 0; threshold < 256 && black >= 0; threshold++) {
            black -= histogram[threshold];
        }
        customThreshold(image, threshold);
    }
 
    static public void meanIterativeSelection(BufferedImage image) {
        int[] histogram = histogram(image);
        double avg = 0;
        int pixels = (int) (image.getWidth() * image.getHeight());
        for (int i = 0; i < 256; i++) {
            avg += 1D * i * histogram[i] / pixels;
        }
        int threshold0, threshold1 = (int) avg, i;
        double s1, s2;
        do {
            threshold0 = threshold1;
            s1 = s2 = 0D;
            for (i = 0; i < threshold0; i++) {
                s1 += i * histogram[i];
                s2 += histogram[i];
            }
            threshold1 = (int) (s1 / s2 / 2);
            s1 = s2 = 0D;
            for (; i < 256; i++) {
                s1 += i * histogram[i];
                s2 += histogram[i];
            }
            threshold1 += (int) (s1 / s2 / 2);
        } while (threshold0 != threshold1);
        customThreshold(image, threshold0);
    }
   
    static public void entropySelection(BufferedImage image) {
        int[] histogram = histogram(image);
        int[] distHistogram = histogram.clone();
        for (int i = 0; i < 255; i++) {
            distHistogram[i + 1] += distHistogram[i];
        }
        double p, entropy, max = 0;
        int threshold = 0;
        for (int i = 0; i < 256; i++) {
            entropy = 0;
            if (distHistogram[i] != 0) {
                for (int j = 0; j <= i; j++) {
                    if (histogram[j] == 0) {
                        continue;
                    }
                    p = 1D * histogram[j] / distHistogram[i];
                    entropy -= p * Math.log(p);
                }
            }
            if (distHistogram[255] - distHistogram[i] != 0) {
                for (int j = i + 1; j < 256; j++) {
                    if (histogram[j] == 0) {
                        continue;
                    }
                    p = 1D * histogram[j] / (distHistogram[255] - distHistogram[i]);
                    entropy -= p * Math.log(p);
                }
            }
            if (entropy > max) {
                max = entropy;
                threshold = i;
            }
        }
        customThreshold(image, threshold);
    }
   
    static public void minimumError(BufferedImage image) {
        int[] histogram = histogram(image);
        long s = 0;
        for (int i = 0; i < 256; i++) {
            s += histogram[i] * i;
        }
        long w = 0, s0 = 0;
        int threshold = 0, n = (int) (image.getWidth() * image.getHeight());
        double between, max = 0;
        for (int i = 0; i < 256; i++) {
            w += histogram[i];
            if (w == n) {
                break;
            }
            if (w != 0) {
                s0 += i * histogram[i];
                between = Math.pow((s0 * n - s * w), 2) / w / (n - w);
                if (between > max) {
                    threshold = i;
                    max = between;
                }
            }
        }
        customThreshold(image, threshold);
    }
 
    static public void fuzzyMinimumError(BufferedImage img) {
        int[] h = histogram(img);
        long[] dh = new long[256], dh1 = new long[256];
        dh[0] = h[0];
        dh1[0] = 0;
        for (int i = 0; i < 255; i++) {
            dh[i + 1] = dh[i] + h[i + 1];
            dh1[i + 1] = dh1[i] + h[i + 1] * (i + 1);
        }
        double e, hf, u, u0, min = Double.MAX_VALUE;
        int c = 255, threshold = 0;
        for (int i = 0; i < 256; i++) {
            e = 0;
            for (int j = 0; j < 256; j++) {
                u0 = j <= i ? 1D * dh1[j] / dh[j] : 1D * (dh1[255] - dh1[j]) / (dh[255] - dh[j]);
                u = 1D / (1 + Math.abs(j - u0) / c);
                hf = -u * Math.log(u) - (1 - u) * Math.log(1 - u);
                if (!Double.isNaN(hf)) {
                    e += hf * h[j];
                }
            }
            if (e < min) {
                min = e;
                threshold = i;
            }
        }
        customThreshold(img, threshold);
    }
}
